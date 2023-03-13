package com.example.qr_code_hunter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import android.widget.ArrayAdapter;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class searchTest {
    @Test
    public void searchPlayerTest(){
        Search mocksearch = new Search();
        mocksearch.searchPlayer("Kyle", new Search.SearchPlayerCallback() {
            @Override
            public void onSearchPlayerComplete(ArrayList<String> usernames) {
                System.out.println(usernames);
            }
        });
    }
}
