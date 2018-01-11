package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Tijd extends AppCompatActivity {
    private FirebaseAuth authTest;
    TextView station1;
    TextView station2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tijd);
        authTest = FirebaseAuth.getInstance();
        laadDataIn();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mybutton) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
    public void logout(){ //Go to the Main class. Called after login is complete.
        authTest.signOut();
        Log.d("Signout", "onAuthStateChanged:signed_out2");
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Reis.class));finish();
        super.onBackPressed();
    }
    public void laadDataIn(){
        Bundle extras = getIntent().getExtras();
        String naam = extras.getString("name");
        String van = extras.getString("van");
        String naar = extras.getString("naar");
        station1 = findViewById(R.id.van);
        station2 = findViewById(R.id.naar);
        station1.setText(van.toString());
        station2.setText(naar.toString());
    }
}
