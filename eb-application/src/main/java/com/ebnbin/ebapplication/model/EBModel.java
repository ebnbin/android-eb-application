package com.ebnbin.ebapplication.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Base model.
 */
public abstract class EBModel implements Serializable {
    /**
     * Implements this method to check and return whether data of current model is valid.
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
