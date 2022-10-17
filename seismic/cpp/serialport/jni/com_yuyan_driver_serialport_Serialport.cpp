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


JNIEXPORT jint JNICALL Java_com_yuyan_driver_serialport_Serialport_nativeRead(JNIEnv *env, jobject o, jbyteArray a) {
    printf("start to read\n");
    char buff[1024];
    int readLen = gSerialport->readBlocked(buff, gHCom);
    printf("end in read\n");

    char ch = '\0';
    int i = 0 ;
    while((ch = buff[i]) != '\0') {
        printf("%c", ch);
        i++;
    }
    printf("[%s], readLen = %d, actual len is %d\n", buff, readLen, i);

    return readLen;
}