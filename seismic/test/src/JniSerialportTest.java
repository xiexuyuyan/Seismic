import com.yuyan.driver.serialport.Serialport;

import java.util.Arrays;

public class JniSerialportTest {

    public static void main(String[] args) {
        System.out.println("Hello world");
        Serialport serialport = new Serialport();
        int i = serialport.open();
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
