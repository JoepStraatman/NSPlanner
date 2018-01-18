package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;
import java.util.Map;

public class Reis extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth authTest;
    EditText station1;
    EditText station2;
    EditText naam;
    public JSONArray ja_data = null;
    public String[] countryNameList;
    private String url = "http://webservices.ns.nl/ns-api-stations-v2?_ga=2.101005377.1354008381.1516190474-680113843.1477057522";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis);
        makeviews();
        authTest = FirebaseAuth.getInstance();
        Button zoek = findViewById(R.id.zoek);
        Button verwijder = findViewById(R.id.verwijder);
        zoek.setOnClickListener(this);
        verwijder.setOnClickListener(this);
        openCategory();
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
            if (naam.getText().toString().equals("") || station1.getText().toString().equals("") || station2.getText().toString().equals("")){
                Toast.makeText(this, "Er mist een veld!",Toast.LENGTH_SHORT).show();
            }else{
                Intent i = new Intent(this, Tijd.class);
                i.putExtra("name", naam.getText().toString());
                i.putExtra("van", station1.getText().toString());
                i.putExtra("naar", station2.getText().toString());
                startActivity(i);
                finish();}
        } else if (v.getId() == R.id.verwijder) {
            startActivity(new Intent(Reis.this, Home.class));finish();
        }
    }
    public void makeviews(){
        station1 = findViewById(R.id.station1);
        station2 = findViewById(R.id.station2);
        naam = findViewById(R.id.naam);
    }
    public void openCategory() {//Create a new volley request to the api.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonparser(response);
            }}, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);}
        })
        {@Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("Content-Type", "application/json");
            String creds = String.format("%s:%s","straatmanjoep@gmail.com","bnjf-LP20gR1WNnYeZ2RYyDvYItt-PtN2Go1ulSipoWfrY42SIuhHA");
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
            params.put("Authorization", auth);
            return params;}
        };
        queue.add(stringRequest);// Add the request to the RequestQueue.

    }

    public void jsonparser(String response){ //Set the questions to the listview adapter  // load data from file
        String newResponse = printResponse(response);
        try {
            JSONObject newjsonObj = new JSONObject(newResponse);
            String reisString = newjsonObj.getString("Stations");
            JSONObject reisObj = new JSONObject(reisString);
            ja_data = reisObj.getJSONArray("Station"); // Get all the travel posibilities in a list
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }getQuestion();
    }

    public String printResponse(String response) {// parse the xml response to json
        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(response);
            Log.e("response", jsonObj.toString());
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public void getQuestion(){//Get the questions from the response.
        JSONArray jArray = ja_data;
        if (jArray != null) {
            countryNameList = new String[jArray.length()];
            for (int i = 0; i < jArray.length(); i++) {try {
                JSONObject jsonArray2 = new JSONObject(jArray.getString(i));
                JSONObject naamObj = new JSONObject(jsonArray2.getString("Namen"));
                countryNameList[i] = (naamObj.getString("Lang") + " ("+ jsonArray2.getString("Code") + ")");
            } catch (JSONException e) {throw new RuntimeException(e);}
            }
        }
        setAutoAdapter();
    }

    public void setAutoAdapter(){
        if (countryNameList != null){
            AutoCompleteTextView auto1 = findViewById(R.id.station1);
            AutoCompleteTextView auto2 = findViewById(R.id.station2);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, countryNameList);
            //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, countryNameList);
            auto1.setAdapter(adapter);
            auto1.setThreshold(1);//start searching from 1 character
            auto1.setAdapter(adapter);
            auto2.setAdapter(adapter);
            auto2.setThreshold(1);//start searching from 1 character
            auto2.setAdapter(adapter);
        }
    }
}
