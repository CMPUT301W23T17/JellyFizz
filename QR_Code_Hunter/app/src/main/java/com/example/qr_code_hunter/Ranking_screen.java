package com.example.qr_code_hunter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Ranking_screen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Ranking_screen extends Fragment {
    ListView rankings;
    ArrayList<Rank> rankArr = new ArrayList<>();
    TextView header;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Ranking_screen() {
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
    public static Ranking_screen newInstance(String param1, String param2) {
        Ranking_screen fragment = new Ranking_screen();
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
        return inflater.inflate(R.layout.fragment_ranking_screen, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        header = getView().findViewById(R.id.ranking_header);
        rankings = getView().findViewById(R.id.leaderboard);

        String[] names = {
                "Jerry West", "Jane Doe", "John Smith", "James Bond", "Jonathan Gulliver", "Juan Carlos Montana", "Jerome Haystack"};

        Integer[] points = {
                117, 800, 900, 300, 500, 1000, 150
        };

        Integer[] positions = {
                7, 3, 2, 5, 4, 1, 6
        };

        for (int i = 0; i < names.length; i++) {
            rankArr.add(new Rank(names[i], points[i], positions[i]));
        }

        // add entries here
        sortRank();

        RankAdapter adapter = new RankAdapter(getActivity(), 0, rankArr);
        rankings.setAdapter(adapter);

        displayYourRank("Jerry West");
    }

    public void updateEntry(Rank rank, int newPos) {
        rank.position = newPos;
    }

    public void sortRank() {
        for (int i = 1; i < rankArr.size(); i++) {
            int j = i - 1;
            int k = i;
            Rank temp = null;
            int tempRank = rankArr.get(i).position;
            while (j >= 0 && rankArr.get(j).score < rankArr.get(k).score) {
                temp = rankArr.get(k);
                rankArr.set(k, rankArr.get(j));
                updateEntry(rankArr.get(j), k+1);
                rankArr.set(j, temp);
                updateEntry(rankArr.get(j), j+1);
                j--;
                k--;
            }
            if(rankArr.get(k).position == tempRank) {
                updateEntry(rankArr.get(i), i+1);
            }
        }
    }

    public void displayYourRank(String yourUsername) {
        TextView yourName = getView().findViewById(R.id.yourName);
        TextView yourPts = getView().findViewById(R.id.yourPoints);

        int yourRank = 0;
        int yourScore = 0;
        for(Rank entry: rankArr) {
            if (Objects.equals(entry.username, yourUsername)) {
                yourRank = entry.position;
                yourScore = entry.score;
            }
        }

        ImageView yourTrophy = getView().findViewById(R.id.yourRankIcon);
        TextView yourRankLabel = getView().findViewById(R.id.yourRank);
        if(yourRank < 4) {
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

        yourName.setText(yourUsername);
        String ptsLabel = String.valueOf(yourScore) + " pts";
        yourPts.setText(ptsLabel);
    }
}