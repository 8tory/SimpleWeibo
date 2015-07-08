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

/**
 * @see http://t.cn/zjM1a2W
 */
@AutoJson
public abstract class Statuss implements android.os.Parcelable {
    @Nullable
    @AutoJson.Field(name = "statuses")
    public abstract List<Status> data();

    @Nullable
    @AutoJson.Field(name = "hasvisible")
    public abstract Boolean hasVisible();
    @Nullable
    @AutoJson.Field(name = "previous_cursor")
    public abstract String previousCursor();
    @Nullable
    @AutoJson.Field(name = "next_cursor")
    public abstract String nextCursor();
    @Nullable
    @AutoJson.Field(name = "total_number")
    public abstract Integer totalNumber();
    @Nullable
    @AutoJson.Field
    public abstract List<String> advertises();

}
