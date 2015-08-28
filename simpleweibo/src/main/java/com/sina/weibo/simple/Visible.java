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

/**
 * 微博的可见性及指定可见分组信息。
 * {"type": 0, "list_id": 0}
 * 0: 普通微博
 * 1: 私密微博
 * 3: 指定分组微博
 * 4: 密友微博
 * list_id 为分组的组号
 */
@AutoJson
public abstract class Visible implements android.os.Parcelable {
    @Nullable
    @AutoJson.Field
    public abstract Long type();
    @Nullable
    @AutoJson.Field(name = "list_id")
    public abstract Long listId();

    @AutoJson.Builder
    public abstract static class Builder {
        public abstract Builder type(Long x);
        public abstract Builder listId(Long x);

        public abstract Visible build();
    }

    public static Builder builder() {
        return new AutoJson_Visible.Builder();
    }
}
