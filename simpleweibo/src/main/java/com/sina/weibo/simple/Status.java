/*
 * Copyright (C) 2015 8tory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sina.weibo.simple;

import auto.json.AutoJson;
import android.support.annotation.Nullable;

import java.util.List;
import rx.Observable;

/**
 * @see http://t.cn/zjM1a2W
 */
@AutoJson
public abstract class Status implements android.os.Parcelable {
    @Nullable
    @AutoJson.Field(name = "created_at")
    public abstract String createdAt();
    @Nullable
    @AutoJson.Field
    public abstract Long id();
    @Nullable
    @AutoJson.Field
    public abstract String mid();
    @Nullable
    @AutoJson.Field
    public abstract String idstr();
    @Nullable
    @AutoJson.Field
    public abstract String text();
    @Nullable
    @AutoJson.Field
    public abstract String source();
    @Nullable
    @AutoJson.Field
    public abstract boolean favorited();
    @Nullable
    @AutoJson.Field
    public abstract boolean truncated();
    @Nullable
    @AutoJson.Field(name = "in_reply_to_status_id")
    public abstract String inReplyToStatusId();
    @Nullable
    @AutoJson.Field(name = "in_reply_to_user_id")
    public abstract String inReplyToUserId();
    @Nullable
    @AutoJson.Field(name = "in_reply_to_screen_name")
    public abstract String inReplyToScreenName();
    @Nullable
    @AutoJson.Field(name = "thumbnail_pic")
    public abstract String thumbnailPic();
    @Nullable
    @AutoJson.Field(name = "bmiddle_pic")
    public abstract String middlePic();
    @Nullable
    @AutoJson.Field(name = "original_pic")
    public abstract String originalPic();
    @Nullable
    @AutoJson.Field
    //public abstract Geo geo(); // TODO
    public abstract String geo();
    @Nullable
    @AutoJson.Field
    public abstract User user();
    @Nullable
    @AutoJson.Field(name = "retweeted_status")
    public abstract Status retweetedStatus();
    @Nullable
    @AutoJson.Field(name = "reposts_count")
    public abstract int repostsCount();
    @Nullable
    @AutoJson.Field(name = "comments_count")
    public abstract int commentsCount();
    @Nullable
    @AutoJson.Field(name = "attitudes_count")
    public abstract int attitudesCount();
    @Nullable
    @AutoJson.Field(name = "mlevel")
    public abstract int level();
    @Nullable
    @AutoJson.Field
    //public abstract Visible visible();
    public abstract boolean visible(); // TODO
    @Nullable
    @AutoJson.Field(name = "pic_urls")
    public abstract List<String> picUrls();

    @AutoJson.Builder
    public abstract static class Builder {
        public abstract Builder createdAt(String x);
        public abstract Builder id(Long x);
        public abstract Builder mid(String x);
        public abstract Builder idstr(String x);
        public abstract Builder text(String x);
        public abstract Builder source(String x);
        public abstract Builder favorited(boolean x);
        public abstract Builder truncated(boolean x);
        public abstract Builder inReplyToStatusId(String x);
        public abstract Builder inReplyToUserId(String x);
        public abstract Builder inReplyToScreenName(String x);
        public abstract Builder thumbnailPic(String x);
        public abstract Builder middlePic(String x);
        public abstract Builder originalPic(String x);
        //public abstract Builder geo(Geo x); // TODO
        public abstract Builder geo(String x);
        public abstract Builder user(User x);
        public abstract Builder retweetedStatus(Status x);
        public abstract Builder repostsCount(int x);
        public abstract Builder commentsCount(int x);
        public abstract Builder attitudesCount(int x);
        public abstract Builder level(int x);
        //public abstract Builder visible(Visible x);
        public abstract Builder visible(boolean x); // TODO
        public abstract Builder picUrls(List<String> x);

        public abstract Status build();
    }

    public static Builder builder() {
        return new AutoJson_Status.Builder();
    }

    public Observable<Comment> comment(String comment) {
        return SimpleWeibo.get().publishComment(comment, this);
    }
}
