package com.example.chitchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesRVAdapter extends RecyclerView.Adapter<MessagesRVAdapter.ViewHolder> {
    private ArrayList<MessagesModelClass>  MessagesList=new ArrayList<>();

    public void setMessagesList(ArrayList<MessagesModelClass> messagesList) {
        MessagesList = messagesList;
    }

    public MessagesRVAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
      String me= FirebaseAuth.getInstance().getCurrentUser().getUid();
      String Sender=MessagesList.get(position).getFrom();
        holder.MyText.setVisibility(View.INVISIBLE);
        holder.HisText.setVisibility(View.INVISIBLE);
        holder.HisPI.setVisibility(View.INVISIBLE);
      if(Sender.equals(me)){
          holder.MyText.setVisibility(View.VISIBLE);
          holder.MyText.setBackgroundResource(R.drawable.sender_messages_layout);
          holder.MyText.setText(MessagesList.get(position).getMessage());
      }
      else{

          holder.HisText.setVisibility(View.VISIBLE);
          holder.HisPI.setVisibility(View.VISIBLE);

          holder.HisText.setBackgroundResource(R.drawable.other_user_messages_layout);
          holder.HisText.setText(MessagesList.get(position).getMessage());

          DatabaseReference dbr= FirebaseDatabase.getInstance().getReference().child("Users").child(MessagesList.get(position).getFrom().toString());
          dbr.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {

                      if(snapshot.hasChild("Image")){
                          String i=snapshot.child("Image").getValue().toString();
                          Picasso.get().load(i).placeholder(R.drawable.profile_image).into(holder.HisPI);
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }



    }

    @Override
    public int getItemCount() {
        return MessagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
      private TextView MyText,HisText;
      private CircleImageView HisPI;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MyText=(TextView)itemView.findViewById(R.id.SenderMessage);
            HisText=(TextView)itemView.findViewById(R.id.OtherUserMessage);
            HisPI=(CircleImageView)itemView.findViewById(R.id.PP);
        }
    }

}
