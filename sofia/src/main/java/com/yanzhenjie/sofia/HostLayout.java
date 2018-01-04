/*
 * Copyright 2017 Yan Zhenjie
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
package com.yanzhenjie.sofia;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


/**
 * Created by YanZhenjie on 2017/8/30.
 */
class HostLayout extends RelativeLayout implements Bar {

    private static final int FLAG_NOT_INVASION = 0x0;
    private static final int FLAG_INVASION_STATUS = 0x1;
    private static final int FLAG_INVASION_NAVIGATION = 0x2;
    private static final int FLAG_INVASION_STATUS_AND_NAVIGATION = FLAG_INVASION_STATUS | FLAG_INVASION_NAVIGATION;

    private Activity mActivity;
    private int mInvasionFlag = FLAG_NOT_INVASION;

    private StatusView mStatusView;
    private NavigationView mNavigationView;
    private FrameLayout mContentLayout;

    HostLayout(Activity activity) {
        super(activity);
        this.mActivity = activity;

        loadView();
        replaceContentView();

        Utils.invasionNavigationBar(mActivity);
        Utils.invasionNavigationBar(mActivity);
        Utils.setStatusBarColor(mActivity, Color.TRANSPARENT);
        Utils.setNavigationBarColor(mActivity, Color.TRANSPARENT);
    }

    private void loadView() {
        inflate(mActivity, R.layout.sofia_host_layout, this);
        mStatusView = findViewById(R.id.status_view);
        mNavigationView = findViewById(R.id.navigation_view);
        mContentLayout = findViewById(R.id.content);
    }

    private void replaceContentView() {
        Window window = mActivity.getWindow();
        ViewGroup contentLayout = window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
        if (contentLayout.getChildCount() > 0) {
            View contentView = contentLayout.getChildAt(0);
            contentLayout.removeView(contentView);
            mContentLayout.addView(contentView);
        }
        contentLayout.addView(this);
    }

    @Override
    public Bar statusBarDarkFont() {
        Utils.setStatusBarDarkFont(mActivity, true);
        return this;
    }

    @Override
    public Bar statusBarLightFont() {
        Utils.setStatusBarDarkFont(mActivity, false);
        return this;
    }

    @Override
    public Bar statusBarBackground(int statusBarColor) {
        mStatusView.setBackgroundColor(statusBarColor);
        return this;
    }

    @Override
    public Bar statusBarBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mStatusView.setBackground(drawable);
        else mStatusView.setBackgroundDrawable(drawable);
        return this;
    }

    @Override
    public Bar statusBarBackgroundAlpha(int alpha) {
        final Drawable background = mStatusView.getBackground();
        if (background != null) background.mutate().setAlpha(alpha);
        return this;
    }

    @Override
    public Bar navigationBarBackground(int navigationBarColor) {
        mNavigationView.setBackgroundColor(navigationBarColor);
        return this;
    }

    @Override
    public Bar navigationBarBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mNavigationView.setBackground(drawable);
        else mNavigationView.setBackgroundDrawable(drawable);
        return this;
    }

    @Override
    public Bar navigationBarBackgroundAlpha(int alpha) {
        final Drawable background = mNavigationView.getBackground();
        if (background != null) background.mutate().setAlpha(alpha);
        return this;
    }

    @Override
    public Bar invasionStatusBar() {
        mInvasionFlag |= FLAG_INVASION_STATUS;
        reLayoutInvasion();
        return this;
    }

    @Override
    public Bar invasionNavigationBar() {
        mInvasionFlag |= FLAG_INVASION_NAVIGATION;
        reLayoutInvasion();
        return this;
    }

    @Override
    public Bar fitsSystemWindowView(int viewId) {
        return fitsSystemWindowView(findViewById(viewId));
    }

    @Override
    public Bar fitsSystemWindowView(View fitView) {
        ViewParent fitParent = fitView.getParent();
        if (fitParent != null && !(fitParent instanceof FitWindowLayout)) {
            ViewGroup fitGroup = (ViewGroup) fitParent;
            fitGroup.removeView(fitView);

            ViewGroup.LayoutParams fitLayoutParams = fitView.getLayoutParams();
            fitLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            FitWindowLayout fitLayout = new FitWindowLayout(mActivity);
            fitGroup.addView(fitLayout, fitLayoutParams);

            ViewGroup.LayoutParams fitViewParams = new ViewGroup.LayoutParams(fitLayoutParams.width, fitLayoutParams.height);
            fitLayout.addView(fitView, fitViewParams);
        }
        return this;
    }

    private void reLayoutInvasion() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        switch (mInvasionFlag) {
            case FLAG_INVASION_STATUS:
                layoutParams.addRule(RelativeLayout.ABOVE, R.id.navigation_view);
                bringChildToFront(mStatusView);
                break;
            case FLAG_INVASION_NAVIGATION:
                layoutParams.addRule(RelativeLayout.BELOW, R.id.status_view);
                bringChildToFront(mNavigationView);
                break;
            case FLAG_INVASION_STATUS_AND_NAVIGATION:
                bringChildToFront(mStatusView);
                bringChildToFront(mNavigationView);
                break;
            case FLAG_NOT_INVASION:
                layoutParams.addRule(RelativeLayout.BELOW, R.id.status_view);
                layoutParams.addRule(RelativeLayout.ABOVE, R.id.navigation_view);
                break;
        }
        mContentLayout.setLayoutParams(layoutParams);
    }
}