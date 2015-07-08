package com.sina.weibo.simple;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.sina.weibo.sdk.auth.*;
import com.sina.weibo.sdk.auth.sso.*;
import com.sina.weibo.sdk.exception.*;

import android.os.Bundle;
import android.text.TextUtils;
import android.content.Intent;
import android.content.Context;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

import rx.Observable;

public class SimpleWeibo {
    private Activity activity;
    private Context context;
    private String appId;
    private String redirectUrl;
    private SsoHandler ssoHandler;
    private static SimpleWeibo self;

    public static final String APPLICATION_ID_PROPERTY = "com.sina.weibo.sdk.ApplicationId";
    public static final String REDIRECT_URL_PROPERTY = "com.sina.weibo.sdk.RedirectUrl";

    public static SimpleWeibo get() {
        return self;
    }

    public static SimpleWeibo create(Activity activity) {
        self = new SimpleWeibo(activity).initialize(activity);
        return self;
    }

    private SimpleWeibo(Activity activity) {
    }

    public SimpleWeibo initialize(Activity activity) {
        this.activity = activity;
        this.context = activity;
        this.appId = getMetaData(activity, APPLICATION_ID_PROPERTY);
        this.redirectUrl = getMetaData(activity, REDIRECT_URL_PROPERTY);
        // null ? throw new IllegalArgumentException()
        ssoHandler = new SsoHandler(activity, new AuthInfo(context, appId, redirectUrl, TextUtils.join(",", Arrays.asList("email"))));
        return this;
    }

    public Observable<AccessToken> logIn() {
        return logInWithPermissions(Arrays.asList(
            "email",
            "direct_messages_read",
            "direct_messages_write",
            "friendships_groups_read",
            "friendships_groups_write",
            "statuses_to_me_read",
            "follow_app_official_microblog",
            "invitation_write"
        ));
    }

    AccessToken accessToken;

    public Observable<AccessToken> logInWithPermissions(Collection<String> permissions) {
        if (accessToken == null) accessToken = AccessTokenPreferences.create(context);

        if (isValid(accessToken)) {
            if (!hasNewPermissions(accessToken, permissions)) {
                return Observable.just(accessToken);
            }
        }

        ssoHandler = new SsoHandler(activity, new AuthInfo(context, appId, redirectUrl,
                TextUtils.join(",", permissions)));
        return logInForOauth2AccessToken(permissions).map(oauth2 -> {
            accessToken.uid(oauth2.getUid());
            accessToken.token(oauth2.getToken());
            accessToken.refreshToken(oauth2.getRefreshToken());
            accessToken.expiresTime(oauth2.getExpiresTime());
            accessToken.phoneNum(oauth2.getPhoneNum());
            accessToken.permissions(new HashSet<>(permissions)); //accessToken.permissions(permissions.addAll(accessToken.permissions()));
            return accessToken;
        });
    }

    public Observable<Oauth2AccessToken> logInForOauth2AccessToken(Collection<String> permissions) {
        return logInForBundle(permissions).map(bundle -> {
            return Oauth2AccessToken.parseAccessToken(bundle);
        }).flatMap(oauth2 -> {
            if (!oauth2.isSessionValid()) {
                return Observable.error(new WeiboException("AccessToken is invalid"));
            }
            return Observable.just(oauth2);
        });
    }

    public Observable<Bundle> logInForBundle(Collection<String> permissions) {
        return Observable.create(sub -> {
            ssoHandler.authorize(new WeiboAuthListener() {
                @Override public void onComplete(Bundle values) {
                    sub.onNext(values);
                    sub.onCompleted();
                }
                @Override public void onCancel() {
                    sub.onCompleted();
                }
                @Override public void onWeiboException(WeiboException e) {
                    sub.onError(e);
                }
            });
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ssoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    public static boolean isValid(AccessToken accessToken) {
        return !TextUtils.isEmpty(accessToken.token()); // TODO && isExpired(accessToken)
    }

    public static boolean hasNewPermissions(AccessToken accessToken, Collection<String> permissions) {
        List<String> newPermissions = new ArrayList<>(accessToken.permissions());
        newPermissions.remove(permissions);
        return !newPermissions.isEmpty();
    }

    public static String getMetaData(Context context, String key) {
        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        if (ai == null || ai.metaData == null) {
            return null;
        }

        return ai.metaData.getString(key);
    }
}
