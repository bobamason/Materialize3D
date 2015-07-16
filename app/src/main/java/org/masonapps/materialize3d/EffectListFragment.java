package org.masonapps.materialize3d;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.masonapps.materialize3d.graphics.effects.BaseEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EffectListFragment extends Fragment {


    private ArrayList<String> categoryList;
    private HashMap<String, List<String>> effectMap;

    public EffectListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_effect_list, container, false);
        final ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.effects_list_view);
        Collection<BaseEffect> effects = Prefs.getInstance().getEffectHashMap().values();
        categoryList = new ArrayList<>();
        effectMap = new HashMap<>();
        for (BaseEffect effect : effects) {
            String category = effect.getCategory();
            if (!categoryList.contains(category)) {
                categoryList.add(category);
            }
        }
        Collections.sort(categoryList);
        for (String s : categoryList) {
            List<String> names = new ArrayList<>();
            for (BaseEffect effect : effects) {
                if (effect.getCategory().equals(s)) {
                    names.add(effect.getEffectName());
                }
            }
            Collections.sort(names);
            effectMap.put(s, names);
        }

        final ExpandableAdapter adapter = new ExpandableAdapter(categoryList, R.layout.item_group, effectMap, R.layout.item_effect);
        expandableListView.setAdapter(adapter);

        final BaseEffect effect = Prefs.getInstance().getEffect();
        if (Prefs.getInstance().isFirstTime()) {
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                expandableListView.expandGroup(i);
            }
        }
        if (effect != null) {
            int[] position = searchForPosition(adapter, effect.getEffectName());
            if (position[0] != -1 && position[1] != -1) {
                expandableListView.expandGroup(position[0]);
                adapter.setSelection(position[0], position[1]);
                expandableListView.setSelectedChild(position[0], position[1], true);
            }
        }
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                adapter.setSelection(groupPosition, childPosition);
                final String key = (String) adapter.getChild(groupPosition, childPosition);
                Prefs.getInstance().setEffectByKey(key);
                ((SettingsActivity) getActivity()).closeDrawer();
                return true;
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private int[] searchForPosition(BaseExpandableListAdapter adapter, String effectName) {
        final int[] pos = new int[]{-1, -1};
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            for (int j = 0; j < adapter.getChildrenCount(i); j++) {
                String text = (String) adapter.getChild(i, j);
                if (text.equals(effectName)) {
                    pos[0] = i;
                    pos[1] = j;
                    return pos;
                }
            }
        }
        return pos;
    }

    private static class ExpandableAdapter extends BaseExpandableListAdapter {

        private final List<String> groupList;
        private final HashMap<String, List<String>> childMap;
        private final int groupResId;
        private final int childResId;
        private int selectedGroup = -1;
        private int selectedChild = -1;

        public ExpandableAdapter(List<String> groupList, int groupResId, HashMap<String, List<String>> childMap, int childResId) {
            this.groupList = groupList;
            this.childMap = childMap;
            this.groupResId = groupResId;
            this.childResId = childResId;
        }

        @Override
        public int getGroupCount() {
            return groupList != null ? groupList.size() : 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupList != null && childMap != null ? childMap.get(groupList.get(groupPosition)).size() : 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList != null ? groupList.get(groupPosition) : null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groupList != null && childMap != null ? childMap.get(groupList.get(groupPosition)).get(childPosition) : null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return -1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(groupResId, parent, false);
                holder = new ViewHolder(convertView, false);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.setText((String) getGroup(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(childResId, parent, false);
                holder = new ViewHolder(convertView, true);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            boolean selected = selectedGroup != -1 &&
                    selectedChild != -1 &&
                    groupPosition == selectedGroup &&
                    childPosition == selectedChild;
            holder.setSelected(selected);

            holder.setText((String) getChild(groupPosition, childPosition));

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public void setSelection(int groupPosition, int childPosition) {
            this.selectedGroup = groupPosition;
            this.selectedChild = childPosition;
            notifyDataSetChanged();
        }

        static class ViewHolder {
            private final TextView textView;
            private ImageView imageView = null;

            public ViewHolder(View v, boolean hasIndicator) {
                textView = (TextView) v.findViewById(R.id.text);
                if (hasIndicator) {
                    imageView = (ImageView) v.findViewById(R.id.indicator);
                }
            }

            public void setText(String text) {
                textView.setText(text);
            }

            public void setSelected(boolean b) {
                if (imageView != null) {
                    imageView.setVisibility(b ? View.VISIBLE : View.GONE);
                }
            }
        }
    }
}
