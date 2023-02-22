package edu.byu.cs.tweeter.client.presenter;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.concrete.BaseObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.BooleanObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.SimpleObserver;
import edu.byu.cs.tweeter.client.presenter.view.MainView;
import edu.byu.cs.tweeter.client.presenter.view.View;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter extends Presenter<MainView> {

    private final FollowService followService;
    private final UserService userService;
    private final StatusService statusService;

    public MainActivityPresenter(MainView view) {
        this.view = view;
        this.followService = new FollowService();
        this.userService = new UserService();
        this.statusService = new StatusService();
    }

    public void checkIsFollower(User selectedUser) {
        followService.checkIsFollower(selectedUser, new IsFollowerObserver(this));
    }

    private class IsFollowerObserver extends BaseObserver<MainActivityPresenter> implements BooleanObserver {

        public IsFollowerObserver(MainActivityPresenter presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess(boolean isFollower) {
            view.updateFollowButton(!isFollower);
        }


        @Override
        public String getErrorMessage() {
            return "determine following relationship";
        }
    }


    public void unfollowUser(User selectedUser) {
        followService.unfollowUser(selectedUser, new UnfollowObserver(this));
    }

    private class UnfollowObserver extends BaseObserver<MainActivityPresenter> implements SimpleObserver{

        public UnfollowObserver(MainActivityPresenter presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess() {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(true);
            view.enableFollowButton(true);
        }

        @Override
        public void handleFailure(String s) {
            super.handleFailure(s);
            view.enableFollowButton(true);
        }

        @Override
        public void handleException(Exception ex) {
            super.handleException(ex);
            view.enableFollowButton(true);
        }

        @Override
        public String getErrorMessage() {
            return "unfollow";
        }

    }


    public void followUser(User selectedUser) {
        followService.followUser(selectedUser, new FollowObserver(this));
    }

    private class FollowObserver extends BaseObserver<MainActivityPresenter> implements SimpleObserver{
        public FollowObserver(MainActivityPresenter presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess() {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(false);
            view.enableFollowButton(true);
        }

        @Override
        public void handleFailure(String s) {
            super.handleFailure(s);
            view.enableFollowButton(true);
        }

        @Override
        public void handleException(Exception ex) {
            super.handleException(ex);
            view.enableFollowButton(true);
        }

        @Override
        public String getErrorMessage() {
            return "follow";
        }

    }


    public void logOut() {
        userService.logout(new LogoutObserver(this));
    }

    private class LogoutObserver extends BaseObserver<MainActivityPresenter> implements SimpleObserver {

        public LogoutObserver(MainActivityPresenter presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess() {
            view.logoutUser();
        }


        @Override
        public String getErrorMessage() {
            return "logout";
        }
    }

    public void postStatus(String post) {
        Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
        statusService.postStatus(newStatus, new PostStatusObserver(this));
    }

    private class PostStatusObserver extends BaseObserver<MainActivityPresenter> implements SimpleObserver{

        public PostStatusObserver(MainActivityPresenter presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess() {
            view.postSuccessful();
        }


        @Override
        public String getErrorMessage() {
            return "post status";
        }
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public void getFollowersCount(User user){
        followService.getFollowerCount(user, new FollowerCountObserver(this));
    }

    private class FollowerCountObserver extends BaseObserver<MainActivityPresenter> implements CountObserver {

        public FollowerCountObserver(MainActivityPresenter presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess(int count) {
            view.setFollowersCount(count);
        }

        @Override
        public String getErrorMessage() {
            return "get followers";
        }
    }
    
    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(selectedUser, new FollowingCountObserver(this));
    }
    
    private class FollowingCountObserver extends BaseObserver<MainActivityPresenter> implements CountObserver{

        public FollowingCountObserver(MainActivityPresenter presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess(int count) {
            view.setFollowingCount(count);
        }


        @Override
        public String getErrorMessage() {
            return "get following count";
        }
    }

}