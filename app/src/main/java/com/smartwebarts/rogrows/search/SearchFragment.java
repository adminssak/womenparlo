package com.smartwebarts.rogrows.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.SearchAdapter;
import com.smartwebarts.rogrows.retrofit.SearchModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.utils.Toolbar_Set;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    EditText searchView;
    RecyclerView recyclerView;
    SearchAdapter adapter;
    List<SearchModel> list = new ArrayList<>();

    private SearchViewModel mViewModel;
    private View view;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.search_fragment, container, false);
        init(view);

        search("");

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

        return view;
    }

    private void init(View view) {
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new SearchAdapter(requireActivity(), list);
        recyclerView.setAdapter(adapter);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        imgr.showSoftInput(searchView, 0);
        searchView.requestFocus();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel
    }

    private void upDate(String newText) {
        if (newText.isEmpty()) {
            adapter.updateList(this.list);
        } else {
            List<SearchModel> models = new ArrayList<>();
            for (SearchModel model: list) {

                if (model.getName()
                        .trim()
                        .toLowerCase()
                        .contains(newText.toLowerCase().trim()))
                {
                    models.add(model);
                }
            }
            adapter.updateList(models);
        }
    }

    private void search(String newText) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(requireActivity())) {

            UtilMethods.INSTANCE.search(requireActivity(), newText, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {

                    Type listType = new TypeToken<ArrayList<SearchModel>>(){}.getType();
                    List<SearchModel> list = new Gson().fromJson(message, listType);
                    SearchFragment.this.list = list;
                    adapter.updateList(SearchFragment.this.list);
                }

                @Override
                public void fail(String from) {

                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(requireActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(requireActivity());
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // This callback will only be called when MyFragment is at least Started.
//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                // Handle the back button event
//
//                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
//    }
}