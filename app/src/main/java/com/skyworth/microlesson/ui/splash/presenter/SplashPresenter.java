package com.skyworth.microlesson.ui.splash.presenter;



import com.skyworth.microlesson.base.RxPresenter;
import com.skyworth.microlesson.model.DataManager;
import com.skyworth.microlesson.ui.splash.contract.SplashContract;

import javax.inject.Inject;

/**
 * Created by skyworth
 */
public class SplashPresenter extends RxPresenter<SplashContract.View> implements SplashContract.Presenter {
    private DataManager mDataManager;

    @Inject
    public SplashPresenter(DataManager mDataManager) {
        super(mDataManager);
        this.mDataManager = mDataManager;
    }

    @Override
    public void attachView(SplashContract.View view) {
        super.attachView(view);
        registerEvent();
    }

    private void registerEvent() {

    }

    @Override
    public boolean isLogin() {
        return mDataManager.getLoginStatus();
    }

    @Override
    public boolean isFrist() {
        return mDataManager.getIsFrist();
    }

    @Override
    public void setIsFrist() {
        mDataManager.setIsFrist(false);
    }
}