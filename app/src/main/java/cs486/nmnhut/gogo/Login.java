package cs486.nmnhut.gogo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    Button btnLogin;
    Button btnSignUp;
    EditText txtUsername;
    EditText txtPassword;
    TextView txtForgetPassword;
    private FirebaseAuth mAuth;


    final int NetworkPermission = 100;
    final int LocationPermission = 101 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUIVariables();

        Toast t = Toast.makeText(this,"Loading",Toast.LENGTH_LONG);
        t.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        txtForgetPassword = findViewById(R.id.linkForgetPassword);
        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String email = txtUsername.getText().toString();
                auth.sendPasswordResetEmail(email);
                Toast t = Toast.makeText(Login.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT);
                t.show();
            }
        });

        btnSignUp.setEnabled(false);
        btnLogin.setEnabled(false);

        checkPermission_and_set_Events_to_button();

    }

    private void checkPermission_and_set_Events_to_button() {
        if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Toast t = Toast.makeText(this,"Not granted",Toast.LENGTH_SHORT);
            t.show();
            requestPermissions(new String[] {Manifest.permission.INTERNET},NetworkPermission);
        }
        else
        {
            Toast t = Toast.makeText(this,"Granted",Toast.LENGTH_SHORT);
            t.show();
            initializeButtons();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NetworkPermission && grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            initializeButtons();
        }


    }

    private void initializeButtons() {
        mAuth = FirebaseAuth.getInstance();

        //DatabaseHelper.SampleTrip();

        btnSignUp.setEnabled(true);
        btnLogin.setEnabled(true);

        setLoginClick();

        setSignUpClick();

        //loginWithUsernameAndPassword("nmnhut@apcs.vn","123456");
    }

    private void setSignUpClick() {
        DatabaseHelper.getUserMap();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                if (username != null && password != null && !username.isEmpty() && !password.isEmpty())
                    mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast t = Toast.makeText(Login.this,"Sign up successfully! Now you can login",Toast.LENGTH_SHORT);
                            t.show();
                        }
                        else
                        {
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast t = Toast.makeText(Login.this,"Sign up failed" + e.getMessage(), Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }
                });
            }


        });
    }

    private void setLoginClick() {
        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    PerformLogin();//match this behavior to your 'Send' (or Confirm) button
                }
                return true;
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformLogin();
            }
        });
    }

    private void PerformLogin() {
        final String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        btnLogin.setEnabled(false);
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty())
            loginWithUsernameAndPassword(username, password);
    }

    private void loginWithUsernameAndPassword(final String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast t = Toast.makeText(Login.this, "Login successfully!", Toast.LENGTH_SHORT);
                    t.show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    btnLogin.setEnabled(true);
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference();
                    String UID = mAuth.getCurrentUser().getUid();
                    ref.child("userlist").child(UID).setValue(username);
                    DatabaseHelper databaseHelper = new DatabaseHelper();
                    intent.putExtra("UID", UID);
                    startActivity(intent);
                    btnLogin.setEnabled(true);
                    finish();
                } else {
                    btnLogin.setEnabled(true);
                    Toast t = Toast.makeText(Login.this, "Login failed!", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

    }

    private void setUIVariables() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtPassword = findViewById(R.id.txtPassword);
        txtUsername = findViewById(R.id.txtUsername);
    }
}
