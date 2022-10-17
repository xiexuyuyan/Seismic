#ifndef _SERIALPORT_H // Serialport.h
#define _SERIALPORT_H // Serialport.h

#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <WinSock2.h>

/************************************************************************/
#define CreateErrorMsg(errno, errMsgBuff)          \
          FormatMessage(                           \
                  FORMAT_MESSAGE_ALLOCATE_BUFFER | \
                  FORMAT_MESSAGE_FROM_SYSTEM |     \
                  FORMAT_MESSAGE_IGNORE_INSERTS,   \
                  NULL,                            \
                  errno,                         \
                  0,                               \
                  (LPTSTR) & (errMsgBuff),           \
                  0,                               \
                  NULL                             \
                 ),                                \
          ((WCHAR *)errMsgBuff)[wcslen((WCHAR *)errMsgBuff)-2] = '\0'
/************************************************************************/
#define BAUDRATE_9600      9600L
#define BAUDRATE_115200    115200L

#define S_PARITY_NONE        0
#define S_PARITY_ODD         1
#define S_PARITY_EVEN        2
#define S_PARITY_MARKUP      3

#define DATABIT_8          8

#define STOPBIT_1          1
#define STOPBIT_2          2
#define STOPBIT_1_DOT_5    3

#define SYNCHRONIC_SYNC    0
#define SYNCHRONIC_ASYNC   1
/************************************************************************/

namespace yuyan {
    class Serialport {
    private:
        const char* mPortname;
        int mAsync;
        int mBaudrate;
        int mParity;
        int mDatabit;
        int mStopbit;

        int configureCommonHandle(
                    const HANDLE hCom
                    , const int baudrate
                    , const int parity
                    , const int databit
                    , const int stopbit);
        HANDLE open_inner(
                    const char* portname
                    , const int async
                    , const int baudrate
                    , const int parity
                    , const int databit
                    , const int stopbit);

        void loge(const char* tag
            , const char* function
            , const int line
            , const char* errnoStr) {
            printf("%s %s():%d Failed cause: %s.\n", tag, function, line, errnoStr);
        }
        void logi(const char* tag
            , const char* function
            , const int line
            , const char* errnoStr) {
            printf("%s %s():%d Info: %s.\n", tag, function, line, errnoStr);
        }

    public:
        Serialport(const char* portname) {
            mPortname = portname;
            mAsync = SYNCHRONIC_ASYNC;
            mBaudrate = BAUDRATE_115200;
            mParity = S_PARITY_NONE;
            mDatabit = DATABIT_8;
            mStopbit = STOPBIT_1;
        }

        HANDLE open();
        void close(HANDLE hCom);

        int readBlocked(char buff[], HANDLE hCom);
        int write(char buff[], int len, HANDLE hCom);
        int refreshRead(HANDLE hCom);
        int refreshWrite(HANDLE hCom);
    };
}

#endif // Serialport.h