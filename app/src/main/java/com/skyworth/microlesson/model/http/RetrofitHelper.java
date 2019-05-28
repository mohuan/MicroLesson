package com.skyworth.microlesson.model.http;

import com.skyworth.microlesson.model.http.api.QBaseApis;

import javax.inject.Inject;

/**
 *
 */
public class RetrofitHelper implements HttpHelper {

    private QBaseApis qBaseApis;

    @Inject
    public RetrofitHelper(QBaseApis qBaseApis) {
        this.qBaseApis = qBaseApis;
    }




}
