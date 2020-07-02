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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {


    private  Button sendVerificaitonCodeButton,verifyButton;
    private  EditText inputPhoneNumber,inputverificationCode;


    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;




    private  String mVerificationId;
    private  PhoneAuthProvider.ForceResendingToken mResendToken;
    private  FirebaseAuth mAuth;



    Context context;
    private  ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth=FirebaseAuth.getInstance();


        sendVerificaitonCodeButton=findViewById(R.id.send_verificaitonCode_button);
        verifyButton=findViewById(R.id.verify_button);

        inputPhoneNumber=findViewById(R.id.phone_number_input);
        inputverificationCode=findViewById(R.id.verification_code_input);


        loadingBar=new ProgressDialog(PhoneLoginActivity.this);





        sendVerificaitonCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber=inputPhoneNumber.getText().toString();
                if(phoneNumber.isEmpty()){
                    Toast.makeText(PhoneLoginActivity.this, "Phone Number is requird", Toast.LENGTH_SHORT).show();
                }else{


                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please Wait .We are Sending your code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }






            }
        });


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificaitonCodeButton.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);

                String verificationcode=inputverificationCode.getText().toString();


                if(verificationcode.isEmpty()){
                    inputverificationCode.setError("Please Write First");
                    inputverificationCode.requestFocus();
                    return;
                }else{
                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Please Wait .While we are verify your code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });





        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();
                inputPhoneNumber.setText(e.toString());
                Toast.makeText(PhoneLoginActivity.this, "Please enter Current PHone Number With Your Country code", Toast.LENGTH_SHORT).show();
                sendVerificaitonCodeButton.setVisibility(View.VISIBLE);
                inputPhoneNumber.setVisibility(View.VISIBLE);


                verifyButton.setVisibility(View.GONE);
                inputverificationCode.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
              mVerificationId = verificationId;
                mResendToken = token;


                Toast.makeText(PhoneLoginActivity.this, "Code has been sent your phone.", Toast.LENGTH_SHORT).show();
                sendVerificaitonCodeButton.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);


                verifyButton.setVisibility(View.VISIBLE);
                inputverificationCode.setVisibility(View.VISIBLE);

            }
        };















    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();

                            Toast.makeText(PhoneLoginActivity.this, "Congratulation..", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();

                       } else {

                            Toast.makeText(PhoneLoginActivity.this, "'Error+"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();



                         }
                    }
                });
    }

    private void sendUserToMainActivity() {


        Intent intent=new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }


}