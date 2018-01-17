package nl.joepstraatman.nsplanner;

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

public class Tijd extends AppCompatActivity {
    private FirebaseAuth authTest;
    TextView station1;
    TextView station2;
    //test url:
    private String url = "http://webservices.ns.nl/ns-api-treinplanner?fromStation=Utrecht+Centraal&toStation=Amsterdam+centraal";
    public JSONArray ja_data = null;
    ArrayList<String> listdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tijd);
        authTest = FirebaseAuth.getInstance();
        laadDataIn();
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
        String naam = extras.getString("name");}
        String van = extras.getString("van");
        String naar = extras.getString("naar");
        station1 = findViewById(R.id.van);
        station2 = findViewById(R.id.naar);
        station1.setText(van.toString());
        station2.setText(naar.toString());
    }
    //requestque:
    public void openCategory() {//Create a new volley request to the api.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                saveToAdapter(response);
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
        listClick();
    }
    public void listClick() { //On listview item click go to a new activity and open the question.
        final ListView list = findViewById(R.id.tijden);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    JSONObject arrayPicked = new JSONObject(ja_data.getString(position));
                    Intent intent = new Intent(Tijd.this, Route.class);
                    intent.putExtra("item", arrayPicked.toString());
                    startActivity(intent);finish();
                }catch (JSONException e){throw new RuntimeException(e);}
            }
        });
    }
    public void saveToAdapter(String response){ //Set the questions to the listview adapter
        listdata = new ArrayList<>();  // load data from file
        Toast.makeText(Tijd.this, response,
                Toast.LENGTH_LONG).show();
        String newResponse = printResponse(response);
        try {
            JSONObject newjsonObj = new JSONObject(newResponse);
            String reisString = newjsonObj.getString("ReisMogelijkheden");
            JSONObject reisObj = new JSONObject(reisString);
            ja_data = reisObj.getJSONArray("ReisMogelijkheid"); // Get all the travel posibilities in a list
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }getQuestion();
        ArrayList<String> myArray = listdata;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Tijd.this, R.layout.list_layout, myArray);
        final ListView list = findViewById(R.id.tijden);
        list.setAdapter(adapter);
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
            for (int i = 0; i < jArray.length(); i++) {try {
                JSONObject jsonArray2 = new JSONObject(jArray.getString(i));
                listdata.add(jsonArray2.toString());
            } catch (JSONException e) {throw new RuntimeException(e);}
            }
        }
    }
}
