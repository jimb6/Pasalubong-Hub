package com.allandroidprojects.ecomsample.ui.common.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.allandroidprojects.ecomsample.R;

public class TermsAndConCustomDialog extends DialogFragment {

    private View root;

    private TextView term;
    private Button accept, decline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.layout_terms_condition, container, false);
        bindView();
        return root;
    }

    private void bindView(){
        this.term = root.findViewById(R.id.tvTerms);
        this.accept = root.findViewById(R.id.btnAccept);
        this.decline = root.findViewById(R.id.btnDecline);
    }
}
