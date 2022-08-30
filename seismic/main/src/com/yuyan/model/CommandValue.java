package com.yuyan.model;

public class CommandValue {
    public String type;
    public String min;
    public String max;

    @Override
    public String toString() {
        return "{" + "\n"
                + "\t\t\t\t\t" + "type: " + type + "\n"
                + "\t\t\t\t\t" + "min: " + min + "\n"
                + "\t\t\t\t\t" + "max: " + max + "\n"
                + "\t\t\t\t" + "}";
    }
}