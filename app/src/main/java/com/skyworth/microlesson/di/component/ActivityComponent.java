package com.skyworth.microlesson.di.component;

import android.app.Activity;


import com.skyworth.microlesson.di.module.ActivityModule;
import com.skyworth.microlesson.di.scope.ActivityScope;
import com.skyworth.microlesson.ui.main.activity.MainActivity;
import com.skyworth.microlesson.ui.mirror.activity.MirrorActivity;
import com.skyworth.microlesson.ui.splash.activity.SplashActivity;

import dagger.Component;

/**
 * 作者：skyworth on 2017/7/10 16:04
 * 邮箱：dqwei@iflytek.com
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {

    Activity getActivity();

    void inject(SplashActivity splashActivity);

    void inject(MainActivity mainActivity);

    void inject(MirrorActivity mirrorActivity);

}
