package mmp.window;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;


@Data
@Accessors(chain = true)
public class SlideWindow {

    // 格子间隔时间 ms
    private int cellLength = 10;

    // 格子数量
    private int cellCount = 10;

    // 窗口间隔
    private int windowTime = cellLength * cellCount;

    private AtomicReferenceArray<SlideCell> slideCellArray = new AtomicReferenceArray<>(cellCount);

    private final ReentrantLock lock = new ReentrantLock();

    public SlideCell getCurrentCell() {
        // 当前时间 ms
        long now = System.currentTimeMillis();
        // 归属的下标
        int idx = (int) (now % cellCount);
        // 当前时间所归属格子的开始时间
        long startTime = now - now % cellLength;

        for (; ; ) {
            SlideCell oldCell = slideCellArray.get(idx);
            // 当前时间段内还没有格子
            if (oldCell == null) {
                oldCell = new SlideCell(startTime);
                if (slideCellArray.compareAndSet(idx, null, oldCell)) {
                    return oldCell;
                }
            }
            // 已经有了
            else if (startTime == oldCell.getStartTime()) {
                return oldCell;
            }
            // 格子划过了
            else if (startTime > oldCell.getStartTime()) {
                if (lock.tryLock()) {
                    try {
                        // 重置
                        return oldCell.reset(startTime);
                    } finally {
                        lock.unlock();
                    }
                }
            } else if (startTime < oldCell.getStartTime()) {
                throw new RuntimeException("???");
            }
        }
    }

    public List<SlideCell> listCell() {
        List<SlideCell> list = new ArrayList<>();
        for (int i = 0; i < slideCellArray.length(); i++) {
            SlideCell slideCell = slideCellArray.get(i);
            // 存在 && 在当前的时间窗口中
            if (slideCell != null && slideCell.getStartTime() + windowTime >= System.currentTimeMillis()) {
                list.add(slideCell);
            }
        }
        return list;
    }

    public void resetCellArray() {
        for (int i = 0; i < slideCellArray.length(); i++) {
            SlideCell slideCell = slideCellArray.get(i);
            if (slideCell != null) slideCell.reset();
        }
    }
}
