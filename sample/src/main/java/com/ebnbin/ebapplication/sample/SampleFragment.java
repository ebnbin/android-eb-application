package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.ebnbin.ebapplication.base.EBFragment;
import com.ebnbin.ebapplication.net.NetCallback;

public final class SampleFragment extends EBFragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        netGet("http://gank.io/api/data/all/1000/1", new NetCallback<SampleModel>() {
            @Override
            public void onSuccess(@NonNull SampleModel model) {
                super.onSuccess(model);

                Toast.makeText(getContext(), "onSuccess", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
