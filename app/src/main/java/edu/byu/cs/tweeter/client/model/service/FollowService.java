package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public interface FollowingObserver {
        void handleError(String message);
        void handleException(Exception ex);

        void handleSuccess(List<User> followees, boolean hasMorePages);
    }

    public void getFollowees(User user, int pageSize, User lastFollowee, FollowingObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }


    public interface FollowerObserver {

        void handleSuccess(List<User> followers, boolean hasMorePages);

        void handleError(String errorMessage);

        void handleException(Exception ex);
    }

    public void getFollowers(User user, int pageSize, User lastFollower, FollowerObserver getFollowerObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new GetFollowersHandler(getFollowerObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    public interface IsFollowerObserver {

        void handleSuccess(boolean isFollower);

        void handleFailure(String s);

        void handleException(Exception ex);
    }

    public void checkIsFollower(User user, IsFollowerObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), user, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }
    // IsFollowerHandler

    public interface UnfollowObserver{

        void handleSuccess();

        void handleFailure(String s);

        void handleException(Exception ex);
    }

    public void unfollowUser(User user, UnfollowObserver observer){
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new UnfollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    // UnfollowHandler

    public interface FollowObserver{

        void handleSuccess();

        void handleFailure(String s);

        void handleException(Exception ex);

    }

    public void followUser(User user, FollowObserver observer){
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new FollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    public interface FollowerCountObserver{

        void handleSuccess(int count);

        void handleFailure(String s);

        void handleException(Exception ex);
    }

    public void getFollowerCount(User user, FollowerCountObserver observer) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetFollowersCountHandler(observer));
        executor.execute(followersCountTask);
    }


    public interface FollowingCountObserver{

        void handleSuccess(int count);

        void handleFailure(String s);

        void handleException(Exception ex);
    }

    public void getFollowingCount(User user, FollowingCountObserver observer){
        ExecutorService executor = Executors.newFixedThreadPool(2);
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetFollowingCountHandler(observer));
        executor.execute(followingCountTask);
    }

}
