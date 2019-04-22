package mmp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;


@Data
@Accessors(chain = true)
public class RPCRequest {

    private String requestId;
    private String className;

    private Method method;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;


}
