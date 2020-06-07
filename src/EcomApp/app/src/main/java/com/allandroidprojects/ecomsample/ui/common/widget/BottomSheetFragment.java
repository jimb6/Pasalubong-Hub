package com.allandroidprojects.ecomsample.ui.common.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.ui.common.components.ItemDetailsActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.sephiroth.android.library.numberpicker.NumberPicker;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private View view;
    private NumberPicker quantityPicker;
    private Button confirm;
    private ItemDetailsActivity owner;


    public BottomSheetFragment(ItemDetailsActivity owner) {
        // Required empty public constructor
        this.owner = owner;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        confirm = view.findViewById(R.id.confirm);
        quantityPicker = view.findViewById(R.id.quantityPicker);

        confirm.setOnClickListener(v ->{
                placeOrder();
        });

        return view;
    }

    private void placeOrder(){
        owner.confirmedOrder(quantityPicker.getProgress());
    }


}
