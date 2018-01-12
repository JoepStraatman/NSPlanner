package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Overstap extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth authTest;
    EditText station1;
    EditText station2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overstap);
        makeviews();
        authTest = FirebaseAuth.getInstance();
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
    public void onClick(View v) {
        if (v.getId() == R.id.zoek) {
            if (station1.getText().toString().equals("") || station2.getText().toString().equals("")){
                Toast.makeText(this, "Er mist een veld!",Toast.LENGTH_SHORT).show();
            }else{
                Intent i = new Intent(this, Tijd.class);
                i.putExtra("van", station1.getText().toString());
                i.putExtra("naar", station2.getText().toString());
                startActivity(i);
                finish();}
        } else if (v.getId() == R.id.verwijder) {
            startActivity(new Intent(Overstap.this, Route.class));finish();
        }
    }
    public void makeviews(){
        station1 = findViewById(R.id.station1);
        station2 = findViewById(R.id.station2);
    }
}
