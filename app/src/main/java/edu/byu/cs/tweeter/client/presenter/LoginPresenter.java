package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter {

    public interface View{

        void prepLoginText();

        void loginSuccess(User loggedInUser);

        void displayMessage(String errorMessage);

    }

    private View view;

    private UserService userService;

    public LoginPresenter(View view){
        this.view = view;
        this.userService = new UserService();
    }

    public void logIn(String aliasString, String passwordString) {
        validateLogin(aliasString, passwordString);
        view.prepLoginText();
        userService.loginUser(aliasString, passwordString, new LoginObserver());
    }

    public void validateLogin(String aliasString, String passwordString) {
        if (aliasString.length() > 0 && aliasString.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (aliasString.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (passwordString.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public class LoginObserver implements UserObserver {

        @Override
        public void handleSuccess(User loggedInUser) {
            view.loginSuccess(loggedInUser);
        }

        @Override
        public void handleFailure(String errorMessage) {
            view.displayMessage("Failed to login: " + errorMessage);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to login because of exception: " + ex.getMessage());
        }
    }

}
