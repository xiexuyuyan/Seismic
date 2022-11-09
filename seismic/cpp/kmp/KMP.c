#include "KMP.h"

static void arraycopy(const int src[], const int srcPos, int dest[], const int destPos, const int length) {
    // todo(IndexOutOfBoundsException)

    for(int i = 0; i < length; i++) {
        dest[i + destPos] = src[i + srcPos];
    }
}

static int isSuffix(const char* pattern, const int l, const int s) {
    // todo(IndexOutOfBoundsException)

    int pl = l - 1;
    int ps = s - 1;

    for (int i = 0; i < s; i++) {
        if (pattern[ps - i] == pattern[pl - i]) {
            continue;
        } else {
            return 0;
        }
    }

    return 1;
}


/*
 * Target size means status amount.
 */
static void setTargetTable(char* pattern, int* target, const int STATUS_AMOUNT) {
    // todo(IndexOutOfBoundsException)
    for (int i = 2; i < STATUS_AMOUNT; i++) {
        for (int j = i-1; j > 0; j--) {
            if (isSuffix(pattern, i, j)) {
                target[i] = j;
                break;
            }
        }
    }
}

void setDFA(char DIC[], const int DIC_SIZE, int* _DFA, char* pattern, const int patLen) {
    const int STATUS_AMOUNT = 1 + patLen;
    int target[STATUS_AMOUNT];
    // int DFA[STATUS_AMOUNT][DIC_SIZE];

    int *DFA[STATUS_AMOUNT];
    for (int i = 0; i < STATUS_AMOUNT; i++) {
        DFA[i] = &_DFA[i * DIC_SIZE];
    }


    memset(target, 0, STATUS_AMOUNT * sizeof(int));
    memset(_DFA, 0, (DIC_SIZE * STATUS_AMOUNT) * sizeof(int));

    // 1. init target table
    setTargetTable(pattern, target, STATUS_AMOUNT);

    // 2. status 0
    for (int i = 0; i < DIC_SIZE; i++) {
        if (DIC[i] == pattern[0]) {
            DFA[0][i] = 1;
            break;
        }
    }

    // 3. others except S-0 and S-end
    for (int i = 1; i < STATUS_AMOUNT-1; i++) {
        arraycopy(DFA[target[i]], 0, DFA[i], 0, DIC_SIZE);
        for (int j = 0; j < DIC_SIZE; j++) {
            if (DIC[j] == pattern[i]) {
                DFA[i][j] = i+1;
            }
        }
    }
}

void printDFA(char DIC[], const int DIC_SIZE, int* _DFA, char* pattern, const int patLen) {
const int STATUS_AMOUNT = 1 + patLen;
    int target[STATUS_AMOUNT];
    // int DFA[STATUS_AMOUNT][DIC_SIZE];

    int *DFA[STATUS_AMOUNT];
    for (int i = 0; i < STATUS_AMOUNT; i++) {
        DFA[i] = &_DFA[i * DIC_SIZE];
    }

    for (int i = 0; i < STATUS_AMOUNT; i++) {
        if (i == 0) {
            printf("\t");
            for (int j = 0; j < DIC_SIZE; j++) {
                printf("%c\t", DIC[j]);
            }
            printf("\n");
        }
        printf("status[%d]", i);
        for(int j = 0; j < DIC_SIZE; j++) {
            printf("%d\t", DFA[i][j]);
        }
        printf("\n");
    }

}

static int getIndex(char DIC[], const int DIC_SIZE, const char ch) {
    for (int i = 0; i < DIC_SIZE; i++) {
        if (DIC[i] == ch) {
            return i;
        }
    }

    return -1;
}

int findByDFA(char DIC[], const int DIC_SIZE, int* _DFA, char* pattern, const int patLen, char* str) {
    const int STATUS_AMOUNT = 1 + patLen;
    int target[STATUS_AMOUNT];
    // int DFA[STATUS_AMOUNT][DIC_SIZE];

    int *DFA[STATUS_AMOUNT];
    for (int i = 0; i < STATUS_AMOUNT; i++) {
        DFA[i] = &_DFA[i * DIC_SIZE];
    }

    int __count = 0;
    int status = 0;
    int i = 0;
    for (; i < strlen(str) && status < patLen; i++) {
        if (__count++ > strlen(pattern) * strlen(str)) { break; }

        int index = getIndex(DIC, DIC_SIZE, str[i]);

        if (index == -1) { break; }

        status = DFA[status][index];
    }

    return status == patLen ? (i - patLen) : -1;
}

int findStr(char const * pattern, char const * str) {
    const int patLen = strlen(pattern);

    const int DIC_SIZE = 62;
    char DIC[DIC_SIZE];
    for (int i = 0; i < DIC_SIZE; i++) {
        if (i < 10) {
            DIC[i] = i + '0';
        } else if (i < 36) {
            DIC[i] = i - 10 + 'A';
        } else if (i < 62) {
            DIC[i] = i - 36 + 'a';
        }
    }

    const int STATUS_AMOUNT = patLen + 1;

    int DFA[STATUS_AMOUNT][DIC_SIZE];

    setDFA(DIC, DIC_SIZE, DFA, pattern, patLen);
    // printDFA(DIC, DIC_SIZE, DFA, pattern, patLen);

    return findByDFA(DIC, DIC_SIZE, DFA, pattern, patLen, str);
}
