package com.cloudhearing.android.lib_common.network.callfactory;


import com.cloudhearing.android.lib_common.network.annotation.Timeout;

import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.Request;
import retrofit2.Invocation;


/**
 * https://github.com/square/retrofit/issues/2982
 */
public class CallFactoryProxy implements Call.Factory {

    protected final Call.Factory delegate;

    public CallFactoryProxy(Call.Factory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call newCall(Request request) {
        Call call = delegate.newCall(request);
        Invocation tag = request.tag(Invocation.class);
        Method method = tag.method();
        Timeout timeout = method.getAnnotation(Timeout.class);
        if (timeout != null) {
            call.timeout().timeout(timeout.value(), timeout.unit());
        }
        return call;
    }
}
