package com.zoo.whale.google;

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;
import com.google.common.util.concurrent.internal.InternalFutures;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.google.common.util.concurrent.Futures.getDone;

/**
 * @Date: 2024年 01月 08日
 * @Time:15时:12分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BrCallbackListener<ResponseBean> implements Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        
    }

    /*final Future<ResponseBean> future;
    final FutureCallback<? super ResponseBean> callback;

    BrCallbackListener(Future<ResponseBean> future, FutureCallback<? super ResponseBean> callback) {
        this.future = future;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (future instanceof InternalFutureFailureAccess) {
            Throwable failure = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess) future);
            if (failure != null) {
                callback.onFailure(failure);
                return;
            }
        }
        final ResponseBean value;
        try {
            value = getDone(future);
        } catch (ExecutionException e) {
            callback.onFailure(e.getCause());
            return;
        } catch (RuntimeException | Error e) {
            callback.onFailure(e);
            return;
        }
        callback.onSuccess(value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(callback).toString();
    }*/

}
