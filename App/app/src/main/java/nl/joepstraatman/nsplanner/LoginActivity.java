package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

/**
 * De activiteit waar een gebruiker zichzelf kan registeren of inloggen voor de app.
 * Door Joep Straatman
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String email;
    String password;
    TextView emailv, passwordv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        final Button login = findViewById(R.id.login);
        final Button create = findViewById(R.id.create);
        emailv = findViewById(R.id.email);
        passwordv = findViewById(R.id.password);
        login.setOnClickListener(this);
        create.setOnClickListener(this);
        authlistener();
    }

    /**
     *  De onclick listener voor de buttons.
     */

    public void onClick(View v) {

        email = emailv.getText().toString();
        password = passwordv.getText().toString();

        // Als de email of het wachtwoord niet is ingevuld.

        switch (v.getId()){
            case R.id.login:
                if (email.length() > 0 && password.length() > 0) {
                    logIn();
                } else{
                    Toast.makeText(LoginActivity.this, "Email or password is empty.", Toast.LENGTH_LONG).show();
                }
            case R.id.create:
                if (email.length() > 0 && password.length() > 0) {
                    createUser();
                } else{
                    Toast.makeText(LoginActivity.this, "Email or password is empty.", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     *  De user listeners voor Firebase.
     */

    @Override
    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     *  Functie die een user aanmaakt in firebase.
     */

    public void createUser(){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            }

                            // Als een user een email invult die al in gebruik is.
                            catch (FirebaseAuthUserCollisionException existEmail) {
                                Log.d("exist_email", "onComplete: exist_email");
                                Toast.makeText(LoginActivity.this, email+ " already exists!",
                                        Toast.LENGTH_SHORT).show();}

                            // Als de user het verkeerde wachtwoord invult.
                            catch (FirebaseAuthWeakPasswordException weakPassword) {
                                Log.d("weak_password", "onComplete: weak_password");
                                Toast.makeText(LoginActivity.this, "Weak password! " +
                                                "Password needs to be at least 6 characters  long!",
                                        Toast.LENGTH_LONG).show();}

                            // Als de user een verkeerde email invult.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Log.d("malformed_email", "onComplete: malformed_email");
                                Toast.makeText(LoginActivity.this,  "Malformed email!",
                                        Toast.LENGTH_SHORT).show();

                            }  catch (Exception e) {
                                Log.d("Error", "onComplete: " + e.getMessage());}

                        // Log de user in als het is gelukt.
                        } else {
                            logIn();
                        }
                    }
                });
    }

    /**
     *  Log de user in bij Firebase.
     */

    public void logIn(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Als het inloggen gelukt is.
                        if (task.isSuccessful()) {
                            Log.d("sign in succesfully", "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "User " +email+ " signed in!",Toast.LENGTH_SHORT).show();
                        }

                        // Als het inloggen mislukt.
                        else {
                            Log.w("Failed to login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();}
                    }
                });
    }

    /**
     *  Ga naar de HomeActivity.
     */

    public void next(){

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *  Listener voor of de user login status.
     */

    public void authlistener(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user!= null){
                    Log.d("Signed in", "onAuthStateChanged:signed_in:" + user.getUid());
                    next();
                } else {
                    Log.d("Signed out", "onAuthStateChanged:signed_out");
                }
            }
        };
    }
}
