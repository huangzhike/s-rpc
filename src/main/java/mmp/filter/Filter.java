package mmp.filter;

public interface Filter {

    void invoke(FilterChain chain) throws Exception;

}