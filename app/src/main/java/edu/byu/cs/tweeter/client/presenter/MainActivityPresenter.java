package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.services.FollowService;
import edu.byu.cs.tweeter.client.model.services.StatusService;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter {
    

    public interface View {

        void updateFollowButton(boolean isFollower);

        void displayMessage(String s);

        void updateSelectedUserFollowingAndFollowers();

        void enableFollowButton(boolean b);

        void logoutUser();

        void postSuccessful();

        void setFollowersCount(int count);

        void setFollowingCount(int count);
    }

    private View view;
    private FollowService followService;
    private UserService userService;
    private StatusService statusService;

    public MainActivityPresenter(View view) {
        this.view = view;
        this.followService = new FollowService();
        this.userService = new UserService();
        this.statusService = new StatusService();
    }

    public void checkIsFollower(User selectedUser) {
        followService.checkIsFollower(selectedUser, new IsFollowerObserver());
    }

    private class IsFollowerObserver implements FollowService.IsFollowerObserver{

        @Override
        public void handleSuccess(boolean isFollower) {

            view.updateFollowButton(!isFollower);
        }

        @Override
        public void handleFailure(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }
    }


    public void unfollowUser(User selectedUser) {
        followService.unfollowUser(selectedUser, new UnfollowObserver());
    }

    private class UnfollowObserver implements FollowService.UnfollowObserver{

        @Override
        public void handleSuccess(boolean b) {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(true);
        }

        @Override
        public void handleFailure(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());
        }

        @Override
        public void enableFollowButton(boolean b) {
            view.enableFollowButton(true);
        }
    }


    public void followUser(User selectedUser) {
        followService.followUser(selectedUser, new FollowObserver());
    }

    private class FollowObserver implements FollowService.FollowObserver{
        @Override
        public void handleSuccess() {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(false);
        }

        @Override
        public void handleFailure(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to follow because of exception: " + ex.getMessage());
        }

        @Override
        public void enableFollowButton(boolean b) {
            view.enableFollowButton(true);
        }
    }


    public void logOut() {
        userService.logout(new LogoutObserver());
    }

    private class LogoutObserver implements UserService.LogoutObserver{

        @Override
        public void handleSuccess() {
            view.logoutUser();
        }

        @Override
        public void handleFailure(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to logout because of exception: " + ex.getMessage());
        }
    }

    public void postStatus(Status newStatus) {
        statusService.postStatus(newStatus, new PostStatusObserver());
    }

    private class PostStatusObserver implements StatusService.PostStatusObserver{

        @Override
        public void handleSuccess() {
            view.postSuccessful();
        }

        @Override
        public void handleFailure(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to post status because of exception: " + ex.getMessage());
        }
    }

    public void getFollowersCount(User user){
        followService.getFollowerCount(user, new FollowerCountObserver());
    }

    private class FollowerCountObserver implements FollowService.FollowerCountObserver{

        @Override
        public void handleSuccess(int count) {
            view.setFollowersCount(count);
        }

        @Override
        public void handleFailure(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get followers count because of exception: " + ex.getMessage());
        }
    }
    
    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(selectedUser, new FollowingCountObserver());
    }
    
    private class FollowingCountObserver implements FollowService.FollowingCountObserver{

        @Override
        public void handleSuccess(int count) {
            view.setFollowingCount(count);
        }

        @Override
        public void handleFailure(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get following count because of exception: " + ex.getMessage());
        }
    }

}