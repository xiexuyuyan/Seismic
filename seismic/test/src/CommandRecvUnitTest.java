import org.yuyan.command.model.Command;
import org.yuyan.command.model.CommandHexCode;
import org.yuyan.command.model.CommandRecv;
import org.yuyan.command.utils.ByteUtils;
import com.yuyan.driver.local.CommandRepository;
import org.yuyan.command.utils.CommandResolver;
import org.yuyan.command.utils.CommandUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandRecvUnitTest {



    public static void main(String[] args) throws IOException {
        int max = 10;

        CommandRepository.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;
        int sum = new Random().nextInt(1) + 1;
        String[] originCommands = new String[sum];
        String randomHexCode = RandomCommandFactory.createRandomCommands(commandList, sum, originCommands);

        List<CommandRecv> commandRecvList = CommandResolver.parse(randomHexCode, commandList, false);
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
                fillString = CommandUtils.valueFiller(recv.commandData.replyHexCode, d);
            } else {
                fillString = CommandUtils.valueFiller(recv.commandData.replyHexCode, valueReplyStr);
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
            byte[] b = ByteUtils.hexStringToBytes(s[i]);
            String s1 = ByteUtils.hexBytesToString(b, b.length);
            // System.out.println("s1 = " + s1);
            if (!s1.equals(s[i])) {
                System.out.println("!!!!!!!!!!!! error");
            }
        }
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
