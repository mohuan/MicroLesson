package com.skyworth.microlesson.ui.mirror.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ToastUtils;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.skyworth.microlesson.R;
import com.skyworth.microlesson.app.AppContext;
import com.skyworth.microlesson.base.RootActivity;
import com.skyworth.microlesson.ui.mirror.contract.MirrorContract;
import com.skyworth.microlesson.ui.mirror.presenter.MirrorPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lionel2Messi
 */
public class MirrorActivity extends RootActivity<MirrorPresenter> implements MirrorContract.View {

    @BindView(R.id.browse_but)
    Button browse_but;

    @BindView(R.id.connect_but)
    Button connect_but;

    @BindView(R.id.mirror_but)
    Button mirror_but;

    private LelinkServiceInfo lelinkServiceInfo;

    /**
     * 打开新Activity
     *
     * @param context
     * @return
     */
    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, MirrorActivity.class);
        return intent;
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_mirror;
    }

    @Override
    protected void initEventAndData() {
        // 1.进入后就打开 搜索

    }

    @Override
    public void browseSuccess(LelinkServiceInfo lelinkServiceInfo) {
        //2.接受到第一个 开始连接
        this.lelinkServiceInfo = lelinkServiceInfo;
    }

    @Override
    public void connectSuccess(LelinkServiceInfo lelinkServiceInfo) {
        //3.连接成功
        ToastUtils.showShort("连接成功");
//        finish();
    }

    @OnClick({R.id.browse_but,R.id.connect_but,R.id.mirror_but})
    void onViewClicked(View view){
        switch (view.getId()){
            case R.id.browse_but:
                AppContext.getInstance().getLelinkHelper().browse(ILelinkServiceManager.TYPE_ALL);
                break;
            case R.id.connect_but:
                AppContext.getInstance().getLelinkHelper().connect(lelinkServiceInfo);
                break;
            case R.id.mirror_but:
                AppContext.getInstance().getLelinkHelper().startMirror(this,lelinkServiceInfo);
                break;
        }
    }

    @Override
    public void responeError(String errorMsg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}