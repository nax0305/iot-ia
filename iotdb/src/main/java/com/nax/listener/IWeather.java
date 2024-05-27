package com.nax.listener;

/* 发布者接口，定义注册发布者接口*/

public interface IWeather {

    void addSubscriber(ISubscriber subscriber);

    void delSubscriber(ISubscriber subscriber);

    void publishInfo(String msg);
}
