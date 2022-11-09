#include <stdio.h>
#include <string.h>

void setDFA(char DIC[], const int DIC_SIZE, int* DFA, char* pattern, const int patLen);
void printDFA(char DIC[], const int DIC_SIZE, int* _DFA, char* pattern, const int patLen);
int findByDFA(char DIC[], const int DIC_SIZE, int* _DFA, char* pattern, const int patLen, char* str);

#ifdef __cplusplus
extern "C" {
#endif
int findStr(char const * pattern, char const * str);
#ifdef __cplusplus
}
#endif



// DFA M = (S, DIC, f(), S0, F)
/*
    S0=0:_:      _
    S1=1:0:      a
    S2=2:1:     ab
    S3=3:2:    abd
    S4=4:3:   abda
    S5=5:4:  abdab
    S6=6:5: abdabc
*/
/*
DFA[][] = {
          dfa[_]         a b c d e f
    S0=0: dfa[0]      _ |1 0 0 0 0 0
    S1=1: dfa[1]      a |1 2 0 0 0 0
    S2=2: dfa[2]     ab |1 0 0 3 0 0
    S3=3: dfa[3]    abd |4 0 0 0 0 0
    S4=4: dfa[4]   abda |1 5 0 0 0 0
    S5=5: dfa[5]  abdab |1 0 6 3 0 0
    S6=6: dfa[6] abdabc |1 0 0 0 0 0
}
*/
