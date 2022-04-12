package com.hua.service;

import android.annotation.SuppressLint;

import androidx.media3.common.C;
import androidx.media3.exoplayer.source.ShuffleOrder;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Xiaoc
 * @since 2021/3/27
 *
 * 自定义Shuffle随机播放策略
 * 默认的 [DefaultShuffleOrder] 由于每次插入都是随机插入，与索引无关，导致在随机模式下添加到下一首等功能失效
 */
@SuppressLint("UnsafeOptInUsageError")
public class FluidMusicShuffleOrder implements ShuffleOrder {

    private final Random random;
    private final int[] shuffled;
    private final int[] indexInShuffled;

    /**
     * Creates an instance with a specified length.
     *
     * @param length The length of the shuffle order.
     */
    public FluidMusicShuffleOrder(int length) {
        this(length, new Random());
    }

    /**
     * Creates an instance with a specified length and the specified random seed. Shuffle orders of
     * the same length initialized with the same random seed are guaranteed to be equal.
     *
     * @param length The length of the shuffle order.
     * @param randomSeed A random seed.
     */
    public FluidMusicShuffleOrder(int length, long randomSeed) {
        this(length, new Random(randomSeed));
    }

    /**
     * Creates an instance with a specified shuffle order and the specified random seed. The random
     * seed is used for {@link #cloneAndInsert(int, int)} invocations.
     *
     * @param shuffledIndices The shuffled indices to use as order.
     * @param randomSeed A random seed.
     */
    public FluidMusicShuffleOrder(int[] shuffledIndices, long randomSeed) {
        this(Arrays.copyOf(shuffledIndices, shuffledIndices.length), new Random(randomSeed));
    }

    private FluidMusicShuffleOrder(int length, Random random) {
        this(createShuffledList(length, random), random);
    }

    private FluidMusicShuffleOrder(int[] shuffled, Random random) {
        this.shuffled = shuffled;
        this.random = random;
        this.indexInShuffled = new int[shuffled.length];
        for (int i = 0; i < shuffled.length; i++) {
            indexInShuffled[shuffled[i]] = i;
        }
    }

    @Override
    public int getLength() {
        return shuffled.length;
    }

    @Override
    public int getNextIndex(int index) {
        int shuffledIndex = indexInShuffled[index];
        return ++shuffledIndex < shuffled.length ? shuffled[shuffledIndex] : C.INDEX_UNSET;
    }

    @Override
    public int getPreviousIndex(int index) {
        int shuffledIndex = indexInShuffled[index];
        return --shuffledIndex >= 0 ? shuffled[shuffledIndex] : C.INDEX_UNSET;
    }

    @Override
    public int getLastIndex() {
        return shuffled.length > 0 ? shuffled[shuffled.length - 1] : C.INDEX_UNSET;
    }

    @Override
    public int getFirstIndex() {
        return shuffled.length > 0 ? shuffled[0] : C.INDEX_UNSET;
    }

    @NotNull
    @Override
    public ShuffleOrder cloneAndInsert(int insertionIndex, int insertionCount) {
        int[] insertionPoints = new int[insertionCount];
        int[] insertionValues = new int[insertionCount];
        for (int i = 0; i < insertionCount; i++) {
            // 默认插入位置为传来的插入索引
            insertionPoints[i] = insertionIndex;
            // 如果在原来的随机顺序索引中找到当前要插入的索引，则该位置就是要插入的位置
            for(int j = 0; j < shuffled.length; j++){
                if(insertionIndex == shuffled[j]){
                    insertionPoints[i] = j;
                    break;
                }
            }
            int swapIndex = random.nextInt(i + 1);
            insertionValues[i] = insertionValues[swapIndex];
            insertionValues[swapIndex] = i + insertionIndex;
        }
        Arrays.sort(insertionPoints);
        int[] newShuffled = new int[shuffled.length + insertionCount];
        int indexInOldShuffled = 0;
        int indexInInsertionList = 0;
        for (int i = 0; i < shuffled.length + insertionCount; i++) {
            if (indexInInsertionList < insertionCount
                    && indexInOldShuffled == insertionPoints[indexInInsertionList]) {
                newShuffled[i] = insertionValues[indexInInsertionList++];
            } else {
                newShuffled[i] = shuffled[indexInOldShuffled++];
                if (newShuffled[i] >= insertionIndex) {
                    newShuffled[i] += insertionCount;
                }
            }
        }
        return new FluidMusicShuffleOrder(newShuffled, new Random(random.nextLong()));
    }

    @NotNull
    @Override
    public ShuffleOrder cloneAndRemove(int indexFrom, int indexToExclusive) {
        int numberOfElementsToRemove = indexToExclusive - indexFrom;
        int[] newShuffled = new int[shuffled.length - numberOfElementsToRemove];
        int foundElementsCount = 0;
        for (int i = 0; i < shuffled.length; i++) {
            if (shuffled[i] >= indexFrom && shuffled[i] < indexToExclusive) {
                foundElementsCount++;
            } else {
                newShuffled[i - foundElementsCount] =
                        shuffled[i] >= indexFrom ? shuffled[i] - numberOfElementsToRemove : shuffled[i];
            }
        }
        return new FluidMusicShuffleOrder(newShuffled, new Random(random.nextLong()));
    }

    @NotNull
    @Override
    public ShuffleOrder cloneAndClear() {
        return new FluidMusicShuffleOrder(/* length= */ 0, new Random(random.nextLong()));
    }

    private static int[] createShuffledList(int length, Random random) {
        int[] shuffled = new int[length];
        for (int i = 0; i < length; i++) {
            int swapIndex = random.nextInt(i + 1);
            shuffled[i] = shuffled[swapIndex];
            shuffled[swapIndex] = i;
        }
        return shuffled;
    }

}
