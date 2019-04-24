package mmp.fuse;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicReference;


@Data
@Accessors(chain = true)
public class FuseState {

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSE);

    private String key;

    private volatile long stateBeginTime;

    public FuseState(String key) {
        this.key = key;
        this.stateBeginTime = System.currentTimeMillis();
    }

    public State getState() {
        return state.get();
    }

    public boolean resetStateBeginTime() {
        this.stateBeginTime = System.currentTimeMillis();
        return true;
    }


    public boolean halfOpenToOpen() {
        return state.compareAndSet(State.HALF_OPEN, State.OPEN);
    }

    public boolean openToHalfOpen() {
        return state.compareAndSet(State.OPEN, State.HALF_OPEN);
    }

    public boolean closeToOpen() {
        return state.compareAndSet(State.CLOSE, State.OPEN);
    }

    public boolean halfOpenToClose() {
        return state.compareAndSet(State.HALF_OPEN, State.CLOSE);
    }


}
