package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.client.presenter.view.PagedView;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedView<T>> {
    protected static final int PAGE_SIZE = 10;

    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading = false;

    protected UserService userService;

    public PagedPresenter(){
        this.userService = new UserService();
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

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            getItems(user, PAGE_SIZE, lastItem);
        }
    }

    public abstract void getItems(User user, int PAGE_SIZE, T lastItem);

    protected class PageObserver implements PagedTaskObserver<T> {

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(isLoading);

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems(items);
        }

        @Override
        public void handleFailure(String errorMessage) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get " + getPresenterType() + ": " +  errorMessage);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get " + getPresenterType() + " because of exception: " + ex.getMessage());
        }
    }

    public void getUserFromService(String aliasString) {
        userService.getUser(aliasString, new GetUserObserver());
    }

    protected class GetUserObserver implements UserObserver {

        @Override
        public void handleSuccess(User user) {
            view.addUser(user);
        }

        @Override
        public void handleFailure(String errorMessage) {
            view.displayMessage("Failed to get user's profile " + errorMessage);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }
    }

    public abstract String getPresenterType();

}
