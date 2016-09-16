package com.yxkang.android.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fine on 2016/9/16.
 */
public class EditDialogFragment extends DialogFragmentCompat {

    private static final String TAG = "EditDialogFragment";

    public static final String PARAM_TAG = "dialogTag";
    public static final String PARAM_TITLE = "dialogTitle";
    public static final String PARAM_TEXT = "dialogText";

    private String dialogTag;
    private String dialogTitle;
    private String dialogText;

    private OnDialogEventListener listener;
    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            dialogTag = args.getString(PARAM_TAG);
            dialogTitle = args.getString(PARAM_TITLE);
            dialogText = args.getString(PARAM_TEXT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_edit, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext(), getTheme())
                .setTitle(getDialogTitle())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getTextInputEditText() != null && getListener() != null) {
                            getListener().onDialogInputFinished(getTextInputEditText().getText().toString(), getDialogTag());
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null) {
            textInputEditText = (TextInputEditText) view.findViewById(R.id.textInputEditText);
            if (textInputEditText != null && !TextUtils.isEmpty(getDialogText())) {
                textInputEditText.setText(getDialogText());
                textInputEditText.setSelection(textInputEditText.length());
            }
            textInputLayout = (TextInputLayout) view.findViewById(R.id.textInputLayout);
        } else {
            Log.w(TAG, "onActivityCreated: view == null");
        }
        if (getListener() != null) {
            getListener().onDialogViewCreated(this, getDialogTag());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDialogEventListener) {
            listener = (OnDialogEventListener) context;
        } else {
            Log.w(TAG, "onAttach: " + context.toString() + " Not implements OnDialogEventListener interface");
        }
    }

    protected String getDialogTag() {
        return dialogTag;
    }

    protected String getDialogTitle() {
        return dialogTitle;
    }

    protected String getDialogText() {
        return dialogText;
    }

    protected OnDialogEventListener getListener() {
        return listener;
    }

    public TextInputEditText getTextInputEditText() {
        return textInputEditText;
    }

    public TextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public interface OnDialogEventListener {

        /**
         * notify its activity the dialog fragment view has been created, you can modify its UI
         *
         * @param fragment the dialog fragment
         * @param tag      the dialog fragment tag, using to distinguish every dialog fragment
         */
        void onDialogViewCreated(EditDialogFragment fragment, String tag);

        /**
         * a callback when dialog input finish
         *
         * @param text the input text, maybe null
         * @param tag  the dialog fragment tag, using to distinguish every dialog fragment
         */
        void onDialogInputFinished(String text, String tag);
    }
}
