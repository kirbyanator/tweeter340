package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.view.View;

public abstract class Presenter<T extends View> {
    protected T view;

    public View getView(){
        return view;
    }
}