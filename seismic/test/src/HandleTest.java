import com.yuyan.tcp.TcpClientRunnable;
import droid.message.Handler;
import droid.message.Looper;
import droid.message.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

public class HandleTest {

    final class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String threadName = Thread.currentThread().getName();
            System.out.println("Thread[" + threadName + "]: handleMessage():msg.what = " + msg.what + ", msg.obj = " + msg.obj);
        }
    }

    public static void main(String[] args) {
        new HandleTest().start();
    }

    private H mH;

    private void start() {
        Looper.prepareMainLooper();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mH = new H();
                System.out.println(mH.toString());
                Looper.loop();
                try {
                    throw new Exception("ContextThread looper stop");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hhh: " + mH.toString());
                while (true) {
                    try {
                        Thread.sleep(1500);
                        Handler handler = mH;
                        Message message = new Message();
                        message.what = 10086;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }



}
