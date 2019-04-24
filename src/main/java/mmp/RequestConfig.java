package mmp;

import lombok.Data;
import lombok.experimental.Accessors;
import mmp.balance.ILoadBalance;
import mmp.balance.LoadBalance;


@Data
@Accessors(chain = true)
public class RequestConfig {

    private ILoadBalance loadBalance = LoadBalance.RANDOM.loadBalance;

}
