package wipb.jsfdemo.web.interceptor;

import javax.ejb.Asynchronous;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InterceptorOfServices {
    Logger logger = Logger.getLogger("Interceptor");
    @AroundInvoke
    @Asynchronous
    public Object methodInterceptor(InvocationContext ctx) throws Exception {
        logger.log(Level.INFO, "Invoked function: " + ctx.getMethod().getName());
        return ctx.proceed();
    }
}
