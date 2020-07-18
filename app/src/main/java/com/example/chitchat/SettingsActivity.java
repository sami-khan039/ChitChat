package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
private Button UpdateProfile;
private Toolbar TB;
private EditText SetName,SetStatus;
private CircleImageView ProfileImage;
 int gallerypiccounter=1;
private ArrayList<String> ImageUriList;
private Uri ProfilePicUri,CropedProfilePicUri;
FirebaseUser CurrentUser=FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(SettingsActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SettingsActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                    return;
                }
            PickImage();
            }
        });
        UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileInfo();
            }
        });
        RetrieveProfileInfo();
    }

    private void PickImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),gallerypiccounter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallerypiccounter && resultCode==RESULT_OK){
           if(data!=null){
               ProfilePicUri=data.getData();
               CropImage.activity()
                       .setGuidelines(CropImageView.Guidelines.ON)
                       .start(this);
           } }        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                CropedProfilePicUri=result.getUri();
                final StorageReference refff= FirebaseStorage.getInstance().getReference().child("Profile Images").child(CurrentUser.getUid());
               UploadTask UT=refff.putFile(CropedProfilePicUri);
               UT.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  refff.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @Override
                      public void onSuccess(Uri uri) {
                          FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getUid()).child("Image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      Toast.makeText(SettingsActivity.this, "Pic saved to Database", Toast.LENGTH_SHORT).show();
                                  }
                              }
                          });
                      }
                  });
                   }
               });
            }
        }      }


    private void RetrieveProfileInfo() {
        DatabaseReference DB=FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getUid());
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists())&&(snapshot.child("Name").exists())&&(snapshot.child("Image").exists())){
                    String Rimageurl=snapshot.child("Image").getValue().toString();
                    String Rname=snapshot.child("Name").getValue().toString();
                    String Rstatus=snapshot.child("Status").getValue().toString();
                    SetName.setText(Rname);
                    SetStatus.setText(Rstatus);
                    Picasso.get().load(Rimageurl).into(ProfileImage);
                }
               else if((snapshot.exists())&&(snapshot.child("Name").exists())){
                    String Rname=snapshot.child("Name").getValue().toString();
                    String Rstatus=snapshot.child("Status").getValue().toString();
                    SetName.setText(Rname);
                    SetStatus.setText(Rstatus);
                }
               else{
                    Toast.makeText(SettingsActivity.this, "Please update your Information..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateProfileInfo() {
        String Name=SetName.getText().toString();
        String Status=SetStatus.getText().toString();
        if(Name.isEmpty()){
            Toast.makeText(SettingsActivity.this, "Please enter your Name", Toast.LENGTH_SHORT).show();
        }
        if(Name.isEmpty()){
            Toast.makeText(SettingsActivity.this, "Please enter your Name", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,Object> ProfileMap=new HashMap<>();
            ProfileMap.put("Uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            ProfileMap.put("Name",Name);
            ProfileMap.put("Status",Status);
        DatabaseReference DB= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
         DB.updateChildren(ProfileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful()){

                     Toast.makeText(SettingsActivity.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                     SendUserToMainActivity();
                     Toast.makeText(SettingsActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                 }
                 else{
                     String ErrorMessage=task.getException().toString();
                     Toast.makeText(SettingsActivity.this, "Error :"+ErrorMessage, Toast.LENGTH_SHORT).show();
                 }
             }
         });
    }}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100 && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                PickImage();
            }
            else{
                Toast.makeText(SettingsActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SendUserToMainActivity() {
        Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void init() {
        TB=(Toolbar)findViewById(R.id.SettingAcitivtyBar);
        setSupportActionBar(TB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile Setting");
        UpdateProfile=(Button) findViewById(R.id.UpdateProfile);
        ImageUriList=new ArrayList<>();
        SetName=(EditText)findViewById(R.id.SetName);
        SetStatus=(EditText) findViewById(R.id.SetStatus);
        ProfileImage=(CircleImageView)findViewById(R.id.profile_image);
    }
}