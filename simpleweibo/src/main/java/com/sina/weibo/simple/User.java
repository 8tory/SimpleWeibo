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

@AutoJson
public abstract class User implements android.os.Parcelable {
    @Nullable
    @AutoJson.Field
    public abstract Long id();
    @Nullable
    @AutoJson.Field
    public abstract String idstr();
    @Nullable
    @AutoJson.Field(name = "screen_name")
    public abstract String screenName();
    @Nullable
    @AutoJson.Field
    public abstract String name();
    @Nullable
    @AutoJson.Field
    public abstract int province();
    @Nullable
    @AutoJson.Field
    public abstract int city();
    @Nullable
    @AutoJson.Field
    public abstract String location();
    @Nullable
    @AutoJson.Field
    public abstract String description();
    @Nullable
    @AutoJson.Field
    public abstract String url();
    @Nullable
    @AutoJson.Field(name = "profile_image_url")
    public abstract String profileImageUrl();
    @Nullable
    @AutoJson.Field(name = "profile_url")
    public abstract String profileUrl();
    @Nullable
    @AutoJson.Field
    public abstract String domain();
    @Nullable
    @AutoJson.Field
    public abstract String weihao();
    @Nullable
    @AutoJson.Field
    public abstract String gender();
    @Nullable
    @AutoJson.Field(name = "followers_count")
    public abstract int followersCount();
    @Nullable
    @AutoJson.Field(name = "friends_count")
    public abstract int friendsCount();
    @Nullable
    @AutoJson.Field(name = "statuses_count")
    public abstract int statusesCount();
    @Nullable
    @AutoJson.Field(name = "favourites_count")
    public abstract int favouritesCount();
    @Nullable
    @AutoJson.Field(name = "created_at")
    public abstract String createdAt();
    @Nullable
    @AutoJson.Field
    public abstract boolean following();
    @Nullable
    @AutoJson.Field(name = "allow_all_act_msg")
    public abstract boolean allowAllActMsg();
    @Nullable
    @AutoJson.Field(name = "geo_enabled")
    public abstract boolean geoEnabled();
    @Nullable
    @AutoJson.Field
    public abstract boolean verified();
    @Nullable
    @AutoJson.Field(name = "verified_type")
    public abstract int verifiedType();
    @Nullable
    @AutoJson.Field
    public abstract String remark();
    @Nullable
    @AutoJson.Field
    public abstract User status();
    @Nullable
    @AutoJson.Field(name = "allow_all_comment")
    public abstract boolean allowAllComment();
    @Nullable
    @AutoJson.Field(name = "avatar_large")
    public abstract String avatarLarge();
    @Nullable
    @AutoJson.Field(name = "avatar_hd")
    public abstract String avatarHd();
    @Nullable
    @AutoJson.Field(name = "verified_reason")
    public abstract String verifiedReason();
    @Nullable
    @AutoJson.Field(name = "follow_me")
    public abstract boolean followMe();
    @Nullable
    @AutoJson.Field(name = "online_status")
    public abstract int onlineStatus();
    @Nullable
    @AutoJson.Field(name = "bi_followers_count")
    public abstract int biFollowersCount();
    @Nullable
    @AutoJson.Field
    public abstract String lang();

    @Nullable
    @AutoJson.Field
    public abstract String star();
    @Nullable
    @AutoJson.Field
    public abstract String mbtype();
    @Nullable
    @AutoJson.Field
    public abstract String mbrank();
    @Nullable
    @AutoJson.Field(name = "block_word")
    public abstract String blockWord();

    @AutoJson.Builder
    public abstract static class Builder {
        public abstract Builder id(Long x);
        public abstract Builder idstr(String x);
        public abstract Builder screenName(String x);
        public abstract Builder name(String x);
        public abstract Builder province(int x);
        public abstract Builder city(int x);
        public abstract Builder location(String x);
        public abstract Builder description(String x);
        public abstract Builder url(String x);
        public abstract Builder profileImageUrl(String x);
        public abstract Builder profileUrl(String x);
        public abstract Builder domain(String x);
        public abstract Builder weihao(String x);
        public abstract Builder gender(String x);
        public abstract Builder followersCount(int x);
        public abstract Builder friendsCount(int x);
        public abstract Builder statusesCount(int x);
        public abstract Builder favouritesCount(int x);
        public abstract Builder createdAt(String x);
        public abstract Builder following(boolean x);
        public abstract Builder allowAllActMsg(boolean x);
        public abstract Builder geoEnabled(boolean x);
        public abstract Builder verified(boolean x);
        public abstract Builder verifiedType(int x);
        public abstract Builder remark(String x);
        public abstract Builder status(User x);
        public abstract Builder allowAllComment(boolean x);
        public abstract Builder avatarLarge(String x);
        public abstract Builder avatarHd(String x);
        public abstract Builder verifiedReason(String x);
        public abstract Builder followMe(boolean x);
        public abstract Builder onlineStatus(int x);
        public abstract Builder biFollowersCount(int x);
        public abstract Builder lang(String x);

        public abstract Builder star(String x);
        public abstract Builder mbtype(String x);
        public abstract Builder mbrank(String x);
        public abstract Builder blockWord(String x);

        public abstract User build();
    }


    public static Builder builder() {
        return new AutoJson_User.Builder();
    }
}
