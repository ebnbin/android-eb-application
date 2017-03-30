package com.ebnbin.ebapplication.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ebnbin.ebapplication.R;
import com.ebnbin.ebapplication.fragment.WebViewFragment;
import com.ebnbin.ebapplication.net.NetCallback;
import com.ebnbin.ebapplication.net.NetHelper;

import java.util.Arrays;

import okhttp3.Call;

/**
 * Base {@link Fragment}.
 */
public abstract class EBFragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initLayoutInflater();

        initSupportFragmentManager();

        initArguments();
    }

    @Override
    public void onDestroy() {

        netCancelCalls();

        disposeSupportFragmentManager();

        super.onDestroy();
    }

    //*****************************************************************************************************************
    // Contexts.

    /**
     * Returns {@link #getActivity()} and casts to {@link AppCompatActivity}.
     *
     * @return A {@link FragmentActivity}.
     */
    @Nullable
    public final AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
    }

    /**
     * Returns {@link #getActivity()} and casts to {@link EBActivity}.
     *
     * @return A {@link FragmentActivity}.
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

                callback.netCallbacks.remove(this);

                setLoadNone();
            }

            @Override
            public void onFailure() {
                super.onFailure();

                callback.netCallbacks.remove(this);

                setLoadFailure(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        netGet(url, callback);
                    }
                });

            }

            @Override
            public void onCancel() {
                super.onCancel();

                callback.netCallbacks.remove(this);

                setLoadFailure(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        netGet(url, callback);
                    }
                });
            }
        };
        callback.netCallbacks.add(loadCallback);

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
        activity.addFragment(webViewFragment, url);
    }

    //*****************************************************************************************************************
    // FragmentManager.

    /**
     * For invalidating child fragments that added in content container.
     */
    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener
            = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if (getChildFragmentManager().getBackStackEntryCount() == mContentFragmentTagArraySet.size()) {
                return;
            }

            for (String tag : mContentFragmentTagArraySet) {
                EBFragment fragment = (EBFragment) getChildFragmentManager().findFragmentByTag(tag);
                if (fragment != null) {
                    continue;
                }

                mContentFragmentTagArraySet.remove(tag);
            }

            invalidateFragmentsShowOrHide();
        }
    };

    private void initSupportFragmentManager() {
        getChildFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    private void disposeSupportFragmentManager() {
        getChildFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    //*****************************************************************************************************************
    // Content fragments.

    /**
     * All child fragment tags that added to content container of current fragment.
     */
    private final ArraySet<String> mContentFragmentTagArraySet = new ArraySet<>();

    /**
     * Adds an {@link EBFragment} to content container and adds it to default back stack. If {@code tag} is exist, do
     * nothing.
     *
     * @param tag
     *         If {@code null}, {@code fragment.getClass().getName()} will be used.
     */
    public void addFragment(@NonNull EBFragment fragment, @Nullable String tag) {
        String validTag = tag == null ? fragment.getClass().getName() : tag;

        if (mContentFragmentTagArraySet.contains(validTag)) {
            return;
        }

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.eb_root, fragment, validTag)
                .addToBackStack(null)
                .commit();

        mContentFragmentTagArraySet.add(validTag);

        invalidateFragmentsShowOrHide();
    }

    //*****************************************************************************************************************
    // Shows and hides fragments.

    /**
     * Invalidates whether to show or to hide content fragments.
     */
    private void invalidateFragmentsShowOrHide() {
        mViewContainerFrameLayout.setVisibility(mContentFragmentTagArraySet.isEmpty() ? View.VISIBLE : View.GONE);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        int size = mContentFragmentTagArraySet.size();
        String[] tags = mContentFragmentTagArraySet.toArray(new String[]{});
        for (int i = 0; i < tags.length - 1; i++) {
            String tag = tags[i];
            EBFragment fragment = (EBFragment) getChildFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                ft = ft.hide(fragment);
            }
        }

        if (size > 0) {
            String topTag = mContentFragmentTagArraySet.valueAt(size - 1);
            EBFragment topFragment = (EBFragment) getChildFragmentManager().findFragmentByTag(topTag);
            if (topFragment != null) {
                ft = ft.show(topFragment);
            }
        }

        ft.commit();
    }

    //*****************************************************************************************************************
    // Instance state.

    private static final String STATE_CONTENT_FRAGMENT_TAGS = "content_fragment_tags";

    private boolean restoreContentFragmentTags(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return false;
        }

        String[] contentFragmentTags = savedInstanceState.getStringArray(STATE_CONTENT_FRAGMENT_TAGS);
        if (contentFragmentTags == null) {
            return false;
        }

        mContentFragmentTagArraySet.addAll(Arrays.asList(contentFragmentTags));

        return true;
    }

    private void saveContentFragmentTags(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        String[] contentFragmentTags = mContentFragmentTagArraySet.toArray(new String[]{});
        outState.putStringArray(STATE_CONTENT_FRAGMENT_TAGS, contentFragmentTags);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        restoreContentFragmentTags(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveContentFragmentTags(outState);
    }

    //*****************************************************************************************************************

    /**
     * Handles back pressed.
     */
    public boolean onBackPressed() {
        int size = mContentFragmentTagArraySet.size();
        if (size > 0) {
            String tag = mContentFragmentTagArraySet.valueAt(size - 1);

            EBFragment fragment = (EBFragment) getChildFragmentManager().findFragmentByTag(tag);
            if (fragment != null && !fragment.onBackPressed()) {
                return false;
            }
        }

        return !getChildFragmentManager().popBackStackImmediate();
    }
}
