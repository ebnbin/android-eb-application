package com.ebnbin.ebapplication.fragment.about;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebnbin.ebapplication.R;

/**
 * Shows about info.
 */
public final class AboutDialogFragment extends DialogFragment {
    private static final String ARG_VERSION_NAME = "version_name";
    private static final String ARG_VERSION_CODE = "version_code";

    public static void show(@NonNull FragmentManager fm, @NonNull String versionName, int versionCode) {
        Bundle args = new Bundle();
        args.putString(ARG_VERSION_NAME, versionName);
        args.putInt(ARG_VERSION_CODE, versionCode);

        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
        aboutDialogFragment.setArguments(args);

        aboutDialogFragment.show(fm, null);
    }

    private String mVersionName;
    private int mVersionCode;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        if (args != null) {
            mVersionName = args.getString(ARG_VERSION_NAME);
            mVersionCode = args.getInt(ARG_VERSION_CODE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = View.inflate(getContext(), R.layout.eb_dialog_fragment_about, null);
        TextView labelTextView = (TextView) view.findViewById(R.id.eb_label);
        labelTextView.setText(R.string.app_label);
        ImageView iconImageView = (ImageView) view.findViewById(R.id.eb_icon);
        iconImageView.setImageResource(R.drawable.eb_icon_128dp);
        String slogan = getString(R.string.app_slogan);
        if (!TextUtils.isEmpty(slogan)) {
            TextView sloganTextView = (TextView) view.findViewById(R.id.eb_slogan);
            sloganTextView.setVisibility(View.VISIBLE);
            sloganTextView.setText(slogan);
        }
        final TextView versionCodeTextView = (TextView) view.findViewById(R.id.eb_version_code);
        versionCodeTextView.setText(getString(R.string.eb_version_code_format, mVersionCode));
        TextView versionNameTextView = (TextView) view.findViewById(R.id.eb_version_name);
        versionNameTextView.setText(mVersionName);
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
