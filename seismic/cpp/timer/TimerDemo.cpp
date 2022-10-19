#include <stdlib.h>
#include <stdio.h>
#include <windows.h>

#define delaytime 1000

int gtime_ID;

void CALLBACK TimeEvent(UINT uTimerID, UINT uMsg, DWORD_PTR dwUser, DWORD_PTR dw1, DWORD_PTR dw2) {
    printf("time ID is %d, started,dwUser is %d\n", gtime_ID, dwUser);
    return;
}
/*
    uDelay          以毫秒指定事件的周期。
    Uresolution     以毫秒指定延时的精度，数值越小定时器事件分辨率越高。缺省值为1ms。
    LpTimeProc      指向一个回调函数。
    DwUser          存放用户提供的回调数据。
    FuEvent         指定定时器事件类型：
    - TIME_ONESHOT  uDelay毫秒后只产生一次事件。
    - TIME_PERIODIC 每隔uDelay毫秒周期性地产生事件。
*/

void StartEventTime(DWORD_PTR duser) {
    gtime_ID = timeSetEvent(delaytime, 10, (LPTIMECALLBACK)TimeEvent, duser, TIME_PERIODIC);
    if(gtime_ID == NULL) {
        printf("time ID is not created\n");
        return;
    }
    return;
}

int main(int argc, char* argv[]) {
    int i = 0;
    while (1) {
        printf("A\n");
        StartEventTime(i);
        printf("B\n");
        Sleep(1100);
        printf("B+\n");
        i++;
        timeKillEvent(gtime_ID);
        printf("C\n");

        if (i == 10) {
            break;
        }
    }
    return 0;
}

/*
 * 版权声明：本文为CSDN博主「tongsean」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/tongsean/article/details/40562857
*/
