package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private ImageButton IBTNsendgroupmessage;
    private EditText    GroupMessageText;
    private ScrollView GMSV;
    private TextView   GMV;
    private String Gname,CurrentUserName,CurrentUserID,CurrentDate,CurrentTime;
    private DatabaseReference DRtoGroupName,DRtomessagekey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Gname=getIntent().getExtras().get("GN").toString();
        DRtoGroupName=FirebaseDatabase.getInstance().getReference().child("Groups").child(Gname);
        init();
        GetUserInfo();
        IBTNsendgroupmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SaveInfoToDB();
               GroupMessageText.setText("");
                GMSV.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showAllGroupMessages();
    }

    private void showAllGroupMessages() {
        DRtoGroupName.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Iterator iter=snapshot.getChildren().iterator();
                    while(iter.hasNext()){
                        String date=(String)((DataSnapshot)iter.next()).getValue();
                        String mesage=(String)((DataSnapshot)iter.next()).getValue();
                        String name=(String)((DataSnapshot)iter.next()).getValue();
                        String time=(String)((DataSnapshot)iter.next()).getValue();

                        GMV.append(name+" :\n"+mesage+"\n"+date+"   "+time+"\n\n\n");
                        GMSV.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Iterator iter=snapshot.getChildren().iterator();
                    while(iter.hasNext()){
                        String date=(String)((DataSnapshot)iter.next()).getValue();
                        String mesage=(String)((DataSnapshot)iter.next()).getValue();
                        String name=(String)((DataSnapshot)iter.next()).getValue();
                        String time=(String)((DataSnapshot)iter.next()).getValue();

                        GMV.append(name+" :\n"+mesage+"\n"+date+"   "+time+"\n\n\n");

                    }
                }
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

    private void SaveInfoToDB() {
     String message=GroupMessageText.getText().toString();
     if(message.isEmpty()){
         Toast.makeText(GroupChatActivity.this, "you cant send empty messages!", Toast.LENGTH_SHORT).show();
     }
     else{
         Calendar calfortime=Calendar.getInstance();
         SimpleDateFormat formatfortime=new SimpleDateFormat("hh:mm a");
         CurrentTime=formatfortime.format(calfortime.getTime());

         Calendar calforDate=Calendar.getInstance();
         SimpleDateFormat formatforDate=new SimpleDateFormat("MMM dd, yyyy");
         CurrentDate=formatforDate.format(calforDate.getTime());
         String MessageKey=DRtoGroupName.push().getKey();
         DRtomessagekey=DRtoGroupName.child(MessageKey);
         HashMap<String,Object> Mmap=new HashMap<>();
         Mmap.put("Date",CurrentDate);
         Mmap.put("Time",CurrentTime);
         Mmap.put("Message",message);
         Mmap.put("SenderName",CurrentUserName);
         DRtomessagekey.updateChildren(Mmap);
     }
    }

    private void GetUserInfo() {
        CurrentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DB= FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists())  {
                  CurrentUserName=snapshot.child("Name").getValue().toString();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        mToolBar=(Toolbar)findViewById(R.id.GroupChatBarLayout);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(Gname);
        IBTNsendgroupmessage=(ImageButton)findViewById(R.id.IBTNsendGroupMessage);
        GroupMessageText=(EditText)findViewById(R.id.GroupTextMessage);
        GMSV=(ScrollView)findViewById(R.id.GMSV);
        GMSV.fullScroll(ScrollView.FOCUS_DOWN);
        GMV=(TextView)findViewById(R.id.GroupMessageView);
    }
}