package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticationTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleObserver;

public class UserService {


    public void getUser(String userAliasString, UserObserver observer){
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                userAliasString, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);
    }


    public void loginUser(String aliasString, String passwordString, UserObserver observer) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(aliasString.toString(),
                passwordString.toString(),
                new AuthenticationTaskHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }



    public void registerUser(String firstName, String lastName, String aliasName, String password, String imageBytesBase64
    , UserObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                aliasName, password, imageBytesBase64, new AuthenticationTaskHandler(observer));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
    }



    public void logout(SimpleObserver observer){
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new SimpleTaskHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }


}
