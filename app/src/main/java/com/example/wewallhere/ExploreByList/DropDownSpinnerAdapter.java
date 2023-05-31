package com.example.wewallhere.ExploreByList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import com.example.wewallhere.R;


public class DropDownSpinnerAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;

    public DropDownSpinnerAdapter(Context context, List<String> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_dropdown_menu, parent, false);
        }
        // Customize the selected item view (view shown when the Spinner is not expanded)
        // You can set any customizations or data here if needed
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_dropdown_menu, parent, false);
        }
        // Customize each dropdown item view
        // You can set any customizations or data here if needed
        return convertView;
    }
}

