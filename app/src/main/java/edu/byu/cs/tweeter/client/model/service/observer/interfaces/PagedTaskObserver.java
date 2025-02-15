package edu.byu.cs.tweeter.client.model.service.observer.interfaces;

import java.util.List;

public interface PagedTaskObserver<T> extends ServiceObserver{
    void handleSuccess(List<T> items, boolean hasMorePages);
}
