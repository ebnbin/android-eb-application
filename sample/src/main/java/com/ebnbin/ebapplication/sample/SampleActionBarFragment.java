package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ebnbin.ebapplication.base.EBActionBarFragment;
import com.ebnbin.ebapplication.base.EBActivity;

public final class SampleActionBarFragment extends EBActionBarFragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View rootView = View.inflate(getContext(), R.layout.sample_home_fragment, null);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new Adapter());

        Button button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(v -> webViewLoadUrl("http://ebnbin.com"));

        getAppbarScrollingViewContainerFrameLayout().addView(rootView);

        setNestedScrollingChild(recyclerView);
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(new Button(getContext())) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(String.valueOf(position));
            holder.itemView.setOnClickListener(v -> {
                EBActivity activity = getEBActivity();
                if (activity == null) {
                    return;
                }

                activity.addFragment(new SampleFragment(), null);
            });
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }
}
