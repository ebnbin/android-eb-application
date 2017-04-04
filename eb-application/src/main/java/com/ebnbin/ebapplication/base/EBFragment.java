package com.ebnbin.ebapplication.base;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.FrameLayout;

import com.ebnbin.ebapplication.R;
import com.ebnbin.ebapplication.fragment.WebViewFragment;
import com.ebnbin.ebapplication.model.EBModel;
import com.ebnbin.ebapplication.net.NetCallback;
import com.ebnbin.ebapplication.net.NetHelper;

import okhttp3.Call;

/**
 * Base {@link Fragment}.
 */
public abstract class EBFragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initLayoutInflater();

        mChildFragmentManagerHelper = new FragmentManagerHelper(getChildFragmentManager(), R.id.eb_root);

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

    private ViewGroup mRootViewGroup;
    private FrameLayout mViewContainerFrameLayout;

    private FrameLayout mContentViewContainerFrameLayout;

    /**
     * This method is overrode, a new way to initialize content view is to override {@link #overrideContentView()} and
     * returns the content view, or to override {@link #overrideContentViewLayout()} and returns the resource id of
     * content view. If both of these two methods returns valid value, the return of {@link #overrideContentView()}
     * will be used, and the return of {@link #overrideContentViewLayout()} will be ignored. After that, override
     * {@link #onInitContentView(View)} to initialize views with the given content view. If content view if
     * {@code null}, method {@link #onInitContentView(View)} will not be called. <br>
     * Root view also contains {@link #mFailureContainerFrameLayout}, {@link #mLoadingContainerFrameLayout} and
     * {@code childFragmentContainerFrameLayout}.
     *
     * @see #overrideContentView()
     * @see #overrideContentViewLayout()
     * @see #onInitContentView(View)
     */
    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mRootViewGroup = (FrameLayout) inflater.inflate(R.layout.eb_fragment, container, false);

        mViewContainerFrameLayout = (FrameLayout) mRootViewGroup.findViewById(R.id.eb_view_container);

        initContentView(mRootViewGroup, inflater, container, savedInstanceState);
        initLoadViews(mRootViewGroup, inflater, container, savedInstanceState);

        return mRootViewGroup;
    }

    private void initContentView(@NonNull ViewGroup rootView, LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mContentViewContainerFrameLayout = (FrameLayout) rootView.findViewById(R.id.eb_content_view_container);

        View contentView = overrideContentView();
        if (contentView == null) {
            int contentViewRes = overrideContentViewLayout();
            if (contentViewRes == 0) {
                return;
            }

            contentView = inflater.inflate(contentViewRes, rootView, false);
        }

        mContentViewContainerFrameLayout.addView(contentView);

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
    // Load.

    private FrameLayout mFailureContainerFrameLayout;
    private FrameLayout mLoadingContainerFrameLayout;

    private void initLoadViews(@NonNull ViewGroup rootView, LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mFailureContainerFrameLayout = (FrameLayout) rootView.findViewById(R.id.eb_failure_container);
        mLoadingContainerFrameLayout = (FrameLayout) rootView.findViewById(R.id.eb_loading_container);
    }

    /**
     * Shows content view, hides failure view and loading view.
     */
    public void setLoadNone() {
        mContentViewContainerFrameLayout.setVisibility(View.VISIBLE);
        mFailureContainerFrameLayout.setVisibility(View.GONE);
        mLoadingContainerFrameLayout.setVisibility(View.GONE);
    }

    /**
     * Shows loading view, hides content view and failure view.
     */
    public void setLoadLoading() {
        mContentViewContainerFrameLayout.setVisibility(View.GONE);
        mFailureContainerFrameLayout.setVisibility(View.GONE);
        mLoadingContainerFrameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Shows failure view, hides content view and loading view.
     *
     * @param listener
     *         {@link View.OnClickListener} of failure view.
     */
    public void setLoadFailure(@Nullable View.OnClickListener listener) {
        mContentViewContainerFrameLayout.setVisibility(View.GONE);
        mFailureContainerFrameLayout.setVisibility(View.VISIBLE);
        mLoadingContainerFrameLayout.setVisibility(View.GONE);

        mFailureContainerFrameLayout.setOnClickListener(listener);
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
            @NonNull final NetCallback<Model> callback) {
        final NetCallback<Model> loadCallback = new NetCallback<Model>() {
            @Override
            public void onLoading() {
                super.onLoading();

                setLoadLoading();
            }

            @Override
            public void onSuccess(@NonNull Model model) {
                super.onSuccess(model);

                setLoadNone();
            }

            @Override
            public void onFailure() {
                super.onFailure();

                setLoadFailure(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        netGet(url, callback);
                    }
                });

            }
        };
        callback.preNetCallbacks.add(loadCallback);

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
        activity.getFragmentManagerHelper().add(webViewFragment, url, true, true);
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

        mChildFragmentManagerHelper.onBackPressed();

        return pop;
    }
}
