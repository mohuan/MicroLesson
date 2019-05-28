package com.skyworth.microlesson.di.component;



import com.skyworth.microlesson.app.AppContext;
import com.skyworth.microlesson.di.module.AppModule;
import com.skyworth.microlesson.di.module.HttpModule;
import com.skyworth.microlesson.model.DataManager;
import com.skyworth.microlesson.model.http.RetrofitHelper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 作者：skyworth on 2017/7/10 14:40
 * 邮箱：dqwei@iflytek.com
 */
@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent {

    AppContext getContext();  // 提供App的Context

    DataManager getDataManager(); //数据中心

    RetrofitHelper retrofitHelper();  //提供http的帮助类
}
