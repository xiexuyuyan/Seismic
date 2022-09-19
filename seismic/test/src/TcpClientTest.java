import com.yuyan.model.Command;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.driver.local.CommandResolver;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TcpClientTest {
    private static final String TAG = "TcpClientTest";

    static class Log {
        static void i(String TAG, String msg) {
            System.out.println(TAG + ":" + msg);
        }
    }

    @Test
    public void toDelete() {
        for (int i = 0; i < 256; i++) {
            String a = Integer.toString(i, 16);
            System.out.println("a = " + a);
            byte b = (byte) Integer.parseInt(a, 16);
            System.out.println("b = " + b);


            System.out.println();
        }
    }


    public static String createCommands(int max) {
        try {
            CommandRepository.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;
        int sum = new Random().nextInt(max) + 1;
        String[] originCommands = new String[sum];
        String randomHexCode = RandomCommandFactory.createRandomCommands(commandList, sum, originCommands);
        String[] matchedCommands = CommandResolver.checkUnit(randomHexCode, commandList, false);
        List<String> checkA = RandomCommandFactory.toConfirm(matchedCommands, originCommands);
        List<String> checkB = RandomCommandFactory.toConfirm(originCommands, matchedCommands);

        System.out.println(Arrays.toString(matchedCommands));
        System.out.println(Arrays.toString(originCommands));
        System.out.println(checkA);
        System.out.println(checkB);

        return randomHexCode;
    }

    private static byte[] sendStringToByte(String commandHexCode) {
        if ((commandHexCode.length() % 2) == 1) {
            commandHexCode = commandHexCode.substring(0, commandHexCode.length()-1);
        }

        byte[] re = new byte[commandHexCode.length()/2];

        for (int i = 0; i < commandHexCode.length(); i+=2) {
            String s = commandHexCode.substring(i, i+2);
            int d = Integer.parseInt(s, 16);
            byte b = (byte) d;
            re[i/2] = b;
        }

        return re;
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
        String[] ipStr = "192.168.18.235".split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
        }
        Log.i(TAG, "[Coder Wu] startTcpClient: stage 2");
        Socket socket = new Socket(InetAddress.getByAddress(ipBuf),53705);
        Log.i(TAG, "[Coder Wu] startTcpClient: " + socket.getRemoteSocketAddress());
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // String s = createCommands(10);
            String s = "38303147853030300D38303153603031300D";
            // String s = "38303153FF3031300D";
            byte[] b = sendStringToByte(s);
            outputStream.write(b);

            byte[] buff = new byte[1024];
            int readLen = inputStream.read(buff, 0, 1024);
            System.out.println("readLen = " + readLen);
            String buffStr = receiveByteToString(buff, readLen);
            System.out.println("buffStr = " + buffStr);
        }
        // Log.i(TAG, "[Coder Wu] startTcpClient: stage 3");
    }

    private static String receiveByteToString(final byte[] buff, int len) {
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