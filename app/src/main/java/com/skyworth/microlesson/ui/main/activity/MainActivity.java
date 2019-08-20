package com.skyworth.microlesson.ui.main.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.skyworth.microlesson.R;
import com.skyworth.microlesson.api.Constants;
import com.skyworth.microlesson.app.AppContext;
import com.skyworth.microlesson.base.BaseFragmentActivity;
import com.skyworth.microlesson.screenrecord.AudioEncodeConfig;
import com.skyworth.microlesson.screenrecord.Notifications;
import com.skyworth.microlesson.screenrecord.ScreenRecorder;
import com.skyworth.microlesson.screenrecord.ScreenSetting;
import com.skyworth.microlesson.screenrecord.VideoEncodeConfig;
import com.skyworth.microlesson.ui.main.contract.MainContract;
import com.skyworth.microlesson.ui.main.fragment.DoodleFragment;
import com.skyworth.microlesson.ui.main.presenter.MainPresenter;
import com.skyworth.microlesson.ui.mirror.activity.MirrorActivity;
import com.skyworth.rxqwelibrary.app.AppConstants;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.skyworth.microlesson.screenrecord.ScreenRecorder.VIDEO_AVC;


public class MainActivity extends BaseFragmentActivity<MainPresenter> implements MainContract.View {

    private static final String FragmentName = "Fragment";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @BindView(R.id.page_tv)
    TextView page_tv;

    private int current_page = 1;
    private int sum_page = 1;

    private int recordPermissions = 0;

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

        initScreenRecord();
    }

    @OnClick({R.id.more_img,R.id.next_page,R.id.front_page,R.id.record_img,R.id.mirror_img})
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
            //录播
            case R.id.record_img:
                //如果有权限
                if(hasPermissions()){
                    if (mRecorder != null) {
                        ToastUtils.showShort("录制完成");
                        stopRecorder();
                    }else {
                        startCaptureIntent();
                    }
                }else {
                    recordPermissions = 0;
                    mPresenter.addRxBindingSubscribe(
                            new RxPermissions(this)
                                    .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.RECORD_AUDIO)
                                    .subscribe(permission -> { // will emit 2 Permission objects
                                                if (permission.granted) {
                                                    if(++recordPermissions == 2){
                                                        if (mRecorder != null) {
                                                            stopRecorder();
                                                        }else {
                                                            startCaptureIntent();
                                                        }
                                                    }
                                                } else if (permission.shouldShowRequestPermissionRationale) {
                                                    ToastUtils.showLong("请开启相关权限");
                                                } else {
                                                    ToastUtils.showLong("请开启相关权限");
                                                }
                                            }
                                    )
                    );
                }
                break;
            //投屏
            case R.id.mirror_img:
//                获取系统文件管理器  https://blog.csdn.net/bzlj2912009596/article/details/80994628
                // https://blog.csdn.net/APTX8899/article/details/88649385
                // https://www.cnblogs.com/blosaa/p/9430996.html
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent,1);

                //调用系统摄像头录制
                // https://www.jianshu.com/p/3f4ad878f6c8
                // MediaStore.EXTRA_OUTPUT：设置媒体文件的保存路径。
                //MediaStore.EXTRA_VIDEO_QUALITY：设置视频录制的质量，0为低质量，1为高质量。
                //MediaStore.EXTRA_DURATION_LIMIT：设置视频最大允许录制的时长，单位为毫秒。
                //MediaStore.EXTRA_SIZE_LIMIT：指定视频最大允许的尺寸，单位为byte。
                Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                //好使
                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,10485760L);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                startActivityForResult(intent,1);

//                startActivity(MirrorActivity.newInstance(this));
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
        //1.获取用户录制屏幕授权
        mMediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
        mNotifications = new Notifications(getApplicationContext());
        screenSetting = new ScreenSetting(getApplicationContext());
    }


    private void startCaptureIntent() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();

        startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //2.在 onActivityResult 对用户的授权做处理
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

    //3初始化 MediaRecorder、创建 VirtualDisplay
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

        if(mNotifications != null){
            mNotifications.clear();
        }

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
