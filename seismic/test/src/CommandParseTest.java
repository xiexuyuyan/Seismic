import com.yuyan.driver.local.CommandRepository;
import com.yuyan.utils.Log;
import model.CommandResolver;
import org.junit.jupiter.api.Test;
import org.yuyan.command.model.CommandRecv;

import java.io.IOException;
import java.util.List;

public class CommandParseTest {
    private static final String TAG = "CommandParseTest";

    @Test
    public void t2() throws IOException {
        String codeStr = "3E3031310B3538353232313130340D";

        String defaultFilePath = "F:\\Users\\azxjq\\" +
                "AndroidProjects\\A311D2\\" +
                "SkgCommandMultiple\\cell_pin\\main\\assets\\";
        String defaultFileName = "A311D2_medium_skg_service_api.json";
        CommandRepository.init(defaultFilePath + defaultFileName);

        List<CommandRecv> commandRecvs = CommandResolver.parse(
                codeStr
                , CommandRepository.INSTANCE.commandList.commands
                , false);

        Log.i(TAG, "[Coder Wu] t2: commandRecvs = " + commandRecvs.get(0));
    }
}
