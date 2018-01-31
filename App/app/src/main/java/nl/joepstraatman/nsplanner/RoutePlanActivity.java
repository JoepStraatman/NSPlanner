package nl.joepstraatman.nsplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import org.json.XML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePlanActivity extends AppCompatActivity implements View.OnClickListener{

    private String naam;
    private FirebaseAuth authTest;
    private DatabaseReference mDatabase;
    private RoutePlanAdapter adapter;

    private List<String> stationlijst = new ArrayList<>();
    private List<String> vertreklijst = new ArrayList<>();
    private List<String> spoorlijst = new ArrayList<>();

    private ArrayList<String> ritnummerList = new ArrayList<>();
    private ArrayList<String> tijddatumList = new ArrayList<>();
    private ArrayList<String> naarList = new ArrayList<>();
    private ArrayList<String> vanList = new ArrayList<>();
    private JSONArray ja_data, filterOverstap, overstappen = null;
    private CheckBox favo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        authTest = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        laadDataIn();
        Button voegtoe = findViewById(R.id.voegToe);
        favo = findViewById(R.id.favorietcheck);
        voegtoe.setOnClickListener(this);
        favo.setOnClickListener(this);
        getRouteFromFirebase();
        openAdapter();
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, HomeActivity.class));
        finish();
        super.onBackPressed();
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
                Intent i = new Intent(this, ReisActivity.class);
                i.putExtra("name", naam);
                i.putExtra("overstap", true);
                startActivity(i);
                finish();
                break;
            case (R.id.favorietcheck):
                if (favo.isChecked()){
                    Toast.makeText(this, "Toegevoegd!", Toast.LENGTH_LONG).show();
                    saveToFavorieten();
                }
                else{
                    Toast.makeText(this, "Verwijderd!", Toast.LENGTH_LONG).show();
                    removeFromFavorieten();
                }
        }
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
                if (dataSnapshot.hasChild("Favorieten/"+user.getUid()+"/"+naam)) {
                    favo.setChecked(true);
                }
                for (DataSnapshot naam : naamChildren) {

                    RouteData routedata = naam.getValue(RouteData.class);
                    Log.d("fireroute", routedata.Ritnummer+ " - " + routedata.TijdDatum + " - " + routedata.Naar + " - " + routedata.Van);
                    saveFirebasedata(routedata);
                }
                getFromApiLoop();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("canceledload", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

    }

    private void saveFirebasedata(RouteData routeData){
        if (!ritnummerList.contains(routeData.Ritnummer)) {
            ritnummerList.add(routeData.Ritnummer);
            tijddatumList.add(routeData.TijdDatum);
            naarList.add(routeData.Naar);
            vanList.add(routeData.Van);
        }
    }

    public void openAdapter(){
        adapter = new RoutePlanAdapter(this,stationlijst, vertreklijst, spoorlijst);
        ListView list = findViewById(R.id.routeplanlist);
        list.setAdapter(adapter);
    }

    private void addToList(){
        adapter.notifyDataSetChanged();
    }

    private void getFromApiLoop(){
        for (int i = 0; i < ritnummerList.size(); i++){
            Log.d("ritnummertje",ritnummerList.get(i));
            doRequestQueue(i);
        }

    }

    private void doRequestQueue(final int i){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getUrl(i), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonparser(response, ritnummerList.get(i));
            }}, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);}
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                String creds = String.format("%s:%s","straatmanjoep@gmail.com","bnjf-LP20gR1WNnYeZ2RYyDvYItt-PtN2Go1ulSipoWfrY42SIuhHA");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;}
            };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private String getUrl(int i){
        return "http://webservices.ns.nl/ns-api-treinplanner?fromStation="+vanList.get(i)+"&toStation="+
                naarList.get(i)+"&dateTime="+tijddatumList.get(i);
    }

    public void jsonparser(String response, final String ritnummer){
        String newResponse = printResponse(response);
        try {
            JSONObject newjsonObj = new JSONObject(newResponse);
            String reisString = newjsonObj.getString("ReisMogelijkheden");
            JSONObject reisObj = new JSONObject(reisString);
            ja_data = reisObj.getJSONArray("ReisMogelijkheid");
            filterOverstappen();
            getRitnummer(ritnummer);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }
    }

    public String printResponse(String response) {
        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(response);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public void filterOverstappen(){

        filterOverstap = new JSONArray();

        for (int i = 0; i < ja_data.length(); i++){ try {
            JSONObject advies = new JSONObject(ja_data.getString(i));
            if (advies.getString("AantalOverstappen").equals("0")){
                filterOverstap.put(ja_data.get(i));
            }
        } catch (JSONException e) { throw new RuntimeException(e);}
        }
        Log.d("filteroverstapplan", filterOverstap.toString());
    }

    public void getRitnummer(String ritnummer){
        for (int i = 0; i < filterOverstap.length(); i++){
            try {
                JSONObject advies = new JSONObject(filterOverstap.getString(i));
                JSONObject reisdeel = new JSONObject(advies.getString("ReisDeel"));
                if (reisdeel.getString("RitNummer").equals(ritnummer)){
                    Log.d("ritnummergoed",filterOverstap.getString(i));
                    giveToAdapter(filterOverstap.getString(i));
                }
            }
            catch (JSONException e) {
                Log.e("JSON exception", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void giveToAdapter(String stringObj){

        try {
            JSONObject overstapObj = new JSONObject(stringObj);
            JSONObject reisdeel = new JSONObject(overstapObj.getString("ReisDeel"));
            overstappen = reisdeel.getJSONArray("ReisStop");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < overstappen.length(); i ++){
            getItemsFromObject(i);
        }
    }

    private void getItemsFromObject(int i){
        try {
            stationlijst.add(overstappen.getJSONObject(i).getString("Naam"));
            vertreklijst.add(overstappen.getJSONObject(i).getString("Tijd").substring(11,16));
            if (i == 0 || i == overstappen.length()-1){
                spoorlijst.add(overstappen.getJSONObject(i).getJSONObject("Spoor").getString("content"));
                adapter.addStationPosition(spoorlijst.size()-1);
            } else {
                spoorlijst.add("");
            }
            Log.d("statlijst", stationlijst.toString());
            addToList();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveToFavorieten(){

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = authTest.getCurrentUser();
                DataSnapshot onlangssnapshot = dataSnapshot.child("Onlangs/"+user.getUid()+"/"+naam);
                mDatabase.child("Favorieten/"+user.getUid()+"/"+naam).setValue(onlangssnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("oops","Oops something went wrong: ",databaseError.toException());
            }
        });
    }

    private void removeFromFavorieten(){

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = authTest.getCurrentUser();
                mDatabase.child("Favorieten/"+user.getUid()+"/"+naam).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("oops","Oops something went wrong: ",databaseError.toException());
            }
        });
    }
}
