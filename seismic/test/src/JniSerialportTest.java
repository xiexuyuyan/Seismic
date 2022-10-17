import com.yuyan.driver.serialport.Serialport;

public class JniSerialportTest {

    public static void main(String[] args) {
        System.out.println("Hello world");
        Serialport serialport = new Serialport();
        int i = serialport.open();
        System.out.println("i = " + i);

        byte[] buff = new byte[1024];

        while (true) {
            int readLen = serialport.read(buff);
            System.out.println("readLen = " + readLen);
        }

        // serialport.close();
    }
}
