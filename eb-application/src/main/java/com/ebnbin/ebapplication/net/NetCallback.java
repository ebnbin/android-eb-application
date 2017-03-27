package com.ebnbin.ebapplication.net;

import android.support.annotation.NonNull;

import com.ebnbin.ebapplication.base.EBModel;
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
     * Adds extra callbacks.
     */
    public final List<NetCallback<Model>> netCallbacks = new ArrayList<>();

    /**
     * Called on loading.
     */
    public void onLoading() {
        for (NetCallback netCallback : netCallbacks) {
            netCallback.onLoading();
        }
    }

    /**
     * Called on success.
     *
     * @param model
     *         Model parsed by {@link Gson}.
     */
    public void onSuccess(@NonNull Model model) {
        for (NetCallback<Model> netCallback : netCallbacks) {
            netCallback.onSuccess(model);
        }
    }

    /**
     * Called on failure.
     */
    public void onFailure() {
        for (NetCallback netCallback : netCallbacks) {
            netCallback.onFailure();
        }
    }

    /**
     * Called on cancel.
     */
    public void onCancel() {
        for (NetCallback netCallback : netCallbacks) {
            netCallback.onCancel();
        }
    }
}
