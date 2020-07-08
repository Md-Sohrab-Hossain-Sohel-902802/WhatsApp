package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    //<-------------------------------------Layout Variable--------------------------------->

    private Button createAccountButton;
    private EditText userEmail,userPassword;
    private TextView allreadyHaveAccountLink;




    //<---------------------------Others Variable--------------------------------------->


        private  ProgressDialog loadinBar;





    //<-------------------------Firebase Variable-------------------------------->

    private  FirebaseAuth mAuth;
    private   DatabaseReference databaseReference,rootRef;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Initialize();


        mAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();
        rootRef=FirebaseDatabase.getInstance().getReference().child("Users");







        allreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendUserToLoginActivity();
            }
        });



        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        createNewAccount();
            }
        });









    }

    private void createNewAccount() {
        
        
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();
        
        if(email.isEmpty()){
            userEmail.setError("Please Enter An email  .");
            userEmail.requestFocus();
            return;
        }else if(password.isEmpty()){
            userPassword.setError("Please Enter  A Password  .");
            userPassword.requestFocus();
            return;
        }else{

            loadinBar.setTitle("Creating new Account");
            loadinBar.setMessage("Please Wait while we are creating new account for you");
            loadinBar.setCanceledOnTouchOutside(true);
            loadinBar.show();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                           String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            String currentUsrid=mAuth.getCurrentUser().getUid();


                            rootRef.child(currentUsrid).child("deviceToken").setValue(deviceToken);


                            databaseReference.child("Users").child(currentUsrid).setValue("");



                            sendUserToMainActivity();
                            Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            loadinBar.dismiss();
                        }else{
                            loadinBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "Something Problem : "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        
        
        
    }

    private void sendUserToMainActivity() {

        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLoginActivity() {



        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);



    }


    private void Initialize() {

      loadinBar=new ProgressDialog(RegisterActivity.this);

        createAccountButton=findViewById(R.id.register_Button);
       userEmail=findViewById(R.id.register_email);
        userPassword=findViewById(R.id.register_password);

        allreadyHaveAccountLink=findViewById(R.id.allready_have_an_accountLink);




    }
}