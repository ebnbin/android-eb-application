package com.ebnbin.ebapplication.context.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebnbin.ebapplication.R;
import com.ebnbin.ebapplication.fragment.WebViewFragment;
import com.ebnbin.ebapplication.model.EBModel;
import com.ebnbin.ebapplication.net.NetHelper;
import com.ebnbin.ebapplication.net.NetModelCallback;
import com.ebnbin.ebapplication.view.StateFrameLayout;

import okhttp3.Call;

/**
 * Base {@link Fragment}.
 */
public abstract class EBFragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initLayoutInflater();

        mChildFragmentManagerHelper = new FragmentManagerHelper(getChildFragmentManager(), R.id.eb_state_frame_layout);

        initArguments();
    }

    @Override
    public void onDestroyView() {
        netCancelCalls();

        super.onDestroyView();
    }

    //*****************************************************************************************************************
    // Contexts.

    /**
     * Returns {@link #getActivity()} and casts to {@link EBActivity}.
     *
     * @return An {@link Activity}.
     */
    @Nullable
    public final EBActivity getEBActivity() {
        return (EBActivity) getActivity();
    }

    //*****************************************************************************************************************
    // Handler.

    /**
     * Handler with main {@link Looper}.
     */
    protected final Handler handler = new Handler(Looper.getMainLooper());

    //*****************************************************************************************************************
    // Layout inflater.

    private LayoutInflater mLayoutInflater;

    private void initLayoutInflater() {
        mLayoutInflater = LayoutInflater.from(getContext());
    }

    /**
     * Returns {@link LayoutInflater} of current fragment {@link Context}.
     */
    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    //*****************************************************************************************************************
    // Arguments.

    /**
     * Initializes fields with arguments.
     */
    private void initArguments() {
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }

        onInitArguments(args);
    }

    /**
     * Called when initializing fields with arguments.
     *
     * @param args
     *         If {@link #getArguments()} returns {@code null}, an empty {@link Bundle} will be used.
     */
    protected void onInitArguments(@NonNull Bundle args) {
    }

    //*****************************************************************************************************************
    // Content view.

    private StateFrameLayout mStateFrameLayout;

    /**
     * This method is overrode, a new way to initialize content view is to override {@link #overrideContentView()} and
     * returns the content view, or to override {@link #overrideContentViewLayout()} and returns the resource id of
     * content view. If both of these two methods returns valid value, the return of {@link #overrideContentView()}
     * will be used, and the return of {@link #overrideContentViewLayout()} will be ignored. After that, override
     * {@link #onInitContentView(View)} to initialize views with the given content view. If content view if
     * {@code null}, method {@link #onInitContentView(View)} will not be called. <br>
     *
     * @see #overrideContentView()
     * @see #overrideContentViewLayout()
     * @see #onInitContentView(View)
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mStateFrameLayout = (StateFrameLayout) inflater.inflate(R.layout.eb_fragment, container, false);

        initContentView(mStateFrameLayout, inflater, container, savedInstanceState);

        return mStateFrameLayout;
    }

    public StateFrameLayout getStateFrameLayout() {
        return mStateFrameLayout;
    }

    private void initContentView(@NonNull ViewGroup rootView, LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View contentView = overrideContentView();
        if (contentView == null) {
            int contentViewRes = overrideContentViewLayout();
            if (contentViewRes == 0) {
                return;
            }

            contentView = inflater.inflate(contentViewRes, rootView, false);
        }

        mStateFrameLayout.addView(contentView);

        onInitContentView(contentView);
    }

    /**
     * @see #onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    @Nullable
    protected View overrideContentView() {
        return null;
    }

    /**
     * @see #onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    @LayoutRes
    protected int overrideContentViewLayout() {
        return 0;
    }

    /**
     * @see #onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    protected void onInitContentView(@NonNull View contentView) {
    }

    //*****************************************************************************************************************
    // Net.

    /**
     * Gets a url async with tag of current fragment. With a default load callback.
     *
     * @param url
     *         Url.
     * @param callback
     *         Net callbacks.
     *
     * @param <Model>
     *         Subclass of {@link EBModel}.
     *
     * @return Current {@link Call}.
     */
    protected final <Model extends EBModel> Call netGet(@NonNull final String url,
            @NonNull final NetModelCallback<Model> callback) {
        final NetModelCallback<Model> loadCallback = new NetModelCallback<Model>() {
            @Override
            public void onLoading(@NonNull Call call) {
                super.onLoading(call);

                mStateFrameLayout.switchLoadingState();
            }

            @Override
            public void onSuccess(@NonNull Call call, @NonNull Model model) {
                super.onSuccess(call, model);

                mStateFrameLayout.clearState();
            }

            @Override
            public void onFailure(@NonNull Call call) {
                super.onFailure(call);

                mStateFrameLayout.switchFailureState(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        netGet(url, callback);
                    }
                });

            }
        };
        callback.preCallbacks.add(loadCallback);

        return NetHelper.getInstance().get(hashCode(), url, callback);
    }

    /**
     * Cancels and removes all saved {@link Call} by tag of current fragment.
     */
    private void netCancelCalls() {
        NetHelper.getInstance().cancelCalls(hashCode());
    }

    //*****************************************************************************************************************
    // WebViewFragment.

    /**
     * Uses {@link WebViewFragment} to load a url.
     */
    public void webViewLoadUrl(@NonNull String url) {
        EBActivity activity = getEBActivity();
        if (activity == null) {
            return;
        }

        WebViewFragment webViewFragment = WebViewFragment.newInstance(url);
        if (activity.getFragmentManagerHelper().canAdd(url)) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            activity.getFragmentManagerHelper().beginTransaction(ft)
                    .add(url, webViewFragment)
                    .hideAll(webViewFragment)
                    .push()
                    .endTransaction();
            ft.commit();
        }
    }

    //*****************************************************************************************************************
    // FragmentManagerHelper.

    private FragmentManagerHelper mChildFragmentManagerHelper;

    public FragmentManagerHelper getChildFragmentManagerHelper() {
        return mChildFragmentManagerHelper;
    }

    //*****************************************************************************************************************
    // Instance state.

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        mChildFragmentManagerHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mChildFragmentManagerHelper.onSaveInstanceState(outState);
    }

    //*****************************************************************************************************************

    /**
     * Handles back pressed.
     */
    public boolean onBackPressed() {
        boolean childShouldPop;
        boolean childHasPopped;
        boolean pop;

        EBFragment topFragment = mChildFragmentManagerHelper.top();
        if (topFragment != null) {
            childShouldPop = topFragment.onBackPressed();
            if (childShouldPop) {
                childHasPopped = getChildFragmentManager().popBackStackImmediate();
                pop = !childHasPopped;
            } else {
                pop = false;
            }
        } else {
            pop = true;
        }

        mChildFragmentManagerHelper.onPopped();

        return pop;
    }
}
