#include <stdio.h>
#include "Serialport.h"

using namespace yuyan;

int main() {
    printf("Hello world!\n");
    char const* portname = "COM6";
    Serialport* serialport = new Serialport(portname);
    HANDLE hCom = serialport->open();
    printf("Open fd = %d\n", hCom);

    return 0;
}
