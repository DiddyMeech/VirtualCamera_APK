#include <jni.h>
#include <android/log.h>

// Dummy JNI_OnLoad function to satisfy build requirements
JNIEXPORT void JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "VCamNative", "JNI_OnLoad called");
    return;
}

// Dummy native functions that will be implemented later
extern "C" {
    
JNIEXPORT void JNICALL Java_com_diddymeech_vcam_VCamNative_initOpenGL(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_DEBUG, "VCamNative", "Initializing OpenGL");
}

JNIEXPORT void JNICALL Java_com_diddymeech_vcam_VCamNative_applyNoise(JNIEnv *env, jobject thiz, jfloat intensity) {
    __android_log_print(ANDROID_LOG_DEBUG, "VCamNative", "Applying noise");
}

JNIEXPORT void JNICALL Java_com_diddymeech_vcam_VCamNative_setBrightness(JNIEnv *env, jobject thiz, jfloat brightness) {
    __android_log_print(ANDROID_LOG_DEBUG, "VCamNative", "Setting brightness");
}

JNIEXPORT void JNICALL Java_com_diddymeech_vcam_VCamNative_setContrast(JNIEnv *env, jobject thiz, jfloat contrast) {
    __android_log_print(ANDROID_LOG_DEBUG, "VCamNative", "Setting contrast");
}
} // extern "C"
