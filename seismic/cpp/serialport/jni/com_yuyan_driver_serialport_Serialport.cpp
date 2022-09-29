#include "com_yuyan_driver_serialport_Serialport.h"
#include "../Serialport.h"

#undef TAG
#define TAG "com_yuyan_driver_serialport_Serialport.cpp"

using namespace yuyan;

JNIEXPORT jobject JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeOpen(JNIEnv* env, jobject o) {
    printf("hello world\n");
    char const* portname = "COM6";
    Serialport* serialport = new Serialport(portname);
    HANDLE hCom = serialport->open();
    printf("Open fd = %d\n", hCom);

    jobject mFileDescriptor;

    return mFileDescriptor;
}

JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeReadByte(JNIEnv* env, jobject o) {
    return 0x12;
}
