#include <jni.h>
#include <string>
#include <iostream>
#include "zabbix_trapper.h"

std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr) {
        return "";
    }

    const auto stringClass = env->GetObjectClass(jStr);
    const auto getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const auto stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    auto length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

extern "C" {
    JNIEXPORT jlong JNICALL
    Java_com_example_zabbixtrapperndk_native_NativeTrapper_createTrapper(JNIEnv *env, jobject, jstring ip) {
        auto* trapper = new ZabbixTrapper(jstring2string(env, ip), 10051);
        return reinterpret_cast<jlong>(trapper);
    }

    JNIEXPORT void JNICALL
    Java_com_example_zabbixtrapperndk_native_NativeTrapper_destroyTrapper(
            JNIEnv *env,
            jobject /* this */,
            jlong ptr) {
        delete reinterpret_cast<ZabbixTrapper*>(ptr);
    }

    JNIEXPORT jint JNICALL
    Java_com_example_zabbixtrapperndk_native_NativeTrapper_dataSend(
            JNIEnv *env,
            jobject /* this */,
            jlong ptr,
            jstring host,
            jstring key,
            jstring data) {
        auto* trapper = reinterpret_cast<ZabbixTrapper*>(ptr);
        return trapper->sendData(jstring2string(env, host),
                                 jstring2string(env, key),
                                 jstring2string(env, data));
    }
}