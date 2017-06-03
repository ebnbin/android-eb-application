package com.ebnbin.ebapplication.fragment.about;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebnbin.eb.util.EBUtil;
import com.ebnbin.ebapplication.R;

/**
 * Shows about info.
 */
public final class AboutDialogFragment extends DialogFragment {
    public static void showDialog(@NonNull FragmentManager fm) {
        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
        aboutDialogFragment.show(fm, AboutDialogFragment.class.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = View.inflate(getContext(), R.layout.eb_dialog_fragment_about, null);
        TextView labelTextView = (TextView) view.findViewById(R.id.eb_label);
        labelTextView.setText(R.string.app_label);
        ImageView iconImageView = (ImageView) view.findViewById(R.id.eb_icon);
        iconImageView.setImageResource(R.drawable.eb_icon_128);
        String slogan = getString(R.string.app_slogan);
        if (!TextUtils.isEmpty(slogan)) {
            TextView sloganTextView = (TextView) view.findViewById(R.id.eb_slogan);
            sloganTextView.setVisibility(View.VISIBLE);
            sloganTextView.setText(slogan);
        }
        final TextView versionCodeTextView = (TextView) view.findViewById(R.id.eb_version_code);
        versionCodeTextView.setText(getString(R.string.eb_version_code_format, EBUtil.INSTANCE.getVersionCode()));
        TextView versionNameTextView = (TextView) view.findViewById(R.id.eb_version_name);
        versionNameTextView.setText(EBUtil.INSTANCE.getVersionName());
        versionNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionCodeTextView.setVisibility(View.VISIBLE);
            }
        });
        String linkA = getString(R.string.app_link_a);
        if (!TextUtils.isEmpty(linkA)) {
            TextView linkATextView = (TextView) view.findViewById(R.id.eb_link_a);
            linkATextView.setVisibility(View.VISIBLE);
            linkATextView.setText(linkA);
        }
        String linkB = getString(R.string.app_link_b);
        if (!TextUtils.isEmpty(linkB)) {
            TextView linkBTextView = (TextView) view.findViewById(R.id.eb_link_b);
            linkBTextView.setVisibility(View.VISIBLE);
            linkBTextView.setText(linkB);
        }
        String linkC = getString(R.string.app_link_c);
        if (!TextUtils.isEmpty(linkC)) {
            TextView linkCTextView = (TextView) view.findViewById(R.id.eb_link_c);
            linkCTextView.setVisibility(View.VISIBLE);
            linkCTextView.setText(linkC);
        }
        TextView linkEbnbinTextView = (TextView) view.findViewById(R.id.eb_link_ebnbin);
        linkEbnbinTextView.setText(R.string.eb_dialog_fragment_about_link_ebnbin);
        builder.setView(view);

        builder.setPositiveButton(R.string.eb_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }
}
