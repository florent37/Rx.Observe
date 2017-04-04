package com.github.florent37.rxobserve.sample;

import com.github.florent37.rxobserve.annotations.Completable;
import com.github.florent37.rxobserve.annotations.Flowable;
import com.github.florent37.rxobserve.annotations.Observe;
import com.github.florent37.rxobserve.annotations.Single;

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

    @Single
    public int getSingleAge() {
        return age;
    }

    @Flowable
    public int getTestAge() {
        return age;
    }

    @Completable
    public void finished() {

    }

    @Completable
    public int finished2() { //will not return an integer
        return 0;
    }
}
