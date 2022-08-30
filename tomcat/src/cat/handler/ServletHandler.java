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
        String[] args = requestUrl.split("/");
        return args.length >= 2 ? args[1] : null;
    }


    protected static String parseUrl(String requestUrl) {
        String[] args = requestUrl.split("/");
        return args.length >= 3 ? args[2] : null;
    }
}
