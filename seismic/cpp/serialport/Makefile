SRC = $(wildcard *.cpp)
OBJ = $(patsubst %.cpp, obj/%.o, $(SRC))

main: main.exe
all: main libSerialport

ReadSendDemo: ReadSendDemo.exe

main.exe: main.cpp obj/Serialport.o
	g++ main.cpp obj/Serialport.o -o main.exe

obj/Serialport.o: Serialport.cpp
	g++ -c Serialport.cpp -o obj/Serialport.o

obj/com_yuyan_driver_serialport_Serialport.o: jni/com_yuyan_driver_serialport_Serialport.cpp
	g++ -c jni/com_yuyan_driver_serialport_Serialport.cpp -fPIC \
		-I "C:\Program Files\Java\jdk1.8.0_231\include" \
		-I "C:\Program Files\Java\jdk1.8.0_231\include\win32" \
		-o ./obj/com_yuyan_driver_serialport_Serialport.o

libSerialport: obj/com_yuyan_driver_serialport_Serialport.o obj/Serialport.o
	g++ -Wl,--add-stdcall-alias -shared -o jni/libSerialport.dll \
		obj/com_yuyan_driver_serialport_Serialport.o obj/Serialport.o

ReadSendDemo.exe: ReadSendDemo.cpp
	g++ ReadSendDemo.cpp -o ReadSendDemo.exe

clean:
	rm -rf $(OBJ) \
		main.exe \
		./obj/com_yuyan_driver_serialport_Serialport.o \
		./jni/libSerialport.dll \
		ReadSendDemo.exe

.PHONY: clean main all jni_Serialport ReadSendDemo
