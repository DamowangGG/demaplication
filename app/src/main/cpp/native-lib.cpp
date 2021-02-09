#include <jni.h>
#include <string>

using namespace std;
const static string id = "8855223311";

extern "C"
JNIEXPORT jstring JNICALL
Java_com_zejian_myapplication_ConstManager_getDeviceId(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(id.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_zejian_myapplication_ConstManager_getAliToken(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(id.c_str());
}