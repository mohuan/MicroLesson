package com.skyworth.microlesson.ui.main.activity;

import android.Manifest;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.skyworth.microlesson.R;
import com.skyworth.microlesson.api.Constants;
import com.skyworth.microlesson.base.BaseFragmentActivity;
import com.skyworth.microlesson.screenrecord.AudioEncodeConfig;
import com.skyworth.microlesson.screenrecord.Notifications;
import com.skyworth.microlesson.screenrecord.ScreenRecorder;
import com.skyworth.microlesson.screenrecord.ScreenSetting;
import com.skyworth.microlesson.screenrecord.VideoEncodeConfig;
import com.skyworth.microlesson.ui.main.contract.MainContract;
import com.skyworth.microlesson.ui.main.fragment.DoodleFragment;
import com.skyworth.microlesson.ui.main.presenter.MainPresenter;
import com.skyworth.rxqwelibrary.app.AppConstants;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.SupportHelper;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION_CODES.M;

import static com.skyworth.microlesson.screenrecord.ScreenRecorder.AUDIO_AAC;
import static com.skyworth.microlesson.screenrecord.ScreenRecorder.VIDEO_AVC;


public class MainActivity extends BaseFragmentActivity<MainPresenter> implements MainContract.View {

    private static final String FragmentName = "Fragment";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @BindView(R.id.page_tv)
    TextView page_tv;

    private int current_page = 1;
    private int sum_page = 1;

    @BindView(R.id.record_but)
    ImageView record_but;

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

        initButtonClick();

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

    ////////////////////////////////////////////录屏 start ////////////////////////////////////////////////////


    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private MediaProjectionManager mMediaProjectionManager;

    private Notifications mNotifications;

    private ScreenRecorder mRecorder;

    private ScreenSetting screenSetting;

    //初始化录屏 参数
    private void initScreenRecord(){
        mMediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
        mNotifications = new Notifications(getApplicationContext());
        screenSetting = new ScreenSetting(getApplicationContext());
    }

    /**
     * 按钮点击
     */
    private void initButtonClick(){
        //录播
        mPresenter.addRxBindingSubscribe(
                RxView.clicks(record_but)
                    .throttleFirst(2, TimeUnit.SECONDS)
                    .compose(
                            new RxPermissions(this)
                                    .ensureEach(
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.RECORD_AUDIO
                                    )
                    )
                    .subscribe(
                            permission -> {
                                // will emit 2 Permission objects
                                if (permission.granted) {
                                    // `permission.name` is granted !
                                    // 用户已经同意该权限
                                    //如果正在录制，则停止
                                    if (mRecorder != null) {
                                        stopRecorder();
                                    }else {
                                        startCaptureIntent();
                                    }
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    // Denied permission without ask never again
                                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时。还会提示请求权限的对话框
                                    ToastUtils.showLong("请开启相关权限");
                                } else {
                                    // Denied permission with ask never again
                                    // Need to go to the settings
                                    // 用户拒绝了该权限，而且选中『不再询问』
                                    ToastUtils.showLong("请开启相关权限");
                                }
                            }
                    )
        );

    }

    private void startCaptureIntent() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();

        startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            // NOTE: Should pass this result data into a Service to run ScreenRecorder.
            // The following codes are merely exemplary.

            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mediaProjection == null) {
                Log.e("@@", "media projection is null");
                return;
            }

            VideoEncodeConfig video = screenSetting.createVideoConfig();
            AudioEncodeConfig audio = screenSetting.createAudioConfig(); // audio can be null
            if (video == null) {
                ToastUtils.showLong("Create ScreenRecorder failure");
                mediaProjection.stop();
                return;
            }

            File dir = new File(AppConstants.FILE_VIDEO_PATH);
            if (!dir.exists() && !dir.mkdirs()) {
                cancelRecorder();
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
            final File file = new File(dir, "Screen-" + format.format(new Date())
                    + "-" + video.width + "x" + video.height + ".mp4");
            Log.d("@@", "Create recorder with :" + video + " \n " + audio + "\n " + file);
            mRecorder = newRecorder(mediaProjection, video, audio, file);
            if (hasPermissions()) {
                startRecorder();
            } else {
                cancelRecorder();
            }
        }
    }

    private ScreenRecorder newRecorder(MediaProjection mediaProjection, VideoEncodeConfig video,
                                       AudioEncodeConfig audio, File output) {
        ScreenRecorder r = new ScreenRecorder(video, audio,
                1, mediaProjection, output.getAbsolutePath());
        r.setCallback(new ScreenRecorder.Callback() {
            long startTime = 0;

            @Override
            public void onStop(Throwable error) {
                runOnUiThread(() -> stopRecorder());
                if (error != null) {
                    ToastUtils.showShort("Recorder error ! See logcat for more details");
                    error.printStackTrace();
                    output.delete();
                } else {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                            .addCategory(Intent.CATEGORY_DEFAULT)
                            .setData(Uri.fromFile(output));
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onStart() {
                mNotifications.recording(0);
            }

            @Override
            public void onRecording(long presentationTimeUs) {
                if (startTime <= 0) {
                    startTime = presentationTimeUs;
                }
                long time = (presentationTimeUs - startTime) / 1000;
                mNotifications.recording(time);
            }
        });
        return r;
    }

    private boolean hasPermissions() {
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        int granted = pm.checkPermission(RECORD_AUDIO, packageName) | pm.checkPermission(WRITE_EXTERNAL_STORAGE, packageName);
        return granted == PackageManager.PERMISSION_GRANTED;
    }

    private void startRecorder() {
        if (mRecorder == null) return;
        mRecorder.start();
        registerReceiver(mStopActionReceiver, new IntentFilter(Constants.ACTION_STOP));
//        moveTaskToBack(false);
    }

    private void stopRecorder() {
        mNotifications.clear();
        if (mRecorder != null) {
            mRecorder.quit();
        }
        mRecorder = null;
        try {
            unregisterReceiver(mStopActionReceiver);
        } catch (Exception e) {
            //ignored
        }
    }

    private void cancelRecorder() {
        if (mRecorder == null) return;
        ToastUtils.showShort("Permission denied! Screen recorder is cancel");
        stopRecorder();
    }


    private BroadcastReceiver mStopActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            File file = new File(mRecorder.getSavedPath());
            if (Constants.ACTION_STOP.equals(intent.getAction())) {
                stopRecorder();
            }
            Toast.makeText(context, "Recorder stopped!\n Saved file " + file, Toast.LENGTH_LONG).show();
            StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
            try {
                // disable detecting FileUriExposure on public file
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
                viewResult(file);
            } finally {
                StrictMode.setVmPolicy(vmPolicy);
            }
        }

        private void viewResult(File file) {
            Intent view = new Intent(Intent.ACTION_VIEW);
            view.addCategory(Intent.CATEGORY_DEFAULT);
            view.setDataAndType(Uri.fromFile(file), VIDEO_AVC);
            view.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(view);
            } catch (ActivityNotFoundException e) {
                // no activity can open this video
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecorder();
    }


    ////////////////////////////////////////////录屏 end ////////////////////////////////////////////////////
}
