main: main.exe

main.exe: main.c obj/KMP.o
	gcc main.c obj/KMP.o -o main.exe

obj/KMP.o: KMP.c
	gcc -c KMP.c -o obj/KMP.o

obj/com_yuyan_utils_KMP.o: jni/com_yuyan_utils_KMP.cpp
	g++ -c jni/com_yuyan_utils_KMP.cpp -fPIC \
		-I "C:\Program Files\Java\jdk1.8.0_231\include" \
		-I "C:\Program Files\Java\jdk1.8.0_231\include\win32" \
		-o ./obj/com_yuyan_utils_KMP.o

libKMP: obj/com_yuyan_utils_KMP.o obj/KMP.o
	g++ -Wl,--add-stdcall-alias -shared -o jni/libKMP.dll \
		obj/KMP.o obj/com_yuyan_utils_KMP.o


.PHONY: clean main all