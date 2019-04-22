package mmp;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RPCResponse<T> {

    private String requestId;

    private Throwable error;

    private T result;
}
