package com.skyworth.microlesson.ui.main.presenter;

import com.skyworth.microlesson.base.RxPresenter;
import com.skyworth.microlesson.model.DataManager;
import com.skyworth.microlesson.ui.main.contract.DoodleContract;

import javax.inject.Inject;

/**
 * Created by weidingqiang
 */
public class DoodlePresenter extends RxPresenter<DoodleContract.View> implements DoodleContract.Presenter {
    private DataManager mDataManager;

    @Inject
    public DoodlePresenter(DataManager mDataManager) {
        super(mDataManager);
        this.mDataManager = mDataManager;
    }

    @Override
    public void attachView(DoodleContract.View view) {
        super.attachView(view);
        registerEvent();
    }

    private void registerEvent() {

    }
}