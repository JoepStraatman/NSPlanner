package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RoutePlanActivity extends AppCompatActivity {

    String naam;
    private FirebaseAuth authTest;
    private FirebaseAuth.AuthStateListener authListenerTest;
    private static final String Tag = "Firebase_test";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        authTest = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        laadDataIn();
        getRouteFromFirebase();
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
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void laadDataIn(){
        Bundle extras = getIntent().getExtras();
        naam = extras.getString("name");
        TextView titelnaam = findViewById(R.id.titelnaam);
        titelnaam.setText(naam);
    }

    public void getRouteFromFirebase(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = authTest.getCurrentUser();
                RouteData routedata = dataSnapshot.child("Onlangs/"+user.getUid()+"/"+naam).getValue(RouteData.class);
                if (routedata != null){
                    Log.d("fireroute", routedata.Ritnummer+ " - " + routedata.TijdDatum + " - " + routedata.Naar + " - " + routedata.Van);
                }}
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("oops","Oops something went wrong: ",databaseError.toException());
                //startActivity(new Intent(getApplicationContext(), Home_screen.class));finish();
            }
        });
    }
}
