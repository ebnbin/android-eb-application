package com.ebnbin.ebapplication.feature.about

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.R

/**
 * Shows about [Dialog].
 */
class AboutDialogFragment : DialogFragment() {
    private var easterEggCount: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        val rootView = View.inflate(context, R.layout.eb_about_dialog_fragment, null) as ViewGroup
        val versionNameTextView = rootView.findViewById(R.id.eb_version_name) as TextView
        val versionCodeTextView = rootView.findViewById(R.id.eb_version_code) as TextView

        val versionNameString = "V${EBUtil.versionName}"
        versionNameTextView.text = versionNameString
        versionNameTextView.setOnClickListener {
            versionCodeTextView.visibility = View.VISIBLE

            ++easterEggCount
        }
        val versionCodeString = " (${EBUtil.versionCode})"
        versionCodeTextView.text = versionCodeString

        builder.setView(rootView)
        builder.setPositiveButton(R.string.eb_ok) { _, _ -> dismiss() }
        builder.setNeutralButton(R.string.eb_easter_egg) { _, _ ->
            if (easterEggCount == 7) {
                Toast.makeText(context, R.string.eb_ebnbin, Toast.LENGTH_LONG).show()
            }
        }

        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

        easterEggCount = 0
    }

    companion object {
        private val TAG = AboutDialogFragment::class.java.name!!

        fun showDialog(fm: FragmentManager) {
            AboutDialogFragment().show(fm, TAG)
        }
    }
}
