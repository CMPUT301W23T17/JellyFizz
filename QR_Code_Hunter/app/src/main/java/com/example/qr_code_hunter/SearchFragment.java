package com.example.qr_code_hunter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    Search search;
    ListView listView;
    ArrayAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.searching, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        search = new Search();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<ArrayList<String>> listArrayList = new ArrayList<>();

        View searchbutton = getView().findViewById(R.id.search_bar);
        listView = getView().findViewById(R.id.search_list);
        TextView textView = getView().findViewById(R.id.textView4);
        textView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = getView().findViewById(R.id.enter_username);
                search.searchPlayer(text.getText().toString(), new Search.SearchPlayerCallback() {
                    @Override
                    public void onSearchPlayerComplete(ArrayList<ArrayList<String>> usernames){
                        listArrayList.addAll(usernames);
                        if(list.isEmpty()){
                            for(int i = 0; i < usernames.size(); i++){
                                list.add(usernames.get(i).get(0) + ":   " + usernames.get(i).get(1) + "pts");
                            }
                        }else{
                            list.clear();
                            for(int i = 0; i < usernames.size(); i++){
                                list.add(usernames.get(i).get(0) + ":   " + usernames.get(i).get(1) + "pts");
                            }
                        }
                        adapter = new ArrayAdapter<>(getActivity(), R.layout.searching_context, list);
                        listView.setAdapter(adapter);

                        if (adapter.getCount() == 0) {
                            textView.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            textView.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }

                    }
                });

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                replaceFragment(new OtherPlayerFragment(listArrayList.get(i).get(0)));
            }
        });
    }

    private void replaceFragment(Fragment fragment ){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}