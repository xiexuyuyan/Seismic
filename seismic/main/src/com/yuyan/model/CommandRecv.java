package com.yuyan.model;

public class CommandRecv extends Command{
    public String code;

    public String toString() {
        return "code:" + code + "-" + super.toString();
    }

    public CommandRecv(Command command) {
        super(command);
    }
    public CommandRecv(String code, Command command) {
        this(command);
        this.code = code;
    }
}
