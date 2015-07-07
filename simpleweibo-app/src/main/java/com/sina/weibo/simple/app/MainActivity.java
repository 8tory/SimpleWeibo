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

package com.sina.weibo.simple.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.*;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
//import com.sina.weibo.simple.*;

import butterknife.InjectView;
import butterknife.ButterKnife;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mAuthInfo = new AuthInfo(this, getString(R.string.weibo_app_key), getString(R.string.weibo_app_redirect_url), getString(R.string.weibo_app_scope));
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        setupAdapter(adapter);
        viewPager.setAdapter(adapter);
    }

    private void setupAdapter(Adapter adapter) {
        adapter.fragments.add(FragmentPage.create().title("Timeline").fragment(() -> {
            return RxCardsFragment.create()
                .items(getFriendsTimeline().map(status -> {
                    RxCard card = new RxCard();
                    card.icon = Observable.just(status.user.avatar_large);
                    card.text1 = Observable.just(status.user.screen_name);
                    card.message = Observable.just(status.text);
                    card.image = Observable.just(status.original_pic);
                    return card;
                }));
        }));
        adapter.fragments.add(FragmentPage.create().title("Timeline").fragment(() -> {
            return RxCardsFragment.create()
                .items(getFriendsTimeline().map(status -> {
                    RxCard card = new RxCard();
                    card.icon = Observable.just(status.user.avatar_large);
                    card.text1 = Observable.just(status.user.screen_name);
                    card.message = Observable.just(status.text);
                    card.image = Observable.just(status.original_pic);
                    return card;
                }));
        }));
        adapter.fragments.add(FragmentPage.create().title("Timeline").fragment(() -> {
            return RxCardsFragment.create()
                .items(getFriendsTimeline().map(status -> {
                    RxCard card = new RxCard();
                    card.icon = Observable.just(status.user.avatar_large);
                    card.text1 = Observable.just(status.user.screen_name);
                    card.message = Observable.just(status.text);
                    card.image = Observable.just(status.original_pic);
                    return card;
                }));
        }));
    }

    private Observable<Status> getFriendsTimeline() {
        if (mAccessToken == null) {
            return Observable.empty();
        }
        return Observable.<List<Status>>create(obs -> {
            StatusesAPI statusesAPI = new StatusesAPI(this, getString(R.string.weibo_app_key),
                    mAccessToken);
            statusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, new RequestListener() {
                @Override
                public void onComplete(String response) {
                    if (!TextUtils.isEmpty(response)) {
                        StatusList statuses = StatusList.parse(response);
                        if ((statuses != null) && (statuses.statusList != null)) {
                            obs.onNext(statuses.statusList);
                        }
                    }
                    obs.onCompleted();
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    obs.onError(e);
                }
            });
        }).flatMap(l -> Observable.from(l));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    static class FragmentPage {
        Func0<Fragment> onFragment;
        Fragment fragment;
        String title;

        public Fragment fragment() {
            if (fragment == null) fragment = onFragment.call();
            return fragment;
        }

        public String title() {
            return title;
        }

        public FragmentPage fragment(Func0<Fragment> onFragment) {
            this.onFragment = onFragment;
            return this;
        }

        public FragmentPage title(String title) {
            this.title = title;
            return this;
        }

        public static FragmentPage create() {
            return new FragmentPage();
        }

    }

    static class Adapter extends FragmentPagerAdapter {
        public List<FragmentPage> fragments = new ArrayList<>(); // NOTICE: memleak

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position).fragment();
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).title();
        }

        @Override
        public int getItemPosition(Object object) {
            return FragmentPagerAdapter.POSITION_NONE;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (!mAccessToken.isSessionValid()) {
                String code = values.getString("code");
                String message = "Session is invalid." + "\nObtained the code: " + code;
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                mAccessToken = null;
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this,
                   "Auth cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(MainActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
