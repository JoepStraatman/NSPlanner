package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RouteActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth authTest;
    String naam;
    String van;
    String naar;
    String tijd;
    String ritNummer;
    JSONObject data;
    private FirebaseAuth.AuthStateListener authListenerTest;
    private static final String Tag = "Firebase_test";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        authTest = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        laadDataIn();
        openAdapter();
        Button voegtoe = findViewById(R.id.voegToe);
        voegtoe.setOnClickListener(this);
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

    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.voegToe):
                dataToFirebase();
                Intent i = new Intent(this, RoutePlanActivity.class);
                i.putExtra("name", naam);
                startActivity(i);
                finish();
                break;
        }
    }

    public void laadDataIn(){
        Bundle extras = getIntent().getExtras();
        naam = extras.getString("name");
        van = extras.getString("van");
        naar = extras.getString("naar");
        tijd = extras.getString("tijd");
        ritNummer = extras.getString("ritnummer");
        try {
            data = new JSONObject(extras.getString("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setTextViews();
    }

    public void setTextViews(){
        TextView vanV = findViewById(R.id.van);
        TextView naarV = findViewById(R.id.naar);
        TextView reistijd = findViewById(R.id.reisTijd);
        TextView reistijdVertraging = findViewById(R.id.reisVertraging);
        vanV.setText(van);
        naarV.setText(naar);
        try {
            reistijd.setText("Reistijd: " + data.getString("GeplandeReisTijd"));
            if (!data.getString("GeplandeReisTijd").equals(data.getString("ActueleReisTijd"))){
                reistijdVertraging.setText(data.getString("ActueleReisTijd"));
                reistijd.setPaintFlags(reistijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }} catch (JSONException e) {
            e.printStackTrace();}
    }
    public void openAdapter(){
        JSONArray overstappen = null;
        try {
            JSONObject reisdeel = new JSONObject(data.getString("ReisDeel"));
            Log.d("reis12", reisdeel.toString());
            overstappen = (reisdeel.getJSONArray("ReisStop"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] overstapLijst = new String[overstappen.length()];
        RouteListAdapter adapter = new RouteListAdapter(this, overstapLijst, data);
        ListView list = findViewById(R.id.routeList);
        list.setAdapter(adapter);
        }

    public void dataToFirebase(){//Get the current karmapoints of the user.
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = authTest.getCurrentUser();
                send(user.getUid());}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("oops","Oops something went wrong: ",databaseError.toException());
            }
        });
    }

    public void send(String uid){
        //mDatabase.child("RouteActivity").setValue(score.getText().toString());
        mDatabase.child("Onlangs").child(uid).child(naam).child(getCodeVanNaar()).child("Ritnummer").setValue(ritNummer);
        mDatabase.child("Onlangs").child(uid).child(naam).child(getCodeVanNaar()).child("TijdDatum").setValue(setTijdDatum(tijd));
        mDatabase.child("Onlangs").child(uid).child(naam).child(getCodeVanNaar()).child("Van").setValue(getCode(van));
        mDatabase.child("Onlangs").child(uid).child(naam).child(getCodeVanNaar()).child("Naar").setValue(getCode(naar));
    }

    public String getCode(String geheel){
        return geheel.substring(geheel.indexOf("(") + 1, geheel.indexOf(")"));
    }

    public String setTijdDatum(String tijddatum){
        return tijddatum.substring(0,10) + "T" + tijddatum.substring(11,16);
    }

    public String getCodeVanNaar(){
        return getCode(van) + getCode(naar);
    }
}
