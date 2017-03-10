package com.github.florent37.rxobserve.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.florent37.rxobserve.Rx;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = new User();

        Rx.observe(user).setAge(user, 3)
                .flatMap(u -> Rx.observe(u).getAge())
                .flatMap(age -> Rx.observeCalculator().addOne(age))

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(integer -> {
                    Toast.makeText(getBaseContext(), "" + integer, Toast.LENGTH_SHORT).show();
                });
    }

}
