package com.example.qr_code_hunter;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 * ChatGPT was referenced for some parts of this class
 */
public class RankingFragment extends Fragment {
    private ListView rankings;
    private TextView buttonTotalScore, buttonHighestCode;
    private RankAdapter adapter;
    private String ownerName = LoginActivity.getOwnerName();

    // Keeps track of which list is currently being displayed
    private boolean isTotalScoreDisplayed = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ArrayList<Rank> totalScoreArr = new ArrayList<>();
        ArrayList<Rank> highestCodeArr = new ArrayList<>();

        //  Handle total score button amd highest code button
        buttonTotalScore = getView().findViewById(R.id.buttonTotalScore);
        buttonHighestCode = getView().findViewById(R.id.buttonHighestCode);

        // Show total score ranking
        buttonTotalScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonHighestCode.setBackgroundColor(Color.parseColor("#ffffff"));
                buttonTotalScore.setBackgroundColor(Color.parseColor("#e0fbfc"));
                isTotalScoreDisplayed = true;
                rankLists.arrangeRankTotal(ranking -> {
                    if (isAdded()) {
                        totalScoreArr.addAll(ranking);
                        adapter = new RankAdapter(requireContext(), 0, ranking, true);
                        rankings.setAdapter(adapter);
                        displayYourRankTotalScore(totalScoreArr);
                    }
                });
            }
        });

        // Show highest code ranking
        buttonHighestCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonTotalScore.setBackgroundColor(Color.parseColor("#ffffff"));
                buttonHighestCode.setBackgroundColor(Color.parseColor("#e0fbfc"));
                isTotalScoreDisplayed = false;
                rankLists.arrangeRankCode(new Rank.ArrangeRankCallback() {
                    @Override
                    public void onArrangeRankComplete(ArrayList<Rank> ranking) {
                        if (isAdded()) {
                            highestCodeArr.addAll(ranking);
                            adapter = new RankAdapter(requireContext(), 0, ranking, false);
                            rankings.setAdapter(adapter);
                            displayYourRankHighest(highestCodeArr);
                        }
                    }
                });
            }
        });

        // Set the total score ranking as default
        buttonTotalScore.setSoundEffectsEnabled(false);
        buttonTotalScore.performClick();
        buttonTotalScore.setSoundEffectsEnabled(true);

        // Click to see other player profile
        rankings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the username from the appropriate list based on isTotalScoreDisplayed
                ArrayList<Rank> rankArr = isTotalScoreDisplayed ? totalScoreArr : highestCodeArr;
                replaceFragment(new OtherPlayerFragment(rankArr.get(i).username));
            }
        });

    }

    // Change fragment to see other people profile
    private void replaceFragment(Fragment fragment ){
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
            yourRankLabel.setText(HtmlCompat.fromHtml(yourRank + "<sup><small>th</small></sup>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        yourName.setText(ownerName);
        String ptsLabel = yourScore + " pts";
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
            yourRankLabel.setText(HtmlCompat.fromHtml(yourScore + "<sup><small>th</small></sup>", HtmlCompat.FROM_HTML_MODE_LEGACY));

        }
        else {
            yourRankLabel.setVisibility(View.VISIBLE);
            yourTrophy.setVisibility(View.INVISIBLE);
            yourRankLabel.setText(HtmlCompat.fromHtml(yourRank + "<sup><small>th</small></sup>", HtmlCompat.FROM_HTML_MODE_LEGACY));

        }

        yourName.setText(ownerName);
        String ptsLabel = yourScore + " pts";
        yourPts.setText(ptsLabel);
    }
}

