package mmp.window;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.LongAdder;



@Data
@Accessors(chain = true)
public class SlideCell {
    /**
     * 请求总量
     */
    private LongAdder total = new LongAdder();
    /**
     * 异常总量
     */
    private LongAdder exception = new LongAdder();
    /**
     * 计数开始时间
     */
    private long startTime;

    public SlideCell(long startTime) {
        this.startTime = startTime;
    }

    public void addErrorCount() {
        exception.add(1L);
    }

    public long getErrorCount() {
        return exception.sum();
    }

    public void addTotalCount() {
        total.add(1L);
    }

    public long getTotalCount() {
        return this.total.sum();
    }

    public SlideCell reset(long time) {
        total.reset();
        exception.reset();
        startTime = time;
        return this;
    }

    public SlideCell reset() {
        total.reset();
        exception.reset();
        return this;
    }
}
