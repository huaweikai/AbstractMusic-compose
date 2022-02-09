#include <jni.h>
#include <string>
#include <tiostream.h>
#include <tfilestream.h>
#include <fileref.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_hua_taglib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject,
        jint fd
        ) {
    // 创建Tag对象，通过fd文件描述符得到对应文件信息
    TagLib::IOStream* ioStream = new TagLib::FileStream(fd);
    TagLib::FileRef ref(ioStream);

    // 如果该文件不存在，则返回null
    if(ref.isNull()){
        return env->NewStringUTF("1");
//        return nullptr;
    }
    TagLib::File* file = ref.file();
    if(file == nullptr){
        return env->NewStringUTF("2");
    }
    return env->NewStringUTF(file->tag()->title().toCString(true));
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
}
