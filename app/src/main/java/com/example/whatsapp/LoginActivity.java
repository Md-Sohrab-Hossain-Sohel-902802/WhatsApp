package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {





    //<-----------------Firebase Variables----------------------->

    private FirebaseUser currentUser;





    private Button loginButton,phoneLoginButton;
    private EditText userEmail,userPassword;
    private TextView needNewAccount,forgotPasswordLInk;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialize();



        needNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        sendUserToRegisterActivity();
            }
        });












    }








    @Override
    protected void onStart() {
        super.onStart();


        if(currentUser!=null){
            setdUserToMainActivity();
        }







    }

    private void setdUserToMainActivity() {

        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);

    }


    private void sendUserToRegisterActivity() {



        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);



    }


    private void Initialize() {


        loginButton=findViewById(R.id.login_Button);
        userEmail=findViewById(R.id.login_email);
        userPassword=findViewById(R.id.login_password);

        phoneLoginButton=findViewById(R.id.phone_login_Button);
        forgotPasswordLInk=findViewById(R.id.forget_PasswordLink);
        needNewAccount=findViewById(R.id.need_New_Account);




    }






}