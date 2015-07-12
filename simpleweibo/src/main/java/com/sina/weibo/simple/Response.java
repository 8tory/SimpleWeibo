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
public abstract class Response implements android.os.Parcelable {
    @Nullable
    @AutoJson.Field
    public abstract String error();
    @Nullable
    @AutoJson.Field(name = "error_code")
    public abstract String errorCode();

    @Nullable
    @AutoJson.Field
    public abstract Long id();
    @Nullable
    @AutoJson.Field
    public abstract String type();
    @Nullable
    @AutoJson.Field(name = "recipient_id")
    public abstract Long recipientId();
    @Nullable
    @AutoJson.Field(name = "sender_id")
    public abstract Long senderId();
    @Nullable
    @AutoJson.Field(name = "created_at")
    public abstract String createdAt();
    @Nullable
    @AutoJson.Field
    public abstract String text();

    @Nullable
    @AutoJson.Field(name = "data")
    public abstract Invitation invitation();

    /* Invite Response
    "id": 1211260020031347,
    "type": "invite",
    "recipient_id": 1902538057,
    "sender_id": 2489518277,
    "created_at": "Mon Jul 16 18:09:20 +0800 2012",
    "text": "这个游戏太好玩了，加入一起玩吧。http://t.cn/zHpnpxj",
    "data": {
        "url": "http://t.cn/zHpnpxj",
        "invite_logo": "http://hubimage.com2us.com/hubweb/contents/123_499.jpg"
    }
    */

    // revoke response
    @Nullable
    @AutoJson.Field
    public abstract boolean result();

    @AutoJson.Builder
    public abstract static class Builder {
        public abstract Builder error(String x);
        public abstract Builder errorCode(String x);
        public abstract Builder id(Long x);
        public abstract Builder type(String x);
        public abstract Builder recipientId(Long x);
        public abstract Builder senderId(Long x);
        public abstract Builder createdAt(String x);
        public abstract Builder text(String x);
        public abstract Builder invitation(Invitation x);
        public abstract Builder result(boolean x);

        public abstract Response build();
    }

    public static Builder builder() {
        return new AutoJson_Response.Builder();
    }
}

