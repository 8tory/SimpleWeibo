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

import com.bluelinelabs.logansquare.typeconverters.DateTypeConverter;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * "EEE MMM dd HH:mm:ss Z yyyy" Thu Jul 09 17:21:10 +0800 2015
 */
public class WeiboDateConverter extends DateTypeConverter {

    private DateFormat mDateFormat;

    public WeiboDateConverter() {
        mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
    }

    @Override
    public DateFormat getDateFormat() {
        return mDateFormat;
    }
}
