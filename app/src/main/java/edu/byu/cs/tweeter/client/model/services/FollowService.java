package edu.byu.cs.tweeter.client.model.services;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public interface GetFollowingObserver {
        void displayError(String message);
        void displayException(Exception ex);

        void addFollowees(List<User> followees, boolean hasMorePages);
    }

    public void getFollowees(User user, int pageSize, User lastFollowee, GetFollowingObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends Handler {

        private GetFollowingObserver observer;

        public GetFollowingHandler(GetFollowingObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);

                observer.addFollowees(followees, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
                observer.displayError("Failed to get following: " + message);
            } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.displayException(ex);
            }
        }
    }


    public interface GetFollowerObserver{

        void handleSuccess(List<User> followers, boolean hasMorePages);

        void displayError(String errorMessage);

        void displayException(Exception ex);
    }

    public void getFollowers(User user, int pageSize, User lastFollower, GetFollowerObserver getFollowerObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new GetFollowersHandler(getFollowerObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends Handler {

        private GetFollowerObserver observer;

        public GetFollowersHandler(GetFollowerObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                observer.handleSuccess(followers, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.displayError("Failed to get followers: " + message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.displayException(ex);
            }
        }
    }
}