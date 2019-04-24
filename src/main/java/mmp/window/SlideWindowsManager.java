package mmp.window;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlideWindowsManager {


    private final Map<String, SlideWindow> slideWindowConcurrentHashMap = new ConcurrentHashMap<>();


    private static final SlideWindowsManager defaultInstance = new SlideWindowsManager();


    public static SlideWindowsManager getInstance() {
        return defaultInstance;
    }


    public void addErrorCount(String key) {
        SlideWindow slideWindow = getSlideWindow(key);
        SlideCell slideCell = slideWindow.getCurrentCell();
        slideCell.addErrorCount();
    }


    public void addTotalCount(String key) {
        SlideWindow slideWindow = getSlideWindow(key);
        SlideCell slideCell = slideWindow.getCurrentCell();
        slideCell.addTotalCount();
    }


    public long getErrorCount(String key) {
        SlideWindow slideWindow = getSlideWindow(key);
        List<SlideCell> list = slideWindow.listCell();
        long count = 0;
        for (SlideCell slideCell : list) {
            count += slideCell.getErrorCount();
        }
        return count;
    }


    public long getTotalCount(String key) {
        SlideWindow sw = getSlideWindow(key);
        List<SlideCell> list = sw.listCell();
        long total = 0;
        for (SlideCell slideCell : list) {
            total += slideCell.getTotalCount();
        }
        return total;
    }


    public double getErrorRatio(String key) {
        SlideWindow slideWindow = getSlideWindow(key);
        List<SlideCell> list = slideWindow.listCell();
        long total = 0;
        long exception = 0;
        for (SlideCell slideCell : list) {
            total += slideCell.getTotalCount();
            exception += slideCell.getErrorCount();
        }
        return total == 0 ? 0.0 : (double) exception / (double) total;
    }

    public void resetWindowCell(String key) {
        SlideWindow slideWindow = getSlideWindow(key);
        slideWindow.resetCellArray();
    }

    private SlideWindow getSlideWindow(String key) {
        SlideWindow slideWindow = slideWindowConcurrentHashMap.get(key);
        if (slideWindow == null) {
            slideWindow = new SlideWindow();
            SlideWindow old = slideWindowConcurrentHashMap.putIfAbsent(key, slideWindow);
            if (old != null) slideWindow = old;
        }
        return slideWindow;
    }

}
