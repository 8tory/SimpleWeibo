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
import com.sina.weibo.sdk.api.*;
import com.sina.weibo.sdk.api.share.*;

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
import rx.subjects.Subject;
import rx.subjects.PublishSubject;

import retroweibo.RetroWeibo;
import android.graphics.Bitmap;

@RetroWeibo
public abstract class SimpleWeibo {

    @RetroWeibo.GET("/statuses/friends_timeline.json")
    public abstract Observable<Status> getFriendStatuses(
            @RetroWeibo.Query("since_id") long sinceId,
            @RetroWeibo.Query("max_id") long maxId,
            @RetroWeibo.Query("base_app") boolean baseApp,
            @RetroWeibo.Query("trim_user") boolean trimUser,
            @RetroWeibo.Query("feature") int featureType
    );
    @RetroWeibo.GET("/statuses/user_timeline.json")
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

    public Observable<Status> getPictureStatuses() {
        return getStatuses(
            UNKNOWN_SINCE_ID,
            UNKNOWN_MAX_ID,
            NOT_APP_ONLY,
            NOT_TRIM_USER,
            FEATURE_PICTURE
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

    /**
     * @param source  (optional)   String  采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。
     * @param access_token    (optional)   String  采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。
     * @param status  (required)    String  要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。
     * @param visible (optional)   int     微博的可见性，0：所有人能看，1：仅自己可见，2：密友可见，3：指定分组可见，默认为0。
     * @param list_id (optional)   String  微博的保护投递指定分组ID，只有当visible参数为3时生效且必选。
     * @param lat     (optional)   float   纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
     * @param long    (optional)   float   经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
     * @param annotations     (optional)   string  元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。
     * @param rip     (optional)   String  开发者上报的操作用户真实IP，形如：211.156.0.1。
     * @see http://open.weibo.com/wiki/2/statuses/update
     */
    @RetroWeibo.POST("/statuses/update.json")
    public abstract Observable<Status> publishStatus(
        @RetroWeibo.Query("status") String content
    );

    @RetroWeibo.POST("/statuses/update.json")
    public abstract Observable<Status> publishLocatedStatus(
        @RetroWeibo.Query("status") String content,
        @RetroWeibo.Query("long") double longtitude,
        @RetroWeibo.Query("lat") double latitude
    );

    /**
     * 發表圖片貼文.
     *
     * 類似 update ，僅多一項 Bitmap.
     *
     * @param source  (optional)   String  采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。
     * @param access_token    (optional)   String  采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。
     * @param status  (required)    String  要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。
     * @param visible (optional)   int     微博的可见性，0：所有人能看，1：仅自己可见，2：密友可见，3：指定分组可见，默认为0。
     * @param list_id (optional)   String  微博的保护投递指定分组ID，只有当visible参数为3时生效且必选。
     * @param pic     (required)    Bitmap  要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M。
     * @param lat     (optional)   float   纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
     * @param long    (optional)   float   经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
     * @param annotations     (optional)   string  元数据，主要是为了方便第三方应用记录一些适合于自己使用的信息，每条微博可以包含一个或者多个元数据，必须以json字串的形式提交，字串长度不超过512个字符，具体内容可以自定。
     * @param rip     (optional)   String  开发者上报的操作用户真实IP，形如：211.156.0.1。
     *
     * @see http://open.weibo.com/wiki/2/statuses/upload
     */
    @RetroWeibo.POST("/statuses/upload.json")
    public abstract Observable<Status> publishPhotoStatus(
        @RetroWeibo.Query("status") String content,
        @RetroWeibo.Query("pic") Bitmap picture
    );

    @RetroWeibo.POST("/statuses/upload.json")
    public abstract Observable<Status> publishLocatedPhotoStatus(
        @RetroWeibo.Query("status") String content,
        @RetroWeibo.Query("pic") Bitmap picture,
        @RetroWeibo.Query("long") double longtitude,
        @RetroWeibo.Query("lat") double latitude
    );

    /**
     * High-level API
     */
    @RetroWeibo.POST("/statuses/upload_url_text.json")
    public abstract Observable<Status> publishPhotoUrlStatus(
        @RetroWeibo.Query("status") String content,
        @RetroWeibo.Query("url") String pictureUrl,
        @RetroWeibo.Query("pic_id") String pictureId,
        @RetroWeibo.Query("long") double longtitude,
        @RetroWeibo.Query("lat") double latitude
    );

    public Observable<Status> publishPhotoUrlStatus(
        String content,
        String pictureUrl,
        double longtitude,
        double latitude
    ) {
        return publishPhotoUrlStatus(content, pictureUrl, null, longtitude, latitude);
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
    private AuthInfo authInfo;

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
        this.authInfo = new AuthInfo(context, appId, redirectUrl, TextUtils.join(",", Arrays.asList("email")));
        this.accessToken = AccessTokenPreferences.create(activity);

        ssoHandler = new SsoHandler(activity, authInfo);
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
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

    public boolean isLogin() {
        if (accessToken == null) accessToken = AccessTokenPreferences.create(context);
        return accessToken.token() != null && !"".equals(accessToken.token());
    }

    public Observable<AccessToken> logInWithPermissions(final Collection<String> permissions) {
        if (accessToken == null) accessToken = AccessTokenPreferences.create(context);

        if (isValid(accessToken)) {
            if (!hasNewPermissions(accessToken, permissions)) {
                return Observable.just(accessToken);
            }
        }

        authInfo = new AuthInfo(context, appId, redirectUrl,
                TextUtils.join(",", permissions));
        ssoHandler = new SsoHandler(activity, authInfo);
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

    public void share(String text, Bitmap bitmap) {
        share(activity, text, bitmap);
    }

    private IWeiboShareAPI mWeiboShareAPI;
    private Subject<BaseResponse, BaseResponse> shareSubject;

    /**
     * onCreate(Activity activity, Response response, Bundle savedInstanceState)
     * onNewIntent(Response response, Intent intent)
     * onResponse(BaseResponse baseResponse)
     */
    // FIXME synchronized mWeiboShareAPI
    public synchronized Observable<BaseResponse> share(final Activity activity, String text, Bitmap bitmap) {
        TextObject textObject = new TextObject();
        textObject.text = text;

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = textObject;

        if (bitmap != null) {
            ImageObject imageObject = new ImageObject();
            imageObject.setImageObject(bitmap);
            weiboMessage.imageObject = imageObject;
        }

        final SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        shareSubject = PublishSubject.create();

        /*
        mWeiboShareAPI.sendRequest(activity, request); // FIXME Should return shareObs before sendRequest()
        */

        mWeiboShareAPI.sendRequest(activity, request, authInfo, accessToken.token(), new WeiboAuthListener() {
            @Override public void onComplete(Bundle values) {
                android.util.Log.d("SimpleWeibo", "values: " + values);
            }
            @Override public void onCancel() {
                android.util.Log.d("SimpleWeibo", "onCancel");
            }
            @Override public void onWeiboException(WeiboException e) {
                android.util.Log.e("SimpleWeibo", "onError");
                e.printStackTrace();
            }
        });

        return shareSubject.asObservable();
    }

    // FIXME synchronized mWeiboShareAPI
    public synchronized void onNewIntent(IWeiboHandler.Response response, Intent intent) {
        mWeiboShareAPI.handleWeiboResponse(intent, response);
    }

    /**
     * WBConstants.ErrorCode.ERR_OK
     * WBConstants.ErrorCode.ERR_CANCEL
     * WBConstants.ErrorCode.ERR_FAIL
     *
     * @param BaseResponse baseResponse baseResponse.errCode
     */
    public void onResponse(BaseResponse baseResponse) {
        shareSubject.onNext(baseResponse);
        shareSubject.onCompleted();
    }

    // FIXME synchronized mWeiboShareAPI
    public synchronized void onCreate(Activity activity, IWeiboHandler.Response response, Bundle savedInstanceState) {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, authInfo.getAppKey()); // appId
        mWeiboShareAPI.registerApp();

        mWeiboShareAPI.handleWeiboResponse(activity.getIntent(), response);
    }
}
