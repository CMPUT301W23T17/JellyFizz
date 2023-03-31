package com.example.qr_code_hunter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private Search search;
    private ListView listView;
    private ArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_searching, container, false);
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
                search.searchPlayer(text.getText().toString(), new Search.SearchPlayerCallback() {   //Use ChatGpt for callback method
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();        //Had help from ChatGpt on how fragmentManager works and how to apply it
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}