package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.XML;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tijd extends Activity {

    private String naam;
    private FirebaseAuth authTest;
    TextView station1;
    TextView station2;
    private String url;
    public JSONArray ja_data = null;
    public String[] vertrek;
    public String[] aankomst;
    public String[] reistijd;
    private String vanCode;
    private String naarCode;
    private String van;
    private String naar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tijd);
        authTest = FirebaseAuth.getInstance();
        laadDataIn();
        createUrl();
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
        startActivity(new Intent(this, Reis.class));finish();
        super.onBackPressed();
    }

    public void laadDataIn(){
        Bundle extras = getIntent().getExtras();
        if (extras.getString("van") != null){
        naam = extras.getString("name");}
        van = extras.getString("van");
        naar = extras.getString("naar");
        station1 = findViewById(R.id.van);
        station2 = findViewById(R.id.naar);
        station1.setText(van.toString());
        station2.setText(naar.toString());
    }

    public void createUrl(){
        vanCode = van.substring(van.indexOf("(") + 1, van.indexOf(")"));
        naarCode = naar.substring(naar.indexOf("(") + 1, naar.indexOf(")"));
        url = "http://webservices.ns.nl/ns-api-treinplanner?fromStation="+vanCode+"&toStation="+naarCode;
    }

    //requestque:
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
            String reisString = newjsonObj.getString("ReisMogelijkheden");
            JSONObject reisObj = new JSONObject(reisString);
            ja_data = reisObj.getJSONArray("ReisMogelijkheid"); // Get all the travel posibilities in a list
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }getQuestion();
    }

    public String printResponse(String response) {// parse the xml response to json
        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(response);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public void getQuestion(){//Get the questions from the response.
        JSONArray jArray = ja_data;
        if (jArray != null) {
            vertrek = new String[jArray.length()];
            aankomst = new String[jArray.length()];
            reistijd = new String[jArray.length()];
            for (int i = 0; i < jArray.length(); i++) {try {
                JSONObject jsonArray2 = new JSONObject(jArray.getString(i));
                vertrek[i] = (jsonArray2.getString("GeplandeVertrekTijd").substring(11,16));
                aankomst[i] = (jsonArray2.getString("GeplandeAankomstTijd").substring(11,16));
                reistijd[i] = (jsonArray2.getString("GeplandeReisTijd"));
            } catch (JSONException e) {throw new RuntimeException(e);}
            }
        }
        openAdapter();
    }

    public void openAdapter(){
        if (vertrek != null) {
            TijdListAdapter adapter = new TijdListAdapter(this, vertrek, aankomst, reistijd);
            ListView list = findViewById(R.id.tijden);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    String Slecteditem = vertrek[+position];
                    Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
