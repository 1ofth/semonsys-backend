package com.semonsys.server.interceptor;

import lombok.extern.log4j.Log4j;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

@Log4j
public class MethodParamsInterceptor {
    @AroundInvoke
    private Object aroundMethod(InvocationContext context) throws Exception{
        int i = 1;
        log.info("Method '" + context.getMethod().getName() + "' was called with params");
        for(Object o : context.getParameters()){
            log.info("param " + i + " [" + o + "]");
            i+=1;
        }

        return context.proceed();
    }
}
