package com.example.qr_code_hunter;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {
    ListView rankings;
    TextView button_highest_qr_score, button_total_score;
    RankAdapter adapter;
    String ownerName = loginActivity.getOwnerName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RankingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Ranking_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static RankingFragment newInstance(String param1, String param2) {
        RankingFragment fragment = new RankingFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rankings = getView().findViewById(R.id.leaderboard);
        Rank rankLists = new Rank();
        ArrayList<Rank> rankArr = new ArrayList<>();
        // add rank to adapter
        rankLists.arrangeRank(new Rank.ArrangeRankCallback() {
            @Override
            public void onArrangeRankComplete(ArrayList<Rank> ranking) {

                rankArr.addAll(ranking);
                adapter = new RankAdapter(getActivity(), 0, ranking);
                rankings.setAdapter(adapter);
                displayYourRank_Total_Score();
            }
        });
        //  Handle total score button amd highest code button
        button_highest_qr_score = getView().findViewById(R.id.button_highest_qr_score);
        button_total_score = getView().findViewById(R.id.button_total_score);

        button_highest_qr_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_total_score.setBackgroundColor(Color.parseColor("#ffffff"));
                button_highest_qr_score.setBackgroundColor(Color.parseColor("#e0fbfc"));
            }
        });
        button_total_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_highest_qr_score.setBackgroundColor(Color.parseColor("#ffffff"));
                button_total_score.setBackgroundColor(Color.parseColor("#e0fbfc"));
            }
        });
    }

    public void displayYourRank_Total_Score() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference owner = db.collection("Players").document(ownerName);
        final int[] yourRank = new int[1];
        final int[] yourScore = new int[1];
        owner.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            yourScore[0] = Math.toIntExact(documentSnapshot.getLong("score"));
                            Log.d(TAG, "Value of myAttribute: " + yourScore[0]);
                        } else {
                            Log.d(TAG, "No such document!");
                        }
                    }
                });
        owner.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            yourRank[0] = Math.toIntExact(documentSnapshot.getLong("rank"));
                            Log.d(TAG, "Value of myAttribute: " + yourRank[0]);
                        } else {
                            Log.d(TAG, "No such document!");
                        }
                    }
                });
        ImageView yourTrophy = getView().findViewById(R.id.yourRankIcon);
        TextView yourRankLabel = getView().findViewById(R.id.yourRank);
        if(yourRank[0] < 4 && yourRank[0] != 0) {
            if(yourRank[0] == 1) {
                yourTrophy.setImageResource(R.drawable.gold_trophy);
            }
            else if(yourRank[0] == 2) {
                yourTrophy.setImageResource(R.drawable.silver_trophy);
            }
            else if (yourRank[0] == 3){
                yourTrophy.setImageResource(R.drawable.bronze_trophy);
            }
            yourTrophy.setVisibility(View.VISIBLE);
            yourRankLabel.setVisibility(View.INVISIBLE);
        }
        else {
            yourRankLabel.setVisibility(View.VISIBLE);
            yourTrophy.setVisibility(View.INVISIBLE);
            yourRankLabel.setText(Html.fromHtml(String.valueOf(yourRank[0]) + "<sup><small>th</small></sup>"));
        }
        TextView yourName = getView().findViewById(R.id.yourName);
        TextView yourPts = getView().findViewById(R.id.yourPoints);
        yourName.setText(ownerName);
        String ptsLabel = String.valueOf(yourScore[0]) + " pts";
        yourPts.setText(ptsLabel);
    }
}