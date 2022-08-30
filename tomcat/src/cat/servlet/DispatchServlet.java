package cat.servlet;

import cat.handler.HandlerMapper;

import javax.servlet.*;
import java.io.IOException;

public class DispatchServlet implements Servlet {
    private HandlerMapper mapper;

    public DispatchServlet(HandlerMapper handlerMapper) {
        mapper = handlerMapper;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        mapper.handle(req, res);
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
