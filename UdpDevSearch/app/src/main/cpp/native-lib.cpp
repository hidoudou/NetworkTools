#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_doudou_lc_devsearch_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "            Talk is cheap, \nShow me the fucking code🤘";
    return env->NewStringUTF(hello.c_str());
}
