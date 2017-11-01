package com.androidstudy.andelatrackchallenge;

import com.uber.autodispose.LifecycleScopeProvider;
import com.uber.autodispose.OutsideLifecycleException;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

import static com.androidstudy.andelatrackchallenge.ActivityEvent.DESTROY;
import static com.androidstudy.andelatrackchallenge.ActivityEvent.PAUSE;
import static com.androidstudy.andelatrackchallenge.ActivityEvent.STOP;

/**
 * Created by anonymous on 11/2/17.
 */

public class RxActivity extends BaseActivity implements LifecycleScopeProvider<ActivityEvent> {
    private static final Function<ActivityEvent, ActivityEvent> CORRESPONDING_EVENTS =
            lastEvent -> {
                switch (lastEvent) {
                    case CREATE:
                        return DESTROY;
                    case START:
                        return STOP;
                    case RESUME:
                        return PAUSE;
                    case PAUSE:
                    case STOP:
                        return DESTROY;
                    case DESTROY:
                        throw new OutsideLifecycleException("Activity is already destroyed!");
                }
                throw new IllegalStateException("State not allowed");
            };

    private BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

    @Override
    public Observable<ActivityEvent> lifecycle() {
        return lifecycle.hide();
    }

    @Override
    public Function<ActivityEvent, ActivityEvent> correspondingEvents() {
        return CORRESPONDING_EVENTS;
    }

    @Nullable
    @Override
    public ActivityEvent peekLifecycle() {
        return lifecycle.getValue();
    }
}

