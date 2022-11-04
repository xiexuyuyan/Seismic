import org.yuyan.command.model.Command;
import org.yuyan.command.utils.ByteUtils;
import com.yuyan.driver.local.CommandRepository;
import org.yuyan.command.utils.CommandResolver;
import org.yuyan.command.model.CommandRecv;
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
        List<CommandRecv> commandRecvs = CommandResolver.parse(randomHexCode, commandList, false);
        String[] matchedCommands = new String[commandRecvs.size()];
        for (int i = 0; i < commandRecvs.size(); i++) {
            matchedCommands[i] = commandRecvs.get(i).code;
        }
        List<String> checkA = RandomCommandFactory.toConfirm(matchedCommands, originCommands);
        List<String> checkB = RandomCommandFactory.toConfirm(originCommands, matchedCommands);

        System.out.println(Arrays.toString(matchedCommands));
        System.out.println(Arrays.toString(originCommands));
        System.out.println(checkA);
        System.out.println(checkB);

        return randomHexCode;
    }


    public static void main(String[] args) {
        try {
            new TcpClientTest().startTcpClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startTcpClient() throws IOException, InterruptedException {
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
            String s1  = "2d";
            String s38 = "38353453";
            String s42 = "423030390D";
            String s45 = "4597";
            String s68 = "6899";
            String s0b = "0b";
            String sb8 = "b855cd";
            String s41 = "41383633";
            String s53 = "53513030300D0ff7";
            String s3  = "3838";
            String s33 = "335376";
            String s30 = "3030300D86d9";
            String s7a = "7abd";
            // String s = "38303153FF3031300D";
            byte[] b1 = ByteUtils.hexStringToBytes(s1);
            byte[] b38 = ByteUtils.hexStringToBytes(s38);
            byte[] b42 = ByteUtils.hexStringToBytes(s42);
            byte[] b45 = ByteUtils.hexStringToBytes(s45);
            byte[] b68 = ByteUtils.hexStringToBytes(s68);
            byte[] b0b = ByteUtils.hexStringToBytes(s0b);
            byte[] bb8 = ByteUtils.hexStringToBytes(sb8);
            byte[] b41 = ByteUtils.hexStringToBytes(s41);
            byte[] b53 = ByteUtils.hexStringToBytes(s53);
            byte[] b3 = ByteUtils.hexStringToBytes(s3);
            byte[] b33 = ByteUtils.hexStringToBytes(s33);
            byte[] b30 = ByteUtils.hexStringToBytes(s30);
            byte[] b7a = ByteUtils.hexStringToBytes(s7a);

            outputStream.write(b1);
            Thread.sleep(500);
            outputStream.write(b1);
            Thread.sleep(500);
            outputStream.write(b38);
            Thread.sleep(500);
            outputStream.write(b42);
            Thread.sleep(500);
            outputStream.write(b45);
            Thread.sleep(500);
            outputStream.write(b68);
            Thread.sleep(500);
            outputStream.write(b0b);
            Thread.sleep(500);
            outputStream.write(bb8);
            Thread.sleep(500);
            outputStream.write(b41);
            Thread.sleep(500);
            outputStream.write(b53);
            Thread.sleep(500);
            outputStream.write(b3);
            Thread.sleep(500);
            outputStream.write(b33);
            Thread.sleep(500);
            outputStream.write(b30);
            Thread.sleep(500);
            outputStream.write(b7a);

            byte[] buff = new byte[1024];
            int readLen = inputStream.read(buff, 0, 1024);
            System.out.println("readLen = " + readLen);
            String buffStr = ByteUtils.hexBytesToString(buff, readLen);
            System.out.println("buffStr = " + buffStr);
        }
        // Log.i(TAG, "[Coder Wu] startTcpClient: stage 3");
    }

}