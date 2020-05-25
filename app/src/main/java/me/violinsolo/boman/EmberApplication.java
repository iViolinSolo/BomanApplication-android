package me.violinsolo.boman;

import android.app.Application;

import com.clj.fastble.BleManager;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/25 9:43 AM
 * @updateAt 2020/5/25 9:43 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class EmberApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }
}
