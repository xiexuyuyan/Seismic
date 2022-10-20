#include <stdio.h>
#include "Serialport.h"

using namespace yuyan;

int main() {
    int err = 0;
    char const* portname = "COM6";
    Serialport* serialport = new Serialport(portname);

    char buff[10] = "12345";
    for(int i = 0; i < 3; i++) {
        Sleep(2000);
        printf("Before open status = %d\n", serialport->getStatus());

        HANDLE hCom = serialport->open();
        printf("Open fd = %d\n", hCom);

        printf("After open status = %d\n", serialport->getStatus());

        serialport->write(buff, 3);
        serialport->close();

        printf("After close status = %d\n", serialport->getStatus());

        printf("---------------------\n");
    }

    return 0;
}
