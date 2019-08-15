package com.skyworth.microlesson.ui.mirror.presenter;

import android.support.annotation.NonNull;

import com.skyworth.microlesson.base.RxBus;
import com.skyworth.microlesson.base.RxPresenter;
import com.skyworth.microlesson.model.DataManager;
import com.skyworth.microlesson.model.event.MirrorEvent;
import com.skyworth.microlesson.ui.mirror.contract.MirrorContract;
import com.skyworth.microlesson.utils.RxUtil;
import com.skyworth.microlesson.widget.CommonSubscriber;

import javax.inject.Inject;

import io.reactivex.functions.Predicate;

/**
 * Created by Lionel2Messi
 */
public class MirrorPresenter extends RxPresenter<MirrorContract.View> implements MirrorContract.Presenter {
    private DataManager mDataManager;

    @Inject
    public MirrorPresenter(DataManager mDataManager) {
        super(mDataManager);
        this.mDataManager = mDataManager;
    }

    @Override
    public void attachView(MirrorContract.View view) {
        super.attachView(view);
        registerEvent();
    }

    private void registerEvent() {
        addSubscribe(RxBus.getDefault().toFlowable(MirrorEvent.class)
                .compose(RxUtil.<MirrorEvent>rxSchedulerHelper())
                .filter(new Predicate<MirrorEvent>() {
                    @Override
                    public boolean test(@NonNull MirrorEvent mirrorEvent) throws Exception {
                        return mirrorEvent.getType().equals(MirrorEvent.BROWSE_SUCCESS)
                                || mirrorEvent.getType().equals(MirrorEvent.CONNECT_SUCCESS);
                    }
                })
                .subscribeWith(new CommonSubscriber<MirrorEvent>(mView) {
                    @Override
                    public void onNext(MirrorEvent s) {
                        switch (s.getType()){
                            case MirrorEvent.BROWSE_SUCCESS:
                                mView.browseSuccess(s.getLelinkServiceInfo());
                                break;
                            case MirrorEvent.CONNECT_SUCCESS:
                                mView.connectSuccess(s.getLelinkServiceInfo());
                                break;
                        }



                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                })
        );
    }
}