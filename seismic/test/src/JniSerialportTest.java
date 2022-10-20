import com.yuyan.driver.serialport.Serialport;
import com.yuyan.utils.Log;

import java.util.Arrays;

public class JniSerialportTest {

    public static void main(String[] args) {
        Serialport serialport = Serialport.getInstance();
        serialport.open("COM5");
        System.out.println(System.currentTimeMillis() + ",status = " + serialport.getStatus());
    }

    public static void __main(String[] args) {
        Serialport serialport = Serialport.getInstance();
        int i = serialport.open("COM6");
        System.out.println("i = " + i);
        System.out.println(System.currentTimeMillis() + ",status = " + serialport.getStatus());

        serialport.close();
        System.out.println(System.currentTimeMillis() + ",status = " + serialport.getStatus());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + ",status = " + serialport.getStatus());
            }
        }).start();
    }

    public static void _main(String[] args) {
        System.out.println("Hello world");
        Serialport serialport = Serialport.getInstance();
        int i = serialport.open("COM6");
        System.out.println("i = " + i);

        byte[] buff = new byte[1024];

        while (true) {
            int readLen = serialport.read(buff);
            System.out.println("readLen = " + readLen
                    + ", buff[0] = " + buff[0]
                    + ", buff = " + receiveByteToString(buff, readLen));
            int writeLen = serialport.write(buff, 2);
            System.out.println("writeLen = " + writeLen);
        }

        // serialport.close();
    }

    public static String receiveByteToString(final byte[] buff, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byte b = buff[i];
            int d = (b & 0x00_00_00_FF);
            String s = Integer.toString(d, 16);
            if (s.length() == 1) {
                s = "0" + s;
            }
            builder.append(s.toUpperCase());
        }
        return builder.toString();
    }
}
