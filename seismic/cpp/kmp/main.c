#include "KMP.h"

int main(int argc, char* argv[]) {
    printf("--------------------------------------------------------------\n");

    if (argc < 3) {
        return;
    }

    char* pattern = argv[1];
    char* str = argv[2];

    int ret = findStr(pattern, str);

    if (ret != -1) {
        printf("success %d!\n", ret);
    }

    return 0;
}

/*
{
    int a[2][2];
    a[0][0] = 0; a[0][1] = 1;
    a[1][0] = 2; a[1][1] = 3;


    {
        int *pa = a;

        int (*b)[2];
        b = &pa[0];
        (*b)[0] = 5;
    }

    {
        int *pa = a;

        int (*b)[2];
        b = &pa[0];

        int *DFA[2];
        DFA[0] = &pa[0];
        DFA[1] = &pa[2];


        DFA[0][0] = 10; DFA[0][1] = 11;
        DFA[1][0] = 12; DFA[1][1] = 13;
    }

    {
        int *pa = a;
        for (int i = 0; i < 4; i++) {
            printf("%d\n", pa[i]);
        }
    }
}
*/
