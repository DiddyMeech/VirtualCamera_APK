#ifndef VCAM_NATIVE_H
#define VCAM_NATIVE_H

#ifdef __cplusplus
extern "C" {
#endif

// JNI function declarations
JNIEXPORT void JNICALL Java_com_diddymeech_vcam_VCamNative_initOpenGL(JNIEnv *env, jobject thiz);
JNIEXPORT void JNICALL Java_com_diddymeech_vcam_VCamNative_applyNoise(JNIEnv *env, jobject thiz, jfloat intensity);
JNIEXPORT void JNICALL Java_com_diddymeech_vcam_VCamNative_setBrightness(JNIEnv *env, jobject thiz, jfloat brightness);
JNIEXPORT void JNICALL Java_com_diddymeech_vcam_VCamNative_setContrast(JNIEnv *env, jobject thiz, jfloat contrast);

// Native functions for sensor injection
void injectSensorData(float* accelerometer_data, float* gyroscope_data);
void applyAntiDebugging();

#ifdef __cplusplus
}
#endif

#endif // VCAM_NATIVE_H
