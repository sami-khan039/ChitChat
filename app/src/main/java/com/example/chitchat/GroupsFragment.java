package com.example.chitchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupsFragment extends Fragment {

    private View myview;
private RecyclerView RVgrouplist;
private GroupListAdapter adapter;
private ArrayList<GroupName> GNlist;
    DatabaseReference DBR;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBR= FirebaseDatabase.getInstance().getReference().child("Groups");
        GNlist=new ArrayList<>();
        RetriveGroupName();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myview= inflater.inflate(R.layout.fragment_groups, container, false);

         init();

        return myview;
    }

    private void init() {
        RVgrouplist=(RecyclerView) myview.findViewById(R.id.RVgrouplist);
        adapter=new GroupListAdapter(getContext());
        adapter.setGroupnamees(GNlist);
        RVgrouplist.setAdapter(adapter);
        RVgrouplist.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void RetriveGroupName() {

        DBR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GNlist.clear();
                Iterator i=snapshot.getChildren().iterator();
                while(i.hasNext()){
                    String Name=((DataSnapshot)i.next()).getKey();
                    GroupName clas=new GroupName(Name);
                    GNlist.add(clas);
                    adapter.notifyDataSetChanged();
                } }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}