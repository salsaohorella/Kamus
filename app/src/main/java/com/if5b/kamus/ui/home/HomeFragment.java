package com.if5b.kamus.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.if5b.kamus.adapters.KamusViewAdapter;
import com.if5b.kamus.database.KamusHelper;
import com.if5b.kamus.databinding.FragmentHomeBinding;
import com.if5b.kamus.models.Kamus;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private KamusViewAdapter kamusViewAdapter;
    private KamusHelper kamusHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        kamusHelper = new KamusHelper(getActivity());
        kamusViewAdapter = new KamusViewAdapter(getActivity());
        binding.rvKamus.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvKamus.setAdapter(kamusViewAdapter);

        getALLDataEnglishIndonesia();

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strSearch = binding.etSearch.getText().toString();

                if(TextUtils.isEmpty(strSearch)){
                    getALLDataEnglishIndonesia();
                }else {
                    kamusHelper.open();
                    ArrayList<Kamus> kamus = kamusHelper.getALLDataEnglishIndonesiaByTitle(strSearch);
                    kamusHelper.close();
                    kamusViewAdapter.setData(kamus);
                }
                hideKeyboard(getActivity());
            }
        });
        return root;
    }

    private void getALLDataEnglishIndonesia() {
        kamusHelper.open();
        ArrayList<Kamus> kamus = kamusHelper.getAllDataEnglishIndonesia();
        kamusHelper.close();
        kamusViewAdapter.setData(kamus);
    }
    private void  hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}