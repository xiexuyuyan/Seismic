package com.yuyan;

import com.yuyan.utils.Log;
import org.yuyan.cat.handler.ServletHandler;
import org.yuyan.cat.server.TomcatServer;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.web.ServletThreadMaintainer;
import org.yuyan.command.model.CommandList;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.Socket;

public class Root {
    private final static ContextThread contextThread = new ContextThread();

    public static ThreadLocal<Socket> getThreadLocalSocket() {
        return contextThread.threadLocalSocket;
    }

    private static class ContextThread {
        private final ThreadLocal<Socket> threadLocalSocket;
        ContextThread() {
            threadLocalSocket = new ThreadLocal<>();
        }
    }

    public static void main(String[] args) {
        try {
            new Root().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        String defaultFilePath = "F:\\Users\\azxjq\\" +
                "AndroidProjects\\A311D2\\" +
                "SkgCommandMultiple\\cell_pin\\main\\assets\\";
        String defaultFileName = "A311D2_medium_skg_service_api.json";
        CommandRepository.init(defaultFilePath + defaultFileName);

        TomcatServer tomcatServer = new TomcatServer();

        ServletHandler handler = new ServletHandler("com.yuyan.seismic") {
            @Override
            public boolean handle(ServletRequest req, ServletResponse res) {
                new ServletThreadMaintainer(req, res).start();
                return true;
            }
        };
        tomcatServer.registerServletHandler(handler);
        tomcatServer.startServer();
    }

}
