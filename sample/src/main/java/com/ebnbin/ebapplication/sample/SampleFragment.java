package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ebnbin.ebapplication.base.EBFragment;
import com.ebnbin.ebapplication.net.NetCallback;

public final class SampleFragment extends EBFragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChildFragmentManagerHelper().add(new SampleFragment2(), String.valueOf(hashCode() - 1), true);
        netGetUrl();
    }

    private void netGetUrl() {
        String url = "http://gank.io/api/data/all/100/1";
        netGet(url, new NetCallback<SampleModel>() {
            @Override
            public void onSuccess(@NonNull SampleModel model) {
                super.onSuccess(model);

                // TODO: Null when callback.
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

        contentView.setAlpha(0.5f);

        int hashCode = hashCode();
        ((TextView) contentView).setText(getTag());

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManagerHelper().add(new SampleFragment(), String.valueOf(hashCode), true);
                // TODO:
                getChildFragmentManagerHelper().add(new SampleFragment(), String.valueOf(hashCode + 1), true);
//                getChildFragmentManagerHelper().add(new SampleFragment(), String.valueOf(hashCode + 2), true);
            }
        });
    }
}
