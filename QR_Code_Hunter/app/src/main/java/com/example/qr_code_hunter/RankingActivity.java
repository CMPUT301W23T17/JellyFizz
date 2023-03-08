package com.example.qr_code_hunter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class RankingActivity extends AppCompatActivity {
    ListView rankings;
    ArrayList<Rank> rankArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rankings = findViewById(R.id.leaderboard);

//        String[] names = {
//                "Jerry West", "Jane Doe", "John Smith", "James Bond", "Jonathan Gulliver", "Juan Carlos Montana", "Jerome Haystack"};
//
//        Integer[] points = {
//                117, 800, 900, 300, 500, 1000, 150
//        };
//
//        Integer[] positions = {
//                7, 3, 2, 5, 4, 1, 6
//        };
//
//        for (int i = 0; i < names.length; i++) {
//            rankArr.add(new Rank(names[i], points[i], positions[i]));
//        }

        // add entries here
        sortRank();

        RankAdapter adapter = new RankAdapter(this, 0, rankArr);
        rankings.setAdapter(adapter);

        displayYourRank("Your Username");

    }

//    public void addEntry(Player player) {
//        rankArr.add(new Rank(player.getUsername(), player.getTotalScore(), player.getRank()));
//    }

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
        TextView yourName = findViewById(R.id.yourName);
        TextView yourPts = findViewById(R.id.yourPoints);

        int yourRank = 0;
        int yourScore = 0;
        for(Rank entry: rankArr) {
            if (Objects.equals(entry.username, yourUsername)) {
                yourRank = entry.position;
                yourScore = entry.score;
            }
        }

        ImageView yourTrophy = findViewById(R.id.yourRankIcon);
        TextView yourRankLabel = findViewById(R.id.yourRank);
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