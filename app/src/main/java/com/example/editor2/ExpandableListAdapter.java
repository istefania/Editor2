package com.example.editor2;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHashMap;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,HashMap<String,List<String>> listHashMap) {
        this.context = context;
        this.listDataHeader=listDataHeader;
        this.listHashMap=listHashMap;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.listHashMap.get(this.listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.listHashMap.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int itemPositon) {
        return this.listHashMap.get(this.listDataHeader.get(groupPosition)).get(itemPositon);//i=group position, i1=item position
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        String headerTitle = "";
        if (groupPosition == 0) {
            headerTitle = "Flip";
        } else {
            headerTitle = "Rotate";
        }

        if( view== null){

            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_group,null);

        }

        TextView ListHeader = (TextView)view.findViewById(R.id.ListHeader);
        ListHeader.setTypeface(null, Typeface.BOLD);
        ListHeader.setText(headerTitle);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
       final String childText = (String)getChild(groupPosition, childPosition);
       if (view == null ) {
           LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view = inflater.inflate(R.layout.list_item, null);

       }

       TextView txtListChild = (TextView) view.findViewById(R.id.ListItem);
       txtListChild.setText(childText);
       return view;
    }

    private void show() {
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
