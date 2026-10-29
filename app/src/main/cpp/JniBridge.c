#include <stdlib.h>
#include "jni.h"
#include "nback.h"

JNIEXPORT jintArray JNICALL
Java_mobappdev_example_nback_1cimpl_NBackHelper_createNBackString(JNIEnv *env, jobject thiz,
                                                                  jint size, jint combinations,
                                                                  jint matchPercentage, jint nBack) {
    Nback s1 = create(size, combinations, matchPercentage, nBack);
    if (s1 == NULL) {
        return NULL;
    }

    // Create a new jintArray to store the content array
    jintArray contentArray = (*env)->NewIntArray(env, s1->size);

    // Check if allocation was successful
    if (contentArray == NULL) {
        free(s1);
        return NULL;
    }

    // Set the content of the jintArray to the content array from the struct
    (*env)->SetIntArrayRegion(env, contentArray, 0, s1->size, s1->content);

    free(s1);
    return contentArray;
}
