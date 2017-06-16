package com.ebnbin.ebapplication.fragment.webview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.ebnbin.ebapplication.R;

/**
 * Shows WebView info.
 */
public final class WebViewDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_URL = "url";
    private static final String ARG_FAVICON = "favicon";

    public static void showDialog(@NonNull FragmentManager fm, @Nullable String title, @Nullable String url,
            @Nullable Bitmap favicon) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_URL, url);
        args.putParcelable(ARG_FAVICON, favicon);

        WebViewDialogFragment dialogFragment = new WebViewDialogFragment();
        dialogFragment.setArguments(args);

        dialogFragment.show(fm, null);
    }

    private String mTitle;
    private String mUrl;
    private Bitmap mFavicon;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString(ARG_TITLE);
            mUrl = args.getString(ARG_URL);
            mFavicon = args.getParcelable(ARG_FAVICON);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(new BitmapDrawable(getResources(), mFavicon));
        builder.setTitle(mTitle);
        builder.setMessage(mUrl);
        builder.setPositiveButton(R.string.eb_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setNeutralButton(R.string.eb_dialog_fragment_web_view_copy_url, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipData clipData = ClipData.newPlainText(mUrl, mUrl);

                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(clipData);

                Toast.makeText(getContext(), R.string.eb_dialog_fragment_web_view_copy_url_success, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return builder.create();
    }
}
