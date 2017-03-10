package com.github.florent37.rxobserve.sample;

import com.github.florent37.rxobserve.annotations.Observe;

public class Calculator {

    private Calculator() {
    }

    @Observe
    public static int addOne(int number) {
        return number + 1;
    }

}
