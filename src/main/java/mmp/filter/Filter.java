package mmp.filter;

public interface Filter<I, O> {

    void filter(I input, O output, FilterChain<I, O> chain) throws Exception;

}