package cat.server;

import cat.handler.HandlerMapper;
import cat.servlet.DispatchServlet;
import cat.handler.ServletHandler;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import java.util.List;

public class TomcatServer {
    private Tomcat tomcat;

    private final HandlerMapper mapper;

    public TomcatServer() {
        mapper = new HandlerMapper();
    }

    public void updateHandlerList(List<ServletHandler> handlerList) {
        mapper.updateHandlerList(handlerList);
    }

    public void registerServletHandler(ServletHandler handler) {
        mapper.register(handler);
    }


    public void startServer(){
        tomcat = new Tomcat();
        tomcat.setPort(6699);
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

        Context context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());
        cat.servlet.DispatchServlet dispatchServlet = new DispatchServlet(mapper);
        Tomcat.addServlet(context, "default_patcher", dispatchServlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/", "default_patcher");
        tomcat.getHost().addChild(context);

        Thread awaitThread = new Thread("tomcat_await_thread"){
            @Override
            public void run() {
                TomcatServer.this.tomcat.getServer().await();
            }
        };
        awaitThread.setDaemon(false);
        awaitThread.start();
    }
}
