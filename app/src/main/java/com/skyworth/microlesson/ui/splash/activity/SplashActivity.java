package com.skyworth.microlesson.ui.splash.activity;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.skyworth.microlesson.R;
import com.skyworth.microlesson.base.RootActivity;
import com.skyworth.microlesson.ui.splash.contract.SplashContract;
import com.skyworth.microlesson.ui.splash.presenter.SplashPresenter;

import butterknife.BindView;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;

/**
 * Created by skyworth
 */

/**
 * 1.api文档地址 http://exam-dev.skyworthedu.com:9009/api/doc/index.html
 * 2.账号 136 6666 6666      000000
 *
 *
 */


public class SplashActivity extends RootActivity<SplashPresenter> implements SplashContract.View {

    @BindView(R.id.welcome_bg)
    LinearLayout welcome_bg;

    @BindView(R.id.banner_guide)
    RelativeLayout banner_guide;

    @BindView(R.id.banner_guide_background)
    BGABanner mBackgroundBanner;

    @BindView(R.id.banner_guide_foreground)
    BGABanner mForegroundBanner;


    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initEventAndData() {
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }

        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if(mPresenter.isFrist()){
            //第一次

            mPresenter.setIsFrist();

            banner_guide.setVisibility(View.VISIBLE);
            welcome_bg.setVisibility(View.GONE);

            setListener();
            processLogic();
        }
        else {
            //第二次进入跳转
            welcome_bg.setVisibility(View.VISIBLE);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.app_start_anim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                //动画完成
                @Override
                public void onAnimationEnd(Animation animation) {
                    initApp(animation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            welcome_bg.setAnimation(animation);
        }
    }

    @Override
    public void responeError(String errorMsg) {

    }

    private void setListener() {
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        mForegroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
//                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                initApp(null);
            }
        });
    }

    private void processLogic() {
        // Bitmap 的宽高在 maxWidth maxHeight 和 minWidth minHeight 之间
        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
        // 设置数据源
        mBackgroundBanner.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.uoko_guide_background_1,
                R.drawable.uoko_guide_background_2,
                R.drawable.uoko_guide_background_3);

        mForegroundBanner.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.uoko_guide_foreground_1,
                R.drawable.uoko_guide_foreground_2,
                R.drawable.uoko_guide_foreground_3);
    }

    private void initApp(Animation animation){

//        startActivity(MainActivity.newInstance(getApplicationContext()));
//        startActivity(LoginActivity.newInstance(getApplicationContext()));

//        if(mPresenter.isLogin())
//        {
//            startActivity(MainActivity.newInstance(getApplicationContext()));
//        }
//        else{
//            startActivity(LoginActivity.newInstance(getApplicationContext()));
//        }
//        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        if(mBackgroundBanner != null){
            mBackgroundBanner.setBackgroundResource(android.R.color.white);
        }
    }
}