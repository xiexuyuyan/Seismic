import com.yuyan.driver.serialport.Serialport;

import java.io.IOException;
import java.io.InputStream;

public class JniSerialportTest {

    public static void main(String[] args) {
        System.out.println("Hello world");
        Serialport serialport = new Serialport();
        serialport.open();

        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        InputStream inputStream = serialport.getInputStream();
        try {
            System.out.println("Start read.");
            int len = inputStream.read();
            System.out.println("Read len = " + len);
        } catch (IOException e) {
            System.out.println("Err = " + e.getMessage());
            e.printStackTrace();
        }
    }
}
