package com.ebnbin.ebapplication.context.ui;

import android.app.ActionBar;
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
    //*****************************************************************************************************************
    // Lifecycle.

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initLayoutInflater();

        initArguments();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFragmentHelper(savedInstanceState);

        initSavedActionBarTitle(savedInstanceState);
        initSavedActionBarDisplayHomeAsUp(savedInstanceState);
    }

    /**
     * This method is overrode, a new way to initialize content view is to override {@link #overrideContentView()} and
     * returns the content view, or to override {@link #overrideContentViewLayout()} and returns the resource id of
     * content view. If both of these two methods returns valid value, the return of {@link #overrideContentView()}
     * will be used, and the return of {@link #overrideContentViewLayout()} will be ignored. After that, override
     * {@link #onInitContentView(View)} to initialize views with the content view that set. If content view if
     * {@code null}, method {@link #onInitContentView(View)} will not be called. <br>
     * <p>
     * If this method is overrode in subclasses, some feature will be disabled.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = initContentView(container);

        initFragmentHelperDefGroup();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        savedActionBarTitleOnSaveInstanceState(outState);
        savedActionBarDisplayHomeAsUpOnSaveInstanceState(outState);

        fragmentHelperOnSaveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        disposeNet();

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        restoreActionBarTitle();
        restoreActionBarDisplayHomeAsUp();

        super.onDestroy();
    }

    //*****************************************************************************************************************
    // OnBackPressed.

    /**
     * @return Whether handled.
     */
    public boolean onBackPressed() {
        return fragmentHelperOnBackPressed();
    }

    //*****************************************************************************************************************
    // Contexts.

    /**
     * Returns {@link #getActivity()} and casts to {@link EBActivity}.
     */
    @Nullable
    public final EBActivity getEBActivity() {
        return (EBActivity) getActivity();
    }

    /**
     * Returns {@link #getParentFragment()} and casts to {@link EBFragment}.
     */
    @Nullable
    public final EBFragment getParentEBFragment() {
        return (EBFragment) getParentFragment();
    }

    //*****************************************************************************************************************
    // Handler.

    /**
     * Handler with main {@link Looper}.
     */
    protected final Handler handler = new Handler(Looper.getMainLooper());

    @NonNull
    public Handler getHandler() {
        return handler;
    }

    //*****************************************************************************************************************
    // LayoutInflater.

    private LayoutInflater mLayoutInflater;

    @NonNull
    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    private void initLayoutInflater() {
        mLayoutInflater = LayoutInflater.from(getContext());
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
     * @param args If {@link #getArguments()} returns {@code null}, an empty {@link Bundle} will be used.
     */
    protected void onInitArguments(@NonNull Bundle args) {
    }

    //*****************************************************************************************************************
    // FragmentHelper.

    private FragmentHelper mFragmentHelper;

    @NonNull
    public FragmentHelper getFragmentHelper() {
        return mFragmentHelper;
    }

    private void initFragmentHelper(@Nullable Bundle savedInstanceState) {
        mFragmentHelper = new FragmentHelper(getChildFragmentManager());

        mFragmentHelper.onRestoreInstanceState(savedInstanceState);
    }

    private void initFragmentHelperDefGroup() {
        mFragmentHelper.defGroup = R.id.eb_state_frame_layout;
    }

    private void fragmentHelperOnSaveInstanceState(@Nullable Bundle outState) {
        mFragmentHelper.onSaveInstanceState(outState);
    }

    /**
     * @return Whether handled.
     */
    private boolean fragmentHelperOnBackPressed() {
        EBFragment topVisibleFragment = mFragmentHelper.topVisible();
        return topVisibleFragment != null && (topVisibleFragment.onBackPressed() || mFragmentHelper.pop());
    }

    //*****************************************************************************************************************
    // Other FragmentHelpers.

    @Nullable
    public FragmentHelper getActivityFragmentHelper() {
        EBActivity activity = getEBActivity();
        if (activity == null) {
            return null;
        }

        return activity.getFragmentHelper();
    }

    @Nullable
    public FragmentHelper getParentFragmentHelper() {
        EBFragment fragment = getParentEBFragment();
        if (fragment == null) {
            return null;
        }

        return fragment.getFragmentHelper();
    }

    @Nullable
    public FragmentHelper getParentOrActivityFragmentHelper() {
        FragmentHelper fragmentHelper = getParentFragmentHelper();
        if (fragmentHelper == null) {
            fragmentHelper = getActivityFragmentHelper();
        }

        return fragmentHelper;
    }

    //*****************************************************************************************************************
    // Content view.

    private StateFrameLayout mStateFrameLayout;

    @Nullable
    public StateFrameLayout getStateFrameLayout() {
        return mStateFrameLayout;
    }

    /**
     * @see #onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    @Nullable
    private View initContentView(@Nullable ViewGroup container) {
        mStateFrameLayout = (StateFrameLayout) mLayoutInflater.inflate(R.layout.eb_fragment, container, false);

        View contentView = overrideContentView();
        if (contentView == null) {
            int contentViewRes = overrideContentViewLayout();
            if (contentViewRes == 0) {
                return mStateFrameLayout;
            }

            contentView = mLayoutInflater.inflate(contentViewRes, mStateFrameLayout, false);
        }

        mStateFrameLayout.addView(contentView);

        onInitContentView(contentView);

        return mStateFrameLayout;
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
    // ActionBar.

    @Nullable
    public ActionBar getActionBar() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        return activity.getActionBar();
    }

    //*****************************************************************************************************************
    // ActionBar title.

    private boolean mRestoreActionBarTitle;

    public boolean shouldRestoreActionBarTitle() {
        return mRestoreActionBarTitle;
    }

    public void setRestoreActionBarTitle(boolean restoreActionBarTitle) {
        mRestoreActionBarTitle = restoreActionBarTitle;
    }

    private CharSequence mSavedActionBarTitle;

    private void saveActionBarTitle() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        mSavedActionBarTitle = actionBar.getTitle();
    }

    private void restoreActionBarTitle() {
        if (!mRestoreActionBarTitle) {
            return;
        }

        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setTitle(mSavedActionBarTitle);
    }

    private static final String INSTANCE_STATE_SAVED_ACTION_BAR_TITLE = "saved_action_bar_title";

    private void initSavedActionBarTitle(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            saveActionBarTitle();

            return;
        }

        mSavedActionBarTitle = savedInstanceState.getCharSequence(INSTANCE_STATE_SAVED_ACTION_BAR_TITLE);
    }

    private void savedActionBarTitleOnSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putCharSequence(INSTANCE_STATE_SAVED_ACTION_BAR_TITLE, mSavedActionBarTitle);
    }

    //*****************************************************************************************************************
    // ActionBar displayHomeAsUp.

    private boolean mRestoreActionBarDisplayHomeAsUp;

    public boolean shouldRestoreActionBarDisplayHomeAsUp() {
        return mRestoreActionBarDisplayHomeAsUp;
    }

    public void setRestoreActionBarDisplayHomeAsUp(boolean restoreActionBarDisplayHomeAsUp) {
        mRestoreActionBarDisplayHomeAsUp = restoreActionBarDisplayHomeAsUp;
    }

    private boolean mSavedActionBarDisplayHomeAsUp;

    private void saveActionBarDisplayHomeAsUp() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        mSavedActionBarDisplayHomeAsUp = (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0;
    }

    private void restoreActionBarDisplayHomeAsUp() {
        if (!mRestoreActionBarDisplayHomeAsUp) {
            return;
        }

        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(mSavedActionBarDisplayHomeAsUp);
    }

    private static final String INSTANCE_STATE_SAVED_ACTION_BAR_DISPLAY_HOME_AS_UP
            = "saved_action_bar_display_home_as_up";

    private void initSavedActionBarDisplayHomeAsUp(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            saveActionBarDisplayHomeAsUp();

            return;
        }

        mSavedActionBarDisplayHomeAsUp = savedInstanceState
                .getBoolean(INSTANCE_STATE_SAVED_ACTION_BAR_DISPLAY_HOME_AS_UP);
    }

    private void savedActionBarDisplayHomeAsUpOnSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putBoolean(INSTANCE_STATE_SAVED_ACTION_BAR_DISPLAY_HOME_AS_UP, mSavedActionBarDisplayHomeAsUp);
    }

    //*****************************************************************************************************************
    // Net.

    private final Object mNetTag = hashCode();

    /**
     * Gets a url async with tag of current fragment. And with a default loading pre-callback added.
     *
     * @return Current {@link Call}.
     */
    protected <Model extends EBModel> Call netGet(@NonNull final String url,
            @NonNull final NetModelCallback<Model> callback) {
        final NetModelCallback<Model> loadingPreCallback = new NetModelCallback<Model>() {
            @Override
            public void onLoading(@NonNull Call call) {
                super.onLoading(call);

                if (mStateFrameLayout != null) {
                    mStateFrameLayout.switchLoadingState();
                }
            }

            @Override
            public void onSuccess(@NonNull Call call, @NonNull Model model) {
                super.onSuccess(call, model);

                if (mStateFrameLayout != null) {
                    mStateFrameLayout.clearState();
                }
            }

            @Override
            public void onFailure(@NonNull Call call) {
                super.onFailure(call);

                if (mStateFrameLayout != null) {
                    mStateFrameLayout.switchFailureState(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            netGet(url, callback);
                        }
                    });
                }

            }
        };
        callback.preCallbacks.add(loadingPreCallback);

        return NetHelper.getInstance().get(mNetTag, url, callback);
    }

    /**
     * Cancels and removes all saved {@link Call} by tag of current fragment.
     */
    private void disposeNet() {
        NetHelper.getInstance().cancelCalls(mNetTag);
    }

    //*****************************************************************************************************************
    // WebViewFragment.

    /**
     * Uses a {@link WebViewFragment} to load a url.
     */
    public void webViewLoadUrl(@NonNull String url) {
        EBActivity activity = getEBActivity();
        if (activity == null) {
            return;
        }

        activity.webViewLoadUrl(url);
    }
}
