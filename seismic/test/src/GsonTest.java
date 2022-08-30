import com.google.gson.Gson;
import com.yuyan.model.CommandList;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GsonTest {

    @Test
    public static void read() throws IOException {
        String filePath = "seismic/main/res/A311D2_medium_auto_test_command.json";
        File commandFile = new File(filePath);
        FileReader fileReader = new FileReader(commandFile);
        Reader reader = new InputStreamReader(
                Files.newInputStream(commandFile.toPath())
                , StandardCharsets.UTF_8);
        int ch = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((ch = reader.read()) != -1) {
            stringBuilder.append((char) ch);
        }
        fileReader.close();
        reader.close();
        String jsonStr = stringBuilder.toString();

        Gson gson = new Gson();
        CommandList commandList =
                gson.fromJson(jsonStr, CommandList.class);
        System.out.println("commandList = " + commandList);
        String jsonStrRevert = gson.toJson(commandList);
        System.out.println("jsonStrRevert = " + jsonStrRevert);
    }
}
