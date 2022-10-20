#include "com_yuyan_driver_serialport_Serialport.h"
#include "../Serialport.h"

#undef TAG
#define TAG "com_yuyan_driver_serialport_Serialport.cpp"

using namespace yuyan;

/* Global Variable */
Serialport* gSerialport = NULL;
/* Global Variable */

/*----------------------------------------------------------------------*/
char* jstringToChar(JNIEnv* env, jstring jstr);
/*----------------------------------------------------------------------*/

/*
 * Class:     com_yuyan_driver_serialport_Serialport
 * Method:    nativeOpen
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeOpen(JNIEnv *env, jobject o, jstring s) {
    char const* portname;
    char *p = jstringToChar(env, s);
    if(p == NULL) {
        portname = "COM6";
    } else {
        portname = p;
    }
    printf("start to open %s.\n", portname);

    Serialport* serialport = new Serialport(portname);
    HANDLE hCom = serialport->open();
    printf("Open fd = %d\n", hCom);

    gSerialport = serialport;

    return 12;
}

/*
 * Class:     com_yuyan_driver_serialport_Serialport
 * Method:    nativeClose
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeClose(JNIEnv *env, jobject) {
    if (gSerialport == NULL) {
        return -1;
    }

    printf("start to close\n");
    gSerialport->close();
    return 13;
}

/*
 * Class:     com_yuyan_driver_serialport_Serialport
 * Method:    nativeGetStatus
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeGetStatus(JNIEnv *env, jobject o) {
    if (gSerialport == NULL) {
        return JNI_FALSE;
    }

    int status = gSerialport->getStatus();;
    if (0 == status) {
        return JNI_FALSE;
    } else {
        return JNI_TRUE;
    }
}

/*
 * Class:     com_yuyan_driver_serialport_Serialport
 * Method:    nativeRead
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeRead(JNIEnv *env, jobject o, jbyteArray bA) {
    printf("start to read\n");
    if (gSerialport == NULL) {
        return -1;
    }

    char buff[1024];
    int readLen = gSerialport->readBlocked(buff);
    printf("end in read\n");
    if (readLen <= 0) {
        return readLen;
    }

    int jBuffLen = env->GetArrayLength(bA);
    jbyte* jBuff = env->GetByteArrayElements(bA, JNI_FALSE);
    int j = 0;
    for (; j < readLen; j++) {
        jBuff[j] = buff[j];
    }
    jBuff[j] = '\0';

    env->ReleaseByteArrayElements(bA, jBuff, 0);

    return readLen;
}

/*
 * Class:     com_yuyan_driver_serialport_Serialport
 * Method:    nativeWrite
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeWrite(JNIEnv *env, jobject o, jbyteArray bA, jint d) {
    if (gSerialport == NULL) {
        return -1;
    }

    char buff[d];
    for(int i = 0; i <= d; i++) {
        buff[i] = '\0';
    }

    int jBuffLen = env->GetArrayLength(bA);
    int wLen = (jBuffLen < d) ? jBuffLen : d;
    jbyte* jBuff = env->GetByteArrayElements(bA, JNI_FALSE);
    for (int j = 0; j < wLen; j++) {
        buff[j] = jBuff[j];
    }
    buff[wLen] = '\0';

    int err = gSerialport->write(buff, wLen);

    return err;
}

/*
 * Class:     com_yuyan_driver_serialport_Serialport
 * Method:    nativeSetReadTimeout
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeSetReadTimeout(JNIEnv *env, jobject o, jlong l) {
    if (gSerialport == NULL) { return -1; }

    return gSerialport->setReadTimeoutMs(l);
}





char* jstringToChar(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*)malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}