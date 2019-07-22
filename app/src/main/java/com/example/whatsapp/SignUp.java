package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class SignUp extends AppCompatActivity
{
    private EditText edtsignUp_name,edtsignUP_password,edt_signup_email;
    private Button btnSignUp;
    private TextView txtforlogin;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtsignUp_name=(EditText) findViewById(R.id.edt_name_signup);
        edtsignUP_password=(EditText)findViewById(R.id.edt_password_signup);
        edt_signup_email=(EditText)findViewById(R.id.edt_email_signUp);
        btnSignUp=(Button)findViewById(R.id.button_signup);
        txtforlogin=(TextView)findViewById(R.id.textforlogin);

        txtforlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp.this,MainActivity.class);
                startActivity(intent);

            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(SignUp.this);
                dialog.setMessage("please wait...");
                dialog.show();
                final ParseUser appUser=new ParseUser();
                appUser.setUsername(edtsignUp_name.getText().toString());
                appUser.setEmail(edt_signup_email.getText().toString());
                appUser.setPassword(edtsignUP_password.getText().toString());

                if (edtsignUp_name.getText().toString().equals("")){
                    FancyToast.makeText(SignUp.this,"Name is Mandatory!", FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                }
                else if (edt_signup_email.getText().toString().equals("")){
                    FancyToast.makeText(SignUp.this, "Email is Mandatory", FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                }
                else if (edtsignUP_password.getText().toString().equals("")){
                    FancyToast.makeText(SignUp.this, "Password is Mandatory!", FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                }
                else {

                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                FancyToast.makeText(SignUp.this, appUser.get("username") + " is Successfully SignUp", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                                Intent intent = new Intent(SignUp.this, Home.class);
                                startActivity(intent);

                                dialog.dismiss();
                                finish();
                            } else {
                                FancyToast.makeText(SignUp.this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

                            }
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    public void backgroundClickSignupActivity(View view){
        try {
            InputMethodManager input_method_service = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            input_method_service.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
