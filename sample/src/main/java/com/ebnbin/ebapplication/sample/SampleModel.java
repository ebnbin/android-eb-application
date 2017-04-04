package com.ebnbin.ebapplication.sample;

import com.ebnbin.ebapplication.model.EBModel;
import com.google.gson.annotations.SerializedName;

public final class SampleModel extends EBModel {
    @SerializedName("error")
    private boolean mError;

    @Override
    public boolean isValid() {
        return true;
    }
}
