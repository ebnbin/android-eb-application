package com.ebnbin.ebapplication.fragment.about;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebnbin.ebapplication.R;

/**
 * Shows about info.
 */
public final class AboutDialogFragment extends DialogFragment {
    private static final String ARG_ICON_ID = "icon_id";
    private static final String ARG_LABEL_ID = "label_id";
    private static final String ARG_SLOGAN = "slogan";
    private static final String ARG_VERSION_NAME = "version_name";
    private static final String ARG_VERSION_CODE = "version_code";
    private static final String ARG_LINK_A = "link_a";
    private static final String ARG_LINK_B = "link_b";
    private static final String ARG_LINK_C = "link_c";

    public static void show(@NonNull FragmentManager fm, @DrawableRes int iconId, @StringRes int labelId,
            @StringRes int slogan, @NonNull String versionName, int versionCode, @StringRes int linkA,
            @StringRes int linkB, @StringRes int linkC) {
        Bundle args = new Bundle();
        args.putInt(ARG_ICON_ID, iconId);
        args.putInt(ARG_LABEL_ID, labelId);
        args.putInt(ARG_SLOGAN, slogan);
        args.putString(ARG_VERSION_NAME, versionName);
        args.putInt(ARG_VERSION_CODE, versionCode);
        args.putInt(ARG_LINK_A, linkA);
        args.putInt(ARG_LINK_B, linkB);
        args.putInt(ARG_LINK_C, linkC);

        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
        aboutDialogFragment.setArguments(args);

        aboutDialogFragment.show(fm, null);
    }

    @DrawableRes
    private int mIconId;
    @StringRes
    private int mLabelId;
    @StringRes
    private int mSlogan;
    private String mVersionName;
    private int mVersionCode;
    @StringRes
    private int mLinkA;
    @StringRes
    private int mLinkB;
    @StringRes
    private int mLinkC;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        if (args != null) {
            mIconId = args.getInt(ARG_ICON_ID);
            mLabelId = args.getInt(ARG_LABEL_ID);
            mSlogan = args.getInt(ARG_SLOGAN);
            mVersionName = args.getString(ARG_VERSION_NAME);
            mVersionCode = args.getInt(ARG_VERSION_CODE);
            mLinkA = args.getInt(ARG_LINK_A);
            mLinkB = args.getInt(ARG_LINK_B);
            mLinkC = args.getInt(ARG_LINK_C);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = View.inflate(getContext(), R.layout.eb_dialog_fragment_about, null);
        TextView labelTextView = (TextView) view.findViewById(R.id.eb_label);
        labelTextView.setText(mLabelId);
        ImageView iconImageView = (ImageView) view.findViewById(R.id.eb_icon);
        iconImageView.setImageResource(mIconId);
        if (mSlogan != 0) {
            TextView sloganTextView = (TextView) view.findViewById(R.id.eb_slogan);
            sloganTextView.setVisibility(View.VISIBLE);
            sloganTextView.setText(mSlogan);
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
        if (mLinkA != 0) {
            TextView linkATextView = (TextView) view.findViewById(R.id.eb_link_a);
            linkATextView.setVisibility(View.VISIBLE);
            linkATextView.setText(mLinkA);
        }
        if (mLinkB != 0) {
            TextView linkBTextView = (TextView) view.findViewById(R.id.eb_link_b);
            linkBTextView.setVisibility(View.VISIBLE);
            linkBTextView.setText(mLinkB);
        }
        if (mLinkC != 0) {
            TextView linkCTextView = (TextView) view.findViewById(R.id.eb_link_c);
            linkCTextView.setVisibility(View.VISIBLE);
            linkCTextView.setText(mLinkC);
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
