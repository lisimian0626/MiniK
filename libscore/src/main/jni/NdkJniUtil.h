/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class NdkJniUtil */

#ifndef _Included_NdkJniUtil
#define _Included_NdkJniUtil
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     NdkJniUtil
 * Signature: ()Ljava/lang/String;
 */

JNIEXPORT jint         JNICALL Java_com_beidousat_score_NdkJniUtil_setNotes
        (JNIEnv *, jobject, jobject);

JNIEXPORT jobjectArray JNICALL Java_com_beidousat_score_NdkJniUtil_getAnalyzeResult
        (JNIEnv *, jobject, jdoubleArray, jlong, jint);

JNIEXPORT jobjectArray JNICALL Java_com_beidousat_score_NdkJniUtil_getAnalyzeResultEasy
        (JNIEnv *, jobject, jdoubleArray, jlong, jint);

JNIEXPORT jfloat         JNICALL Java_com_beidousat_score_NdkJniUtil_getScore
        (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
