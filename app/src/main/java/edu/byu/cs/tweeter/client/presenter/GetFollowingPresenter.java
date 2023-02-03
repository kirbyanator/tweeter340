package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.services.FollowService;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter {

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

    public GetFollowingPresenter(View view){
        this.view = view;
        this.followService = new FollowService();
        this.userService = new UserService();
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            followService.loadMoreItems(user, PAGE_SIZE, lastFollowee, new GetFollowingObserver());
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

    private class GetFollowingObserver implements FollowService.Observer{

        @Override
        public void displayError(String message) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
        }

        @Override
        public void addFollowees(List<User> followees, boolean hasMorePages) {
            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            setHasMorePages(hasMorePages);
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.addMoreItems(followees);
        }
    }

    public void getUserFromService(String userAliasString) {
        userService.getUser(userAliasString, new GetUserObserver());
    }

    private class GetUserObserver implements UserService.Observer{

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
