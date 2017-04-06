package com.ebnbin.ebapplication.sample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ebnbin.ebapplication.context.ui.EBFragment;

public final class SampleFragment extends EBFragment {
    @Nullable
    @Override
    protected View overrideContentView() {
        return new Button(getContext());
    }

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViewLoadUrl("http://ebnbin.com");
            }
        });
    }
}
