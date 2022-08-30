package com.yuyan.model;

public class CommandData {
    public String name;
    public String stringCode;
    public CommandHexCode commandHexCode;

    @Override
    public String toString() {
        return "{" + "\n"
                + "\t\t\t" + "name: " + name + "\n"
                + "\t\t\t" + "stringCode: " + stringCode + "\n"
                + "\t\t\t" + "commandHexCode: " + commandHexCode + "\n"
                + "\t\t" + "}";
    }
}
