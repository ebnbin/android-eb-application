package com.ebnbin.ebapplication.net;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.ebnbin.eb.base.EBRuntimeException;
import com.ebnbin.ebapplication.base.EBApplication;
import com.ebnbin.ebapplication.base.EBModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Helper for net.
 */
public final class NetHelper {
    private static NetHelper sInstance;

    /**
     * Called in {@link EBApplication}.
     */
    public static void init() {
        if (sInstance != null) {
            throw new EBRuntimeException();
        }

        sInstance = new NetHelper();
    }

    @NonNull
    public static NetHelper getInstance() {
        if (sInstance == null) {
            throw new EBRuntimeException();
        }

        return sInstance;
    }

    private NetHelper() {
    }

    /**
     * Called in {@link EBApplication}.
     */
    public void dispose() {
        cancelCalls();

        sInstance = null;
    }

    //*****************************************************************************************************************

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    /**
     * All {@link Call} saved.
     */
    private final Map<Object, List<Call>> mCallsMap = new ArrayMap<>();

    /**
     * Gets a url async.
     *
     * @param tag
     *         Activities or fragments tag.
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
    public <Model extends EBModel> Call get(@NonNull Object tag, @NonNull String url,
            @NonNull final NetCallback<Model> callback) {
        Request request = new Request.Builder().tag(tag).url(url).build();

        Call call = mOkHttpClient.newCall(request);
        addCall(call);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    return;
                }

                removeCall(call);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                removeCall(call);

                if (!response.isSuccessful()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure();
                        }
                    });

                    return;
                }

                String responseString = response.body().string();
                Type type = ((ParameterizedType) (callback.getClass().getGenericSuperclass()))
                        .getActualTypeArguments()[0];

                final Model model;
                try {
                    Gson gson = new Gson();
                    model = gson.fromJson(responseString, type);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure();
                        }
                    });

                    return;
                }

                if (model == null || !model.isValid()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure();
                        }
                    });

                    return;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(model);
                    }
                });
            }
        });

        return call;
    }

    //*****************************************************************************************************************
    // Add, remove.

    /**
     * Saves a {@link Call}.
     *
     * @param call {@link Call}.
     */
    private void addCall(@NonNull Call call) {
        Object tag = call.request().tag();
        if (tag == null) {
            return;
        }

        List<Call> calls = mCallsMap.get(tag);
        if (calls == null) {
            calls = new ArrayList<>();
            mCallsMap.put(tag, calls);
        }

        calls.add(call);
    }

    /**
     * Removes a saved {@link Call}.
     *
     * @param call {@link Call}.
     */
    private void removeCall(@NonNull Call call) {
        Object tag = call.request().tag();
        if (tag == null) {
            return;
        }

        List<Call> calls = mCallsMap.get(tag);
        if (calls == null) {
            return;
        }

        calls.remove(call);
    }

    /**
     * Removes all saved calls by tag.
     *
     * @param tag
     *         Tag.
     */
    public void removeCalls(@NonNull Object tag) {
        mCallsMap.remove(tag);
    }

    /**
     * Clears all saved calls.
     */
    public void clearCalls() {
        mCallsMap.clear();
    }

    //*****************************************************************************************************************
    // Cancel.

    /**
     * Cancels and removes all saved {@link Call} by tag.
     *
     * @param tag
     *         Tag.
     */
    public void cancelCalls(@NonNull Object tag) {
        cancelCalls(tag, true);
    }

    /**
     * Cancels all saved {@link Call} by tag.
     *
     * @param tag
     *         Tag.
     * @param remove
     *         Whether to remove saved {@link Call} after cancels.
     */
    public void cancelCalls(@NonNull Object tag, boolean remove) {
        List<Call> calls = mCallsMap.get(tag);
        if (calls == null) {
            return;
        }

        for (Call call : calls) {
            call.cancel();
        }

        if (!remove) {
            return;
        }

        removeCalls(tag);
    }

    /**
     * Cancels and clears all saved {@link Call}.
     */
    public void cancelCalls() {
        cancelCalls(true);
    }

    /**
     * Cancels all saved {@link Call}.
     *
     * @param remove
     *         Whether to clear saved {@link Call} after cancels.
     */
    public void cancelCalls(boolean remove) {
        for (Object tag : mCallsMap.keySet()) {
            cancelCalls(tag, false);
        }

        if (!remove) {
            return;
        }

        clearCalls();
    }
}
