import com.yuyan.driver.local.CommandRepository;
import com.yuyan.driver.serialport.Serialport;
import com.yuyan.utils.Log;
import org.yuyan.command.model.CommandRecv;
import org.yuyan.command.utils.ByteUtils;
import org.yuyan.command.utils.CommandResolver;
import org.yuyan.command.utils.CommandUtils;

import java.io.IOException;
import java.util.List;

public class AutomaticSendCommandTest {
    private static final String TAG = "AutomaticSendCommandTes";

    public static void main(String[] args) {
        getCurrent();
    }

    public static void _main(String[] args) throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            setToAndroid();

            Thread.sleep(2000);

            setToHDMI2();

            Thread.sleep(5000);
            // getCurrent();
        }
    }

    public static void setToHDMI1() {
        try {
            CommandRepository.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Serialport serialport = Serialport.getInstance();
        serialport.open("COM7");
        System.out.println(System.currentTimeMillis() + ",status = " + serialport.getStatus());

        // String getStr = "38303147853030300D";
        String getStr = "373031536030320D";
        byte[] getBytes = ByteUtils.hexStringToBytes(getStr);
        serialport.setReadTimeout(3000);
        serialport.write(getBytes, getBytes.length);

        byte[] buff = new byte[20];
        int readLen = serialport.read(buff);
        String readStr = ByteUtils.hexBytesToString(buff, readLen);
        List<CommandRecv> commandRecvs = CommandResolver.parse(readStr
                , CommandRepository.INSTANCE.commandList.commands, true);
        String value = CommandUtils.getValueString(commandRecvs.get(0).commandData.replyHexCode
                , commandRecvs.get(0).code);
        Log.i(TAG, "[Coder Wu] main: readStr = " + readStr);
        Log.i(TAG, "[Coder Wu] main: readLen = " + readLen);
        Log.i(TAG, "[Coder Wu] main: value = " + value);

        serialport.close();
    }

    public static void setToHDMI2() {
        try {
            CommandRepository.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Serialport serialport = Serialport.getInstance();
        serialport.open("COM7");
        System.out.println(System.currentTimeMillis() + ",status = " + serialport.getStatus());

        // String getStr = "38303147853030300D";
        String getStr = "373031536030330D";
        byte[] getBytes = ByteUtils.hexStringToBytes(getStr);
        serialport.setReadTimeout(3000);
        serialport.write(getBytes, getBytes.length);

        byte[] buff = new byte[20];
        int readLen = serialport.read(buff);
        String readStr = ByteUtils.hexBytesToString(buff, readLen);
        List<CommandRecv> commandRecvs = CommandResolver.parse(readStr
                , CommandRepository.INSTANCE.commandList.commands, true);
        String value = CommandUtils.getValueString(commandRecvs.get(0).commandData.replyHexCode
                , commandRecvs.get(0).code);
        Log.i(TAG, "[Coder Wu] main: readStr = " + readStr);
        Log.i(TAG, "[Coder Wu] main: readLen = " + readLen);
        Log.i(TAG, "[Coder Wu] main: value = " + value);

        serialport.close();
    }

    public static void setToAndroid() {
        try {
            CommandRepository.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Serialport serialport = Serialport.getInstance();
        serialport.open("COM7");
        System.out.println(System.currentTimeMillis() + ",status = " + serialport.getStatus());

        // String getStr = "38303147853030300D";
        String getStr = "373031536030360D";
        byte[] getBytes = ByteUtils.hexStringToBytes(getStr);
        serialport.setReadTimeout(3000);
        serialport.write(getBytes, getBytes.length);

        byte[] buff = new byte[20];
        int readLen = serialport.read(buff);
        String readStr = ByteUtils.hexBytesToString(buff, readLen);
        List<CommandRecv> commandRecvs = CommandResolver.parse(readStr
                , CommandRepository.INSTANCE.commandList.commands, true);
        String value = CommandUtils.getValueString(commandRecvs.get(0).commandData.replyHexCode
                , commandRecvs.get(0).code);
        Log.i(TAG, "[Coder Wu] main: readStr = " + readStr);
        Log.i(TAG, "[Coder Wu] main: readLen = " + readLen);
        Log.i(TAG, "[Coder Wu] main: value = " + value);

        serialport.close();
    }

    public static void getCurrent() {
        try {
            CommandRepository.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Serialport serialport = Serialport.getInstance();
        serialport.open("COM7");
        System.out.println(System.currentTimeMillis() + ",status = " + serialport.getStatus());

        String getStr = "38303147853030300D";
        // String getStr = "373031536030360D";
        byte[] getBytes = ByteUtils.hexStringToBytes(getStr);
        serialport.setReadTimeout(3000);
        serialport.write(getBytes, getBytes.length);

        byte[] buff = new byte[20];
        int readLen = serialport.read(buff);
        String readStr = ByteUtils.hexBytesToString(buff, readLen);
        List<CommandRecv> commandRecvs = CommandResolver.parse(readStr
                , CommandRepository.INSTANCE.commandList.commands, true);
        String value = CommandUtils.getValueString(commandRecvs.get(0).commandData.replyHexCode
                , commandRecvs.get(0).code);
        Log.i(TAG, "[Coder Wu] main: readStr = " + readStr);
        Log.i(TAG, "[Coder Wu] main: readLen = " + readLen);
        Log.i(TAG, "[Coder Wu] main: value = " + value);

        serialport.close();
    }

}
