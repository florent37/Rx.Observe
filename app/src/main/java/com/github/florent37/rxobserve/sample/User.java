package com.github.florent37.rxobserve.sample;

import com.github.florent37.rxobserve.annotations.Observe;

public class User {

    private int age;

    @Observe
    public int getAge() {
        return age;
    }

    @Observe
    public void setAge(int age) {
        this.age = age;
    }
}
