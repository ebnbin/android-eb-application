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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        initArguments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        netCancelCalls();
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
    // OnCreateView.

    /**
     * This method is overrode, a new way to initialize content view is to override {@link #overrideContentView()} and
     * returns the content view, or to override {@link #overrideContentViewRes()} and returns the resource id of
     * content view. If both of these two methods returns valid value, the return of {@link #overrideContentView()}
     * will be used, and the return of {@link #overrideContentViewRes()} will be ignored. After that, override
     * {@link #onInitContentView(View)} to initialize views with the given content view. If content view if
     * {@code null}, method {@link #onInitContentView(View)} will not be called. <br>
     * If this method is overrode in subclasses, this way of initializing content view will be ignored, and methods
     * {@link #overrideContentView()},{@link #overrideContentViewRes()} and {@link #onInitContentView(View)} will not
     * be called.
     *
     * @see #overrideContentView()
     * @see #overrideContentViewRes()
     * @see #onInitContentView(View)
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View contentView = overrideContentView();
        if (contentView == null) {
            int contentViewRes = overrideContentViewRes();
            if (contentViewRes == 0) {
                return null;
            }

            contentView = inflater.inflate(contentViewRes, container, false);
        }

        onInitContentView(contentView);

        return contentView;
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
    protected int overrideContentViewRes() {
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
     * Gets a url async with tag of current fragment.
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
    protected final <Model extends EBModel> Call netGet(@NonNull String url, @NonNull NetCallback<Model> callback) {
        return NetHelper.getInstance().get(hashCode(), url, callback);
    }

    /**
     * Cancels and removes all saved {@link Call} by tag of current fragment.
     */
    private void netCancelCalls() {
        NetHelper.getInstance().cancelCalls(hashCode());
    }
}
