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
