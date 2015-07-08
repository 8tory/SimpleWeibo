# SimpleWeibo

[![Build Status](https://travis-ci.org/8tory/SimpleWeibo.svg)](https://travis-ci.org/8tory/SimpleWeibo)

![](art/SimpleWeibo.png)

Simple Weibo SDK turns Weibo API into a Java interface with RxJava.

![](art/screenshot-timeline.png)

## Usage

My posts:

```java
weibo = SimpleWeibo.create(activity);

Observable<Post> myPosts = weibo.getPosts();
myPosts.take(100).forEach(System.out::println);
```

logIn (default permissions):

```java
weibo.logIn().subscribe();
```

logInWithPermissions:

```java
weibo.logInWithPermissions(Arrays.asList("email", "statuses_to_me_read")).subscribe();
```

## Integration

AndroidManifest.xml:

```xml
<meta-data android:name="com.sina.weibo.sdk.ApplicationId" android:value="@string/weibo_app_id" />
<meta-data android:name="com.sina.weibo.sdk.RedirectUrl" android:value="@string/weibo_redirect_url" />
```

Activity:

```java
SimpleWeibo weibo;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    weibo = SimpleWeibo.create(activity);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    weibo.onActivityResult(requestCode, resultCode, data);
}
```

## Add API using RetroWeibo

```java
@RetroWeibo
abstract class Weibo {
    @GET("/statuses/user_timeline.json")
    abstract Observable<Post> getPosts();
}
```

## Installation

via jitpack:

```gradle
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
  compile 'com.github.8tory:simpleweibo:1.0.0'
}
```

via jcenter(in progress):

```gradle
repositories {
    jcenter()
}

dependencies {
  compile 'com.infstory:simpleweibo:1.0.0'
}
```

## See Also

* http://open.weibo.com/wiki/

## License

```
Copyright 2015 8tory, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
