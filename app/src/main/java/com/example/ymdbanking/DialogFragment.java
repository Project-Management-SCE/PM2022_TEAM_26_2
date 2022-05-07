package com.example.ymdbanking;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DialogFragment extends android.app.DialogFragment {
    public DialogFragment() {
        super();
    }

    private static final String TAG = "DialogFragment";

    public interface OnInputListener {
        void sendInput(String input);
    }
    public OnInputListener mOnInputListener;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(
                R.layout.transfer_dialog, container, false);

//        mActionCancel.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override public void onClick(View v)
//                    {
//                        Log.d(TAG, "onClick: closing dialog");
//                        getDialog().dismiss();
//                    }
//                });
//
//        mActionOk.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override public void onClick(View v)
//                    {
//                        Log.d(TAG, "onClick: capturing input");
//                        String input
//                                = mInput.getText().toString();
//                        mOnInputListener.sendInput(input);
//                        getDialog().dismiss();
//                    }
//                });

        return view;
    }

    @Override public void onAttach(Context context)
    {
        super.onAttach(context);
//        try {
////            mOnInputListener
////                    = (OnInputListener)getActivity();
//        }
//        catch (ClassCastException e) {
//            Log.e(TAG, "onAttach: ClassCastException: "
//                    + e.getMessage());
//        }
    }
}

