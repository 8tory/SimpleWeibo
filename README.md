# RetroWeibo

Retrofit Weibo SDK.

RetroWeibo turns Weibo API into a Java interface with RxJava.

Easy to add API and model.

Inspired by retrofit.

## Usage

My posts:

```java
Weibo weibo = Weibo.create(activity);

Observable<Post> myPosts = weibo.getPosts();
myPosts.take(100).forEach(System.out::println);
```

```java
@RetroWeibo
abstract class Weibo {
    @GET("/statuses/user_timeline.json")
    abstract Observable<Post> getPosts();
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
