package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileActivity extends AppCompatActivity {
private String OtherUserId,Current_State;
private CircleImageView OUPI;
private TextView OUN,OUS;
private Button btnSendMessageRequest,btnCancelMessageRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        OtherUserId=getIntent().getExtras().get("OtherUserId").toString();
        
        init();
        RetrieveOtherUserInfo();
        Current_State="new";

    }




    private void RetrieveOtherUserInfo() {
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(OtherUserId)){
            btnSendMessageRequest.setVisibility(View.GONE);
        }
        DatabaseReference D= FirebaseDatabase.getInstance().getReference().child("Users").child(OtherUserId);
        D.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists())   && (snapshot.hasChild("Image"))){
                  String i=snapshot.child("Image").getValue().toString();
                  String n=snapshot.child("Name").getValue().toString();
                  String s=snapshot.child("Status").getValue().toString();

                  Picasso.get().load(i).into(OUPI);
                  OUN.setText(n);
                  OUS.setText(s);

                  ManageChatRequests();

                }
                else{
                    String n=snapshot.child("Name").getValue().toString();
                    String s=snapshot.child("Status").getValue().toString();

                    OUN.setText(n);
                    OUS.setText(s);
                    ManageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ManageChatRequests() {
        DatabaseReference DBr=FirebaseDatabase.getInstance().getReference().child("Chat_Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DBr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.hasChild(OtherUserId)){
                        String RT=snapshot.child(OtherUserId).child("Request_Type").getValue().toString();
                        if(RT.equals("Sent")){
                            Current_State="Request_Sent";
                            btnSendMessageRequest.setText("Cancel Message Request Sent");
                        }
                         if(RT.equals("Received")){
                             Current_State="Request_Received";
                             btnSendMessageRequest.setText("Accept Message Request");
                             btnCancelMessageRequest.setVisibility(View.VISIBLE);
                             btnCancelMessageRequest.setEnabled(true);
                             btnCancelMessageRequest.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     CancelMessageRequest();
                                     Current_State="new";
                                     btnCancelMessageRequest.setVisibility(View.INVISIBLE);
                                     btnCancelMessageRequest.setEnabled(false);
                                 }
                             });
                         }
                    }
                    else{
                      DatabaseReference DBrefrence=FirebaseDatabase.getInstance().getReference().child("Contacts");
                      DBrefrence.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if(snapshot.hasChild(OtherUserId)) {
                                 Current_State="Friends";
                                 btnCancelMessageRequest.setVisibility(View.INVISIBLE);
                                 btnCancelMessageRequest.setEnabled(false);
                                 btnSendMessageRequest.setText("Remove Contact");
                                 btnSendMessageRequest.setEnabled(true);
                             }
                             else{
                                 btnSendMessageRequest.setText("Send Message");
                                 btnSendMessageRequest.setEnabled(true);
                                 btnCancelMessageRequest.setEnabled(false);
                                 btnCancelMessageRequest.setVisibility(View.INVISIBLE);
                                 Current_State="new";
                             }
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {

                          }
                      });
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     btnSendMessageRequest.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             btnSendMessageRequest.setEnabled(false);
             if(Current_State.equals("new")){
                SendChatRequest();
             }
             if(Current_State.equals("Request_Sent")){
                 btnSendMessageRequest.setEnabled(false);
                 CancelMessageRequest();
             }
             if(Current_State.equals("Request_Received")){
                 btnSendMessageRequest.setEnabled(false);
                 AcceptMessageRequest();
             }
             if(Current_State.equals("Friends")){
                 btnSendMessageRequest.setEnabled(false);
                 RemoveContact();
             }
         }
     });

    }

    private void RemoveContact() {
        final DatabaseReference DR=FirebaseDatabase.getInstance().getReference().child("Contacts");
        DR.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OtherUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DR.child(OtherUserId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(OtherUserProfileActivity.this, "Contact Removed Successfully", Toast.LENGTH_SHORT).show();
                                Current_State="new";
                                btnSendMessageRequest.setText("Send Message");
                                btnSendMessageRequest.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });





    }

    private void AcceptMessageRequest() {
    final    DatabaseReference DB=FirebaseDatabase.getInstance().getReference().child("Contacts");
        DB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OtherUserId).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DB.child(OtherUserId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                            final     DatabaseReference  DBRef=FirebaseDatabase.getInstance().getReference().child("Chat_Requests");
                            DBRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OtherUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()) {
                                       DBRef.child(OtherUserId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   Current_State="Friends";
                                                   btnCancelMessageRequest.setVisibility(View.INVISIBLE);
                                                   btnCancelMessageRequest.setEnabled(false);
                                                   btnSendMessageRequest.setText("Remove Contact");
                                                   btnSendMessageRequest.setEnabled(true);

                                               }
                                           }
                                       });

                                   }
                                }
                            });
                            }
                        }
                    });
                }
            }
        });


    }

    private void CancelMessageRequest() {
        final DatabaseReference DR=FirebaseDatabase.getInstance().getReference().child("Chat_Requests");
        DR.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OtherUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DR.child(OtherUserId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Current_State="new";
                                btnSendMessageRequest.setText("Send Message");
                                btnSendMessageRequest.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendChatRequest() {
        final DatabaseReference DB=FirebaseDatabase.getInstance().getReference().child("Chat_Requests");
        DB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OtherUserId).child("Request_Type").setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DB.child(OtherUserId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Request_Type").setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                HashMap<String,Object>  NM=new HashMap<>();
                                NM.put("From",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                NM.put("Type","Request");
                                DatabaseReference Nref=FirebaseDatabase.getInstance().getReference().child("Notifications").child(OtherUserId).push();
                                Nref.setValue(NM).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()) {
                                           Toast.makeText(OtherUserProfileActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                           Current_State="Request_Sent";
                                           btnSendMessageRequest.setEnabled(true);
                                           btnSendMessageRequest.setText("Cancel Message Request Sent");
                                       }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });
    }

    private void init() {
        btnCancelMessageRequest=(Button)findViewById(R.id.btnCancelMessageRequest) ;
        OUPI=(CircleImageView) findViewById(R.id.OPI);
        OUN=(TextView) findViewById(R.id.OUN);
        OUS=(TextView) findViewById(R.id.OUS);
        btnSendMessageRequest=(Button) findViewById(R.id.btnSendMessageRequest);
    }
}