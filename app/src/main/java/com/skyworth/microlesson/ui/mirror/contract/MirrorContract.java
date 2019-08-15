package com.skyworth.microlesson.ui.mirror.contract;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.skyworth.rxqwelibrary.base.BasePresenter;
import com.skyworth.rxqwelibrary.base.BaseView;

/**
 * Created by Lionel2Messi
 */
public interface MirrorContract {

    interface View extends BaseView {
        //返回登陆结果
        void responeError(String errorMsg);

        void browseSuccess(LelinkServiceInfo lelinkServiceInfo);

        void connectSuccess(LelinkServiceInfo lelinkServiceInfo);
    }

    interface Presenter extends BasePresenter<View> {
    }
}