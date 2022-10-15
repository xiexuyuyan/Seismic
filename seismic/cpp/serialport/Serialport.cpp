#include "Serialport.h"

#undef TAG
#define TAG "Serialport.cpp"


HANDLE yuyan::Serialport::open() {
    return open_inner(mPortname, mAsync
            , mBaudrate, mParity, mDatabit, mStopbit);
}

HANDLE yuyan::Serialport::open_inner(
        const char* portname
        , const int async
        , const int baudrate
        , const int parity
        , const int databit
        , const int stopbit) {
    int err = 0;

    DWORD asyncFlag = (async) ? FILE_ATTRIBUTE_NORMAL : 0;

    HANDLE hCom = CreateFileA(portname
                    , GENERIC_READ | GENERIC_WRITE
                    , 0, NULL, OPEN_EXISTING, asyncFlag, NULL);

    if (hCom == (HANDLE)-1) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(errno));
        return NULL;
    }

    err = configureCommonHandle(
            hCom, baudrate, parity, databit, stopbit);
    if (err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(errno));
        return NULL;
    }

    if(hCom == INVALID_HANDLE_VALUE){
        loge(TAG, __FUNCTION__, __LINE__, "INVALID_HANDLE_VALUE");
        return NULL;
    }

    return hCom;
}

// Default 96-N-8-1
int yuyan::Serialport::configureCommonHandle(
        const HANDLE hCom
        , const int baudrate
        , const int parity
        , const int databit
        , const int stopbit) {
    int err = 0;

    err = SetupComm(hCom, 1024, 1024);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(errno));
        return -1;
    }

    DCB configure;
    memset(&configure, 0, sizeof(configure));
    GetCommState(hCom, &configure);
    configure.DCBlength = sizeof(configure);
    configure.BaudRate = baudrate;
    configure.ByteSize = databit;

    switch (parity) {
        case 0:
            configure.Parity = NOPARITY;
            break;
        case 1:
            configure.Parity = ODDPARITY;
            break;
        case 2:
            configure.Parity = EVENPARITY;
            break;
        case 3:
            configure.Parity = MARKPARITY;
            break;
    }

    switch (stopbit) {
        case 1:
            configure.StopBits = ONESTOPBIT;
            break;
        case 2:
            configure.StopBits = TWOSTOPBITS;
            break;
        case 3:
            configure.StopBits = ONE5STOPBITS;
            break;
    }

    err = SetCommState(hCom, &configure);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(errno));
        return -1;
    }

    COMMTIMEOUTS commTimeouts;
    commTimeouts.ReadIntervalTimeout = MAXDWORD;
    commTimeouts.ReadTotalTimeoutMultiplier = 0;
    commTimeouts.ReadTotalTimeoutConstant = 0;

    commTimeouts.WriteTotalTimeoutMultiplier = 1;
    commTimeouts.WriteTotalTimeoutConstant = 1;
    err = SetCommTimeouts(hCom, &commTimeouts);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(errno));
        return -1;
    }

    err = PurgeComm(hCom
        , PURGE_RXCLEAR | PURGE_TXCLEAR
        | PURGE_RXABORT | PURGE_TXABORT);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(errno));
        return -1;
    }

    return 0;
}
