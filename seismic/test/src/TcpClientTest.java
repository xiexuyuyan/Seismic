import com.yuyan.model.Command;
import com.yuyan.repository.CommandHelper;
import com.yuyan.repository.CommandResolver;
import com.yuyan.repository.DecodeUtils;
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

    public static void main(String[] args) {
        try {
            new TcpClientTest().startTcpClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void toDelete() {
        for (int i = 0; i < 100; i++) {
            int randomInt = new Random().nextInt(256);
            String s = Integer.toString(randomInt, 16);
            System.out.println("s = " + s);
            int d = DecodeUtils.inputFormat(s);
            System.out.println("d = " + d);
            byte b = DecodeUtils.sendFormat(d);
            System.out.println("b = " + b);
        }
    }


    public static String createCommands(int max) {
        try {
            CommandHelper.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Command> commandList = CommandHelper.INSTANCE.commandList.commands;
        int sum = new Random().nextInt(max) + 1;
        String[] originCommands = new String[sum];
        String randomHexCode = RandomCommandFactory.createRandomCommands(commandList, sum, originCommands);
        String[] matchedCommands = CommandResolver.checkUnit(randomHexCode, commandList);
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
            int d = DecodeUtils.inputFormat(s);
            byte b = DecodeUtils.sendFormat(d);
            re[i/2] = b;
        }

        return re;
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

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String s = createCommands(10);
            byte[] b = sendStringToByte(s);
            outputStream.write(b);
        }
        // Log.i(TAG, "[Coder Wu] startTcpClient: stage 3");
    }
}