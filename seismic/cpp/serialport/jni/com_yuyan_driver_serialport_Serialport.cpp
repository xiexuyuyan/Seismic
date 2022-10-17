#include "com_yuyan_driver_serialport_Serialport.h"
#include "../Serialport.h"

#undef TAG
#define TAG "com_yuyan_driver_serialport_Serialport.cpp"

using namespace yuyan;

/* Global Variable */
HANDLE gHCom;
Serialport* gSerialport;
/* Global Variable */

JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeOpen(JNIEnv *env, jobject o) {
    printf("start to open\n");
    char const* portname = "COM6";
    Serialport* serialport = new Serialport(portname);
    HANDLE hCom = serialport->open();
    printf("Open fd = %d\n", hCom);

    gHCom = hCom;
    gSerialport = serialport;

    return 12;
}

JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeClose(JNIEnv *env, jobject) {
    printf("start to close %d\n", gHCom);
    gSerialport->close(gHCom);
    return 13;
}


JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeRead(JNIEnv *env, jobject o, jbyteArray bA) {
    printf("start to read\n");
    char buff[1024];
    int readLen = gSerialport->readBlocked(buff, gHCom);
    printf("end in read\n");

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


JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeWrite(JNIEnv *env, jobject o, jbyteArray bA, jint d) {
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

    int err = gSerialport->write(buff, wLen, gHCom);

    return err;
}