package com.yuyan.model;

import java.util.List;

public class CommandList {
    public int size;
    public String model;
    public String brand;
    public String manufacturer;
    public String version;
    public String date;
    public String author;
    public String board;
    public List<Command> commands;

    @Override
    public String toString() {
        StringBuilder commandListStr = new StringBuilder();
        commandListStr.append("[");
        for (Command command : commands) {
            commandListStr.append(command.toString());
            commandListStr.append(",");
        }
        commandListStr.deleteCharAt(commandListStr.length()-1);
        commandListStr.append("]");

        return "{" + "\n"
                + "\t" + "size: " + size + "\n"
                + "\t" + "model: " + model + "\n"
                + "\t" + "brand: " + brand + "\n"
                + "\t" + "manufacturer: " + manufacturer + "\n"
                + "\t" + "version: " + version + "\n"
                + "\t" + "date: " + date + "\n"
                + "\t" + "author: " + author + "\n"
                + "\t" + "board: " + board + "\n"
                + "\t" + "commands: " + commandListStr + "\n"
                + "}";
    }
}







