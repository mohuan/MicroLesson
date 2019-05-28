package com.skyworth.microlesson.ui.splash.contract;

import com.skyworth.rxqwelibrary.base.BasePresenter;
import com.skyworth.rxqwelibrary.base.BaseView;

/**
 * Created by skyworth
 */
public interface SplashContract {

    interface View extends BaseView {
        //返回登陆结果
        void responeError(String errorMsg);
    }

    interface Presenter extends BasePresenter<View> {
        boolean isLogin();

        boolean isFrist();

        void setIsFrist();
    }
}