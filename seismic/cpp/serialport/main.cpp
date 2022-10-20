#include <stdio.h>
#include "Serialport.h"

using namespace yuyan;

int main() {
    int err = 0;
    char buff[10];
    char const* portname = "COM6";
    Serialport* serialport = new Serialport(portname);

    serialport->open();
    serialport->setReadTimeoutMs(3000);
    memset(buff, '\0', 10);
    int readLen = serialport->readBlocked(buff);

    printf("readLen = %d, char[0] = [%c]\n", readLen, buff[0]);

    return 0;
}
