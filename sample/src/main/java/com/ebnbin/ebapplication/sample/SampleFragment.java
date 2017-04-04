package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.ebnbin.ebapplication.context.ui.EBFragment;
import com.ebnbin.ebapplication.net.NetModelCallback;

import okhttp3.Call;

public final class SampleFragment extends EBFragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        netGet("http://gank.io/api/data/all/1000/1", new NetModelCallback<SampleModel>() {
            @Override
            public void onSuccess(@NonNull Call call, @NonNull SampleModel model) {
                super.onSuccess(call, model);

                Toast.makeText(getContext(), "onSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call call) {
                super.onFailure(call);

                Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
