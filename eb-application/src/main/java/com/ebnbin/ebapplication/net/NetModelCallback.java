package com.ebnbin.ebapplication.net;

import android.support.annotation.NonNull;

import com.ebnbin.ebapplication.model.EBModel;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Net callbacks for getting models.
 *
 * @param <Model> Subclass of {@link EBModel}.
 */
public abstract class NetModelCallback<Model extends EBModel> {
    /**
     * Adds extra pre-callbacks with this.
     */
    public final ArrayList<NetModelCallback<Model>> preCallbacks = new ArrayList<>();
    /**
     * Adds extra post-callbacks with this.
     */
    public final ArrayList<NetModelCallback<Model>> postCallbacks = new ArrayList<>();

    /**
     * Called when loading with pre-callbacks and post-callbacks.
     */
    final void onLoadingCallback(@NonNull Call call) {
        for (NetModelCallback<Model> callback : preCallbacks) {
            callback.onLoading(call);
        }

        onLoading(call);

        for (NetModelCallback<Model> callback : postCallbacks) {
            callback.onLoading(call);
        }
    }

    /**
     * Called when success with pre-callbacks and post-callbacks.
     *
     * @param model Valid model.
     */
    final void onSuccessCallback(@NonNull Call call, @NonNull Model model) {
        for (NetModelCallback<Model> callback : preCallbacks) {
            callback.onSuccess(call, model);
        }

        onSuccess(call, model);

        for (NetModelCallback<Model> callback : postCallbacks) {
            callback.onSuccess(call, model);
        }
    }

    /**
     * Called when failure with pre-callbacks and post-callbacks.
     */
    final void onFailureCallback(@NonNull Call call) {
        for (NetModelCallback<Model> callback : preCallbacks) {
            callback.onFailure(call);
        }

        onFailure(call);

        for (NetModelCallback<Model> callback : postCallbacks) {
            callback.onFailure(call);
        }
    }

    /**
     * Called when loading.
     */
    public void onLoading(@NonNull Call call) {
    }

    /**
     * Called when success.
     *
     * @param model Valid model.
     */
    public void onSuccess(@NonNull Call call, @NonNull Model model) {
    }

    /**
     * Called when failure.
     */
    public void onFailure(@NonNull Call call) {
    }
}
