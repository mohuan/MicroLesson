package com.skyworth.microlesson.ui.main.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.skyworth.microlesson.R;
import com.skyworth.microlesson.base.RootFragment;
import com.skyworth.microlesson.ui.main.contract.DoodleContract;
import com.skyworth.microlesson.ui.main.presenter.DoodlePresenter;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.Util;
import cn.hzw.doodle.DoodleActivity;
import cn.hzw.doodle.DoodleColor;
import cn.hzw.doodle.DoodleOnTouchGestureListener;
import cn.hzw.doodle.DoodleParams;
import cn.hzw.doodle.DoodlePen;
import cn.hzw.doodle.DoodleShape;
import cn.hzw.doodle.DoodleTouchDetector;
import cn.hzw.doodle.DoodleView;
import cn.hzw.doodle.IDoodleListener;
import cn.hzw.doodle.core.IDoodle;
import cn.hzw.doodle.core.IDoodleColor;
import cn.hzw.doodle.core.IDoodleItemListener;
import cn.hzw.doodle.core.IDoodlePen;
import cn.hzw.doodle.core.IDoodleSelectableItem;
import cn.hzw.doodle.core.IDoodleTouchDetector;

/**
 * Created by weidingqiang
 */
public class DoodleFragment extends RootFragment<DoodlePresenter> implements DoodleContract.View {

    @BindView(R.id.doodle_container)
    FrameLayout doodle_container;

    private IDoodle mDoodle;

    private DoodleParams params;

    private DoodleOnTouchGestureListener mTouchGestureListener;

    //保存图片路径
    private String mImagePath="";

    public static DoodleFragment newInstance() {
        Bundle args = new Bundle();

        DoodleFragment fragment = new DoodleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_doodle_fragment;
    }

    @Override
    protected void initEventAndData() {

//        extraTransaction().setTag("111");

        // 涂鸦参数
        params = new DoodleParams();
        params.mIsFullScreen = true;
        // 图片路径
//        params.mImagePath = list.get(0);
        // 初始画笔大小
        params.mPaintUnitSize = DoodleView.DEFAULT_SIZE;
        // 画笔颜色
        params.mPaintColor = Color.RED;
        // 是否支持缩放item
        params.mSupportScaleItem = true;

//        Bitmap bitmap = ImageUtils.createBitmapFromPath(mImagePath, getContext());
        //这里的宽高有些问题  以后需要调整
        Bitmap bitmap = Bitmap.createBitmap(ScreenUtils.getAppScreenWidth(),ScreenUtils.getAppScreenHeight(),Bitmap.Config.RGB_565);

        DoodleView doodleView = new DoodleView(getContext(), bitmap, true, new IDoodleListener() {
            /*
                called when save the doodled iamge.
                保存涂鸦图像时调用
             */
            @Override
            public void onSaved(IDoodle doodle, Bitmap doodleBitmap, Runnable callback) {

            }
            /*
             called when it is ready to doodle because the view has been measured. Now, you can set size, color, pen, shape, etc.
             此时view已经测量完成，涂鸦前的准备工作已经完成，在这里可以设置大小、颜色、画笔、形状等。
             */
            @Override
            public void onReady(IDoodle doodle) {

            }
        },
                null);


        mTouchGestureListener = new DoodleOnTouchGestureListener(doodleView, new DoodleOnTouchGestureListener.ISelectionListener() {
            /*
             called when the item(such as text, texture) is selected/unselected.
             item（如文字，贴图）被选中或取消选中时回调
             */
            @Override
            public void onSelectedItem(IDoodle doodle, IDoodleSelectableItem selectableItem, boolean selected) {
                //do something
            }

            /*
             called when you click the view to create a item(such as text, texture).
             点击View中的某个点创建可选择的item（如文字，贴图）时回调
             */
            @Override
            public void onCreateSelectableItem(IDoodle doodle, float x, float y) {
                //do something
                    /*
            if (mDoodleView.getPen() == DoodlePen.TEXT) {
                    IDoodleSelectableItem item = new DoodleText(mDoodleView, "hello", 20 * mDoodleView.getUnitSize(), new DoodleColor(Color.RED), x, y);
                    mDoodleView.addItem(item);
            } else if (mDoodleView.getPen() == DoodlePen.BITMAP) {
                    IDoodleSelectableItem item = new DoodleBitmap(mDoodleView, bitmap, 80 * mDoodle.getUnitSize(), x, y);
                    mDoodleView.addItem(item);
            }
                    */
            }
        });

        // create touch detector, which dectects the gesture of scoll, scale, single tap, etc.
        // 创建手势识别器，识别滚动，缩放，点击等手势
        IDoodleTouchDetector detector = new DoodleTouchDetector(getContext(), mTouchGestureListener);
        doodleView.setDefaultTouchDetector(detector);

        // Setting parameters.设置参数
        doodleView.setPen(DoodlePen.BRUSH);
        doodleView.setShape(DoodleShape.HAND_WRITE);
        doodleView.setColor(new DoodleColor(Color.RED));

        doodle_container.addView(doodleView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        mDoodle.setDoodleMinScale(mDoodleParams.mMinScale);
//        mDoodle.setDoodleMaxScale(mDoodleParams.mMaxScale);

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();

    }

    @Override
    public void responeError(String errorMsg) {

    }

}