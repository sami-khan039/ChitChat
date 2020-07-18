package com.example.chitchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestListFragment extends Fragment {
private View view;
private RecyclerView rvRequests;
private DatabaseReference DBREF;


    public RequestListFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   DBREF= FirebaseDatabase.getInstance().getReference().child("Chat_Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_request_list, container, false);
       init();

        return view;
    }

    private void init() {
        rvRequests=(RecyclerView)view.findViewById(R.id.rvRequestList);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> op=new FirebaseRecyclerOptions.Builder<Users>().setQuery(DBREF,Users.class).build();
        FirebaseRecyclerAdapter<Users,ViewHolder> adapter=new FirebaseRecyclerAdapter<Users, ViewHolder>(op) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Users model) {

final String  OUID=getRef(position).getKey();
                DBREF.child(OUID).child("Request_Type").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if(snapshot.exists()) {
                           String value=snapshot.getValue().toString();
                           if(value.equals("Received")){

                               holder.AR.setVisibility(View.VISIBLE);
                               holder.DR.setVisibility(View.VISIBLE);
                               DatabaseReference D=FirebaseDatabase.getInstance().getReference().child("Users").child(OUID);
                               D.addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                                           if(snapshot.hasChild("Image")){
                                               String i=snapshot.child("Image").getValue().toString();
                                               Picasso.get().load(i).placeholder(R.drawable.profile_image).into(holder.PI);
                                           }
                                               String n=snapshot.child("Name").getValue().toString();
                                               String s=snapshot.child("Status").getValue().toString();
                                               holder.UN.setText(n);
                                               holder.US.setText(s);

                                       holder.AR.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               AcceptMessageRequest(OUID);
                                           }
                                       });
                                       holder.DR.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               DeclineRequest(OUID);
                                           }
                                       });

                                       }
                                                                      @Override
                                   public void onCancelled(@NonNull DatabaseError error) {

                                   }
                               });

                           }
                           else{
                               holder.itemView.setVisibility(View.GONE);
                           }

                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                ViewHolder holder=new ViewHolder(view);
                return holder;
            }
        };

rvRequests.setAdapter(adapter);
adapter.startListening();
    }

    private void DeclineRequest(final String OUID) {
        final DatabaseReference DR=FirebaseDatabase.getInstance().getReference().child("Chat_Requests");
        DR.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DR.child(OUID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Contact Removed Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void AcceptMessageRequest(final String OUID) {
        final    DatabaseReference DBaro=FirebaseDatabase.getInstance().getReference().child("Contacts");
        DBaro.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OUID).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DBaro.child(OUID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                final     DatabaseReference  DBRef0=FirebaseDatabase.getInstance().getReference().child("Chat_Requests");
                                DBRef0.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(OUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            DBRef0.child(OUID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){

                                                        Toast.makeText(getContext(), "Contact Added Successfully", Toast.LENGTH_SHORT).show();

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

    public class ViewHolder extends RecyclerView.ViewHolder {
    private Button AR,DR;
    private CircleImageView PI;
    private TextView UN,US;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

      AR=(Button)itemView.findViewById(R.id.btnAcceptRequest);
      DR=(Button)itemView.findViewById(R.id.btnDeclineRequest);
      UN=(TextView)itemView.findViewById(R.id.OnlineUsersName);
      US=(TextView)itemView.findViewById(R.id.OnlineUserStatus);
      PI=(CircleImageView) itemView.findViewById(R.id.UsersProfileImage);
        }
    }

}