package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class RoutePlanActivity extends AppCompatActivity {

    String naam;
    private FirebaseAuth authTest;
    private FirebaseAuth.AuthStateListener authListenerTest;
    private static final String Tag = "Firebase_test";
    private DatabaseReference mDatabase;
    private List<String> stationlijst;
    private RoutePlanAdapter adapter;
    private ArrayList<String> ritnummerList = new ArrayList<>();
    private ArrayList<String> tijddatumList = new ArrayList<>();
    private ArrayList<String> naarList = new ArrayList<>();
    private ArrayList<String> vanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        authTest = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        laadDataIn();
        getRouteFromFirebase();
        setList();
        openAdapter();
        addToList();
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
        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = authTest.getCurrentUser();
                DataSnapshot naamsnapshot = dataSnapshot.child("Onlangs/"+user.getUid()+"/"+naam);
                Iterable<DataSnapshot> naamChildren = naamsnapshot.getChildren();

                for (DataSnapshot naam : naamChildren) {

                    RouteData routedata = naam.getValue(RouteData.class);
                    saveFirebasedata(routedata);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("canceledload", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

    private void saveFirebasedata(RouteData routeData){

        ritnummerList.add(routeData.Ritnummer);
        tijddatumList.add(routeData.TijdDatum);
        naarList.add(routeData.Naar);
        vanList.add(routeData.Van);
    }

  /*  public void getRouteFromFirebase2(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = authTest.getCurrentUser();
                *//*RouteData routedata = dataSnapshot.child("Onlangs/"+user.getUid()+"/"+naam).getValue(RouteData.class);
                if (routedata != null){
                    Log.d("fireroute", routedata.Ritnummer+ " - " + routedata.TijdDatum + " - " + routedata.Naar + " - " + routedata.Van);
                }*//*
                DataSnapshot naamsnapshot = dataSnapshot.child("Onlangs/"+user.getUid()+"/"+naam);
                Iterable<DataSnapshot> naamChildren = naamsnapshot.getChildren();
                for (DataSnapshot naam : naamChildren) {
                    RouteData routedata = naam.getValue(RouteData.class);
                    Log.d("fireroute", routedata.Ritnummer+ " - " + routedata.TijdDatum + " - " + routedata.Naar + " - " + routedata.Van);
                    //contacts.add(c);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("oops","Oops something went wrong: ",databaseError.toException());
                //startActivity(new Intent(getApplicationContext(), Home_screen.class));finish();
            }
        });
    }*/
    public void openAdapter(){
        adapter = new RoutePlanAdapter(this,stationlijst);
        ListView list = findViewById(R.id.routeplanlist);
        list.setAdapter(adapter);
        adapter.addStationPosition(0);
        adapter.addStationPosition(3);
    }

    private void setList(){
        stationlijst = new ArrayList<String>();
        stationlijst.add("1");
        stationlijst.add("2");
        stationlijst.add("3");
        stationlijst.add("4");
    }
    private void addToList(){
        adapter.addStationPosition(adapter.getStationMaxPosition()+1);
        stationlijst.add("5 NIEUW");
        stationlijst.add("6 NIEUW");
        stationlijst.add("7 NIEUW");
        stationlijst.add("8 NIEUW");
        adapter.addStationPosition(stationlijst.size()-1);
        adapter.notifyDataSetChanged();
    }
}
