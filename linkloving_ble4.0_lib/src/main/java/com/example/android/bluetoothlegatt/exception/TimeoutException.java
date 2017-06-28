package com.example.android.bluetoothlegatt.exception;

/**
 * Created by Daniel.Xu on 2017/5/19.
 */

public class TimeoutException extends Exception {
    public TimeoutException() {
        super("命令执行超时异常");
    }
}
