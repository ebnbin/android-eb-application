package com.ebnbin.ebapplication.base;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Base model.
 */
public abstract class EBModel implements Serializable {
    /**
     * Returns whether data is valid.
     *
     * @return {@code True} if data is valid.
     */
    public abstract boolean isValid();

    /**
     * Parses current model to Json.
     *
     * @return Json string.
     */
    @NonNull
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
