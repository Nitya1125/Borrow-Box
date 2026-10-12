package com.borrowbox.app.data.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T result);
    void onError(Throwable throwable);
}
