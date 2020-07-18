package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
private Toolbar TB;
private RecyclerView rvPrivateMessages;
private EditText InputMessageText;
private ImageButton btnSendMessage;
private String OUPIs,OUNs,OUids;
private TextView OUName,LSeen;
private CircleImageView OUPImage;
private ArrayList<MessagesModelClass> M;
private    MessagesRVAdapter adapter;
private String CurrentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
      OUids=getIntent().getExtras().get("OUID") .toString() ;
      OUNs=getIntent().getExtras().get("Naam").toString();
      OUPIs=getIntent().getExtras().get("Img").toString();


        init();

        OUName.setText(OUNs);
        Picasso.get().load(OUPIs).placeholder(R.drawable.profile_image).into(OUPImage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
       DatabaseReference Myref=FirebaseDatabase.getInstance().getReference().child("Messages").child(CurrentUserId).child(OUids) ;
       Myref.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               if(snapshot.exists()){
                   MessagesModelClass Message=snapshot.getValue(MessagesModelClass.class);
                   M.add(Message);
                   adapter.notifyDataSetChanged();
                   rvPrivateMessages.smoothScrollToPosition(rvPrivateMessages.getAdapter().getItemCount());
               }
               else{

               }

           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot snapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }

    private void init() {
        TB=(Toolbar) findViewById(R.id.ChatbarLayout);
        setSupportActionBar(TB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater LI=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =LI.inflate(R.layout.custom_chat_bar_layout,null);
        getSupportActionBar().setCustomView(view);
        OUName=(TextView)findViewById(R.id.OUName) ;
        LSeen=(TextView)findViewById(R.id.Lseen) ;
        OUPImage=(CircleImageView)findViewById(R.id.OUPimage);
        rvPrivateMessages=(RecyclerView)findViewById(R.id.rvChatMessages);
        rvPrivateMessages.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        adapter=new MessagesRVAdapter();
        rvPrivateMessages.setAdapter(adapter);
        M=new ArrayList<>();
        adapter.setMessagesList(M);
        InputMessageText=(EditText) findViewById(R.id.MessageInput);
        btnSendMessage=(ImageButton) findViewById(R.id.btnSendMessage);
    }
    private void SendMessage() {
        String MessageText=InputMessageText.getText().toString();
        if(MessageText.isEmpty()){
            Toast.makeText(ChatActivity.this, "Enter Something to Send", Toast.LENGTH_SHORT).show();
        }
        else{
            final String MessageKey= FirebaseDatabase.getInstance().getReference().child("Messages").child(CurrentUserId).child(OUids).push().getKey();

            DatabaseReference DBM=FirebaseDatabase.getInstance().getReference().child("Messages").child(CurrentUserId).child(OUids).child(MessageKey);
            final Map MM=new HashMap();
            MM.put("From",CurrentUserId);
            MM.put("Message",MessageText);
            MM.put("Type","text");
            DBM.updateChildren(MM).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        DatabaseReference DBMm=FirebaseDatabase.getInstance().getReference().child("Messages").child(OUids).child(CurrentUserId).child(MessageKey);
                        DBMm.updateChildren(MM).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    String e=task.getException().toString();
                                    Toast.makeText(ChatActivity.this, e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            InputMessageText.setText("");
        }
    }

}