package com.example.chitchat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private ArrayList<GroupName> Groupnamees;
    public Context c;
    public void setGroupnamees(ArrayList<GroupName> groupnamees) {
        Groupnamees = groupnamees;
        notifyDataSetChanged();
    }

    public GroupListAdapter(Context c) {
        this.c=c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.grouplistlayout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
       holder.groupname.setText(Groupnamees.get(position).getName());
       holder.CVgroup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String GN=Groupnamees.get(position).getName().toString();
               Intent intent=new Intent(c,GroupChatActivity.class);
               intent.putExtra("GN",GN);
               c.startActivity(intent);
           }
       });

    }

    @Override
    public int getItemCount() {
        return Groupnamees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView groupname;
        private CardView CVgroup;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupname=itemView.findViewById(R.id.GroupName);
            CVgroup=itemView.findViewById(R.id.CVgroup);
        }
    }

}
