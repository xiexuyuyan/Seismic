import com.yuyan.model.Command;
import com.yuyan.model.CommandHexCode;
import com.yuyan.model.CommandRecv;
import com.yuyan.repository.CommandHelper;
import com.yuyan.repository.CommandResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandRecvUnitTest {



    public static void main(String[] args) throws IOException {
        int max = 10;

        CommandHelper.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        List<Command> commandList = CommandHelper.INSTANCE.commandList.commands;
        int sum = new Random().nextInt(max) + 1;
        String[] originCommands = new String[sum];
        String randomHexCode = RandomCommandFactory.createRandomCommands(commandList, sum, originCommands);

        List<CommandRecv> commandRecvList = CommandResolver.checkUnitRecv(randomHexCode, commandList, false);
        String[] matchedCommands = new String[commandRecvList.size()];
        for (int i = 0; i < commandRecvList.size(); i++) {
            matchedCommands[i] = commandRecvList.get(i).code;
        }

        List<String> checkA = RandomCommandFactory.toConfirm(matchedCommands, originCommands);
        List<String> checkB = RandomCommandFactory.toConfirm(originCommands, matchedCommands);

        System.out.println("randomHexCode = " + randomHexCode);
        System.out.println(Arrays.toString(matchedCommands));
        System.out.println(Arrays.toString(originCommands));
        System.out.println(checkA);
        System.out.println(checkB);

        checkReply(commandRecvList);
        checkTransform(matchedCommands);
    }

    private static void checkReply(List<CommandRecv> commandRecvList) {
        for (CommandRecv recv : commandRecvList) {
            String replyCode = RandomCommandFactory.setRandomCommandValue(recv, true);
            String replyValueStr = replyCode.substring(10, replyCode.length()-2);

            String valueType = recv.commandData.replyHexCode.commandValue.type;
            String valueReplyStr = getValueString(recv.commandData.replyHexCode, replyCode);

            String fillString;
            if (valueType.equals("integer")) {
                int d = Integer.parseInt(valueReplyStr);
                fillString = CommandResolver.fillValue(recv, d);
            } else {
                fillString = CommandResolver.fillValue(recv, valueReplyStr);
            }

//            System.out.println("fillString = " + fillString);

            if (!replyValueStr.equals(fillString)) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!error in fillString = " + fillString);
            }
        }
    }

    public static void checkTransform(String[] s) {
        String[] s0 = new String[]{ "38303747763030300D", "383534535E3030320D", "383635535E3030310D"
                                 , "38323553713030300D", "38393653673030310D", "38393753423031310D"
                                 , "38303953423030390D", "38333353423031330D", "38373753753030300D"};
        for (int i = 0; i < s.length; i++) {
            byte[] b = sendStringToBytes(s[i]);
            String s1 = receiveByteToString(b, b.length);
            // System.out.println("s1 = " + s1);
            if (!s1.equals(s[i])) {
                System.out.println("!!!!!!!!!!!! error");
            }
        }
    }

    private static byte[] sendStringToBytes(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < s.length()-1; i+=2) {
            String s1 = s.substring(i, i+2);
            int d = Integer.parseInt(s1, 16);
            byte b1 = (byte) (d & 0x00_00_00_FF);
            b[i/2] = b1;
        }
        return b;
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

    private static String getValueString(CommandHexCode commandHexCode, String code) {
        String valuePayload = code.substring(10, code.length()-2);
        String valueType = commandHexCode.commandValue.type;
        String valueReplyStr = "0";
        if (valueType.equals("integer")) {
            StringBuilder c = new StringBuilder();
            for (int i = 0; i < valuePayload.length()-1; i+=2) {
                c.append(valuePayload.charAt(i+1));
            }
            valueReplyStr = c.toString();
        } else if (valueType.equals("string")){
            StringBuilder c = new StringBuilder();
            for (int i = 0; i < valuePayload.length()-1; i+=2) {
                String d = valuePayload.substring(i, i+2);
                int e = Integer.parseInt(d, 16);
                char f = (char) e;
                c.append(f);
            }
            valueReplyStr = c.toString();
        }

        return valueReplyStr;
    }
}
