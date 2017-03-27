package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ebnbin.ebapplication.base.EBFragment;
import com.ebnbin.ebapplication.net.NetCallback;

public final class SampleFragment extends EBFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        netGetUrl();
    }

    private void netGetUrl() {
        String url = "http://gank.io/api/data/all/10/1";
        netGet(url, new NetCallback<SampleModel>() {
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

    @Nullable
    @Override
    protected View overrideContentView() {
        return new Button(getContext());
    }

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        SampleLoadingFragment sampleLoadingFragment = (SampleLoadingFragment) getChildFragmentManager()
                .findFragmentByTag(SampleLoadingFragment.class.getName());
        if (sampleLoadingFragment == null) {
            sampleLoadingFragment = new SampleLoadingFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .add(getChildFragmentContainerViewId(), sampleLoadingFragment,
                            SampleLoadingFragment.class.getName())
                    .commit();
        }
    }
}
