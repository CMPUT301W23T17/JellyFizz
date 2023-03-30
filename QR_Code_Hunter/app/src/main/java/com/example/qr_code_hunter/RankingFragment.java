package com.example.qr_code_hunter;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {
    private ListView rankings;
    private TextView buttonTotalScore, buttonHighestCode;
    private RankAdapter adapter;
    private String ownerName = LoginActivity.getOwnerName();

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

        //  Handle total score button amd highest code button
        buttonTotalScore = getView().findViewById(R.id.buttonTotalScore);
        buttonHighestCode = getView().findViewById(R.id.buttonHighestCode);


        //
        final boolean[] a = new boolean[1];
        buttonTotalScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonHighestCode.setBackgroundColor(Color.parseColor("#ffffff"));
                buttonTotalScore.setBackgroundColor(Color.parseColor("#e0fbfc"));
                /////////////////////////////////////////////////////////////////
                a[0] = true;
                ///////////////////////////////////////////////////////////////
                rankLists.arrangeRankTotal(ranking -> {
                    rankArr.addAll(ranking);
                    adapter = new RankAdapter(getActivity(), 0, ranking, true);
                    rankings.setAdapter(adapter);
                    displayYourRankTotalScore(rankArr);
                });
            }
        });
        buttonHighestCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonTotalScore.setBackgroundColor(Color.parseColor("#ffffff"));
                buttonHighestCode.setBackgroundColor(Color.parseColor("#e0fbfc"));
                //////////////////////////////////////////////////////////////////
                a[0] = false;
                ///////////////////////////////////////////////////////////////
                rankLists.arrangeRankCode(new Rank.ArrangeRankCallback() {
                    @Override
                    public void onArrangeRankComplete(ArrayList<Rank> ranking) {
                        rankArr.addAll(ranking);
                        adapter = new RankAdapter(getActivity(), 0, ranking, false);
                        rankings.setAdapter(adapter);
                        displayYourRankHighest(rankArr);
                    }
                });
            }
        });
        buttonTotalScore.setSoundEffectsEnabled(false);
        buttonTotalScore.performClick();
        buttonTotalScore.setSoundEffectsEnabled(true);

        rankings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                replaceFragment(new OtherPlayerFragment(rankArr.get(i).username));
            }
        });

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void displayYourRankTotalScore(ArrayList<Rank> rankArr) {
        TextView yourName = getView().findViewById(R.id.yourName);
        TextView yourPts = getView().findViewById(R.id.yourPoints);

        int yourRank = 0;
        int yourScore = 0;
        for(Rank entry: rankArr) {
            if (Objects.equals(entry.username, ownerName)) {
                yourRank = entry.rankingTotalScore;
                yourScore = entry.totalScore;
            }
        }

        ImageView yourTrophy = getView().findViewById(R.id.yourRankIcon);
        TextView yourRankLabel = getView().findViewById(R.id.yourRank);
        if(yourRank < 4 && yourRank != 0) {
            if(yourRank == 1) {
                yourTrophy.setImageResource(R.drawable.gold_trophy);
            }
            else if(yourRank == 2) {
                yourTrophy.setImageResource(R.drawable.silver_trophy);
            }
            else if (yourRank == 3){
                yourTrophy.setImageResource(R.drawable.bronze_trophy);
            }
            yourTrophy.setVisibility(View.VISIBLE);
            yourRankLabel.setVisibility(View.INVISIBLE);
        }
        else {
            yourRankLabel.setVisibility(View.VISIBLE);
            yourTrophy.setVisibility(View.INVISIBLE);
            yourRankLabel.setText(Html.fromHtml(String.valueOf(yourRank) + "<sup><small>th</small></sup>"));
        }

        yourName.setText(ownerName);
        String ptsLabel = String.valueOf(yourScore) + " pts";
        yourPts.setText(ptsLabel);
    }


    public void displayYourRankHighest(ArrayList<Rank> rankArr) {
        TextView yourName = getView().findViewById(R.id.yourName);
        TextView yourPts = getView().findViewById(R.id.yourPoints);

        int yourRank = 0;
        int yourScore = 0;
        for(Rank entry: rankArr) {
            if (Objects.equals(entry.username, ownerName)) {
                yourRank = entry.rankingCode;
                yourScore = entry.highestCode;
            }
        }

        ImageView yourTrophy = getView().findViewById(R.id.yourRankIcon);
        TextView yourRankLabel = getView().findViewById(R.id.yourRank);
        if(yourRank < 4 && yourRank != 0 && yourScore != 0) {
            if(yourRank == 1) {
                yourTrophy.setImageResource(R.drawable.gold_trophy);
            }
            else if(yourRank == 2) {
                yourTrophy.setImageResource(R.drawable.silver_trophy);
            }
            else if (yourRank == 3){
                yourTrophy.setImageResource(R.drawable.bronze_trophy);
            }
            yourTrophy.setVisibility(View.VISIBLE);
            yourRankLabel.setVisibility(View.INVISIBLE);
        }
        else if (yourScore == 0){
            yourRankLabel.setVisibility(View.VISIBLE);
            yourTrophy.setVisibility(View.INVISIBLE);
            yourRankLabel.setText(Html.fromHtml(String.valueOf(yourScore) + "<sup><small>th</small></sup>"));
        }
        else {
            yourRankLabel.setVisibility(View.VISIBLE);
            yourTrophy.setVisibility(View.INVISIBLE);
            yourRankLabel.setText(Html.fromHtml(String.valueOf(yourRank) + "<sup><small>th</small></sup>"));
        }

        yourName.setText(ownerName);
        String ptsLabel = String.valueOf(yourScore) + " pts";
        yourPts.setText(ptsLabel);
    }
}