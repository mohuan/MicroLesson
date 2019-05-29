package com.skyworth.microlesson.ui.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.skyworth.microlesson.R;
import com.skyworth.microlesson.base.BaseFragmentActivity;
import com.skyworth.microlesson.ui.main.contract.MainContract;
import com.skyworth.microlesson.ui.main.fragment.DoodleFragment;
import com.skyworth.microlesson.ui.main.presenter.MainPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.SupportHelper;

public class MainActivity extends BaseFragmentActivity<MainPresenter> implements MainContract.View {

    private static final String FragmentName = "Fragment";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    public static final int FIRST = 0;

    private SupportFragment[] mFragments = new SupportFragment[3];

    @BindView(R.id.page_tv)
    TextView page_tv;

    private int current_page = 1;
    private int sum_page = 1;

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

        SupportFragment fragment = DoodleFragment.newInstance();
        extraTransaction().setTag(FragmentName+current_page)
                .loadRootFragment(R.id.fragment_content, fragment);

    }

    @OnClick({R.id.more_img,R.id.next_page,R.id.front_page})
    void onViewClicked(View view){
        final ISupportFragment topFragment = getTopFragment();
        SupportFragment supportFragment = (SupportFragment) topFragment;
        switch (view.getId()) {
            //展开抽屉
            case R.id.more_img:
                if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.openDrawer(GravityCompat.START);
                }

                break;
            // 前一页
            case R.id.front_page:
                if(current_page > 1){
                    DoodleFragment hidefragment = findFragment(FragmentName+current_page);
                    current_page--;
                    setPageLabel();
                    DoodleFragment showfragment = findFragment(FragmentName+current_page);
                    showHideFragment(showfragment,hidefragment);
                }


                break;
            //后一页
            case R.id.next_page:
                //最后一页
                if(current_page == sum_page){
                    current_page++;
                    sum_page++;

                    //创建一个新页
                    SupportFragment fragment = DoodleFragment.newInstance();
                    extraTransaction().setTag(FragmentName+current_page).start(fragment);
                }else {

                    DoodleFragment hidefragment = findFragment(FragmentName+current_page);
                    current_page++;
                    setPageLabel();
                    DoodleFragment showfragment = findFragment(FragmentName+current_page);
                    showHideFragment(showfragment,hidefragment);
//                    current_page++;
//                    DoodleFragment fragment = findFragment(FragmentName+current_page);
//                    supportFragment.start(fragment, SupportFragment.SINGLETOP);
                }

                setPageLabel();
                break;
        }
    }

    private void setPageLabel(){
        page_tv.setText(current_page + " / "+ sum_page);

    }
}
