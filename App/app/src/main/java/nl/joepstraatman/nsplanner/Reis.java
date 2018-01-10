package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Reis extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth authTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis);
        authTest = FirebaseAuth.getInstance();
        Button zoek = findViewById(R.id.zoek);
        Button verwijder = findViewById(R.id.verwijder);
        zoek.setOnClickListener(this);
        verwijder.setOnClickListener(this);
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
        startActivity(new Intent(this, Home.class));finish();
        super.onBackPressed();
    }
    public void onClick(View v) {
        if (v.getId() == R.id.zoek) {
            EditText naam = findViewById(R.id.naam);
            String naamtxt = naam.getText().toString();
            if (naamtxt.equals("")){
                Toast.makeText(this, "Geen naam ingevuld!",Toast.LENGTH_SHORT).show();
            }else{
                Intent i = new Intent(this, Tijd.class);
                i.putExtra("name", naam.getText().toString());
                startActivity(i);
                finish();
            }
        } else if (v.getId() == R.id.verwijder) {
            startActivity(new Intent(Reis.this, Home.class));finish();
        }
    }
}
