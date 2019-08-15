package com.skyworth.microlesson.model.event;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;

public class MirrorEvent {

    public static final String BROWSE_SUCCESS = "Browse_success";

    public static final String CONNECT_SUCCESS = "connect_success";

    private String type;

    private LelinkServiceInfo lelinkServiceInfo;

    public MirrorEvent(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LelinkServiceInfo getLelinkServiceInfo() {
        return lelinkServiceInfo;
    }

    public void setLelinkServiceInfo(LelinkServiceInfo lelinkServiceInfo) {
        this.lelinkServiceInfo = lelinkServiceInfo;
    }
}
