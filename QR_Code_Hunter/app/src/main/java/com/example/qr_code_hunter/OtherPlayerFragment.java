package com.example.qr_code_hunter;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;


public class OtherPlayerFragment extends Fragment{
    TextView userNameText;
    TextView email;
    TextView mobile_number;
    TextView rank;
    TextView score;
    String userName;
    ImageView imageView;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OtherPlayerFragment(String userName){
        this.userName = userName;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profie_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerProfileFragment newInstance(String param1, String param2) {
        PlayerProfileFragment fragment = new PlayerProfileFragment();
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
        return inflater.inflate(R.layout.other_player_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle saveInstanceState){
        super.onViewCreated(view, saveInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference otherplayer = db.collection("Players").document(userName);

        userNameText = getView().findViewById(R.id.user_name);
        userNameText.setText(userName);

        otherplayer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Boolean hideInfo = documentSnapshot.getBoolean("hideInfo");
                    email = (TextView) getView().findViewById(R.id.email);
                    mobile_number = (TextView) getView().findViewById(R.id.mobile_phone);
                    ImageView imageView = getView().findViewById(R.id.rectangle_);
                    if(hideInfo == true){
                        imageView.setVisibility(View.GONE);
                        email.setText("This profile is private.");
                        mobile_number.setVisibility((View.GONE));
                        Integer myAttribute3 = Math.toIntExact(documentSnapshot.getLong("score"));
                        score = (TextView) getView().findViewById(R.id.number_points);
                        score.setText(myAttribute3.toString());

                        Integer myAttribute4 = Math.toIntExact(documentSnapshot.getLong("rank"));
                        rank = (TextView) getView().findViewById(R.id.number_rank);
                        rank.setText(myAttribute4.toString());
                    }else{
                        String myAttribute = documentSnapshot.getString("email");
                        email.setText("Email: "+myAttribute);

                        String myAttribute2 = documentSnapshot.getString("phoneNumber");
                        mobile_number.setText("Mobile Phone: "+ myAttribute2);

                        Integer myAttribute3 = Math.toIntExact(documentSnapshot.getLong("score"));
                        score = (TextView) getView().findViewById(R.id.number_points);
                        score.setText(myAttribute3.toString());

                        Integer myAttribute4 = Math.toIntExact(documentSnapshot.getLong("rank"));
                        rank = (TextView) getView().findViewById(R.id.number_rank);
                        rank.setText(myAttribute4.toString());
                    }
                }else{
                    Log.d(TAG, "No such document!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e(TAG, "Error reading document", e);
            }
        });

        imageView = getView().findViewById(R.id.imageView7);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SearchFragment());
            }
        });

    }

    private void replaceFragment(Fragment fragment ){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

}
