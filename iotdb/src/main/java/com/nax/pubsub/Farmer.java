package com.nax.pubsub;

public class Farmer implements ISubscriber{

    @Override
    public void todoTomorrow(String msg) {
        if ("rain".equals(msg)){
            System.out.println("Farmer: a wonderful day!");
        }else if("sunny".equals(msg)){
            System.out.println("Farmer: a terrible day!");
        }else {

        }
    }
}
