package com.ebnbin.ebapplication.base;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.ebnbin.ebapplication.net.NetCallback;
import com.ebnbin.ebapplication.net.NetHelper;

import okhttp3.Call;

/**
 * Base {@link Fragment}.
 */
public abstract class EBFragment extends Fragment {
    @Override
    public void onDestroy() {
        super.onDestroy();

        netCancelCalls();
    }

    //*****************************************************************************************************************
    // Tag.

    /**
     * Fragment tag.
     */
    public static final String TAG = EBFragment.class.getName();

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
        return NetHelper.getInstance().get(TAG, url, callback);
    }

    /**
     * Cancels and removes all saved {@link Call} by tag of current fragment.
     */
    private void netCancelCalls() {
        NetHelper.getInstance().cancelCalls(TAG);
    }
}
