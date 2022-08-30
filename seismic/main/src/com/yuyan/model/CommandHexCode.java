package com.yuyan.model;

public class CommandHexCode {
    public String type;
    public String code;
    public CommandValue commandValue;

    @Override
    public String toString() {
        return "{" + "\n"
                + "\t\t\t\t" + "type: " + type + "\n"
                + "\t\t\t\t" + "code: " + code + "\n"
                + "\t\t\t\t" + "commandValue: " + commandValue + "\n"
                + "\t\t\t" + "}";
    }
}