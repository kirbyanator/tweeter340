package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter {

    private static final int PAGE_SIZE = 10;

    private User lastFollowee;
    private boolean hasMorePages;
    private boolean isLoading = false;


    public interface View{
        void setLoadingFooter(boolean value);
        void displayMessage(String message);
        void addMoreItems(List<User> followees);

        void addUser(User user);
    }

    private final View view;

    private final FollowService followService;
    private final UserService userService;

    public FollowingPresenter(View view){
        this.view = view;
        this.followService = new FollowService();
        this.userService = new UserService();
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            followService.getFollowees(user, PAGE_SIZE, lastFollowee, new FollowingObserver());
        }
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    private class FollowingObserver implements PagedTaskObserver<User> {

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess(List<User> followees, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(isLoading);

            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems(followees);
        }
    }

    public void getUserFromService(String userAliasString) {
        userService.getUser(userAliasString, new GetUserObserver());
    }

    private class GetUserObserver implements UserObserver {

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
