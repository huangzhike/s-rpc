package mmp.flow;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class SemaphoreManager {

    private final Integer MAX_LIMIT = 10;

    private final ConcurrentHashMap<String, Semaphore> semaphoreConcurrentHashMap = new ConcurrentHashMap<>();

    public Semaphore getSemaphore(String key) {
        Semaphore semaphore = semaphoreConcurrentHashMap.get(key);
        if (semaphore == null) {
            semaphore = new Semaphore(MAX_LIMIT);
            Semaphore old = semaphoreConcurrentHashMap.putIfAbsent(key, semaphore);
            if (old != null) {
                semaphore = old;
            }
        }
        return semaphore;
    }


    public void release(String key) {
        getSemaphore(key).release();
    }

    public void acquire(String key) throws Exception {
        getSemaphore(key).acquire();
    }

    public boolean tryAcquire(String key) {
        return getSemaphore(key).tryAcquire();
    }


    public static SemaphoreManager getInstance() {
        return SemaphoreManagerLazyHolder.INSTANCE;
    }

    private static class SemaphoreManagerLazyHolder {
        private static final SemaphoreManager INSTANCE = new SemaphoreManager();
    }

}
