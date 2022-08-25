package com.smartwebarts.rogrows.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.SearchAdapter;
import com.smartwebarts.rogrows.retrofit.SearchModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.utils.Toolbar_Set;

public class SearchActivity extends AppCompatActivity {

    EditText searchView;
    RecyclerView recyclerView;
    SearchAdapter adapter;
    List<SearchModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        searchView.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new SearchAdapter(this, list);
        recyclerView.setAdapter(adapter);

        Toolbar_Set.INSTANCE.setBottomNav(this);
        Toolbar_Set.INSTANCE.setToolbar(this, "Search");

        search("");

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
////                search(newText);
//                upDate(newText);
//                return false;
//            }
//        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                upDate(searchView.getText().toString().trim());
            }
        });
    }

    private void upDate(String newText) {
        if (newText.isEmpty()) {
            adapter.updateList(SearchActivity.this.list);
        } else {
            List<SearchModel> models = new ArrayList<>();
            for (SearchModel model: list) {
                if (model.getName().trim().toLowerCase().contains(newText.toLowerCase().trim())) {
                    models.add(model);
                }
            }
            adapter.updateList(models);
        }
    }

    private void search(String newText) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {

            UtilMethods.INSTANCE.search(this, newText, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {

                    Type listType = new TypeToken<ArrayList<SearchModel>>(){}.getType();
                    List<SearchModel> list = new Gson().fromJson(message, listType);
                    SearchActivity.this.list = list;
                    adapter.updateList(SearchActivity.this.list);
                }

                @Override
                public void fail(String from) {

                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
    }

}