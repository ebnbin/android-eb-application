package com.ebnbin.ebapplication.net;

import android.support.annotation.NonNull;

import com.ebnbin.ebapplication.base.EBModel;
import com.google.gson.Gson;

/**
 * Net callbacks.
 *
 * @param <Model>
 *         Subclass of {@link EBModel}.
 */
public abstract class NetCallback<Model extends EBModel> {
    /**
     * Called on success.
     *
     * @param model
     *         Model parsed by {@link Gson}.
     */
    public void onSuccess(@NonNull Model model) {
    }

    /**
     * Called on failure.
     */
    public void onFailure() {
    }
}
