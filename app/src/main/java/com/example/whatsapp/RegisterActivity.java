package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    //<-------------------------------------Layout Variable--------------------------------->

    private Button createAccountButton;
    private EditText userEmail,userPassword;
    private TextView allreadyHaveAccountLink;




    //<---------------------------Others Variable--------------------------------------->








    //<-------------------------Firebase Variable-------------------------------->












    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Initialize();


        allreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendUserToLoginActivity();
            }
        });



        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });









    }

    private void sendUserToLoginActivity() {



        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);



    }


    private void Initialize() {


        createAccountButton=findViewById(R.id.register_Button);
       userEmail=findViewById(R.id.register_email);
        userPassword=findViewById(R.id.register_password);

        allreadyHaveAccountLink=findViewById(R.id.allready_have_an_accountLink);




    }
}