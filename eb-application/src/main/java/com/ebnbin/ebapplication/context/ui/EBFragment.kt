package com.ebnbin.ebapplication.context.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.R
import com.ebnbin.ebapplication.model.EBModel
import com.ebnbin.ebapplication.net.NetHelper
import com.ebnbin.ebapplication.net.NetModelCallback
import com.ebnbin.ebapplication.view.StateView
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * Base [Fragment].
 */
abstract class EBFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initArguments()
        initFragmentHelper(savedInstanceState)
        initOptionsMenu()
    }

    /**
     * Please override [overrideContentView] and return a view, or override [overrideContentViewLayout] and return the
     * layout id of a view to provide the content view. If both of these two functions returns valid values, the return
     * of [overrideContentView] will be used, and the return of [overrideContentViewLayout] will be ignored.
     *
     * You can override [onInitContentView] to change the way of initializing content view.
     */
    override final fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? {
        this.container = container

        onInitContentView(stateView, savedInstanceState)

        return stateView
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        fragmentHelperOnSaveInstanceState(outState)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        disposeNet()

        super.onDestroyView()
    }

    //*****************************************************************************************************************
    // On back pressed.

    /**
     * @return Whether handled.
     */
    open fun onBackPressed(): Boolean {
        val topVisibleFragment = fragmentHelper.topVisible()
        return topVisibleFragment != null && (topVisibleFragment.onBackPressed() || fragmentHelper.pop())
    }

    //*****************************************************************************************************************
    // Arguments.

    /**
     * Initializes properties with arguments.
     */
    private fun initArguments() {
        if (EBUtil.isEmpty(arguments)) return

        onInitArguments(arguments)
    }

    /**
     * Called when initializing properties with arguments.
     *
     * @param args If [getArguments] is empty, this function will not be called.
     */
    protected open fun onInitArguments(args: Bundle) {}

    //*****************************************************************************************************************
    // FragmentHelper.

    /**
     * [FragmentHelper] for managing child fragments. Its [android.support.v4.app.FragmentManager] is
     * [getChildFragmentManager]. Default group is [R.id.eb_state_view].
     */
    val fragmentHelper: FragmentHelper by lazy {
        FragmentHelper(childFragmentManager, R.id.eb_state_view)
    }

    private fun initFragmentHelper(savedInstanceState: Bundle?) {
        fragmentHelper.onRestoreInstanceState(savedInstanceState)
    }

    private fun fragmentHelperOnSaveInstanceState(outState: Bundle?) {
        fragmentHelper.onSaveInstanceState(outState)
    }

    //*****************************************************************************************************************
    // Other fragmentHelpers.

    /**
     * Activity's [FragmentHelper].
     */
    val rootFragmentHelper: FragmentHelper by lazy {
        ebActivity.fragmentHelper
    }

    /**
     * [FragmentHelper] for managing self. Either its parent fragment's [FragmentHelper] or activity's
     * [FragmentHelper].
     */
    val parentFragmentHelper: FragmentHelper by lazy {
        parentEBFragment?.fragmentHelper ?: rootFragmentHelper
    }

    //*****************************************************************************************************************
    // Options menu.

    private fun initOptionsMenu() {
        setHasOptionsMenu(overrideHasOptionsMenu())
    }

    protected open fun overrideHasOptionsMenu(): Boolean {
        return false
    }

    //*****************************************************************************************************************
    // Content view.

    private var container: ViewGroup? = null

    val stateView: StateView by lazy {
        layoutInflater.inflate(R.layout.eb_fragment, container, false)!! as StateView
    }

    /**
     * @see [onCreateView]
     */
    protected open fun onInitContentView(stateView: StateView, savedInstanceState: Bundle?) {
        var contentView = overrideContentView()
        if (contentView == null) {
            val contentViewLayout = overrideContentViewLayout()
            if (contentViewLayout == 0) return

            contentView = layoutInflater.inflate(contentViewLayout, stateView, false)
        }

        stateView.addView(contentView)
    }

    /**
     * @see [onCreateView]
     */
    protected open fun overrideContentView(): View? = null

    /**
     * @see [onCreateView]
     */
    @LayoutRes protected open fun overrideContentViewLayout(): Int = 0

    //*****************************************************************************************************************
    // Contexts.

    /**
     * Returns [getActivity] and casts to [AppCompatActivity]. If `null` is returned, throws a [RuntimeException].
     */
    val appCompatActivity: AppCompatActivity by lazy {
        activity!! as AppCompatActivity
    }

    /**
     * Returns [getActivity] and casts to [EBActivity]. If `null` is returned, throws a [RuntimeException].
     */
    val ebActivity: EBActivity by lazy {
        activity!! as EBActivity
    }

    /**
     * Returns [getParentFragment] and casts to [EBFragment]. May be `null`.
     */
    val parentEBFragment: EBFragment? by lazy {
        parentFragment as EBFragment?
    }

    //*****************************************************************************************************************
    // Net.

    private val netTag = hashCode()

    /**
     * Gets a url async with tag of current fragment. And added with a default pre-callback for loading ui.
     *
     * @return Current [Call].
     */
    protected fun <Model: EBModel> netGet(url: String, callback: NetModelCallback<Model>): Call {
        val preCallback = object : NetModelCallback<Model>() {
            override fun onBegin(call: Call) {
                super.onBegin(call)

                stateView.stateLoading()
            }

            override fun onSuccess(call: Call, model: Model, response: Response, byteArray: ByteArray) {
                super.onSuccess(call, model, response, byteArray)

                stateView.clearState()
            }

            override fun onFailure(call: Call, errorCode: Int, e: IOException?, response: Response?) {
                super.onFailure(call, errorCode, e, response)

                failure()
            }

            override fun onCancel(call: Call) {
                super.onCancel(call)

                failure()
            }

            override fun onEnd(call: Call) {
                super.onEnd(call)

                callback.preCallbacks.remove(this)
            }

            private fun failure() {
                stateView.stateFailure(object : StateView.OnRefreshListener {
                    override fun onRefresh() {
                        netGet(url, callback)
                    }
                })
            }
        }
        callback.preCallbacks.add(preCallback)

        return NetHelper.instance.get(netTag, url, callback)
    }

    /**
     * Cancels and removes all saved [Call]s by tag of current fragment.
     */
    private fun disposeNet() {
        NetHelper.instance.cancelCalls(netTag)
    }

    //*****************************************************************************************************************
    // ActionBar fragment.

    /**
     * [EBActionBarFragment] type parent fragment. May be `null`.
     */
    val actionBarParentFragment: EBActionBarFragment? by lazy {
        getTParentFragment(EBActionBarFragment::class.java, this)
    }

    /**
     * Gets a specify type parent fragment. Or returns `null` if not found.
     */
    private fun <T> getTParentFragment(tClass: Class<T>, fragment: Fragment?): T? {
        if (fragment == null) return null

        if (tClass.isAssignableFrom(fragment.javaClass)) return tClass.cast(fragment)

        return getTParentFragment(tClass, fragment.parentFragment)
    }

    //*****************************************************************************************************************
    // Front.

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) onFront()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) onFront()
    }

    /**
     * Called when current fragment is shown from hidden or is visible to user in [android.support.v4.view.ViewPager].
     */
    @CallSuper protected open fun onFront() {}
}
