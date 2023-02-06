package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.services.StatusService;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter {
    public interface View{

        void setLoadingFooter(boolean value);

        void displayMessage(String message);

        void addMoreItems(List<Status> statuses);

        void addUser(User user);
    }
    private View view;

    private StatusService statusService;
    private UserService userService;

    private static final int PAGE_SIZE = 10;

    private Status lastStatus;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public StoryPresenter(View view){
        this.view = view;
        this.userService = new UserService();
        this.statusService = new StatusService();
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            statusService.getStory(user, PAGE_SIZE, lastStatus, new StoryObserver());
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

    private class StoryObserver implements StatusService.StoryObserver {

        @Override
        public void handleError(String message) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage(message);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get story because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
            lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            setHasMorePages(hasMorePages);
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.addMoreItems(statuses);
        }
    }


    public void getUserFromService(String aliasName) {
        userService.getUser(aliasName, new GetUserObserver());
    }

    private class GetUserObserver implements UserService.UserObserver {

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
