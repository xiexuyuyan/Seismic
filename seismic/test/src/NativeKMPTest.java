import com.yuyan.driver.local.CommandRepository;
import com.yuyan.utils.KMP;
import org.yuyan.command.model.Command;

import java.io.IOException;
import java.util.List;

public class NativeKMPTest {

    public static void main(String[] args) throws IOException {
        CommandRepository.init("seismic/test/res/A311D2_medium_auto_test_command.json");
        for (int i = 0; i < 100; i++) {
            _main();
        }
    }

    public static void _main() throws IOException {
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;
        int sum = 1;
        String[] originCommands = new String[sum];
        String randomHexCode = RandomCommandFactory.createRandomCommands(commandList, sum, originCommands);

        String pat = originCommands[0];

        int indexByKMP = KMP.find(pat, randomHexCode);

        int indexByString = randomHexCode.indexOf(pat);

        if (indexByKMP == indexByString) {
            System.out.println("randomHexCode = " + randomHexCode);
            System.out.println("originCommands = " + originCommands[0]);
            System.out.println("indexByKMP = " + indexByKMP);
            System.out.println("indexByString = " + indexByString);
        }
    }
}
