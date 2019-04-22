package mmp.filter;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class ConsumerFilterChain implements FilterChain, InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        doFilter();
        return null;
    }


    private Queue<Filter> chainList = new LinkedBlockingQueue<>();

    public void addFilter(Filter filter) {
        chainList.offer(filter);
    }

    @Override
    public void doFilter() throws Exception {
        Filter filter = nextFilter();
        if (filter != null) {
            nextFilter().invoke(this);
        }

    }

    private Filter nextFilter() {
        return chainList.poll();
    }
}
