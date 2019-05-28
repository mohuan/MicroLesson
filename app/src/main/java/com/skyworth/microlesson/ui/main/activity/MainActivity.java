package com.skyworth.microlesson.ui.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.skyworth.microlesson.R;
import com.skyworth.microlesson.base.BaseFragmentActivity;
import com.skyworth.microlesson.ui.main.contract.MainContract;
import com.skyworth.microlesson.ui.main.fragment.DoodleFragment;
import com.skyworth.microlesson.ui.main.presenter.MainPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends BaseFragmentActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    public static final int FIRST = 0;

    private SupportFragment[] mFragments = new SupportFragment[3];

    /**
     * 打开新Activity
     *
     * @param context
     * @return
     */
    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SupportFragment firstFragment = findFragment(DoodleFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = DoodleFragment.newInstance();
            loadMultipleRootFragment(R.id.fragment_content, FIRST,
                    mFragments[FIRST]);
        }
    }

    @OnClick({R.id.more_img})
    void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.more_img:
                if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.openDrawer(GravityCompat.START);
                }
                break;
//            case R.id.close_button:
//                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
//                    drawer_layout.closeDrawer(GravityCompat.START);
//                }
//                break;
        }
    }
}
