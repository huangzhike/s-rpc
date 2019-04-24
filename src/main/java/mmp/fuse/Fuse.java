package mmp.fuse;

import mmp.window.SlideWindowsManager;

/*
 * 推荐看看这篇文章 http://www.cnblogs.com/jager/p/6253166.html
 * */
public class Fuse {

    private int sleepTimeOut = 5 * 1000;

    private int leastRequestCount = 10;

    private double errorRatio = 0.5;

    public boolean tryPass(FuseState state) {
        // CLOSE状态，立即调用
        if (state.getState() == State.CLOSE) return true;
        // HALF_OPEN状态，停止调用
        if (state.getState() == State.HALF_OPEN) return false;
        // OPEN状态，如果超时，CAS尝试改为HALF_OPEN并调用
        if (state.getStateBeginTime() + sleepTimeOut < System.currentTimeMillis())
            return state.openToHalfOpen();
        return true;
    }


    // 成功调用的话，尝试CAS把HALF_OPEN->CLOSE，成功的话重置时间
    public void success(FuseState state) {
        if (state.halfOpenToClose()) state.resetStateBeginTime();
    }

    public void fail(FuseState state) {
        // HALF_OPEN调用失败，改为OPEN
        if (state.getState() == State.HALF_OPEN) tryHalfOpenToOpen(state);
            // CLOSE调用失败，判断是否超过错误百分比
        else if (state.getState() == State.CLOSE) tryCloseToOpen(state);
    }


    // 熔断，不重置计数器了
    private void tryCloseToOpen(FuseState state) {
        long totalRequestCount = SlideWindowsManager.getInstance().getTotalCount(state.getKey());
        // 大于leastRequestCount个请求才触发熔断逻辑
        if (totalRequestCount < leastRequestCount) return;
        // 错误率大于errorRatio则熔断
        if (SlideWindowsManager.getInstance().getErrorRatio(state.getKey()) >= errorRatio) {
            if (state.closeToOpen()) state.resetStateBeginTime();
        }
    }

    // 允许
    private void tryHalfOpenToOpen(FuseState state) {
        if (state.halfOpenToOpen()) state.resetStateBeginTime();
    }


    public static Fuse getInstance() {
        return defaultInstance;
    }

    private static final Fuse defaultInstance = new Fuse();

}
