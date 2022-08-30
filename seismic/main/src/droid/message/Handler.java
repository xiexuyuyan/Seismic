package droid.message;

public abstract class Handler {
    final Looper mLooper;
    final MessageQueue mQueue;

    public Handler() {
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new NullPointerException("Handler:Handler() mLooper is null");
        }
        mQueue = mLooper.mQueue;
    }

    public Handler(Looper looper) {
        mLooper = looper;
        if (mLooper == null) {
            throw new NullPointerException("Handler:Handler() mLooper is null");
        }
        mQueue = mLooper.mQueue;
    }

    public abstract void handleMessage(Message msg);

    public boolean sendMessage(Message msg) {
        MessageQueue queue = mQueue;
        if (queue == null) {
            throw new NullPointerException("queue in Handler:sendMessage()");
        }
        return enqueueMessage(queue, msg);
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg) {
        msg.target = this;
        return queue.enqueueMessage(msg);
    }

    public void dispatchMessage(Message msg) {
        handleMessage(msg);
    }
}
