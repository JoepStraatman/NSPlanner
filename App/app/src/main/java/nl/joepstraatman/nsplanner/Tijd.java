package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tijd extends AppCompatActivity {
    private FirebaseAuth authTest;
    TextView station1;
    TextView station2;
    //test url:
    private String url = "https://webservices.ns.nl/ns-api-avt?station=amsterdam";
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
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                saveToAdapter(response);
            }}, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);}
        });
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
    public void saveToAdapter(JSONObject response){ //Set the questions to the listview adapter
        listdata = new ArrayList<>();  // load data from file
        Toast.makeText(Tijd.this, response.toString(),
                Toast.LENGTH_LONG).show();
        /*try {
            JSONObject jsonObj = new JSONObject(response.toString());
            ja_data = jsonObj.getJSONArray("results"); // Get objects of request
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }*/getQuestion();
        ArrayList<String> myArray = listdata;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Tijd.this, R.layout.list_layout, myArray);
        final ListView list = findViewById(R.id.tijden);
        list.setAdapter(adapter);
    }
    public void getQuestion(){//Get the questions from the response.
        JSONArray jArray = ja_data;
        /*if (jArray != null) {
            for (int i = 0; i < jArray.length(); i++) {try {
                JSONObject jsonArray2 = new JSONObject(jArray.getString(i));
                listdata.add(jsonArray2.getString("question").replaceAll("&quot;", "'").replaceAll("&#039;", "'"));
            } catch (JSONException e) {throw new RuntimeException(e);}
            }
        }*/
    }
}
