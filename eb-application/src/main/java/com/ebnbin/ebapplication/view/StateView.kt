package com.ebnbin.ebapplication.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.ebnbin.eb.util.EBRuntimeException
import com.ebnbin.ebapplication.R

/**
 * An extended [FrameLayout] that switches states easily.
 */
open class StateView : FrameLayout {
    private val layoutInflater: LayoutInflater

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        layoutInflater = LayoutInflater.from(context)
    }

    //*****************************************************************************************************************
    // States.

    enum class State {
        /**
         * No state.
         */
        NONE,
        /**
         * Shows a [ProgressBar] and an optional hint. Child views will be hidden.
         */
        LOADING,
        /**
         * Shows a horizontal style [ProgressBar] at view top and shows child views.
         */
        PROGRESSING,
        /**
         * Shows a refresh [View] and an optional hint. Child views will be hidden.
         */
        FAILURE,
        /**
         * Shows a hint for no data states and hides child views.
         */
        NO_DATA
    }

    /**
     * Each state should have and only have a layer [ViewGroup] added to current [FrameLayout] except [State.NONE].
     */
    var state = State.NONE
        private set

    /**
     * Returns whether there is a state set currently.
     */
    fun hasState(): Boolean {
        return state != State.NONE
    }

    /**
     * Clears current state.
     */
    fun clearState() {
        if (state == State.NONE) {
            return
        }

        removeViewAt(childCount - 1)

        state = State.NONE

        setChildViewsHidden(false)
    }

    /**
     * General method for all states except [State.NONE].
     *
     * @param state Should NOT be [State.NONE].
     * @param hideChildViews Whether to hide child views.
     */
    private fun switchState(state: State, hideChildViews: Boolean) {
        if (state == State.NONE) {
            throw EBRuntimeException()
        }

        clearState()

        setChildViewsHidden(hideChildViews)

        this.state = state
    }

    //*****************************************************************************************************************

    /**
     * Saved visibilities of child views.
     */
    private var childViewsVisibilities: MutableList<Int>? = null

    /**
     * Returns whether child views has been hidden.
     */
    fun isChildViewsHidden(): Boolean {
        return childViewsVisibilities != null
    }

    /**
     * Sets visibilities of child views. Only works if there is no state currently.
     *
     * @param hidden If `true`, saves visibilities of child views and sets them [View.GONE]. Otherwise, restores
     * visibilities of child views that saved. If visibilities has been saved, NEITHER change visibilities of child
     * views NOR add or remove child views.
     */
    fun setChildViewsHidden(hidden: Boolean) {
        if (hasState()) {
            return
        }

        if (hidden) {
            if (childViewsVisibilities != null) {
                return
            }

            childViewsVisibilities = ArrayList<Int>(childCount)

            for (i in 0..childCount - 1) {
                val childView = getChildAt(i)

                childViewsVisibilities!!.add(childView.visibility)
                childView.visibility = View.GONE
            }
        } else {
            if (childViewsVisibilities == null) {
                return
            }

            if (childViewsVisibilities!!.size != childCount) {
                throw EBRuntimeException()
            }

            for (i in childViewsVisibilities!!.indices) {
                getChildAt(i).visibility = childViewsVisibilities!![i]
            }

            childViewsVisibilities = null
        }
    }

    //*****************************************************************************************************************
    // State LOADING.

    private var loadingViewGroup: ViewGroup? = null
    private var loadingHintTextView: TextView? = null

    /**
     * Default hint for state [State.LOADING].
     */
    var defLoadingHint: String = context.getString(R.string.eb_state_view_hint_loading)

    /**
     * Switches to state [State.LOADING].
     *
     * @param hint Optional hint. If `null`, default value will be used. If not set, `null` will be used.
     */
    fun stateLoading(hint: String? = null) {
        switchState(State.LOADING, true)

        if (loadingViewGroup == null) {
            loadingViewGroup = layoutInflater.inflate(R.layout.eb_state_view_loading, this, false) as ViewGroup
            loadingHintTextView = loadingViewGroup!!.findViewById(R.id.eb_hint) as TextView
        }

        val tmpHint = hint ?: defLoadingHint
        if (tmpHint == EMPTY_HINT) {
            loadingHintTextView!!.visibility = View.GONE
            loadingHintTextView!!.text = null
        } else {
            loadingHintTextView!!.visibility = View.VISIBLE
            loadingHintTextView!!.text = tmpHint
        }

        addView(loadingViewGroup)
    }

    //*****************************************************************************************************************
    // State PROGRESSING.

    private var progressingViewGroup: ViewGroup? = null
    private var progressingProgressBar: ProgressBar? = null

    /**
     * Switches to state [State.PROGRESSING].
     *
     * @param progress If `-1`, [ProgressBar] will be indeterminate mode. Otherwise, be between `0` and `100` to
     * disable indeterminate mode. If not set, `-1` will be used.
     * @param updateProgressOnly If `true`, only to change progress if current state is [State.PROGRESSING] already.
     * Otherwise, switches to state [State.PROGRESSING] firstly. If not set, `false` will be used.
     */
    fun stateProgressing(progress: Int = -1, updateProgressOnly: Boolean = false) {
        if (updateProgressOnly) {
            if (state != State.PROGRESSING) {
                return
            }
        } else {
            switchState(State.PROGRESSING, false)

            if (progressingViewGroup == null) {
                progressingViewGroup = layoutInflater.inflate(R.layout.eb_state_view_progressing, this, false)
                        as ViewGroup
                progressingProgressBar = progressingViewGroup!!.findViewById(R.id.progress_bar) as ProgressBar
            }

            addView(progressingViewGroup)
        }

        if (progress == -1) {
            progressingProgressBar!!.isIndeterminate = true
            progressingProgressBar!!.progress = 0
        } else {
            progressingProgressBar!!.isIndeterminate = false
            progressingProgressBar!!.progress = progress
        }
    }

    //*****************************************************************************************************************
    // State FAILURE.

    /**
     * Default [OnRefreshListener] for state [State.FAILURE].
     */
    var defOnRefreshListener: OnRefreshListener = EMPTY_ON_REFRESH_LISTENER
    /**
     * Default hint for  state [State.FAILURE].
     */
    var defFailureHint: String = context.getString(R.string.eb_state_view_hint_failure)

    /**
     * Switches to state [State.FAILURE].
     *
     * @param onRefreshListener [View.OnClickListener] of the refresh [View]. If `null`, default value will be used. If
     * not set, `null` will be used.
     * @param hint Optional hint. If `null`, default value will be used. If not set, `null` will be used.
     */
    fun stateFailure(onRefreshListener: OnRefreshListener? = null, hint: String? = null) {
        val tmpOnRefreshListener = onRefreshListener ?: defOnRefreshListener
        val tmpHint = hint ?: defFailureHint
        stateMessage(State.FAILURE, tmpOnRefreshListener, tmpHint)
    }

    /**
     * On refresh listener for state [State.FAILURE].
     */
    interface OnRefreshListener {
        fun onRefresh()
    }

    //*****************************************************************************************************************
    // State NO_DATA.

    /**
     * Default hint for  state [State.NO_DATA].
     */
    var defNoDataHint: String = context.getString(R.string.eb_state_view_hint_no_data)

    /**
     * Switches to state [State.NO_DATA].
     *
     * @param hint Hint. If `null`, default value will be used. If not set, `null` will be used.
     */
    fun stateNoData(hint: String? = null) {
        val tmpHint = hint ?: defNoDataHint
        stateMessage(State.NO_DATA, EMPTY_ON_REFRESH_LISTENER, tmpHint)
    }

    //*****************************************************************************************************************
    // State FAILURE or NO_DATA.

    private var messageViewGroup: ViewGroup? = null
    private var messageRefreshView: View? = null
    private var messageHintTextView: TextView? = null

    /**
     * Switches to state [State.FAILURE] or [State.NO_DATA].
     *
     * @param state Should be [State.FAILURE] or [State.NO_DATA].
     * @param onRefreshListener If non-empty, shows a refresh [View] and provides its [View.OnClickListener].
     * @param hint Optional hint.
     */
    private fun stateMessage(state: State, onRefreshListener: OnRefreshListener, hint: String) {
        if (state != State.FAILURE && state != State.NO_DATA) {
            throw EBRuntimeException()
        }

        switchState(state, true)

        if (messageViewGroup == null) {
            messageViewGroup = layoutInflater.inflate(R.layout.eb_state_view_message, this, false) as ViewGroup
            messageRefreshView = messageViewGroup!!.findViewById(R.id.eb_refresh)
            messageHintTextView = messageViewGroup!!.findViewById(R.id.eb_hint) as TextView
        }

        if (onRefreshListener == EMPTY_ON_REFRESH_LISTENER) {
            messageRefreshView!!.visibility = View.GONE
            messageRefreshView!!.setOnClickListener(null)
        } else{
            messageRefreshView!!.visibility = View.VISIBLE
            messageRefreshView!!.setOnClickListener { onRefreshListener.onRefresh() }
        }

        if (hint == EMPTY_HINT) {
            messageHintTextView!!.visibility = View.GONE
            messageHintTextView!!.text = null
        } else {
            messageHintTextView!!.visibility = View.VISIBLE
            messageHintTextView!!.text = hint
        }

        addView(messageViewGroup)
    }

    //*****************************************************************************************************************

    companion object {
        //*************************************************************************************************************
        // Empty value for states.

        const val EMPTY_HINT = ""
        val EMPTY_ON_REFRESH_LISTENER = object : StateView.OnRefreshListener {
            override fun onRefresh() {}
        }
    }
}
