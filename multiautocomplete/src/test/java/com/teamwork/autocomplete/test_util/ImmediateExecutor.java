package com.teamwork.autocomplete.test_util;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class ImmediateExecutor implements Executor {

    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }

}