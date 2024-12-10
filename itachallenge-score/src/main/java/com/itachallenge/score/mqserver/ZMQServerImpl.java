package com.itachallenge.score.mqserver;

public interface ZMQServerImpl {
    void start();

    void stop();

    boolean isRunning();

    void run();
}
