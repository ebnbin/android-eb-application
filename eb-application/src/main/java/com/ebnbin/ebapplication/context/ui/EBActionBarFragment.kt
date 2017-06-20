package com.ebnbin.ebapplication.context.ui

import android.animation.AnimatorInflater
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.util.ArrayMap
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.R
import com.ebnbin.ebapplication.view.StateView

/**
 * Base [EBFragment] with ActionBar.
 */
abstract class EBActionBarFragment : EBFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initFragmentHelper()
    }

    private fun initFragmentHelper() {
        fragmentHelper.defGroup = coordinatorLayoutContentId
    }

    //*****************************************************************************************************************
    // Content view.

    private var actionBarContainer: ViewGroup? = null

    val coordinatorLayout: CoordinatorLayout by lazy {
        actionBarContainer!!.findViewById<CoordinatorLayout>(R.id.eb_coordinator_layout)
    }

    val appBarLayout: AppBarLayout by lazy {
        actionBarContainer!!.findViewById<AppBarLayout>(R.id.eb_app_bar_layout)
    }

    val collapsingToolbarLayout: CollapsingToolbarLayout by lazy {
        actionBarContainer!!.findViewById<CollapsingToolbarLayout>(R.id.eb_collapsing_toolbar_layout)
    }

    val collapsingToolbarLayoutContentFrameLayout: FrameLayout by lazy {
        actionBarContainer!!.findViewById<FrameLayout>(R.id.eb_collapsing_toolbar_layout_content)
    }

    val toolbar: Toolbar by lazy {
        actionBarContainer!!.findViewById<Toolbar>(R.id.eb_toolbar)
    }

    val coordinatorLayoutContentFrameLayout: FrameLayout by lazy {
        actionBarContainer!!.findViewById<FrameLayout>(R.id.eb_coordinator_layout_content)
    }

    private val defAppBarLayoutStateListAnimator by lazy {
        appBarLayout.stateListAnimator
    }

    private val ignoreExpandedAppBarLayoutStateListAnimator by lazy {
        AnimatorInflater.loadStateListAnimator(context,
                R.animator.eb_app_bar_layout_state_list_animator_ignore_expanded)
    }

    private var appBarLayoutExpanded: Boolean = true

    val onOffsetChangedListener: AppBarLayout.OnOffsetChangedListener by lazy {
        // Init first.
        defAppBarLayoutStateListAnimator

        AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val visibleHeight = appBarLayout.height + verticalOffset

            if (visibleHeight == toolbar.height && actionBarMode == ActionBarMode.SCROLL) {
                appBarLayout.stateListAnimator = ignoreExpandedAppBarLayoutStateListAnimator
            } else {
                appBarLayout.stateListAnimator = defAppBarLayoutStateListAnimator
            }

            appBarLayoutExpanded = visibleHeight != 0
        }
    }

    override fun onInitContentView(stateView: StateView, savedInstanceState: Bundle?) {
        actionBarContainer = layoutInflater.inflate(R.layout.eb_fragment_action_bar, stateView, true)!! as ViewGroup

        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener)

        initActionBarMode(savedInstanceState)

        var contentView = overrideContentView()
        if (contentView == null) {
            val contentViewLayout = overrideContentViewLayout()
            if (contentViewLayout == 0) return

            contentView = layoutInflater.inflate(contentViewLayout, coordinatorLayoutContentFrameLayout, false)
        }

        coordinatorLayoutContentFrameLayout.addView(contentView)
    }

    override fun onDestroyView() {
        if (postActionBarModeRunnable != null) {
            postActionBarModeRunnable!!.run()
        }

        appBarLayout.removeOnOffsetChangedListener(onOffsetChangedListener)

        super.onDestroyView()
    }

    //*****************************************************************************************************************
    // AppBarLayout can drag.

    private fun setAppBarLayoutCanDrag(appBarLayoutCanDrag: Boolean) {
        EBUtil.handler.post { object : Runnable {
            override fun run() {
                if (!ViewCompat.isLaidOut(appBarLayout)) {
                    EBUtil.handler.postDelayed(this, 16L)

                    return
                }

                val behavior = (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams)
                        .behavior as AppBarLayout.Behavior? ?: return
                behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return appBarLayoutCanDrag
                    }
                })
            }
        } }
    }

    //*****************************************************************************************************************
    // Nested scrolling.

    private var nestedScrollingChild: NestedScrollingChild? = null
    private var nestedScrollingEnabled = false

    fun setNestedScrollingChild(nestedScrollingChild: NestedScrollingChild?) {
        this.nestedScrollingChild = nestedScrollingChild

        invalidateNestedScrolling()
    }

    private fun setNestedScrollingEnabled(nestedScrollingEnabled: Boolean) {
        this.nestedScrollingEnabled = nestedScrollingEnabled

        invalidateNestedScrolling()
    }

    private fun invalidateNestedScrolling() {
        if (nestedScrollingChild != null && nestedScrollingChild !is SwipeRefreshLayout) {
            nestedScrollingChild!!.isNestedScrollingEnabled = nestedScrollingEnabled
        }
    }

    //*****************************************************************************************************************
    // ActionBar mode.

    enum class ActionBarMode {
        STANDARD,
        SCROLL
    }

    private var actionBarMode = ActionBarMode.STANDARD

    fun setActionBarMode(actionBarMode: ActionBarMode, forceInvalidate: Boolean, expanded: Boolean?,
            animate: Boolean) {
        if (!forceInvalidate && this.actionBarMode == actionBarMode) return

        postActionBarModeRunnable?.run()

        if (expanded != null) {
            appBarLayoutExpanded = expanded
        }

        appBarLayout.setExpanded(appBarLayoutExpanded, animate)

        val actionBarModeConstant = actionBarModeConstants[actionBarMode]!!

        setAppBarLayoutCanDrag(actionBarModeConstant.appBarLayoutCanDrag)

        postActionBarModeRunnable = PostActionBarModeRunnable(actionBarMode)
        if (animate) {
            EBUtil.handler.postDelayed(postActionBarModeRunnable!!,
                    actionBarModeConstant.appBarLayoutAnimationDuration)
        } else{
            postActionBarModeRunnable!!.run()
        }

        setNestedScrollingEnabled(actionBarModeConstant.nestedScrollingEnabled)
    }

    private fun initActionBarMode(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            setActionBarMode(ActionBarMode.STANDARD, true, true, false)

            return
        }

        val actionBarMode = savedInstanceState.getSerializable(INSTANCE_STATE_ACTION_BAR_MODE) as ActionBarMode
        val appBarLayoutExpanded = savedInstanceState.getBoolean(INSTANCE_STATE_APP_BAR_LAYOUT_EXPANDED)

        setActionBarMode(actionBarMode, true, appBarLayoutExpanded, false)
    }

    private fun actionBarModeOnSaveInstanceState(outState: Bundle?) {
        if (outState == null) return

        outState.putSerializable(INSTANCE_STATE_ACTION_BAR_MODE, actionBarMode)
        outState.putBoolean(INSTANCE_STATE_APP_BAR_LAYOUT_EXPANDED, appBarLayoutExpanded)
    }

    //*****************************************************************************************************************

    private var postActionBarModeRunnable: PostActionBarModeRunnable? = null

    private inner class PostActionBarModeRunnable(private val actionBarMode: ActionBarMode) : Runnable {
        override fun run() {
            EBUtil.handler.removeCallbacks(this)

            postActionBarModeRunnable = null

            val actionBarModeConstant = actionBarModeConstants[actionBarMode]!!

            val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
            params.scrollFlags = actionBarModeConstant.collapsingToolbarLayoutScrollFlags

            collapsingToolbarLayoutContentFrameLayout.visibility =
                    if (actionBarModeConstant.collapsingToolbarLayoutContentFrameLayoutVisible) View.VISIBLE
                    else View.GONE

            this@EBActionBarFragment.actionBarMode = actionBarMode

            appCompatActivity.setSupportActionBar(toolbar)
        }
    }

    //*****************************************************************************************************************

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        actionBarModeOnSaveInstanceState(outState)
    }

    //*****************************************************************************************************************

    override fun onFront() {
        super.onFront()

        appCompatActivity.setSupportActionBar(toolbar)
    }

    //*****************************************************************************************************************

    companion object {
        @IdRes protected val collapsingToolbarLayoutContentId = R.id.eb_collapsing_toolbar_layout_content
        @IdRes protected val coordinatorLayoutContentId = R.id.eb_coordinator_layout_content

        //*************************************************************************************************************
        // AppBarLayout animation duration.

        private val APP_BAR_LAYOUT_ANIMATION_DURATION_STANDARD = 300L
        private val APP_BAR_LAYOUT_ANIMATION_DURATION_MAX = 600L

        //*************************************************************************************************************
        // ActionBarMode constants.

        private val actionBarModeConstants = ArrayMap<ActionBarMode, ActionBarModeConstant>()

        private data class ActionBarModeConstant(val appBarLayoutCanDrag: Boolean,
                val appBarLayoutAnimationDuration: Long, val collapsingToolbarLayoutScrollFlags: Int,
                val collapsingToolbarLayoutContentFrameLayoutVisible: Boolean, val nestedScrollingEnabled: Boolean)

        init {
            actionBarModeConstants.put(ActionBarMode.STANDARD, ActionBarModeConstant(false, 0L,
                    AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                            or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED, false, false))
            actionBarModeConstants.put(ActionBarMode.SCROLL, ActionBarModeConstant(false,
                    APP_BAR_LAYOUT_ANIMATION_DURATION_STANDARD, AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                            or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                            or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP, false, true))
        }

        //*****************************************************************************************************************
        // Instance state.

        private val INSTANCE_STATE_ACTION_BAR_MODE = "action_bar_mode"
        private val INSTANCE_STATE_APP_BAR_LAYOUT_EXPANDED = "app_bar_layout_expanded"
    }
}
