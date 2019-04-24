package mmp.filter;


import java.lang.reflect.Method;

public interface FilterChain<I, O> {

    void beforeFilters();

    void doFilter(I input, O output) throws Exception;

    void afterFilters();

    FilterChain<I, O> addFilter(Filter<I, O> filter);

    Filter<I, O> nextFilter();
}