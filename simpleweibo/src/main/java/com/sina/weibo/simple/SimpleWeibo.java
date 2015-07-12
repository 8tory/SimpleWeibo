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
import rx.functions.*;
import rx.Subscriber;

import retroweibo.RetroWeibo;
import android.graphics.Bitmap;

@RetroWeibo
public abstract class SimpleWeibo {

    @RetroWeibo.GET("/statuses/friends_timeline.json")
    public abstract Observable<Status> getStatuses(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("base_app") boolean baseApp,
        @RetroWeibo.Query("trim_user") boolean trimUser,
        @RetroWeibo.Query("feature") int featureType
    );

    public Observable<Status> getStatuses() {
        return getStatuses(
            UNKNOWN_SINCE_ID,
            UNKNOWN_MAX_ID,
            NOT_APP_ONLY,
            NOT_TRIM_USER,
            FEATURE_ALL
        );
    }

    @RetroWeibo.GET("/statuses/mentions.json")
    public abstract Observable<Status> getMentionedStatuses(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("filter_by_author") int filterByAuthor,
        @RetroWeibo.Query("filter_by_source") int filterBySource,
        @RetroWeibo.Query("filter_by_type") int filterByType,
        @RetroWeibo.Query("trim_user") boolean trimUser
    );

    public Observable<Status> getMentionedStatuses() {
        return getMentionedStatuses(UNKNOWN_SINCE_ID,
            UNKNOWN_MAX_ID,
            AUTHOR_FILTER_ALL,
            SRC_FILTER_ALL,
            TYPE_FILTER_ALL,
            NOT_TRIM_USER
        );
    }

    @RetroWeibo.GET("/users/show.json")
    public abstract Observable<User> getUsersById(@RetroWeibo.Query("uid") long uid);

    @RetroWeibo.GET("/users/show.json")
    public abstract Observable<User> getUsersByName(@RetroWeibo.Query("screen_name") String screenName);

    @RetroWeibo.GET("/users/domain_show.json")
    public abstract Observable<User> getUsersByDomain(@RetroWeibo.Query("domain") String domain);

    @RetroWeibo.GET("/users/counts.json")
    public abstract Observable<User> getUsersCount(@RetroWeibo.Query("uids") long[] uids);

    @RetroWeibo.GET("/comments/show.json")
    public abstract Observable<Comment> getCommentsById(
        @RetroWeibo.Query("id") int id,
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_author") int filterByAuthor
    );

    public Observable<Comment> getCommentsById(int id) {
        return getCommentsById(
            id,
            UNKNOWN_SINCE_ID,
            UNKNOWN_MAX_ID,
            DEFAULT_LIMIT,
            FRONT_PAGE,
            AUTHOR_FILTER_ALL
        );
    }

    @RetroWeibo.GET("/comments/by_me.json")
    public abstract Observable<Comment> getCommentsByMe(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_source") int filterBySource
    );

    public Observable<Comment> getCommentsByMe() {
        return getCommentsByMe(SRC_FILTER_ALL);
    }

    public Observable<Comment> getCommentsByMe(int filterBySource) {
        return getCommentsByMe(
            UNKNOWN_SINCE_ID,
            UNKNOWN_MAX_ID,
            DEFAULT_LIMIT,
            FRONT_PAGE,
            filterBySource
        );
    }

    @RetroWeibo.GET("/comments/to_me.json")
    public abstract Observable<Comment> getCommentsToMe(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_author") int filterByAuthor,
        @RetroWeibo.Query("filter_by_source") int filterBySource
    );

    public Observable<Comment> getCommentsToMe() {
        return getCommentsToMe(AUTHOR_FILTER_ALL, SRC_FILTER_ALL);
    }

    public Observable<Comment> getCommentsToMe(int filterByAuthor, int filterBySource) {
        return getCommentsToMe(
            UNKNOWN_SINCE_ID,
            UNKNOWN_MAX_ID,
            DEFAULT_LIMIT,
            FRONT_PAGE,
            filterByAuthor,
            filterBySource
        );
    }

    @RetroWeibo.GET("/comments/timeline.json")
    public abstract Observable<Comment> getComments(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("trim_user") boolean trimUser
    );

    public Observable<Comment> getComments() {
        return getComments(NOT_TRIM_USER);
    }

    public Observable<Comment> getComments(boolean trimUser) {
        return getComments(
            UNKNOWN_SINCE_ID,
            UNKNOWN_MAX_ID,
            DEFAULT_LIMIT,
            FRONT_PAGE,
            trimUser
        );
    }

    @RetroWeibo.GET("/comments/mentions.json")
    public abstract Observable<Comment> getMentionedComments(
        @RetroWeibo.Query("since_id") long sinceId,
        @RetroWeibo.Query("max_id") long maxId,
        @RetroWeibo.Query("count") int count,
        @RetroWeibo.Query("page") int page,
        @RetroWeibo.Query("filter_by_author") int filterByAuthor,
        @RetroWeibo.Query("filter_by_source") int filterBySource
    );

    public Observable<Comment> getMentionedComments() {
        return getMentionedComments(AUTHOR_FILTER_ALL, SRC_FILTER_ALL);
    }

    public Observable<Comment> getMentionedComments(int filterByAuthor, int filterBySource) {
        return getMentionedComments(
            UNKNOWN_SINCE_ID,
            UNKNOWN_MAX_ID,
            DEFAULT_LIMIT,
            FRONT_PAGE,
            filterByAuthor,
            filterBySource
        );
    }

    @RetroWeibo.GET("/comments/show_batch.json")
    public abstract Observable<Comment> getComments(@RetroWeibo.Query("cids") long[] commentIds);

    //@RetroWeibo.POST("/messages/invite.json")
    @RetroWeibo.POST("https://m.api.weibo.com/2/messages/invite.json")
    //public abstract Observable<Response> invite(@RetroWeibo.Query("uid") long uid, @RetroWeibo.Body("data") Invitation invitation);
    public abstract Observable<Response> invite(@RetroWeibo.Query("uid") long uid, @RetroWeibo.Query("data") Invitation invitation);

    /*
    @RetroWeibo.POST("/statuses/update.json")
    public abstract Observable<Response> publishStatus(
        @RetroWeibo.Query("status") String content,
        @RetroWeibo.Query("long") String longtitude,
        @RetroWeibo.Query("lat") String latitude
    );
    */

    @RetroWeibo.POST("/statuses/update.json")
    public abstract Observable<Status> publishStatus(
        @RetroWeibo.Query("status") String content,
        @RetroWeibo.Query("long") double longtitude,
        @RetroWeibo.Query("lat") double latitude
    );

    @RetroWeibo.POST("/statuses/upload.json")
    public abstract Observable<Status> publishStatus(
        @RetroWeibo.Query("status") String content,
        @RetroWeibo.Query("pic") Bitmap picture,
        @RetroWeibo.Query("long") double longtitude,
        @RetroWeibo.Query("lat") double latitude
    );

    @RetroWeibo.POST("/statuses/upload_url_text.json")
    public abstract Observable<Status> publishStatus(
        @RetroWeibo.Query("status") String content,
        @RetroWeibo.Query("url") String pictureUrl,
        @RetroWeibo.Query("pic_id") String pictureId,
        @RetroWeibo.Query("long") double longtitude,
        @RetroWeibo.Query("lat") double latitude
    );

    public Observable<Status> publishStatus(
        String content,
        String pictureUrl,
        double longtitude,
        double latitude
    ) {
        return publishStatus(content, pictureUrl, null, longtitude, latitude);
    }

    @RetroWeibo.POST("/comments/create.json")
    public abstract Observable<Comment> publishComment(
        @RetroWeibo.Query("comment") String comment,
        @RetroWeibo.Query("id") long id,
        @RetroWeibo.Query("comment_ori") boolean pingback
    );

    public Observable<Comment> publishComment(String comment, long id) {
        return publishComment(comment, id, true);
    }

    public Observable<Comment> publishComment(String comment, Status status) {
        return publishComment(comment, status.id());
    }

    public Observable<Comment> publishComment(String comment, String id) {
        return publishComment(comment, Long.valueOf(id));
    }

    @RetroWeibo.POST("/comments/destroy.json")
    public abstract Observable<Comment> deleteComment(
        @RetroWeibo.Query("cid") long commentId
    );

    @RetroWeibo.POST("/comments/sdestroy_batch.json")
    public abstract Observable<Comment> deleteComments(
        @RetroWeibo.Query("cids") long[] commentIds
    );

    @RetroWeibo.POST("/comments/reply.json")
    public abstract Observable<Comment> replyComment(
        @RetroWeibo.Query("comment") String comment,
        @RetroWeibo.Query("cid") long cid,
        @RetroWeibo.Query("id") long id,
        @RetroWeibo.Query("without_mention") boolean withoutMention,
        @RetroWeibo.Query("comment_ori") boolean pingback
    );

    public Observable<Comment> replyComment(
        String comment,
        long cid,
        long id
    ) {
        return replyComment(
            comment,
            cid,
            id,
            false,
            true
        );
    }

    public Observable<Comment> replyComment(String comment, Comment parentComment) {
        return replyComment(comment, parentComment.id(), parentComment.status().id());
    }

    @RetroWeibo.POST("/oauth2/revokeoauth2")
    public abstract Observable<Response> revoke();

    public Observable<Response> logOut() {
        return revoke();
    }

    private Activity activity;
    private Context context;
    private String appId;
    private String redirectUrl;
    private SsoHandler ssoHandler;
    private static SimpleWeibo self;
    private AccessToken accessToken;

    public static final String APPLICATION_ID_PROPERTY = "com.sina.weibo.sdk.ApplicationId";
    public static final String REDIRECT_URL_PROPERTY = "com.sina.weibo.sdk.RedirectUrl";
    private static final String DEFAULT_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    public static SimpleWeibo get() {
        return self;
    }

    public static SimpleWeibo create(Activity activity) {
        self = new RetroWeibo_SimpleWeibo(activity).initialize(activity);
        return self;
    }

    public SimpleWeibo initialize(Activity activity) {
        this.activity = activity;
        this.context = activity;
        this.appId = getMetaData(activity, APPLICATION_ID_PROPERTY);
        // null ? throw new IllegalArgumentException()
        this.redirectUrl = getMetaData(activity, REDIRECT_URL_PROPERTY);
        if (this.redirectUrl == null || "".equals(this.redirectUrl)) this.redirectUrl = DEFAULT_REDIRECT_URL;

        ssoHandler = new SsoHandler(activity, new AuthInfo(context, appId, redirectUrl, TextUtils.join(",", Arrays.asList("email"))));
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public Observable<AccessToken> logIn() {
        return logInWithPermissions("email",
            "direct_messages_read",
            "direct_messages_write",
            "friendships_groups_read",
            "friendships_groups_write",
            "statuses_to_me_read",
            "follow_app_official_microblog",
            "invitation_write"
        );
    }

    public Observable<AccessToken> logInWithPermissions(String... permissions) {
        return logInWithPermissions(Arrays.asList(permissions));
    }

    public Observable<AccessToken> logInWithPermissions(final Collection<String> permissions) {
        if (accessToken == null) accessToken = AccessTokenPreferences.create(context);

        if (isValid(accessToken)) {
            if (!hasNewPermissions(accessToken, permissions)) {
                return Observable.just(accessToken);
            }
        }

        ssoHandler = new SsoHandler(activity, new AuthInfo(context, appId, redirectUrl,
                TextUtils.join(",", permissions)));
        return logInForOauth2AccessToken(permissions).map(new Func1<Oauth2AccessToken, AccessToken>() {
            @Override public AccessToken call(Oauth2AccessToken oauth2) {
                accessToken.uid(oauth2.getUid());
                accessToken.token(oauth2.getToken());
                accessToken.refreshToken(oauth2.getRefreshToken());
                accessToken.expiresTime(oauth2.getExpiresTime());
                accessToken.phoneNum(oauth2.getPhoneNum());
                accessToken.permissions(new HashSet<>(permissions)); //accessToken.permissions(permissions.addAll(accessToken.permissions()));
                return accessToken;
            }
        });
    }

    public Observable<Oauth2AccessToken> logInForOauth2AccessToken(Collection<String> permissions) {
        return logInForBundle(permissions).map(new Func1<Bundle, Oauth2AccessToken>() {
            @Override public Oauth2AccessToken call(Bundle bundle) {
                return Oauth2AccessToken.parseAccessToken(bundle);
            }
        }).flatMap(new Func1<Oauth2AccessToken, Observable<Oauth2AccessToken>>() {
            @Override public Observable<Oauth2AccessToken> call(Oauth2AccessToken oauth2) {
                if (!oauth2.isSessionValid()) {
                    return Observable.error(new WeiboException("AccessToken is invalid"));
                }
                return Observable.just(oauth2);
            }
        });
    }

    public Observable<Bundle> logInForBundle(Collection<String> permissions) {
        return Observable.create(new Observable.OnSubscribe<Bundle>() {
            @Override public void call(final Subscriber<? super Bundle> sub) {
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
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ssoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    public static boolean isValid(AccessToken accessToken) {
        return !TextUtils.isEmpty(accessToken.token()); // TODO && isExpired(accessToken)
    }

    public static boolean hasNewPermissions(AccessToken accessToken, Collection<String> permissions) {
        List<String> newPermissions = new ArrayList<>(permissions);
        newPermissions.removeAll(accessToken.permissions());
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

    /** TODO enum */

    public static final int FEATURE_ALL      = 0;
    public static final int FEATURE_ORIGINAL = 1;
    public static final int FEATURE_PICTURE  = 2;
    public static final int FEATURE_VIDEO    = 3;
    public static final int FEATURE_MUSICE   = 4;

    public static final int AUTHOR_FILTER_ALL        = 0;
    public static final int AUTHOR_FILTER_ATTENTIONS = 1;
    public static final int AUTHOR_FILTER_STRANGER   = 2;

    public static final int SRC_FILTER_ALL      = 0;
    public static final int SRC_FILTER_WEIBO    = 1;
    public static final int SRC_FILTER_WEIQUN   = 2;

    public static final int TYPE_FILTER_ALL     = 0;
    public static final int TYPE_FILTER_ORIGAL  = 1;

    public static final boolean APP_ONLY = true;
    public static final boolean NOT_APP_ONLY = !APP_ONLY;

    public static final boolean TRIM_USER = true;
    public static final boolean NOT_TRIM_USER = !TRIM_USER;

    public static final int FRONT_PAGE = 1;

    public static final int DEFAULT_LIMIT = 32;

    public static final long UNKNOWN_MAX_ID = 0L;
    public static final long UNKNOWN_SINCE_ID = 0L;

}
