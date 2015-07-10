# SimpleWeibo

[![Build Status](https://travis-ci.org/8tory/SimpleWeibo.svg)](https://travis-ci.org/8tory/SimpleWeibo)

[![](https://avatars0.githubusercontent.com/u/5761889?v=3&s=48)](https://github.com/Wendly)
[![](https://avatars3.githubusercontent.com/u/213736?v=3&s=48)](https://github.com/yongjhih)
Contributors..

![](art/SimpleWeibo.png)

Simple Weibo SDK turns Weibo API into a Java interface with RxJava.

[#Demo](#demo)

![](art/screenshot-timeline.png)

## Usage

My posts:

```java
weibo = SimpleWeibo.create(activity);

Observable<Status> myStatuses = weibo.getStatuses();
myStatuses.take(10).forEach(System.out::println);
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

Ready API:

[SimpleWeibo.java](simpleweibo/src/main/java/com/sina/weibo/simple/SimpleWeibo.java):

```java
    @RetroWeibo.GET("/statuses/friends_timeline.json")
    public abstract Observable<Status> getStatuses(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("base_app") boolean baseApp,
        @RetroWeibo.Query("trim_user") boolean trimUser,
        @RetroWeibo.Query("feature") int featureType
    );

    public Observable<Status> getStatuses() {
        // ...
    }

    @RetroWeibo.GET("/mentions.json")
    public abstract Observable<Status> getMentionedStatuses(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_author") int filterByAuthor,
        @RetroWeibo.Query("filter_by_source") int filterBySource,
        @RetroWeibo.Query("filter_by_type") int filterByType,
        @RetroWeibo.Query("trim_user") boolean trimUser
    );

    public Observable<Status> getMentionedStatuses() {
        // ...
    }

    @RetroWeibo.GET("/users/show.json")
    public abstract Observable<User> getUsersById(@RetroWeibo.Query("uid") long uid);

    @RetroWeibo.GET("/users/show.json")
    public abstract Observable<User> getUsersByName(@RetroWeibo.Query("screen_name") String screenName);

    @RetroWeibo.GET("/users/domain_show.json")
    public abstract Observable<User> getUsersByDomain(@RetroWeibo.Query("domain") String domain);

    @RetroWeibo.GET("/users/counts.json")
    public abstract Observable<User> getUsersCount(@RetroWeibo.Query("uids") long[] uids);

    @RetroWeibo.GET("/comments/show.json")
    public abstract Observable<Comment> getCommentsById(
        @RetroWeibo.Query("id") int id,
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_author") int filterByAuthor
    );

    public Observable<Comment> getCommentsById(int id) {
        // ...
    }

    @RetroWeibo.GET("/comments/by_me.json")
    public abstract Observable<Comment> getCommentsByMe(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_source") int filterBySource
    );

    public Observable<Comment> getCommentsByMe() {
        // ...
    }

    public Observable<Comment> getCommentsByMe(int filterBySource) {
        // ...
    }

    @RetroWeibo.GET("/comments/to_me.json")
    public abstract Observable<Comment> getCommentsToMe(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_author") int filterByAuthor,
        @RetroWeibo.Query("filter_by_source") int filterBySource
    );

    public Observable<Comment> getCommentsToMe() {
        // ...
    }

    public Observable<Comment> getCommentsToMe(int filterByAuthor, int filterBySource) {
        // ...
    }

    @RetroWeibo.GET("/comments/timeline.json")
    public abstract Observable<Comment> getComments(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("trim_user") boolean trimUser
    );

    public Observable<Comment> getComments() {
        // ...
    }

    public Observable<Comment> getComments(boolean trimUser) {
        // ...
    }

    @RetroWeibo.GET("/comments/mentions.json")
    public abstract Observable<Comment> getMentionedComments(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_author") int filterByAuthor,
        @RetroWeibo.Query("filter_by_source") int filterBySource
    );

    public Observable<Comment> getMentionedComments() {
        // ...
    }

    public Observable<Comment> getMentionedComments(int filterByAuthor, int filterBySource) {
        // ...
    }

    @RetroWeibo.GET("comments/show_batch.json")
    public abstract Observable<Comment> getBatchComments(@RetroWeibo.Query("cids") long[] cids);
```

Add Model: [Status.java](simpleweibo/src/main/java/com/sina/weibo/simple/Status.java):

```java
@AutoJson
public abstract class Status implements android.os.Parcelable {
    @Nullable
    @AutoJson.Field(name = "created_at")
    public abstract String createdAt();
    @Nullable
    @AutoJson.Field
    public abstract String id();
    // ...
}
```

## Demo

* Sample code: [MainActivity.java](simpleweibo-app/src/main/java/com/sina/weibo/simple/app/MainActivity.java)
* apk: https://github.com/8tory/SimpleWeibo/releases/download/1.0.0/simpleweibo-app-debug.apk

## Installation

via jitpack:

```gradle
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
  compile 'com.github.8tory.SimpleWeibo:simpleweibo:-SNAPSHOT'
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
