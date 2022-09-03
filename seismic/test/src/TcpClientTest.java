import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class TcpClientTest {
    private static final String TAG = "TcpClientTest";

    static class Log {
        static void i(String TAG, String msg) {
            System.out.println(TAG + ":" + msg);
        }
    }

    public static void main(String[] args) {
        try {
            new TcpClientTest().startTcpClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startTcpClient() throws IOException {
        Log.i(TAG, "[Coder Wu] startTcpClient: start");
        String[] ipStr = "192.168.3.34".split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
        }
        Log.i(TAG, "[Coder Wu] startTcpClient: stage 2");
        Socket socket = new Socket(InetAddress.getByAddress(ipBuf),53705);
        OutputStream outputStream = socket.getOutputStream();
        PrintStream p = new PrintStream(outputStream);
        p.println("from client");
        Log.i(TAG, "[Coder Wu] startTcpClient: stage 3");


        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        char[] buff = new char[1024];
        int readLength = -1;
        do {
            readLength = bufferedReader.read(buff, 0, 1024);
            Log.i(TAG, "[Coder Wu] run: readLength = " + readLength + ", buff = " + Arrays.toString(buff));
        } while (true);

//         socket.shutdownOutput();
//         outputStream.flush();
//         socket.close();
    }
}