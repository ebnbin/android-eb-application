package com.ebnbin.ebapplication.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.webkit.ValueCallback;

import java.lang.ref.WeakReference;

import im.delight.android.webview.AdvancedWebView;

/**
 * Add simple support for {@link Fragment}.
 */
public class EBWebView extends AdvancedWebView {
    protected WeakReference<Fragment> mSupportFragment;

    public EBWebView(Context context) {
        super(context);
    }

    public EBWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EBWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(final Fragment supportFragment, final Listener listener) {
        setListener(supportFragment, listener, REQUEST_CODE_FILE_PICKER);
    }

    public void setListener(final Fragment supportFragment, final Listener listener, final int requestCodeFilePicker) {
        if (supportFragment != null) {
            mSupportFragment = new WeakReference<>(supportFragment);
        } else {
            mSupportFragment = null;
        }

        setListener(listener, requestCodeFilePicker);
    }

    @Override
    @SuppressLint("NewApi")
    protected void setGeolocationDatabasePath() {
        final Activity activity;

        if (mSupportFragment != null && mSupportFragment.get() != null && Build.VERSION.SDK_INT >= 11
                && mSupportFragment.get().getActivity() != null) {
            activity = mSupportFragment.get().getActivity();
        } else
        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11 && mFragment.get().getActivity() != null) {
            activity = mFragment.get().getActivity();
        }
        else if (mActivity != null && mActivity.get() != null) {
            activity = mActivity.get();
        }
        else {
            return;
        }

        getSettings().setGeolocationDatabasePath(activity.getFilesDir().getPath());
    }

    @Override
    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);

        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }

        i.setType(mUploadableFileTypes);

        if (mSupportFragment != null && mSupportFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
            mSupportFragment.get().startActivityForResult(
                    Intent.createChooser(i, getFileUploadPromptLabel()), mRequestCodeFilePicker);
        } else
        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
            mFragment.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mRequestCodeFilePicker);
        }
        else if (mActivity != null && mActivity.get() != null) {
            mActivity.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mRequestCodeFilePicker);
        }
    }
}
