#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

#define debug(...) printf(__VA_ARGS__)

int main(void)
{
    DCB dcb;
    HANDLE hCom;
    COMMTIMEOUTS CommTimeouts;
    DWORD wRead, wWrite;
    WINBOOL bReadStat;
    char hj212_string[1500];

    hCom = CreateFile(TEXT("COM6"), GENERIC_READ | GENERIC_WRITE, 0, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);

    if(hCom == INVALID_HANDLE_VALUE)
    {
        debug("Can not open COM2 !\r\n");
        return -1;
    }else
    {
        debug("Open COM2 Successfully !\r\n");
    }

    // 设置读写缓存大小
    SetupComm(hCom, 2048, 2048);

    //设定读超时
    CommTimeouts.ReadIntervalTimeout = MAXDWORD;//读间隔超时
    CommTimeouts.ReadTotalTimeoutMultiplier = 0;//读时间系数
    CommTimeouts.ReadTotalTimeoutConstant = 0;//读时间常量

    //设定写超时
    CommTimeouts.WriteTotalTimeoutMultiplier = 1;//写时间系数
    CommTimeouts.WriteTotalTimeoutConstant = 1;//写时间常量
    SetCommTimeouts(hCom, &CommTimeouts); //设置超时


    GetCommState(hCom, &dcb);
    dcb.BaudRate = 115200; //波特率为9600
    dcb.ByteSize = 8; //每个字节有8位
    dcb.Parity = NOPARITY; //无奇偶校验位
    dcb.StopBits = ONESTOPBIT; //一个停止位
    SetCommState(hCom, &dcb);

    while(1)
    {
        wRead = 0;
        bReadStat = ReadFile(hCom, hj212_string, sizeof(hj212_string), &wRead, NULL);
        if(bReadStat && wRead != 0)
        {
            printf("[%s] len = %d\n", hj212_string, wRead);
            WriteFile(hCom, hj212_string, wRead, &wWrite, NULL);
        }
    }

    CloseHandle(hCom);
}
