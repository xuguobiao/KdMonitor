package com.kido.sdk.kdmonitor.core;

public interface IUpLoadlistener {

    void onStart();

    void onUpLoad();

    void onSucess();

    void onFailure();

    void onCancell();
}
