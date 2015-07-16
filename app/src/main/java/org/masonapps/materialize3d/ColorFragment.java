package org.masonapps.materialize3d;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.masonapps.materialize3d.views.ColorPickerView;


public class ColorFragment extends Fragment {


    public static ColorFragment newInstance(int color) {
        ColorFragment fragment = new ColorFragment();
        return fragment;
    }

    public ColorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_color, container, false);
        final ColorPickerView colorPickerView = (ColorPickerView) view.findViewById(R.id.colorPicker);
        colorPickerView.setCurrentColor(Prefs.getInstance().getColor());
        colorPickerView.setListener(new ColorPickerView.OnColorChangeListener() {
            @Override
            public void onColorChanged(int color) {
                Prefs.getInstance().setColor(color);
            }
        });
        return view;
    }

}
