package droid.message;

public class MessageQueue {
    Message mMessages;

    public Message next() {
        synchronized (this) {
            for (;;) {
                Message msg = mMessages;
                if (msg != null) {
                    mMessages = msg.next;
                    msg.next = null;
                    return msg;
                }
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    boolean enqueueMessage(Message msg) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        synchronized (this) {
            Message current = mMessages;
            if (current == null) {
                msg.next = current;
                mMessages = msg;
            } else {
                Message pre;
                for (;;) {// find the tail of the queue
                    pre = current;
                    current = pre.next;
                    if (current == null) {
                        break;
                    }
                }
                msg.next = null;// current == null
                pre.next = msg;
            }
            this.notify();
        }

        return true;
    }
}
