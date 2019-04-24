package mmp.filter;

import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@Accessors(chain = true)
public class RPCFilterChain<I, O> implements FilterChain<I, O>, InvocationHandler {

    private Object proxy;

    private Method method;

    private Object[] args;

    private I input;
    private O output;

    private Queue<Filter<I, O>> chainList = new LinkedBlockingQueue<>();

    public RPCFilterChain(Object proxy, Method method, Object[] args) {
        setProxy(proxy);
        setMethod(method);
        setArgs(args);
    }

    public RPCFilterChain(I input, O output) {
        setInput(input);
        setOutput(output);
    }

    @Override
    public O invoke(Object proxy, Method method, Object[] args) throws Exception {

        setProxy(proxy);
        setMethod(method);
        setArgs(args);

        I input = null;
        O output = null;
        beforeFilters();
        doFilter(input, output);
        afterFilters();
        return output;
    }


    @Override
    public void beforeFilters() {

    }

    @Override
    public void afterFilters() {

    }


    @Override
    public RPCFilterChain<I, O> addFilter(Filter<I, O> filter) {
        chainList.offer(filter);
        return this;
    }

    @Override
    public void doFilter(I input, O output) throws Exception {
        Filter<I, O> filter = nextFilter();
        if (filter != null) {
            filter.filter(input, output, this);
        }

    }

    @Override
    public Filter<I, O> nextFilter() {
        return chainList.poll();
    }


}
