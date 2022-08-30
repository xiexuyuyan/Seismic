package droid.message;

public class Looper {
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
    private static Looper sMainLooper;
    private static String SET_THREAD_NAME = "";

    final MessageQueue mQueue;
    final Thread mThread;

    public Looper() {
        mQueue = new MessageQueue();
        mThread = Thread.currentThread();
    }

    public static void prepareMainLooper() {
        prepare();
        if (sMainLooper != null) {
            throw new IllegalStateException("\n" +
                    "\tThe main Looper has already been prepared.\n" +
                    "\tLast set in thread[" + SET_THREAD_NAME + "].\n" +
                    "\tCurrent in thread[" + Thread.currentThread().getName() + "].");
        }
        sMainLooper = myLooper();
    }

    public static void prepare() {
        SET_THREAD_NAME = Thread.currentThread().getName();
        System.out.println("Thread[" + Thread.currentThread().getName() + "]:, "
                + "prepare: "+SET_THREAD_NAME);
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper());
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }

        final MessageQueue queue = me.mQueue;

        for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                System.out.println("msg = null");
                return;
            }
            msg.target.dispatchMessage(msg);
        }
    }
}
