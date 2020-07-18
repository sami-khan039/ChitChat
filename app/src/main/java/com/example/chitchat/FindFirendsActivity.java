package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFirendsActivity extends AppCompatActivity {
private Toolbar TB;
private RecyclerView rvFindFirends;
private DatabaseReference DBR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_firends);
        DBR=FirebaseDatabase.getInstance().getReference().child("Users");
        TB=(Toolbar) findViewById(R.id.FindFriendsbar);
        rvFindFirends=(RecyclerView) findViewById(R.id.rvFindFriends);

        setSupportActionBar(TB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options=new FirebaseRecyclerOptions.Builder<Users>().setQuery(DBR,Users.class).build();

        FirebaseRecyclerAdapter<Users,ViewHolder>  adapter=new FirebaseRecyclerAdapter<Users, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Users model) {
             holder.OUN.setText(model.getName());
             holder.OUS.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.UPI);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String OtherUserId=getRef(position).getKey();
                        Intent intent=new Intent(FindFirendsActivity.this,OtherUserProfileActivity.class);
                        intent.putExtra("OtherUserId",OtherUserId);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                ViewHolder holder=new ViewHolder(view);
                return holder;
            }
        };
        rvFindFirends.setAdapter(adapter);
        rvFindFirends.setLayoutManager(new LinearLayoutManager(FindFirendsActivity.this));
        adapter.startListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView UPI;
        private ImageView OS;
        private TextView   OUN,OUS;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UPI=(CircleImageView) itemView.findViewById(R.id.UsersProfileImage);
            OS=(ImageView)itemView.findViewById(R.id.OnlineStatus);
            OUN=(TextView) itemView.findViewById(R.id.OnlineUsersName);
            OUS=(TextView)itemView.findViewById(R.id.OnlineUserStatus);
        }
    }

}