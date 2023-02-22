package edu.byu.cs.tweeter.client.model.service.observer.concrete;

import edu.byu.cs.tweeter.client.model.service.observer.interfaces.ServiceObserver;
import edu.byu.cs.tweeter.client.presenter.Presenter;
import edu.byu.cs.tweeter.client.presenter.view.View;

public abstract class BaseObserver implements ServiceObserver {

    protected Presenter<View> presenter;

    public BaseObserver(Presenter<View> presenter){
        this.presenter = presenter;
    }

    @Override
    public void handleFailure(String message) {
        presenter.getView().displayMessage("Failed to " + getErrorMessage() + ": " + message);
    }

    @Override
    public void handleException(Exception exception) {
        presenter.getView().displayMessage("Failed to " + getErrorMessage() + " because of exception " + exception.getMessage());
    }


    public abstract String getErrorMessage();
}
