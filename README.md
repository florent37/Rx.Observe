# Rx.Observe

**RxJava2 ready !**

Create an interface with `@Observe` annotated method
```
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
```

Will generate 2 observables

`Observable<Integer> getAge()`

`Observable<T> setAge(T toReturn, int age)`

setAge is a `void` method so it will return the first `T toReturn` argument (useful for your flatmaps)

Those observable are accessible through `Rx.observe(user)`

# Static

```
public class Calculator {

    @Observe
    public static int addOne(int number) {
        return number + 1;
    }

}
```

Will generate

`Observable<T> addOne(int age)`

Accessible through `Rx.observeCalculator()`

# Example

```java
User user = new User();

Rx.observe(user).setAge(user, 3)
         .flatMap(u -> Rx.observe(u).getAge())
         .flatMap(age -> Rx.observeCalculator().addOne(age))

         .subscribeOn(Schedulers.newThread())
         .observeOn(AndroidSchedulers.mainThread())

         .subscribe(integer -> {
             Toast.makeText(getBaseContext(), "" + integer, Toast.LENGTH_SHORT).show();
         });
```

# RxJava2

Rx.Observable is also compatible with `@Single`, `@Flowable`, `@Completable`

# Download

<a href='https://ko-fi.com/A160LCC' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi1.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

In your module [![Download](https://api.bintray.com/packages/florent37/maven/rxobserve-compiler/images/download.svg)](https://bintray.com/florent37/maven/rxobserve-compiler/_latestVersion)

```groovy
repositories {
    google()
    jcenter()
    maven { url "https://dl.bintray.com/florent37/maven" }
    // ...
}
```

```groovy
provided 'com.github.florent37:rxobserve-annotations:1.0.0'
annotationProcessor 'com.github.florent37:rxobserve-compiler:1.0.0'

compile 'io.reactivex:rxjava2:2.0.7'
```
# Credits

Author: Florent Champigny [http://www.florentchampigny.com/](http://www.florentchampigny.com/)

Blog : [http://www.tutos-android-france.com/](http://www.www.tutos-android-france.com/)

<a href="https://plus.google.com/+florentchampigny">
  <img alt="Follow me on Google+"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/gplus.png" />
</a>
<a href="https://twitter.com/florent_champ">
  <img alt="Follow me on Twitter"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/twitter.png" />
</a>
<a href="https://www.linkedin.com/in/florentchampigny">
  <img alt="Follow me on LinkedIn"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/linkedin.png" />
</a>

# License

    Copyright 2017 florent37, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
