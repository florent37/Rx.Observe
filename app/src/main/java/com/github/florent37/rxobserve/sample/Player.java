package com.github.florent37.rxobserve.sample;

import com.github.florent37.rxobserve.annotations.Completable;
import com.github.florent37.rxobserve.annotations.Flowable;
import com.github.florent37.rxobserve.annotations.Observe;
import com.github.florent37.rxobserve.annotations.Single;

@Single
public class Player {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static void setUserStatic(User user) {

    }

}
