#include <jni.h>
#include <cstdlib>
#include <iostream>
#include <android/log.h>
#include <tiostream.h>
#include <tfilestream.h>
#include <fileref.h>
#include <tpropertymap.h>

#define LOG_TAG "taglib_jni"
/**
 * 获取音乐的Tag通用标签信息
 * @param fd 文件描述符
 *
 * 通过文件描述符获取文件信息，获取该文件（音乐）的信息
 * 会自动包装为 [MediaTag] Java 对象
 *
 * 会自动包装以下信息：（如果有的话，如果没有则返回null）
 * TITLE 标题
 * TRACKNUMBER 曲目顺序
 * DATE 发行年份
 * GENRE 流派
 * ARTIST 歌手
 * COMPOSER 作曲家
 * ALBUM 专辑
 * COMMENT 注释
 * ALBUMARTIST 专辑艺术家
 * DISCNUMBER 唱片编号
 * BITRATE 比特率
 * CHANNELS 音道
 * SAMPLERATE 采样率
 * COPYRIGHT 版权信息
 * LENGH 时长
 * SIZE 文件大小
 *
 */
extern "C"
JNIEXPORT jobject JNICALL
Java_com_hua_taglib_TaglibLibrary_taglibGetMediaTag(JNIEnv *env, jobject thiz, jint fd) {
    // 创建Tag对象，通过fd文件描述符得到对应文件信息
    TagLib::IOStream* ioStream = new TagLib::FileStream(fd);
    TagLib::FileRef ref(ioStream);

    // 如果该文件不存在，则返回null
    if(ref.isNull()){
        return nullptr;
    }

    jclass mediaTagClass = env -> FindClass("com/hua/taglib/MediaTag");
    jmethodID  methodId = env -> GetMethodID(mediaTagClass,"<init>","()V");
    jobject mediaTag = env -> NewObject(mediaTagClass,methodId);

    jfieldID fileName = env -> GetFieldID(mediaTagClass,"fileName", "Ljava/lang/String;");
    jfieldID title = env -> GetFieldID(mediaTagClass,"title", "Ljava/lang/String;");
    jfieldID track = env -> GetFieldID(mediaTagClass,"track", "I");
    jfieldID year = env -> GetFieldID(mediaTagClass,"year", "I");
    jfieldID genre = env -> GetFieldID(mediaTagClass,"genre", "Ljava/lang/String;");
    jfieldID artist = env -> GetFieldID(mediaTagClass,"artist", "Ljava/lang/String;");
    jfieldID composer = env -> GetFieldID(mediaTagClass,"composer", "Ljava/lang/String;");
    jfieldID album = env -> GetFieldID(mediaTagClass,"album", "Ljava/lang/String;");
    jfieldID comment = env -> GetFieldID(mediaTagClass,"comment", "Ljava/lang/String;");
    jfieldID albumArtist = env -> GetFieldID(mediaTagClass,"albumArtist", "Ljava/lang/String;");
    jfieldID disc = env -> GetFieldID(mediaTagClass,"disc", "I");
    jfieldID copyright = env -> GetFieldID(mediaTagClass,"copyright", "Ljava/lang/String;");
    jfieldID bitrate = env -> GetFieldID(mediaTagClass,"bitrate", "I");
    jfieldID channels = env -> GetFieldID(mediaTagClass,"channels", "I");
    jfieldID sampleRate = env -> GetFieldID(mediaTagClass,"sampleRate", "I");
    jfieldID length = env -> GetFieldID(mediaTagClass,"length", "I");
    jfieldID size = env -> GetFieldID(mediaTagClass,"size", "J");

    TagLib::File* file = ref.file();
    if(file == nullptr){
        return nullptr;
    }

    // 文件大小
    env -> SetLongField(mediaTag,size,(jlong)file->length());
    // 文件名
    env -> SetObjectField(mediaTag,fileName,env -> NewStringUTF(file->name()));

    TagLib::Tag* tag = ref.tag();

    // 标题
    env -> SetObjectField(mediaTag,title,env -> NewStringUTF(tag->title().toCString(true)));
    // 曲目
    env -> SetIntField(mediaTag,track,(jint)tag->track());
    // 年份
    env -> SetIntField(mediaTag,year,(jint)tag->year());
    // 流派
    env -> SetObjectField(mediaTag,genre,env -> NewStringUTF(tag->genre().toCString(true)));
    // 歌手
    env -> SetObjectField(mediaTag,artist,env -> NewStringUTF(tag->artist().toCString(true)));
    // 专辑
    env -> SetObjectField(mediaTag,album,env -> NewStringUTF(tag->album().toCString(true)));
    // 注释
    env -> SetObjectField(mediaTag,comment,env -> NewStringUTF(tag->comment().toCString(true)));

    TagLib::PropertyMap map = ref.file()->properties();

    // 作曲家
    if(map.contains("COMPOSER")){
        env -> SetObjectField(mediaTag,composer,env -> NewStringUTF(map.find("COMPOSER")->second.toString().toCString(true)));
    }
    // 版权信息
    if(map.contains("COPYRIGHT")){
        env -> SetObjectField(mediaTag,copyright,env -> NewStringUTF(map.find("COPYRIGHT")->second.toString().toCString(true)));
    }
    // 唱片编号
    if(map.contains("DISCNUMBER")){
        env -> SetIntField(mediaTag,disc,map.find("DISCNUMBER")->second.toString().toInt());
    }
    // 专辑艺术家
    if(map.contains("ALBUMARTIST")){
        env -> SetObjectField(mediaTag,albumArtist,env -> NewStringUTF(map.find("ALBUMARTIST")->second.toString().toCString(true)));
    }

    TagLib::AudioProperties* audioProper = ref.audioProperties();
    if(audioProper != nullptr){
        // 比特率
        env -> SetIntField(mediaTag,bitrate,
                           audioProper->bitrate());

        // Channels
        env -> SetIntField(mediaTag,channels,
                           audioProper->channels());
        // 采样率
        env -> SetIntField(mediaTag,sampleRate,
                           audioProper->sampleRate());
        // 持续时长
        env -> SetIntField(mediaTag,length,
                           audioProper->length());

    }

    return mediaTag;
}
/**
 * 修改媒体基本标签内容
 * 提供基本的标签修改内容，如下：
 * TITLE 标题
 * TRACKNUMBER 曲目顺序
 * DATE 发行年份
 * GENRE 流派
 * ARTIST 歌手
 * COMPOSER 作曲家
 * ALBUM 专辑
 * COMMENT 注释
 * ALBUMARTIST 专辑艺术家
 * COPYRIGHT 版权信息
 *
 * 涉及到音频相关内容的，不能直接修改标签
 */
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_hua_taglib_TaglibLibrary_taglibSetMediaTag(JNIEnv *env, jobject thiz, jint fd,
                                                    jobject editTag) {
    // 创建Tag对象，通过fd文件描述符得到对应文件信息
    TagLib::IOStream* ioStream = new TagLib::FileStream(fd);
    TagLib::FileRef ref(ioStream);

    if(ref.isNull()){
        return false;
    }

    jclass mediaTagClass = env -> GetObjectClass(editTag);
    jfieldID fTitle = env -> GetFieldID(mediaTagClass,"title", "Ljava/lang/String;");
    jfieldID fTrack = env -> GetFieldID(mediaTagClass,"track", "I");
    jfieldID fYear = env -> GetFieldID(mediaTagClass,"year", "I");
    jfieldID fGenre = env -> GetFieldID(mediaTagClass,"genre", "Ljava/lang/String;");
    jfieldID fArtist = env -> GetFieldID(mediaTagClass,"artist", "Ljava/lang/String;");
    jfieldID fComposer = env -> GetFieldID(mediaTagClass,"composer", "Ljava/lang/String;");
    jfieldID fAlbum = env -> GetFieldID(mediaTagClass,"album", "Ljava/lang/String;");
    jfieldID fComment = env -> GetFieldID(mediaTagClass,"comment", "Ljava/lang/String;");
    jfieldID fAlbumArtist = env -> GetFieldID(mediaTagClass,"albumArtist", "Ljava/lang/String;");
    jfieldID fCopyright = env -> GetFieldID(mediaTagClass,"copyright", "Ljava/lang/String;");

    auto jTitle = (jstring)env -> GetObjectField(editTag,fTitle);

    const char* title = env -> GetStringUTFChars(jTitle,nullptr);

    int track = env -> GetIntField(editTag,fTrack);
    int year = env -> GetIntField(editTag,fYear);

    auto jGenre = (jstring)env -> GetObjectField(editTag,fGenre);
    const char* genre = env -> GetStringUTFChars(jGenre,nullptr);

    auto jArtist = (jstring)env -> GetObjectField(editTag,fArtist);
    const char* artist = env -> GetStringUTFChars(jArtist,nullptr);

    auto jComposer = (jstring)env -> GetObjectField(editTag,fComposer);
    const char* composer = env -> GetStringUTFChars(jComposer,nullptr);

    auto jAlbum = (jstring)env -> GetObjectField(editTag,fAlbum);
    const char* album = env -> GetStringUTFChars(jAlbum,nullptr);

    auto jComment = (jstring)env -> GetObjectField(editTag,fComment);
    const char* comment = env -> GetStringUTFChars(jComment,nullptr);

    auto jAlbumArtist = (jstring)env -> GetObjectField(editTag,fAlbumArtist);
    const char* albumArtist = env -> GetStringUTFChars(jAlbumArtist,nullptr);

    auto jCopyright = (jstring)env -> GetObjectField(editTag,fCopyright);
    const char* copyright = env -> GetStringUTFChars(jCopyright,nullptr);

    // 保存有对应API的值（因为拿到的char为UTF-8，所以应使用UTF-8编码）
    TagLib::Tag* tag = ref.tag();
    tag->setTitle(TagLib::String(title,TagLib::String::UTF8));
    tag->setTrack(track);
    tag->setYear(year);
    tag->setGenre(TagLib::String(genre,TagLib::String::UTF8));
    tag->setArtist(TagLib::String(artist,TagLib::String::UTF8));
    tag->setAlbum(TagLib::String(album,TagLib::String::UTF8));
    tag->setComment(TagLib::String(comment,TagLib::String::UTF8));

    // 存储没有提供对应API的Tag信息
    TagLib::PropertyMap map = ref.file()->properties();
    map.replace("COMPOSER",TagLib::String(composer,TagLib::String::UTF8));

    map.replace("ALBUMARTIST",TagLib::String(albumArtist,TagLib::String::UTF8));

    map.replace("COPYRIGHT",TagLib::String(copyright,TagLib::String::UTF8));

    ref.file()->setProperties(map);

    bool editSuccess = ref.save();

    /**
     * 注意，当代码使用通过"GetStringUTFChars"或"GetStringChars"类方法得到的"string"字符串时，在结束使用时，必须调用它对应的"Release"方法
     * 调用"Release"类方法表明不再需要这此字符串了，如果不释放，JVM是无法将其释放的
     * 调用后，该string占有的内存将被释放。没有调用"Release"类方法将导致内存泄漏，这将可能最终导致内存的耗尽。
     */
    env -> ReleaseStringUTFChars(jTitle,title);
    env -> ReleaseStringUTFChars(jGenre,genre);
    env -> ReleaseStringUTFChars(jArtist,artist);
    env -> ReleaseStringUTFChars(jComposer,composer);
    env -> ReleaseStringUTFChars(jAlbum,album);
    env -> ReleaseStringUTFChars(jComment,comment);
    env -> ReleaseStringUTFChars(jAlbumArtist,albumArtist);
    env -> ReleaseStringUTFChars(jCopyright,copyright);
    env -> DeleteLocalRef(editTag);

    return editSuccess;
}

/**
 * 单独得到歌曲的歌词
 * 该方法从 歌曲媒体 内嵌字段中获取
 * 它将从以下字段获取具体歌词内容（按照顺序获取，如果均未获取到，返回空字符串）：
 * LYRICS 歌词信息
 * LYRICIST 歌词信息
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_hua_taglib_TaglibLibrary_taglibGetLyrics(JNIEnv *env, jobject thiz, jint fd) {
    // 创建Tag对象，通过fd文件描述符得到对应文件信息
    TagLib::IOStream* ioStream = new TagLib::FileStream(fd);
    TagLib::FileRef ref(ioStream);

    if(ref.isNull()){
        return env -> NewStringUTF("");
    }

    TagLib::PropertyMap map = ref.file()->properties();

    if(map.contains("LYRICS")) {
        return env -> NewStringUTF(map.find("LYRICS")->second.toString().toCString(true));
    } else if(map.contains("LYRICIST")){
        return env -> NewStringUTF(map.find("LYRICIST")->second.toString().toCString(true));
    }

    return env -> NewStringUTF("");
}