package com.example.qr_code_hunter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CodeDetailsFragment extends Fragment {
    String hashString; // what is provided into this fragment
    ArrayList<String> otherPlayers = new ArrayList<String>();

    TextView codeName;
    TextView codeVisual;
    TextView codeLocation;
    TextView codeScore;
    ImageView codeImage;
    EditText codeDesc;
    TextView codeOthers;
    TextView backButton;
    TextView editButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CodeDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CodeDetailsFragment newInstance(String param1, String param2) {
        CodeDetailsFragment fragment = new CodeDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_details, container, false);

        codeName = view.findViewById(R.id.details_codename);
        codeVisual = view.findViewById(R.id.details_visual);
        codeLocation = view.findViewById(R.id.details_location);
        codeScore = view.findViewById(R.id.details_points);
        codeImage = view.findViewById(R.id.details_image);
        codeDesc = view.findViewById(R.id.details_comment);
        codeOthers = view.findViewById(R.id.details_others);

        backButton = view.findViewById(R.id.details_backBtn);
        editButton = view.findViewById(R.id.details_editCommentBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
