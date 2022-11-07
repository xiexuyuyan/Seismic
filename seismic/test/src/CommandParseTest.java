import com.yuyan.driver.local.CommandRepository;
import com.yuyan.utils.Log;
import model.CommandResolver;
import org.junit.jupiter.api.Test;
import org.yuyan.command.model.CommandHexCode;
import org.yuyan.command.model.CommandRecv;
import org.yuyan.command.utils.CommandUtils;

import java.io.IOException;
import java.util.List;

public class CommandParseTest {
    private static final String TAG = "CommandParseTest";

    @Test
    public void t2() throws IOException {
        String codeStr = "383031311A3030300D";

        String defaultFilePath = "F:\\Users\\azxjq\\" +
                "AndroidProjects\\A311D2\\" +
                "SkgCommandMultiple\\cell_pin\\main\\assets\\";
        String defaultFileName = "A311D2_medium_skg_service_api.json";
        CommandRepository.init(defaultFilePath + defaultFileName);

        List<CommandRecv> commandRecvs = CommandResolver.parse(
                codeStr
                , CommandRepository.INSTANCE.commandList.commands
                , false);
        String valueStr = CommandUtils.valueFiller(
                commandRecvs.get(0).commandData.replyHexCode, "11:22:33:AA:BB:cc");
        CommandHexCode replyHexCode = commandRecvs.get(0).commandData.replyHexCode;
        String replyString = CommandUtils.formatSimpleHexCodeString(replyHexCode, valueStr);
        Log.i(TAG, "[Coder Wu] t2: replyString = " + replyString);
        Log.i(TAG, "[Coder Wu] t2: valueStr = " + valueStr);
        Log.i(TAG, "[Coder Wu] t2: commandRecvs = " + commandRecvs.get(0));
    }
}
