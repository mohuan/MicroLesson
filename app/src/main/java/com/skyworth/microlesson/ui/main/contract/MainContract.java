package com.skyworth.microlesson.ui.main.contract;

import com.skyworth.rxqwelibrary.base.BasePresenter;
import com.skyworth.rxqwelibrary.base.BaseView;

/**
 * Created by weidingqiang
 */
public interface MainContract {

    interface View extends BaseView {
    }

    interface Presenter extends BasePresenter<View> {
    }
}