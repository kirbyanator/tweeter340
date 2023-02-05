package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.FollowService;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter {

    public interface View{
        void setLoadingFooter(boolean value);
        void addFollowers(List<User> followers);

        void displayMessage(String errorMessage);

        void addUser(User user);
    }
    private View view;

    private FollowService followService;
    private UserService userService;
    private boolean isLoading;
    private static final int PAGE_SIZE = 10;

    private User lastFollower;

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading(){
        return isLoading;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    private boolean hasMorePages;

    public GetFollowersPresenter(View view) {
        this.view = view;
        this.followService = new FollowService();
        this.userService = new UserService();
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(isLoading);
            followService.getFollowers(user, PAGE_SIZE, lastFollower, new GetFollowerObserver());
        }
    }

    private class GetFollowerObserver implements FollowService.GetFollowerObserver{

        @Override
        public void addFollowers(List<User> followers, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(isLoading);

            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addFollowers(followers);
        }

        @Override
        public void displayError(String errorMessage) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage(errorMessage);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get followers because of exception: " + ex.getMessage());
        }
    }


    public void getUserFromService(String aliasString) {
        userService.getUser(aliasString, new GetUserObserver());
    }

    private class GetUserObserver implements UserService.GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            view.addUser(user);
        }

        @Override
        public void handleFailure(String errorMessage) {
            view.displayMessage(errorMessage);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }
    }


}
