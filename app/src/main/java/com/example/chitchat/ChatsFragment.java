package com.example.chitchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {
private View view;
private RecyclerView rvChatList;
private DatabaseReference myR;



    public ChatsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myR= FirebaseDatabase.getInstance().getReference().child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_chats, container, false);
      rvChatList=view.findViewById(R.id.rvChatList);
      rvChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> op=new FirebaseRecyclerOptions.Builder<Users>().setQuery(myR,Users.class).build();
        FirebaseRecyclerAdapter<Users,ViewHolder>  adapter=new FirebaseRecyclerAdapter<Users, ViewHolder>(op) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Users model) {
                final String Uid=getRef(position).getKey();
                final String[] Img = new String[1];
                final String[] Naam = new String[1];
                DatabaseReference DBaseR=FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                DBaseR.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            if(snapshot.hasChild("Image")){
                                Img[0] =snapshot.child("Image").getValue().toString();
                                Picasso.get().load(Img[0]).into(holder.PI);
                            }

                            Naam[0] =snapshot.child("Name").getValue().toString();
                            String Status=snapshot.child("Status").getValue().toString();

                            holder.N.setText(Naam[0]);
                            holder.S.setText(Status);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getContext(),ChatActivity.class);
                        intent.putExtra("OUID",Uid);
                        intent.putExtra("Naam",Naam[0].toString());
                        intent.putExtra("Img",Img[0].toString());
                        startActivity(intent);
                    }
                });
            }



            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(getContext()).inflate(R.layout.users_display_layout,parent,false);
                ViewHolder holder=new ViewHolder(view);
                return holder;
            }
        };
        rvChatList.setAdapter(adapter);
        adapter.startListening();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView PI;
        private TextView N,S;
        private ImageView OS;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PI=(CircleImageView)itemView.findViewById(R.id.UsersProfileImage);
            N=(TextView)itemView.findViewById(R.id.OnlineUsersName);
            S=(TextView)itemView.findViewById(R.id.OnlineUserStatus);
            OS=(ImageView) itemView.findViewById(R.id.OnlineStatus);}
    }
}