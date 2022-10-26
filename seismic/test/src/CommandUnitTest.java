import org.yuyan.command.model.Command;
import com.yuyan.driver.local.CommandRepository;
import org.yuyan.command.utils.CommandResolver;
import org.yuyan.command.model.CommandRecv;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandUnitTest {

    public static void main(String[] args) throws IOException {
        int max = 10;

        CommandRepository.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;
        int sum = new Random().nextInt(max) + 1;
        String[] originCommands = new String[sum];
        String randomHexCode = RandomCommandFactory.createRandomCommands(commandList, sum, originCommands);
        List<CommandRecv> commandRecvs = CommandResolver.checkUnitRecv(randomHexCode, commandList, false);
        String[] matchedCommands = new String[commandRecvs.size()];
        for (int i = 0; i < commandRecvs.size(); i++) {
            matchedCommands[i] = commandRecvs.get(i).code;
        }
        List<String> checkA = RandomCommandFactory.toConfirm(matchedCommands, originCommands);
        List<String> checkB = RandomCommandFactory.toConfirm(originCommands, matchedCommands);

        System.out.println("randomHexCode = " + randomHexCode);
        System.out.println(Arrays.toString(matchedCommands));
        System.out.println(Arrays.toString(originCommands));
        System.out.println(checkA);
        System.out.println(checkB);
    }

    public static void _main(String[] args) throws IOException {
        CommandRepository.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;

        // String randomHexCode = "38393053713031300D383535536C3030300D";
        String randomHexCode = "38303147853030300D38303153603031300D";
        List<CommandRecv> commandRecvs = CommandResolver.checkUnitRecv(randomHexCode, commandList, false);
        String[] matchedCommands = new String[commandRecvs.size()];
        for (int i = 0; i < commandRecvs.size(); i++) {
            matchedCommands[i] = commandRecvs.get(i).code;
        }

        System.out.println("matchedCommands = " + Arrays.toString(matchedCommands));
    }


    public static void __main(String[] args) throws IOException {
        CommandRepository.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;

        String randomHexCode = "d439b1f638373053673030300Da8a135540a0e66738383053643030310Ddc38303747863030300D77a7";
        // [38303747863030300D, 38373053673030300D, 38383053643030310D]
        // [38373053673030300D, 38383053643030310D, 38303747863030300D]
        List<CommandRecv> commandRecvs = CommandResolver.checkUnitRecv(randomHexCode, commandList, false);
        String[] matchedCommands = new String[commandRecvs.size()];
        for (int i = 0; i < commandRecvs.size(); i++) {
            matchedCommands[i] = commandRecvs.get(i).code;
        }

        for (String matchedCommand : matchedCommands) {
            int i = randomHexCode.indexOf(matchedCommand);
            System.out.println(i);
        }
        StringBuilder builder = new StringBuilder();

        System.out.println("matchedCommands = " + Arrays.toString(matchedCommands));
    }
}
