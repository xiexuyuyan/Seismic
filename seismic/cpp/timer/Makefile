SRC = $(wildcard *.cpp)
OBJ = $(patsubst %.cpp, obj/%.o, $(SRC))

TimerDemo: TimerDemo.exe
all: TimerDemo

TimerDemo.exe: obj/TimerDemo.o
	g++ obj/TimerDemo.o -o TimerDemo.exe -lwinmm

obj/TimerDemo.o: TimerDemo.cpp
	g++ -c TimerDemo.cpp -o obj/TimerDemo.o

clean:
	rm -rf $(OBJ) \
		TimerDemo.exe

.PHONY: clean main all TimerDemo
