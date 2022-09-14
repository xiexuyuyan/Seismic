package com.yuyan.model;

public class CommandData {
    public String name;
    public CommandHexCode commandHexCode;
    public CommandHexCode replyHexCode;

    @Override
    public String toString() {
        return "{" + "\n"
                + "\t\t\t" + "name: " + name + "\n"
                + "\t\t\t" + "commandHexCode: " + commandHexCode + "\n"
                + "\t\t\t" + "replyHexCode: " + replyHexCode + "\n"
                + "\t\t" + "}";
    }
}
