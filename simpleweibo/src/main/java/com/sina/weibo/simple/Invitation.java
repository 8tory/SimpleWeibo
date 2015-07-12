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
public abstract class Invitation implements android.os.Parcelable {
    @Nullable
    @AutoJson.Field
    public abstract String text();
    @Nullable
    @AutoJson.Field
    public abstract String url();
    @Nullable
    @AutoJson.Field(name = "invite_logo")
    public abstract String logo();

    @AutoJson.Builder
    public abstract static class Builder {
        public abstract Builder text(String x);
        public abstract Builder url(String x);
        public abstract Builder logo(String x);

        public abstract Invitation build();
    }

    public static Builder builder() {
        return new AutoJson_Invitation.Builder();
    }
}

