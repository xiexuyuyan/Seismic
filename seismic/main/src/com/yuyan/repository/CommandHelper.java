package com.yuyan.repository;

import com.google.gson.Gson;
import com.yuyan.model.Command;
import com.yuyan.model.CommandList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public enum CommandHelper {
    INSTANCE;
    public CommandList commandList;

    public Map<String, Command> commandMap;

    public static void init() throws IOException {
        init("seismic/main/res/A311D2_medium_auto_test_command.json");
    }

    public static void init(String filePath) throws IOException {
        File commandFile = new File(filePath);
        init(Files.newInputStream(commandFile.toPath()));
    }

    public static void init(InputStream inputStream) throws IOException {
        Reader reader = new InputStreamReader(
                inputStream, StandardCharsets.UTF_8);
        int ch = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((ch = reader.read()) != -1) {
            stringBuilder.append((char) ch);
        }
        reader.close();
        String jsonStr = stringBuilder.toString();

        Gson gson = new Gson();
        INSTANCE.commandList = gson.fromJson(jsonStr, CommandList.class);

        INSTANCE.commandMap = new HashMap<>();
        for (Command command : INSTANCE.commandList.commands) {
            INSTANCE.commandMap.put(command.commandData.name, command);
        }
    }
}
