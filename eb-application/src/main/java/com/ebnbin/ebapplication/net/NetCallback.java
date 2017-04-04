package com.ebnbin.ebapplication.net;

import android.support.annotation.NonNull;

import com.ebnbin.ebapplication.model.EBModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Net callbacks.
 *
 * @param <Model>
 *         Subclass of {@link EBModel}.
 */
public abstract class NetCallback<Model extends EBModel> {
    /**
     * Adds extra pre callbacks.
     */
    public final List<NetCallback<Model>> preNetCallbacks = new ArrayList<>();
    /**
     * Adds extra post callbacks.
     */
    public final List<NetCallback<Model>> postNetCallbacks = new ArrayList<>();

    public final void onCallLoading() {
        for (NetCallback<Model> netCallback : preNetCallbacks) {
            netCallback.onLoading();
        }

        onLoading();

        for (NetCallback<Model> netCallback : postNetCallbacks) {
            netCallback.onLoading();
        }
    }

    /**
     * Called on loading.
     */
    public void onLoading() {
    }

    public final void onCallSuccess(@NonNull Model model) {
        for (NetCallback<Model> netCallback : preNetCallbacks) {
            netCallback.onSuccess(model);
        }

        onSuccess(model);

        for (NetCallback<Model> netCallback : postNetCallbacks) {
            netCallback.onSuccess(model);
        }
    }

    /**
     * Called on success.
     *
     * @param model
     *         Model parsed by {@link Gson}.
     */
    public void onSuccess(@NonNull Model model) {
    }

    public final void onCallFailure() {
        for (NetCallback<Model> netCallback : preNetCallbacks) {
            netCallback.onFailure();
        }

        onFailure();

        for (NetCallback<Model> netCallback : postNetCallbacks) {
            netCallback.onFailure();
        }
    }

    /**
     * Called on failure.
     */
    public void onFailure() {
    }
}
