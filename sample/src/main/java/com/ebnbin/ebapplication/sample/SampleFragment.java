package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ebnbin.ebapplication.base.EBFragment;
import com.ebnbin.ebapplication.fragment.WebViewFragment;

public final class SampleFragment extends EBFragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChildFragmentManagerHelper().add(new SampleFragment2(), String.valueOf(hashCode() - 1), true, false);

        WebViewFragment webViewFragment = WebViewFragment.newInstance("http://ebnbin.com");
        getChildFragmentManagerHelper().add(webViewFragment, null, true, true);
    }

    @Nullable
    @Override
    protected View overrideContentView() {
        return new View(getContext());
    }

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        contentView.setAlpha(0.5f);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hashCode = hashCode();
                getChildFragmentManagerHelper().add(new SampleFragment(), String.valueOf(hashCode), true, true);
                // TODO:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getChildFragmentManagerHelper().add(new SampleFragment(), String.valueOf(hashCode + 1), true,
                                true);
                    }
                });
            }
        });
    }
}
