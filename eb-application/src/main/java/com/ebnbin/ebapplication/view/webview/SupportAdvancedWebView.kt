package com.ebnbin.ebapplication.view.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.webkit.ValueCallback
import im.delight.android.webview.AdvancedWebView
import java.lang.ref.WeakReference

/**
 * Add simple support for [Fragment].
 */
open class SupportAdvancedWebView : AdvancedWebView {
    protected var supportFragment: WeakReference<Fragment>? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @JvmOverloads fun setListener(supportFragment: Fragment?, listener: Listener,
            requestCodeFilePicker: Int = AdvancedWebView.REQUEST_CODE_FILE_PICKER) {
        if (supportFragment != null) {
            this.supportFragment = WeakReference(supportFragment)
        } else {
            this.supportFragment = null
        }

        setListener(listener, requestCodeFilePicker)
    }

    override fun setGeolocationDatabasePath() {
        val activity: Activity

        if (supportFragment != null && supportFragment!!.get() != null && supportFragment!!.get()!!.activity != null) {
            activity = supportFragment!!.get()!!.activity
        } else if (mFragment != null && mFragment.get() != null && mFragment.get()!!.activity != null) {
            activity = mFragment.get()!!.activity
        } else if (mActivity != null && mActivity.get() != null) {
            activity = mActivity.get()!!
        } else {
            return
        }

        @Suppress("DEPRECATION")
        settings.setGeolocationDatabasePath(activity.filesDir.path)
    }

    override fun openFileInput(fileUploadCallbackFirst: ValueCallback<Uri>,
            fileUploadCallbackSecond: ValueCallback<Array<Uri>>, allowMultiple: Boolean) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null)
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null)
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond

        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)

        if (allowMultiple) {
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

        i.type = mUploadableFileTypes

        if (supportFragment != null && supportFragment!!.get() != null) {
            supportFragment!!.get()!!.startActivityForResult(Intent.createChooser(i, fileUploadPromptLabel),
                    mRequestCodeFilePicker)
        } else if (mFragment != null && mFragment.get() != null) {
            mFragment.get()!!.startActivityForResult(Intent.createChooser(i, fileUploadPromptLabel),
                    mRequestCodeFilePicker)
        } else if (mActivity != null && mActivity.get() != null) {
            mActivity.get()!!.startActivityForResult(Intent.createChooser(i, fileUploadPromptLabel),
                    mRequestCodeFilePicker)
        }
    }
}
