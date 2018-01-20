package com.erikterwiel.mountainviews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity.java";

    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirm;
    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = (EditText) findViewById(R.id.register_user);
        mEmail = (EditText) findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.register_password);
        mConfirm = (EditText) findViewById(R.id.register_confirm);
        mRegister = (Button) findViewById(R.id.register_register);

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        final CognitoUserPool userPool = new CognitoUserPool(this, Constants.cognitoPoolID,
                Constants.cognitoClientID, Constants.cognitoClientSecret, clientConfiguration);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPassword.getText().toString().equals(mConfirm.getText().toString())) {
                    SignUpHandler signUpHandler = new SignUpHandler() {
                        @Override
                        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                                              CognitoUserCodeDeliveryDetails
                                                      cognitoUserCodeDeliveryDetails) {
                            Toast.makeText(RegisterActivity.this, "Sign up successful, " +
                                    "check email for verification link.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        @Override
                        public void onFailure(Exception exception) {
                            Toast.makeText(RegisterActivity.this,
                                    "Network error, try again", Toast.LENGTH_LONG).show();
                            exception.printStackTrace();
                        }
                    };git
                    CognitoUserAttributes userAttributes = new CognitoUserAttributes();
                    userAttributes.addAttribute("email", mEmail.getText().toString());
                    userPool.signUpInBackground(mUsername.getText().toString(),
                            mPassword.getText().toString(), userAttributes, null,signUpHandler);
                } else {
                    Toast.makeText(RegisterActivity.this, "Password and confirmation do not" +
                            " match, try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
