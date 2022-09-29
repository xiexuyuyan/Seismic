-Djava.library.path=F:\Users\azxjq\IntelliJIdeaProjects\Seismic\seismic\cpp\serialport\jni

"C:\Program Files\Java\jdk1.8.0_231\bin\javac" main/src/com/yuyan/driver/serialport/*.java -d cpp/serialport/jni/

"C:\Program Files\Java\jdk1.8.0_231\bin\javah" -cp cpp/serialport/jni/ -d cpp/serialport/jni/ com.yuyan.driver.serialport.Serialport
