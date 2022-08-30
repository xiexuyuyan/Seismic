package droid.message;

public class Message {
    public int what;
    public int arg1;
    public int arg2;
    public Object obj;

    Handler target;
    Message next;
}
