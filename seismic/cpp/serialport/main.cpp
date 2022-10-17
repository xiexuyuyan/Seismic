#include <stdio.h>
#include "Serialport.h"

using namespace yuyan;

int main() {
    int err = 0;
    char const* portname = "COM6";
    Serialport* serialport = new Serialport(portname);
    HANDLE hCom = serialport->open();
    printf("Open fd = %d\n", hCom);

    char buff[1024];
    int readLen = serialport->readBlocked(buff, hCom);

    char ch = '\0';
    int i = 0 ;
    while((ch = buff[i]) != '\0') {
        printf("%c", ch);
        i++;
    }
    printf("[%s], readLen = %d, actual len is %d", buff, readLen, i);

/*
    // BYTE byte;
    char buff[1024];
    DWORD dwNumBytesRead;

    while(1) {
        dwNumBytesRead = 0;
        err = ReadFile(hCom, &buff, sizeof(buff), &dwNumBytesRead, NULL);
        if(err && (dwNumBytesRead != 0)) {
            printf("[%c] len = %d\n", buff[0], dwNumBytesRead);
            printf("[%c] len = %d\n", buff[1], dwNumBytesRead);
            printf("[%c] len = %d\n", buff[2], dwNumBytesRead);
            printf("[%c] len = %d\n", buff[3], dwNumBytesRead);
            printf("[%c] len = %d\n", buff[4], dwNumBytesRead);
            printf("[%c] len = %d\n", buff[5], dwNumBytesRead);
            printf("[%c] len = %d\n", buff[6], dwNumBytesRead);
            printf("[%c] len = %d\n", buff[7], dwNumBytesRead);

            LPCWSTR lpMsgBuf;
            CreateErrorMsg(GetLastError(), lpMsgBuf);
            printf("Error %s\n", lpMsgBuf);
        }
    }*/
    CloseHandle(hCom);
    return 0;
}
