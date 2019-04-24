package mmp;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RPCConfig {

    public static final Integer DEFAULT_TIMEOUT = 10;

}
