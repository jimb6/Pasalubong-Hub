package com.allandroidprojects.ecomsample.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.config.helpers.utility.OnSearchOptionDataChangeListener;
import com.allandroidprojects.ecomsample.config.helpers.utility.PrefManager;
import com.allandroidprojects.ecomsample.data.models.SearchData;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

public class SearchOptionDialog extends DialogFragment {

    public static final String TAG = "example_dialog";
    private Toolbar toolbar;
    private OnSearchOptionDataChangeListener dataChangeListener;
    private View root;
    private TextInputEditText priceFrom, priceTo;
    private CheckBox mati, lupon, govGen, fiveStar, fourStar, threeStar, twoStar, oneStar;
    private SearchData searchData;
    private PrefManager prefManager;

    public static SearchOptionDialog display(FragmentManager fragmentManager) {
        SearchOptionDialog exampleDialog = new SearchOptionDialog();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefManager = new PrefManager(getActivity());
        this.searchData = searchData;
        this.dataChangeListener = dataChangeListener;
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.search_option_dialog, container, false);

        toolbar = root.findViewById(R.id.toolbar);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.inflateMenu(R.menu.search_option_menu);
        toolbar.setOnMenuItemClickListener(item -> {
//            dataChangeListener.onDataChange(searchData);
            SearchData newData = new SearchData();
            newData.priceFrom = Double.parseDouble(priceFrom.getText().toString());
            newData.priceTo = Double.parseDouble(priceTo.getText().toString());
            prefManager.setSearchData(newData);
            dismiss();
            return true;
        });


        initComponents();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {

        SearchData data = prefManager.getSearhData();

        this.priceFrom = root.findViewById(R.id.price_from);
        this.priceTo = root.findViewById(R.id.price_to);

        this.fiveStar = root.findViewById(R.id.fiveStar);
        this.fourStar = root.findViewById(R.id.fourStar);
        this.threeStar = root.findViewById(R.id.threeStar);
        this.twoStar = root.findViewById(R.id.twoStar);
        this.oneStar = root.findViewById(R.id.oneStar);

        priceFrom.setText(String.valueOf(data.priceFrom));
        priceTo.setText(String.valueOf(data.priceTo));

    }

    private void addChipToGroup(String txt, ChipGroup chipGroup) {
        Chip chip = new Chip(getContext());
        chip.setText(txt);
        chip.setCloseIconVisible(true);
        chip.setClickable(false);
        chip.setCheckable(false);
        chipGroup.addView(chip);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
        });
//        val chip = Chip(context)
//        chip.text = txt
////        chip.chipIcon = ContextCompat.getDrawable(requireContext(), baseline_person_black_18)
//        chip.isCloseIconEnabled = true
//        chip.setChipIconTintResource(R.color.chipIconTint)

        // necessary to get single selection working
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }
}
