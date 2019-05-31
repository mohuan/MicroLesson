package com.skyworth.rxqwelibrary.app;

import java.io.File;

/**
 * 常用的变量值
 * 创建平行文件
 * @author skyworth
 *
 *         2015年6月2日
 */
public class AppConstants {

    public static String PARENT_FOLD_PATH = "";
    //缓存相关
    /**
     * 缓存地址
     */
//    public static String CACHE_PATH = "";

    /**
     * logs 文件
     * @param string
     */
//    public static String LOGS_PATH = "";

    public static void initPath(String string){
        PARENT_FOLD_PATH = string+ File.separator;
//        CACHE_PATH = PARENT_FOLD_PATH;

//        DOWNLOAD_PATH = PARENT_FOLD_PATH + "download" + File.separator;
//        LOGS_PATH = PARENT_FOLD_PATH + "logs" + File.separator;

        //download
//        RECORD_DOWNLOAD_PATH = DOWNLOAD_PATH + "audio" + File.separator;
        FILE_DOWNLOAD_PATH = PARENT_FOLD_PATH + "files" + File.separator;
        APK_DOWNLOAD_PATH = PARENT_FOLD_PATH + "apk" + File.separator;
        FILE_VIDEO_PATH = PARENT_FOLD_PATH + "video" + File.separator;

    }

    //--------------------------------------------------------------------------//
    //下载相关
    /**
     * 下载文件的存储地址
     */
//    public static String DOWNLOAD_PATH = "";
    /**
     * 录音文件下载存储地址
     */
//    public static String RECORD_DOWNLOAD_PATH = "";
    /**
     * 普通文件下载存储地址
     */
    public static String FILE_DOWNLOAD_PATH = "";

    /**
     * 视频存储地址
     */
    public static String FILE_VIDEO_PATH = "";

    //-----------------------------------------------//
    /**
     * APK文件下载地址
     */
    public static String APK_DOWNLOAD_PATH = "";

    // -----------------文件后缀名-------------
    /**
     * 音频文件后缀名
     */
    public static final String AUDIO_FILE_SUFFIX = ".mp3";
    /**
     * 音频文件后缀名
     */
    public static final String SPEECHS_FILE_SUFFIX = ".spx";

    //------------------------------------------------------//
}
