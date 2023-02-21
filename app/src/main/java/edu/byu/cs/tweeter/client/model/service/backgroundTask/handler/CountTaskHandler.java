package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.observer.GetCountObserver;

public class CountTaskHandler extends BackgroundTaskHandler<GetCountObserver>{
    public CountTaskHandler(GetCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(GetCountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
