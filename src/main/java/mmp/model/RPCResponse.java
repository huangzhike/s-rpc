package mmp.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RPCResponse {

    private String requestId;

    private Throwable error;

    private Object result;


}
