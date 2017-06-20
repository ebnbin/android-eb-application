package com.ebnbin.ebapplication.context.ui

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.StyleRes
import android.support.v7.app.AppCompatActivity
import com.ebnbin.ebapplication.R
import com.ebnbin.ebapplication.fragment.webview.WebViewActionBarFragment

/**
 * Base [Activity].
 */
abstract class EBActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        initTaskDescription()

        super.onCreate(savedInstanceState)

        initFragmentHelper(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        fragmentHelperOnSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (fragmentHelperOnBackPressed()) {
            return
        }

        super.onBackPressed()

        fragmentHelperOnBackPressedAfterSuper()
    }

    //*****************************************************************************************************************
    // Custom theme.

    private fun initTheme() {
        @StyleRes
        var themeId = overrideTheme()

        if (themeId == -1) {
            return
        }

        if (themeId == 0) {
            themeId = DEFAULT_THEME_ID
        }

        setTheme(themeId)
    }

    /**
     * Overrides this method to set a custom theme.
     *
     * @return Sets the default custom theme if `0` is returned, not to set a custom theme if `-1` is returned.
     */
    @StyleRes
    protected fun overrideTheme(): Int = 0

    //*****************************************************************************************************************
    // Custom TaskDescription.

    private fun initTaskDescription() {
        setTaskDescription(taskDescription)
    }

    private val taskDescription: ActivityManager.TaskDescription by lazy {
        var icon: Bitmap? = null
        val vectorDrawable = getDrawable(R.drawable.eb) as VectorDrawable?
        if (vectorDrawable != null) {
            vectorDrawable.setTint(Color.WHITE)

            val size = resources.getDimensionPixelSize(R.dimen.eb_task_description_icon_size)
            icon = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(icon!!)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
        }

        @ColorInt val colorPrimary = getColor(R.color.eb_primary_light)

        ActivityManager.TaskDescription(null, icon, colorPrimary)
    }

    //*****************************************************************************************************************
    // FragmentHelper.

    val fragmentHelper: FragmentHelper by lazy {
        FragmentHelper(supportFragmentManager, android.R.id.content)
    }

    private fun initFragmentHelper(savedInstanceState: Bundle?) {
        fragmentHelper.onRestoreInstanceState(savedInstanceState)
    }

    private fun fragmentHelperOnSaveInstanceState(outState: Bundle?) {
        fragmentHelper.onSaveInstanceState(outState)
    }

    /**
     * @return Whether handled. Call super if `false` is returned.
     */
    private fun fragmentHelperOnBackPressed(): Boolean {
        val topVisibleFragment = fragmentHelper.topVisible()
        return topVisibleFragment != null && topVisibleFragment.onBackPressed()
    }

    /**
     * Can't know whether popped, always check after calling super.
     */
    private fun fragmentHelperOnBackPressedAfterSuper() {
        fragmentHelper.onPopped()
    }

    //*****************************************************************************************************************
    // Contexts.

    /**
     * Returns current instance as a [Context].
     */
    val context: Context
        get() = this

    /**
     * Returns current instance as an [Activity].
     */
    val activity: Activity
        get() = this

    /**
     * Returns current instance as an [AppCompatActivity].
     */
    val appCompatActivity: AppCompatActivity
        get() = this

    /**
     * Returns current instance as an [EBActivity].
     */
    val ebActivity: EBActivity
        get() = this

    //*****************************************************************************************************************
    // WebView.

    /**
     * Loads url using [WebViewActionBarFragment].
     */
    fun loadUrl(url: String) {
        val fragment = WebViewActionBarFragment.newInstance(url)
        fragmentHelper.push(fragment)
    }

    //*****************************************************************************************************************

    companion object {
        //*************************************************************************************************************
        // Custom theme.

        /**
         * Default theme id.
         */
        @StyleRes
        private val DEFAULT_THEME_ID = R.style.EBLightTheme
    }
}
