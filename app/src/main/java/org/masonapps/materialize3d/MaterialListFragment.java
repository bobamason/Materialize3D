package org.masonapps.materialize3d;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.masonapps.materialize3d.graphics.effects.BaseEffect;
import org.masonapps.materialize3d.utils.LabelIconRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class MaterialListFragment extends Fragment {

    public static MaterialListFragment newInstance() {
        MaterialListFragment fragment = new MaterialListFragment();
        return fragment;
    }

    public MaterialListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_material_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.material_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> icons = new ArrayList<>();
        HashMap<String, BaseEffect> map = Prefs.getInstance().getEffectHashMap();
        for (BaseEffect effect : map.values()) {
            labels.add(effect.getEffectName());
        }
        Collections.sort(labels);
        for (String s : labels) {
            icons.add(map.get(s).getIconResource());
        }
        int index = labels.indexOf(Prefs.getInstance().getEffect().getEffectName());

        LabelIconRecyclerAdapter adapter = new LabelIconRecyclerAdapter(getActivity(), labels, icons);
        adapter.setListener(new LabelIconRecyclerAdapter.OnRecyclerItemClickedListener() {
            @Override
            public void onItemClicked(String label) {
                Prefs.getInstance().setEffectByKey(label);
            }
        });
        adapter.setSelectedItem(index);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
