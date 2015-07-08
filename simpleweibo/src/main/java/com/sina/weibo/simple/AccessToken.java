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

import de.devland.esperandro.SharedPreferenceMode;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

import java.util.Set;
import java.util.List;

@SharedPreferences(mode = SharedPreferenceMode.PRIVATE)
public interface AccessToken {
  public static final String UID = "uid";
  public static final String ACCESS_TOKEN = "access_token";
  public static final String REFRESH_TOKEN = "refresh_token";
  public static final String EXPIRES_IN = "expires_in";
  public static final String EXPIRES_TIME = "expires_time";
  public static final String PHONE_NUM = "phone_num";

  public String uid();
  public void uid(String uid);

  public String token();
  public void token(String token);

  public String refreshToken();
  public void refreshToken(String token);

  public long expiresTime();
  public void expiresTime(long expiresTime);

  public long expiresIn();
  public void expiresIn(long expiresIn);

  public String phoneNum();
  public void phoneNum(String phoneNum);

  public Set<String> permissions();
  public void permissions(Set<String> permissions);
}
