package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.ebnbin.ebapplication.base.EBActivity;
import com.ebnbin.ebapplication.net.NetCallback;
import com.ebnbin.ebapplication.net.NetHelper;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = "http://gank.io/api/data/all/10/1";
        NetHelper.getInstance().get(hashCode(), url, new NetCallback<SampleModel>() {
            @Override
            public void onSuccess(@NonNull SampleModel model) {
                super.onSuccess(model);

                Toast.makeText(getContext(), model.toJson(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                super.onFailure();

                Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetHelper.getInstance().cancelCalls(hashCode());
    }
}
