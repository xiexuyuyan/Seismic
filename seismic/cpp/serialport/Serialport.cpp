#include "Serialport.h"

#undef TAG
#define TAG "Serialport.cpp"


HANDLE yuyan::Serialport::open() {
    logi(TAG, __FUNCTION__, __LINE__, "Open!");
    mHCom = inner_open(mPortname, mAsync
            , mBaudrate, mParity, mDatabit, mStopbit);
    return mHCom;
}

void yuyan::Serialport::close() {
    logi(TAG, __FUNCTION__, __LINE__, "Close!");
    CloseHandle(mHCom);
}

/*
 * GetHandleInformation(HANDLE handle, DWORD flags)
 * flag:
 *   76: first not open
 *    0: opening
 *   15: closed by CloseHandle
 *   21: not open
 * ret: 0 invalid, 1 valid
 * it makes GetLastError()
 */
int yuyan::Serialport::getStatus() {
    DWORD dFlags;
    return GetHandleInformation(mHCom,&dFlags);
}

HANDLE yuyan::Serialport::inner_open(
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
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        return NULL;
    }

    err = inner_configureCommonHandle(
            hCom, baudrate, parity, databit, stopbit);
    if (err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        return NULL;
    }

    if(hCom == INVALID_HANDLE_VALUE){
        loge(TAG, __FUNCTION__, __LINE__, "INVALID_HANDLE_VALUE");
        return NULL;
    }

    return hCom;
}

// Default 96-N-8-1
int yuyan::Serialport::inner_configureCommonHandle(
        const HANDLE hCom
        , const int baudrate
        , const int parity
        , const int databit
        , const int stopbit) {
    int err = 0;

    err = SetupComm(hCom, 1024, 1024);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
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
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        return -1;
    }

    if (-1 == inner_configureCommonTimeout(hCom)) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        return -1;
    }

    err = PurgeComm(hCom
        , PURGE_RXCLEAR | PURGE_TXCLEAR
        | PURGE_RXABORT | PURGE_TXABORT);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        return -1;
    }

    return 0;
}

int yuyan::Serialport::refreshRead() {
    int err = PurgeComm(mHCom, PURGE_RXCLEAR | PURGE_RXABORT);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        return -1;
    }

    return err;
}

int yuyan::Serialport::refreshWrite() {
    int err = PurgeComm(mHCom, PURGE_TXCLEAR | PURGE_TXABORT);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        return -1;
    }

    return err;
}

int yuyan::Serialport::inner_configureCommonTimeout(const HANDLE hCom) {
    int err = 0;
    COMMTIMEOUTS commTimeouts;
    commTimeouts.ReadIntervalTimeout = 10;
    commTimeouts.ReadTotalTimeoutMultiplier = 0;
    commTimeouts.ReadTotalTimeoutConstant = readTotalTimeoutConstant;

    commTimeouts.WriteTotalTimeoutMultiplier = 0;
    commTimeouts.WriteTotalTimeoutConstant = 0;
    err = SetCommTimeouts(hCom, &commTimeouts);
    if (!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        return -1;
    }
    return err;
}

int yuyan::Serialport::setReadTimeoutMs(long milliseconds) {
    readTotalTimeoutConstant = milliseconds;
    return inner_configureCommonTimeout(mHCom);
}

int yuyan::Serialport::readBlocked(char _buff[]) {
    int err = 0;
    char buff[1024];
    memset(buff, '\0', 1024);
    DWORD actualLen = 0;

    err = ReadFile(mHCom, &buff, sizeof(buff), &actualLen, NULL);

    if(err && (actualLen != 0)) {
        for(int i = 0; i < actualLen; i++) {
            _buff[i] = buff[i];
        }
        _buff[actualLen] = '\0';
    } else {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
        err = -err;
    }

    return (actualLen > 0) ? actualLen : err;
}

int yuyan::Serialport::write(char buff[], int len) {
    int err = 0;
    DWORD actualLen = 0;

    err = WriteFile(mHCom, buff, len, &actualLen, NULL);

    if(!err) {
        loge(TAG, __FUNCTION__, __LINE__, strerror(GetLastError()));
    }

    return (actualLen > 0) ? actualLen : err;
}
