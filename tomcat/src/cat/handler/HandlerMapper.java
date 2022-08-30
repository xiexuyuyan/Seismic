package cat.handler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class HandlerMapper {
    private final List<ServletHandler> handlers = new ArrayList<>();

    public void handle(ServletRequest req, ServletResponse res) {
        for (ServletHandler handler : handlers) {
            if (handler.handle(req, res)) {
                return;
            }
        }
        String request = ((HttpServletRequest)req).getRequestURI();
        System.out.print("暂无可匹配的服务: request = " + request);
        System.out.print(", 参数 = ");
        for (String s : req.getParameterMap().keySet()) {
            System.out.print(s + ",");
        }
        System.out.println();
    }

    public void updateHandlerList(List<ServletHandler> handlerList) {
        handlers.addAll(handlerList);
    }

    public List<ServletHandler> getHandlerList() {
        return handlers;
    }

    public void register(ServletHandler handler) {
        handlers.add(handler);
    }
}
