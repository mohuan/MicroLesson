package com.skyworth.microlesson.ui.main.presenter;



import com.skyworth.microlesson.base.RxPresenter;
import com.skyworth.microlesson.model.DataManager;
import com.skyworth.microlesson.ui.main.contract.MainContract;

import javax.inject.Inject;

/**
 * Created by weidingqiang
 */
public class MainPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter {
    private DataManager mDataManager;

    @Inject
    public MainPresenter(DataManager mDataManager) {
        super(mDataManager);
        this.mDataManager = mDataManager;
    }

    @Override
    public void attachView(MainContract.View view) {
        super.attachView(view);
        registerEvent();
    }

    private void registerEvent() {

    }
}