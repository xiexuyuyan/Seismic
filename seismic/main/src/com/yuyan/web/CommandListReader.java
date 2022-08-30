package com.yuyan.web;

import com.yuyan.model.Command;
import com.yuyan.repository.CommandHelper;

public class CommandListReader {
    public static int getSize() {
        return CommandHelper.INSTANCE.commandList.commands.size();
    }

    public static Command getCommand(int index) {
        if (index < getSize() && index >= 0)
            return CommandHelper.INSTANCE.commandList.commands.get(index);
        else return null;
    }

}
