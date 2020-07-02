package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {



    private  Button updateAccountSettings;
    private  EditText userName,userStatus;
    private  CircleImageView userProfileImage;


    private  String currentUserid;
    private  FirebaseAuth mAuth;
    private  DatabaseReference rootRef;

    private  static  final  int GALLERY_PIC=1;

    private  StorageReference userProfileImagesRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth=FirebaseAuth.getInstance();
        currentUserid=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();

        userProfileImagesRef=FirebaseStorage.getInstance().getReference().child("Profile Images");



        Initialize();

        userName.setVisibility(View.INVISIBLE);
        RetriveUserInfo();
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                   Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,GALLERY_PIC);


            }
        });





        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        UpdateSettings();
            }
        });






    }


    private void Initialize() {


        updateAccountSettings=findViewById(R.id.update_settings_button);
        userName=findViewById(R.id.set_user_name);
        userStatus=findViewById(R.id.set_profile_status);
        userProfileImage=findViewById(R.id.set_profile_image);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PIC && resultCode==RESULT_OK && data.getData()!=null) {

            Uri imageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(SettingActivity.this);


        }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if(resultCode==RESULT_OK){
                    Uri resultUri = result.getUri();
                    Picasso.get().load(resultUri).into(userProfileImage);


                  saveImage(resultUri);
                }






        }






    }



    private void saveImage(Uri resultUri) {



        final ProgressDialog progressDialog=new ProgressDialog(SettingActivity.this);
        progressDialog.setTitle("Uploading.......");
        progressDialog.show();




        StorageReference reference=userProfileImagesRef.child(currentUserid+".jpg");
        reference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isSuccessful());
                Uri url=uri.getResult();
                    rootRef.child("Users").child(currentUserid).child("image").setValue(url.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(SettingActivity.this, "Your Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SettingActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }









    private void UpdateSettings() {

        String setuserName=userName.getText().toString();
        String setuserStatus=userStatus.getText().toString();


        if(setuserName.isEmpty()){
            userName.setError("Please Write Your User Name");
            userName.requestFocus();
            return;
        }if(setuserStatus.isEmpty()){
            userStatus.setError("Please Write Your Status");
            userStatus.requestFocus();
            return;
        }else{
            HashMap<String ,String> profileMap=new HashMap<>();;

                profileMap.put("uid",currentUserid);
                profileMap.put("name",setuserName);
                profileMap.put("status",setuserStatus);

                    rootRef.child("Users").child(currentUserid).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                            Toast.makeText(SettingActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        }else

                        {
                           Toast.makeText(SettingActivity.this, "Error : "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    });



        }










    }


    private void sendUserToMainActivity() {

        Intent intent=new Intent(SettingActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void RetriveUserInfo() {

        rootRef.child("Users").child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists() )&& (snapshot.hasChild("name"))   && (snapshot.hasChild("status") )  && (snapshot.hasChild("image"))){

                    String name=snapshot.child("name").getValue().toString();
                    String status=snapshot.child("status").getValue().toString();
                    String image=snapshot.child("image").getValue().toString();


                    userName.setText(""+name);
                    userStatus.setText(""+status);
                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(userProfileImage);





                }else if((snapshot.exists() )&& (snapshot.hasChild("name"))   && (snapshot.hasChild("status") )) {
                    String name=snapshot.child("name").getValue().toString();
                    String status=snapshot.child("status").getValue().toString();

                    userName.setText(""+name);
                    userStatus.setText(""+status);

                }else{
                    Toast.makeText(SettingActivity.this, "Please Set & update your profile information", Toast.LENGTH_SHORT).show();
                    userName.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }







}