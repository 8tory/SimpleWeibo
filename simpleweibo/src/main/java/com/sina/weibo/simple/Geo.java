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
 * 地理信息（geo）.
 * <pre>
 * longitude    string  经度坐标
 * latitude     string  维度坐标
 * city string  所在城市的城市代码
 * province     string  所在省份的省份代码
 * city_name    string  所在城市的城市名称
 * province_name        string  所在省份的省份名称
 * address      string  所在的实际地址，可以为空
 * pinyin       string  地址的汉语拼音，不是所有情况都会返回该字段
 * more string  更多信息，不是所有情况都会返回该字段
 * </pre>
 *
 * @see http://open.weibo.com/wiki/%E5%B8%B8%E8%A7%81%E8%BF%94%E5%9B%9E%E5%AF%B9%E8%B1%A1%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84#.E5.9C.B0.E7.90.86.E4.BF.A1.E6.81.AF.EF.BC.88geo.EF.BC.89
 */
@AutoJson
public abstract class Geo implements android.os.Parcelable {
    @Nullable
    @AutoJson.Field
    public abstract String type();
    @Nullable
    @AutoJson.Field
    public abstract List<Float> coordinates();

    @Nullable
    @AutoJson.Field
    public abstract Float longitude();
    @Nullable
    @AutoJson.Field
    public abstract Float latitude();
    @Nullable
    @AutoJson.Field
    public abstract String city();
    @Nullable
    @AutoJson.Field
    public abstract String province();
    @Nullable
    @AutoJson.Field(name = "city_name")
    public abstract String cityName();
    @Nullable
    @AutoJson.Field
    public abstract String address();
    @Nullable
    @AutoJson.Field
    public abstract String pinyin();

    @AutoJson.Builder
    public abstract static class Builder {
        public abstract Builder type(String x);
        public abstract Builder coordinates(List<Float> x);
        public abstract Builder longitude(Float x);
        public abstract Builder latitude(Float x);
        public abstract Builder city(String x);
        public abstract Builder province(String x);
        public abstract Builder cityName(String x);
        public abstract Builder address(String x);
        public abstract Builder pinyin(String x);

        public abstract Geo build();
    }

    public static Builder builder() {
        return new AutoJson_Geo.Builder();
    }
}
