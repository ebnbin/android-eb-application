package com.ebnbin.ebapplication.net;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.ArraySet;

import com.ebnbin.eb.util.EBRuntimeException;
import com.ebnbin.eb.util.EBUtil;
import com.ebnbin.ebapplication.model.EBModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    //*****************************************************************************************************************
    // Calls.

    /**
     * Saved {@link Call}.
     */
    private final ArrayMap<Object, ArraySet<Call>> mCallMap = new ArrayMap<>();

    /**
     * Adds a {@link Call}.
     */
    private void addCall(@NonNull Call call) {
        Object tag = call.request().tag();
        if (tag == null) {
            return;
        }

        ArraySet<Call> callSet = mCallMap.get(tag);
        if (callSet == null) {
            callSet = new ArraySet<>();
            mCallMap.put(tag, callSet);
        }

        callSet.add(call);
    }

    /**
     * Removes a {@link Call}.
     */
    private void removeCall(@NonNull Call call) {
        Object tag = call.request().tag();
        if (tag == null) {
            return;
        }

        ArraySet<Call> callSet = mCallMap.get(tag);
        if (callSet == null) {
            return;
        }

        callSet.remove(call);
    }

    /**
     * Cancels and removes calls by tag.
     */
    public void cancelCalls(@NonNull Object tag) {
        ArraySet<Call> callSet = mCallMap.remove(tag);
        if (callSet == null) {
            return;
        }

        for (Call call : callSet) {
            call.cancel();
        }
    }

    //*****************************************************************************************************************

    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Gets a url async.
     *
     * @param tag An activity or fragment tag.
     * @param callback Callback to get a model.
     *
     * @param <Model> Subclass of {@link EBModel}.
     *
     * @return Current {@link Call}.
     */
    public <Model extends EBModel> Call get(@NonNull final Object tag, @NonNull String url,
            @NonNull final NetModelCallback<Model> callback) {
        Request request = new Request
                .Builder()
                .tag(tag)
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);

        addCall(call);

        callback.onBeginCallback(call);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postOnFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    postOnFailureCallback();

                    return;
                }

                String responseString = response.body().string();
                Type type = ((ParameterizedType) callback.getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];

                Model model;
                try {
                    Gson gson = new Gson();
                    model = gson.fromJson(responseString, type);
                } catch (JsonSyntaxException e) {
                    EBUtil.INSTANCE.log(e);

                    postOnFailureCallback();

                    return;
                }

                if (model == null || !model.isValid()) {
                    postOnFailureCallback();

                    return;
                }

                postOnSuccessCallback(model);
            }

            /**
             * Checks and returns whether current {@link Call} has been canceled and removed. If that, posted
             * {@link Runnable} should not be executed.
             */
            private boolean canPost() {
                ArraySet<Call> callSet = mCallMap.get(tag);
                return callSet != null && callSet.contains(call);
            }

            /**
             * Posts a success callback.
             */
            private void postOnSuccessCallback(@NonNull final Model model) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!canPost()) {
                            callback.onCancelCallback(call);

                            callback.onEndCallback(call);

                            return;
                        }

                        callback.onSuccessCallback(call, model);

                        removeCall(call);

                        callback.onEndCallback(call);
                    }
                });
            }

            /**
             * Posts a failure callback.
             */
            private void postOnFailureCallback() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!canPost()) {
                            callback.onCancelCallback(call);

                            callback.onEndCallback(call);

                            return;
                        }

                        callback.onFailureCallback(call);

                        removeCall(call);

                        callback.onEndCallback(call);
                    }
                });
            }
        });

        return call;
    }
}
