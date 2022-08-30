package com.yuyan.model;

public class Command {
    public String name;
    public String description;
    public CommandData commandData;

    @Override
    public String toString() {
        return "{" + "\n"
                + "\t\t" + "name: " + name + "\n"
                + "\t\t" + "description: " + description + "\n"
                + "\t\t" + "commandData: " + commandData + "\n"
                + "\t" + "}";
    }
}