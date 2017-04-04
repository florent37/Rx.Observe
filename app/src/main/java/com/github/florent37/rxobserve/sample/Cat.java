package com.github.florent37.rxobserve.sample;

import com.github.florent37.rxobserve.annotations.Observe;
import com.github.florent37.rxobserve.annotations.Single;

@Single
public class Cat {

    private int age;

    @Single
    public int getSingleAge() {
        return age;
    }

}
