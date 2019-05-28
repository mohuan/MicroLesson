package com.skyworth.microlesson.model.http.response;

/**
 * 作者：skyworth on 2017/9/7 09:56
 * 邮箱：dqwei@iflytek.com
 */

public class HttpResponse<T> {

//    private String errorMsg;
//    private int errorCode;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

//    public String getErrorMsg() {
//        return errorMsg;
//    }
//
//    public void setErrorMsg(String errorMsg) {
//        this.errorMsg = errorMsg;
//    }
//
//    public int getErrorCode() {
//        return errorCode;
//    }
//
//    public void setErrorCode(int errorCode) {
//        this.errorCode = errorCode;
//    }
}
