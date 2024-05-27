package com.nax.listener;

import java.util.ArrayList;
import java.util.List;

public class WeatherServer implements IWeather{


    private List<ISubscriber> subscribers = new ArrayList<ISubscriber>();

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        subscribers.add(subscriber);
        System.out.println("a new subscriber is joining");
    }

    @Override
    public void delSubscriber(ISubscriber subscriber) {
        subscribers.remove(subscriber);
        System.out.println("a subscriber is leaving");
    }

    @Override
    public void publishInfo(String msg) {
        for (ISubscriber subscriber : subscribers)
            subscriber.todoTomorrow(msg);
        System.out.println(String.format("publish a msg: %s", msg));
    }
}
