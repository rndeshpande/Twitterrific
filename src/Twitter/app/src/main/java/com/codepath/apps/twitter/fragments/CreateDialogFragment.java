package com.codepath.apps.twitter.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.databinding.FragmentCreateDialogBinding;
import com.codepath.apps.twitter.models.TweetRequest;

import org.parceler.Parcels;

/**
 * Created by rdeshpan on 9/26/2017.
 */

public class CreateDialogFragment extends DialogFragment {
    EditText etStatus;
    TextView tvCharCount;
    Button btnSubmit;
    ImageView ivCancel;

    long inReplyToStatusId;
    String inReplyToScreenName;

    private final static int CHARACTER_LIMIT = 140;

    FragmentCreateDialogBinding binding;

    private OnFragmentInteractionListener mListener;

    public CreateDialogFragment() {
        // Required empty public constructor
    }

    public static CreateDialogFragment newInstance() {
        CreateDialogFragment fragment = new CreateDialogFragment();
        return fragment;
    }

    public static CreateDialogFragment newInstance(Parcelable tweetRequest) {
        CreateDialogFragment fragment = new CreateDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("tweet_request", tweetRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_dialog, container, false);
        View view = binding.getRoot();

        if (getArguments() != null) {
            TweetRequest tweetRequest = (TweetRequest) Parcels.unwrap(getArguments().getParcelable("tweet_request"));
            binding.setTweet(tweetRequest);
            inReplyToStatusId = tweetRequest.getInReplyToStatusId();
            inReplyToScreenName = tweetRequest.getInReplyToScreenName();
        }

        setSubmitBehavior();
        setCancelBehavior();
        setCharacterLimit();

        return view;
    }

    private void setSubmitBehavior() {
        btnSubmit = binding.btnSubmit;
        btnSubmit.setOnClickListener(v -> {
            onButtonPressed();
            dismiss();
        });
    }

    private void setCharacterLimit() {
        etStatus = binding.etStatus;
        tvCharCount = binding.tvCharCount;
        String test = getString(R.string.test);
        String remainingChars = Integer.toString(CHARACTER_LIMIT);
        tvCharCount.setText(remainingChars);

        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                String remainingChars = Integer.toString(CHARACTER_LIMIT - length);
                tvCharCount.setText(remainingChars);
            }
        });
    }

    private void setCancelBehavior() {
        ivCancel = binding.ivCancel;
        ivCancel.setOnClickListener(v-> {
            dismiss();
        });
    }

    public void onButtonPressed() {
        mListener = (OnFragmentInteractionListener) getActivity();
        if (mListener != null) {
            TweetRequest tweetRequest = new TweetRequest(binding.etStatus.getText().toString(), inReplyToStatusId, inReplyToScreenName);
            mListener.onFragmentInteraction(tweetRequest);
        }
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(TweetRequest tweetRequest);
    }

}
