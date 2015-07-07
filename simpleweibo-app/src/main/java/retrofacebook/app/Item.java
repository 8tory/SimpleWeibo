/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package retrofacebook.app;

import auto.parcel.AutoParcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

@AutoParcel
public abstract class Item implements Parcelable {
    @Nullable
    public abstract String icon();
    @Nullable
    public abstract String text1();

    @AutoParcel.Builder
    public abstract static class Builder {
        public abstract Builder icon(String s);
        public abstract Builder text1(String s);
        public abstract Item build();
    }

    public static Builder builder() {
        return new AutoParcel_Item.Builder();
    }

    public abstract Builder toBuilder();
}
