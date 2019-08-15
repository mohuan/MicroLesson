package com.skyworth.microlesson.lebo;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkMirrorManager;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkPlayer;
import com.hpplay.sdk.source.api.LelinkPlayerInfo;
import com.hpplay.sdk.source.browse.api.IAPI;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.hpplay.sdk.source.browse.api.LelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkSetting;
import com.skyworth.microlesson.base.RxBus;
import com.skyworth.microlesson.model.event.MirrorEvent;
import com.skyworth.microlesson.ui.main.activity.MainActivity;
import com.skyworth.microlesson.ui.mirror.activity.MirrorActivity;

import java.io.File;
import java.util.List;

/**
 * Created by Zippo on 2018/10/13.
 * Date: 2018/10/13
 * Time: 17:08:24
 */
public class LelinkHelper {

    private static final String TAG = "LelinkHelper";

    private static final String APP_ID = "12191";
    private static final String APP_SECRET = "0f154e0dfd40465112ceb9eea3bd641e";

    private static LelinkHelper sLelinkHelper;

    private ILelinkServiceManager lelinkServiceManager;

    private LelinkPlayer leLinkPlayer;

    private Context mContext;

    public static LelinkHelper getInstance(Context context) {
        if (sLelinkHelper == null) {
            sLelinkHelper = new LelinkHelper(context);
        }
        return sLelinkHelper;
    }

    private LelinkHelper(Context context) {
        mContext = context;

        LelinkSetting lelinkSetting = new LelinkSetting.LelinkSettingBuilder(APP_ID, APP_SECRET).build();
        lelinkServiceManager = LelinkServiceManager.getInstance(context);
        lelinkServiceManager.setLelinkSetting(lelinkSetting);

        lelinkServiceManager.setOnBrowseListener(browserListener);

        leLinkPlayer = new LelinkPlayer(context);
        leLinkPlayer.setPlayerListener(mPlayerListener);
        leLinkPlayer.setConnectListener(connectListener);
    }

    // 1.第一步 搜索
    // 搜索
    // ILelinkServiceManager.TYPE_ALL：可以搜索到乐联和DLNA协议
    // ILelinkServiceManager.TYPE_LELINK：仅搜索乐联协议
    public void browse(int type) {
        lelinkServiceManager.browse(type);
    }
    // 停止搜索
    public void stopBrowse() {
        lelinkServiceManager.stopBrowse();
    }

    // 搜索响应
    private IBrowseListener browserListener = new IBrowseListener() {

        @Override
        public void onBrowse(int resultCode, List<LelinkServiceInfo> list) {
            LogUtils.d(TAG, "onSuccess size:" + (list == null ? 0 : list.size()));
            if (resultCode == IBrowseListener.BROWSE_SUCCESS) {
                LogUtils.d(TAG, "browse success");
                StringBuffer buffer = new StringBuffer();
                if (null != list) {
//                    for (LelinkServiceInfo info : list) {
//                        buffer.append("name：").append(info.getName())
//                                .append(" uid: ").append(info.getUid())
//                                .append(" type:").append(info.getTypes()).append("\n");
//                    }
//                    buffer.append("---------------------------\n");
                    MirrorEvent mirrorEvent = new MirrorEvent();
                    mirrorEvent.setType(MirrorEvent.BROWSE_SUCCESS);
                    mirrorEvent.setLelinkServiceInfo(list.get(0));
                    RxBus.getDefault().post(mirrorEvent);
                }
            } else {
//                if (null != mUIHandler) {
//                    // 发送文本信息
//                    LogUtils.e(TAG, "browse error:Auth error");
//                    mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_SEARCH_ERROR, "搜索错误：Auth错误"));
//                }
                ToastUtils.showShort("搜索错误：Auth错误");
            }
        }

    };

    // 第二步连接
    public void connect(LelinkServiceInfo info) {
        leLinkPlayer.connect(info);
    }

    public void disConnect(LelinkServiceInfo info) {
        leLinkPlayer.disConnect(info);
    }

    //获取正在连接的设备
    public List<LelinkServiceInfo> getConnectInfos() {
        return leLinkPlayer.getConnectLelinkServiceInfos();
    }

    //连接
    IConnectListener connectListener = new IConnectListener() {
        // LelinkSeviceInfo代表您正在操作的设备信息
        @Override
        public void onConnect(LelinkServiceInfo serviceInfo, int extra) {
            LogUtils.d(TAG, "onConnect:" + serviceInfo.getName());
            String type = extra == TYPE_LELINK ? "Lelink" : extra == TYPE_DLNA ? "DLNA" : extra == TYPE_NEW_LELINK ? "NEW_LELINK" : "IM";
            String text;
            if (TextUtils.isEmpty(serviceInfo.getName())) {
                text = "pin码连接" + type + "成功";
            } else {
                text = serviceInfo.getName() + "连接" + type + "成功";
            }
            //发送事件 或者 回调
            MirrorEvent mirrorEvent = new MirrorEvent();
            mirrorEvent.setType(MirrorEvent.CONNECT_SUCCESS);
            mirrorEvent.setLelinkServiceInfo(serviceInfo);
            RxBus.getDefault().post(mirrorEvent);
        }

        //IConnectListener.CONNECT_INFO_DISCONNECT：连接断开
        // （说明: 当接收端设备关机或者网络断开发送端不会立即产生回调，因为要做多次检测回调时间大概在10秒到1分钟之内）
        //IConnectListener.CONNECT_ERROR_FAILED：连接失败
        @Override
        public void onDisconnect(LelinkServiceInfo serviceInfo, int what, int extra) {
            LogUtils.d(TAG, "onDisconnect:" + serviceInfo.getName() + " disConnectType:" + what + " extra:" + extra);
            if (what == IConnectListener.CONNECT_INFO_DISCONNECT) {
                String text;
                if (TextUtils.isEmpty(serviceInfo.getName())) {
                    text = "pin码连接断开";
                } else {
                    text = serviceInfo.getName() + "连接断开";
                }
            } else if (what == IConnectListener.CONNECT_ERROR_FAILED) {
                String text = null;
                if (extra == IConnectListener.CONNECT_ERROR_IO) {
                    text = serviceInfo.getName() + "连接失败";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_WAITTING) {
                    text = serviceInfo.getName() + "等待确认";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_REJECT) {
                    text = serviceInfo.getName() + "连接拒绝";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_TIMEOUT) {
                    text = serviceInfo.getName() + "连接超时";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_BLACKLIST) {
                    text = serviceInfo.getName() + "连接黑名单";
                }
            }
        }

    };

    // 第三步 镜像
    public void startMirror(MirrorActivity activity, LelinkServiceInfo info) {
        if(leLinkPlayer != null){
            LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
            lelinkPlayerInfo.setType(LelinkPlayerInfo.TYPE_MIRROR);
            lelinkPlayerInfo.setActivity(activity);
            lelinkPlayerInfo.setLelinkServiceInfo(info);
            // 是否开启录制声音
            lelinkPlayerInfo.setMirrorAudioEnable(true);
            //ResolutionLevel的取值
            //ILelinkMirrorManager.RESOLUTION_HIGH：1080p分辨率
            //ILelinkMirrorManager.RESOLUTION_MID：720p分辨率
            //ILelinkMirrorManager.RESOLUTION_AUTO：屏幕分辨率
            lelinkPlayerInfo.setResolutionLevel(ILelinkMirrorManager.RESOLUTION_MID);
            //MirrorBitrateLevel的取值
            //ILelinkMirrorManager.BITRATE_HIGH：高比特率
            //ILelinkMirrorManager.BITRATE_MID：中比特率
            //ILelinkMirrorManager.BITRATE_LOW：低比特率
            lelinkPlayerInfo.setBitRateLevel(ILelinkMirrorManager.BITRATE_MID);
            leLinkPlayer.setDataSource(lelinkPlayerInfo);
            leLinkPlayer.start();
        }
    }

    public void startScreenShot() {
        LelinkPlayerInfo playerInfo = new LelinkPlayerInfo();
        playerInfo.setOption(IAPI.OPTION_19, "/sdcard" + File.separator + "screenshot.jpg");
        leLinkPlayer.setDataSource(playerInfo);
        leLinkPlayer.start();
    }

    public void stopMirror() {
        if (leLinkPlayer != null) {
            leLinkPlayer.stop();
        }
    }


    private ILelinkPlayerListener mPlayerListener = new ILelinkPlayerListener() {

        @Override
        public void onLoading() {
//            if (null != mUIHandler) {
//                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_LOADING, "开始加载"));
//            }
        }

        @Override
        public void onStart() {
            LogUtils.d(TAG, "onStart:");
//            if (null != mUIHandler) {
//                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_PLAY, "开始播放"));
//            }
        }

        @Override
        public void onPause() {
            LogUtils.d(TAG, "onPause");
//            if (null != mUIHandler) {
//                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_PAUSE, "暂停播放"));
//            }
        }

        @Override
        public void onCompletion() {
            LogUtils.d(TAG, "onCompletion");
//            if (null != mUIHandler) {
//                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_COMPLETION, "播放完成"));
//            }
        }

        @Override
        public void onStop() {
            LogUtils.d(TAG, "onStop");
//            if (null != mUIHandler) {
//                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_STOP, "播放结束"));
//            }
        }

        @Override
        public void onSeekComplete(int pPosition) {
            LogUtils.d(TAG, "onSeekComplete position:" + pPosition);
//            mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_SEEK, "设置进度"));
        }

        @Override
        public void onInfo(int what, int extra) {
            LogUtils.d(TAG, "onInfo what:" + what + " extra:" + extra);
            String text = null;
            if (what == ILelinkPlayerListener.INFO_SCREENSHOT) {
                if (extra == ILelinkPlayerListener.INFO_SCREENSHOT_COMPLATION) {
                    text = "截图完成";
                } else {
                    text = "截图失败";
                }
//                if (null != mUIHandler) {
//                    mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_SCREENSHOT, text));
//                }
            }
        }

        @Override
        public void onError(int what, int extra) {
            LogUtils.d(TAG, "onError what:" + what + " extra:" + extra);
            String text = null;
            if (what == ILelinkPlayerListener.PUSH_ERROR_INIT) {
                if (extra == ILelinkPlayerListener.PUSH_ERRROR_FILE_NOT_EXISTED) {
                    text = "文件不存在";
                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IM_OFFLINE) {
                    text = "IM TV不在线";
                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IMAGE) {

                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IM_UNSUPPORTED_MIMETYPE) {
                    text = "IM不支持的媒体类型";
                } else {
                    text = "未知";
                }
            } else if (what == ILelinkPlayerListener.MIRROR_ERROR_INIT) {
                if (extra == ILelinkPlayerListener.MIRROR_ERROR_UNSUPPORTED) {
                    text = "不支持镜像";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_REJECT_PERMISSION) {
                    text = "镜像权限拒绝";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_DEVICE_UNSUPPORTED) {
                    text = "设备不支持镜像";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
                }
            } else if (what == ILelinkPlayerListener.MIRROR_ERROR_PREPARE) {
                if (extra == ILelinkPlayerListener.MIRROR_ERROR_GET_INFO) {
                    text = "获取镜像信息出错";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_GET_PORT) {
                    text = "获取镜像端口出错";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
//                    if (null != mUIHandler) {
//                        mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_INPUT_SCREENCODE, text));
//                    }
                    return;
                } else if (extra == ILelinkPlayerListener.GRAP_UNSUPPORTED) {
                    text = "投屏码模式不支持抢占";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_PLAY) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "播放无响应";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
//                    if (null != mUIHandler) {
//                        mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_INPUT_SCREENCODE, text));
//                    }
                    return;
                } else if (extra == ILelinkPlayerListener.RELEVANCE_DATA_UNSUPPORTED) {
                    text = "老乐联不支持数据透传,请升级接收端的版本！";
//                    if (null != mUIHandler) {
//                        mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.RELEVANCE_DATA_UNSUPPORT, text));
//                    }
                    return;
                } else if (extra == ILelinkPlayerListener.GRAP_UNSUPPORTED) {
                    text = "投屏码模式不支持抢占";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_STOP) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "退出 播放无响应";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_PAUSE) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "暂停无响应";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_RESUME) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "恢复无响应";
                }
            }
//            if (null != mUIHandler) {
//                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_PLAY_ERROR, text));
//            }
        }

        /**
         * 音量变化回调
         *
         * @param percent 当前音量
         */
        @Override
        public void onVolumeChanged(float percent) {
            LogUtils.d(TAG, "onVolumeChanged percent:" + percent);
        }

        /**
         * 进度更新回调
         *
         * @param duration 媒体资源总长度
         * @param position 当前进度
         */
        @Override
        public void onPositionUpdate(long duration, long position) {
            LogUtils.d(TAG, "onPositionUpdate duration:" + duration + " position:" + position);
            long[] arr = new long[]{duration, position};
//            if (null != mUIHandler) {
//                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_POSITION_UPDATE, "进度更新", arr));
//            }
        }

    };


}