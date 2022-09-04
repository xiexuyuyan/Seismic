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
        String[] ipStr = "192.168.3.17".split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
        }
        Log.i(TAG, "[Coder Wu] startTcpClient: stage 2");
        Socket socket = new Socket(InetAddress.getByAddress(ipBuf),53705);
        Log.i(TAG, "[Coder Wu] startTcpClient: " + socket.getRemoteSocketAddress());
        OutputStream outputStream = socket.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.print("12345ABCDE");
        Log.i(TAG, "[Coder Wu] startTcpClient: stage 3");
    }
}