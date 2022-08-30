package cat.handler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class ServletHandler {
    public String pkgName = null;

    public ServletHandler(String _pkgName) {
        this.pkgName = _pkgName;
    }

    public abstract boolean handle(ServletRequest req, ServletResponse res);

    public static String parsePackageName(String requestUrl){
        return requestUrl.split("/")[1];
    }


    protected static String parseUrl(String requestUrl) {
        return requestUrl.split("/")[2];
    }
}
