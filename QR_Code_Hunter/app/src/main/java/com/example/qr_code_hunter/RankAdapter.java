package com.example.qr_code_hunter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RankAdapter extends ArrayAdapter<Rank> {
    private Context ctx;
    private ArrayList<Rank> rankArr;
    private boolean typeTotal;

    public RankAdapter(@NonNull Context context, int resource, @NonNull List<Rank> objects, boolean typeTotal) {
        super(context, resource, objects);
        this.ctx = context;
        this.rankArr = new ArrayList<>(objects);
        this.typeTotal = typeTotal;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater i = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = i.inflate(R.layout.item_ranking, null);
        }
        if (typeTotal) {
            if (rankArr.size() > 0) {
                Rank r = rankArr.get(position);
                ImageView trophy = convertView.findViewById(R.id.rankIcon);
                TextView pos = convertView.findViewById(R.id.rankNum);
                if (r.rankingTotalScore < 4 && r.rankingTotalScore != 0) {
                    if (r.rankingTotalScore == 1) {
                        trophy.setImageResource(R.drawable.gold_trophy);
                    } else if (r.rankingTotalScore == 2) {
                        trophy.setImageResource(R.drawable.silver_trophy);
                    } else if (r.rankingTotalScore == 3) {
                        trophy.setImageResource(R.drawable.bronze_trophy);
                    }
                    trophy.setVisibility(View.VISIBLE);
                    pos.setVisibility(View.INVISIBLE);
                } else {
                    pos.setVisibility(View.VISIBLE);
                    trophy.setVisibility(View.INVISIBLE);
                    pos.setText(Html.fromHtml(String.valueOf(r.rankingTotalScore) + "<sup><small>th</small></sup>"));
                }
                TextView nameString = convertView.findViewById(R.id.userName);
                TextView userPoints = convertView.findViewById(R.id.userPoints);
                nameString.setText(r.username);
                String ptsLabel = String.valueOf(r.totalScore) + " pts";
                userPoints.setText(ptsLabel);
            }
        } else {
            if (rankArr.size() > 0) {
                Rank r = rankArr.get(position);
                ImageView trophy = convertView.findViewById(R.id.rankIcon);
                TextView pos = convertView.findViewById(R.id.rankNum);
                if (r.rankingCode < 4 && r.rankingCode != 0) {
                    if (r.rankingCode == 1) {
                        trophy.setImageResource(R.drawable.gold_trophy);
                    } else if (r.rankingCode == 2) {
                        trophy.setImageResource(R.drawable.silver_trophy);
                    } else if (r.rankingCode == 3) {
                        trophy.setImageResource(R.drawable.bronze_trophy);
                    }
                    trophy.setVisibility(View.VISIBLE);
                    pos.setVisibility(View.INVISIBLE);
                } else {
                    pos.setVisibility(View.VISIBLE);
                    trophy.setVisibility(View.INVISIBLE);
                    pos.setText(Html.fromHtml(String.valueOf(r.rankingCode) + "<sup><small>th</small></sup>"));
                }
                TextView nameString = convertView.findViewById(R.id.userName);
                TextView userPoints = convertView.findViewById(R.id.userPoints);
                nameString.setText(r.username);
                String ptsLabel = String.valueOf(r.highestCode) + " pts";
                userPoints.setText(ptsLabel);
            }
        }
        return convertView;
    }
}