package com.github.florent37.rxobserve.sample;

import com.github.florent37.rxobserve.annotations.Observe;

@Observe
public class Player {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
