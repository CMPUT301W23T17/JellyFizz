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

    public RankAdapter(@NonNull Context context, int resource, @NonNull List<Rank> objects) {
        super(context, resource, objects);
        this.ctx = context;
        this.rankArr = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater i = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = i.inflate(R.layout.item_ranking, null);
        }
        if (rankArr.size() > 0) {
            Rank r = rankArr.get(position);
            ImageView trophy = convertView.findViewById(R.id.rankIcon);
            TextView pos = convertView.findViewById(R.id.rankNum);
            if(r.position < 4) {
                if(r.position == 1) {
                    trophy.setImageResource(R.drawable.gold_trophy);
                }
                else if(r.position == 2) {
                    trophy.setImageResource(R.drawable.silver_trophy);
                }
                else if (r.position == 3){
                    trophy.setImageResource(R.drawable.bronze_trophy);
                }
                trophy.setVisibility(View.VISIBLE);
                pos.setVisibility(View.INVISIBLE);
            }
            else {
                pos.setVisibility(View.VISIBLE);
                trophy.setVisibility(View.INVISIBLE);
                pos.setText(Html.fromHtml(String.valueOf(r.position) + "<sup><small>th</small></sup>"));
            }

            TextView nameString = convertView.findViewById(R.id.userName);
            TextView userPoints = convertView.findViewById(R.id.userPoints);
            nameString.setText(r.username);
            String ptsLabel = String.valueOf(r.score) + " pts";
            userPoints.setText(ptsLabel);
        }
        return convertView;
    }
}
