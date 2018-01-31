package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class TijdActivity extends AppCompatActivity {

    private String naam, van, naar, soort, tijd;
    private FirebaseAuth authTest;
    private String url;
    private Boolean overstap;
    private JSONArray ja_data, filterOverstap = null;

    private String[] vertrek, aankomst, reistijd, vertrekVertraging, aankomstVertraging,
            reistijdVertraging, ritNummer, statusReis, arrayList;

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

    //Go to the Main class. Called after login is complete.
    public void logout(){

        authTest.signOut();
        Log.d("Signout", "onAuthStateChanged:signed_out2");
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ReisActivity.class));finish();
        super.onBackPressed();
    }

    public void laadDataIn(){

        Bundle extras = getIntent().getExtras();
        Intent extra = getIntent();
        if ( extras.getString("van") != null){
            naam = extras.getString("name");}
        soort = extras.getString("departure");
        van = extras.getString("van");
        naar = extras.getString("naar");
        TextView station1 = findViewById(R.id.van);
        TextView station2 = findViewById(R.id.naar);
        station1.setText(van.toString());
        station2.setText(naar.toString());
        tijd = extras.getString("tijd");
        if ( extra.hasExtra("overstap")) {
            overstap = extras.getBoolean("overstap"); }
    }

    public void createUrl(){

        String vanCode = van.substring(van.indexOf("(") + 1, van.indexOf(")"));
        String naarCode = naar.substring(naar.indexOf("(") + 1, naar.indexOf(")"));
        Boolean departure;
        if (soort.equals("Vertrek: ")){
            departure = true;
        }
        else {
            departure = false;
        }
        url = "http://webservices.ns.nl/ns-api-treinplanner?fromStation=" + vanCode + "&toStation="
                + naarCode + "&dateTime=" + tijd + "&departure=" + departure;
    }

    //Create a new volley request to the api.
    public void openCategory() {

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
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void jsonparser(String response){

        String newResponse = printResponse(response);

        try {
            JSONObject newjsonObj = new JSONObject(newResponse);
            String reisString = newjsonObj.getString("ReisMogelijkheden");
            JSONObject reisObj = new JSONObject(reisString);
            ja_data = reisObj.getJSONArray("ReisMogelijkheid");
            filterOverstappen();
            getRitnummer();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }getData();
    }

    public void getRitnummer(){

        ritNummer = new String[filterOverstap.length()];
        for ( int i = 0; i < filterOverstap.length(); i++){
            try {
            JSONObject advies = new JSONObject(filterOverstap.getString(i));
            JSONObject reisdeel = new JSONObject(advies.getString("ReisDeel"));
                ritNummer[i] = reisdeel.getString("RitNummer");
            }
         catch (JSONException e) { throw new RuntimeException(e);}
        }
    }

    public void filterOverstappen(){

        filterOverstap = new JSONArray();

        for ( int i = 0; i < ja_data.length(); i++){ try {
            JSONObject advies = new JSONObject(ja_data.getString(i));
            if (advies.getString("AantalOverstappen").equals("0")){
                filterOverstap.put(ja_data.get(i));
            }
        }   catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        Log.d("filteroverstap", filterOverstap.toString());
    }

    // parse the xml response to json
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

    public void getData(){

        JSONArray jArray = filterOverstap;
        if (jArray != null) {
            setStringArrays();
            checkVertraging();
            for ( int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject jsonArray2 = new JSONObject(jArray.getString(i));
                    arrayList[i] = jsonArray2.toString();
                    vertrek[i] = (jsonArray2.getString("GeplandeVertrekTijd").substring(11,16));
                    aankomst[i] = (jsonArray2.getString("GeplandeAankomstTijd").substring(11,16));
                    reistijd[i] = (jsonArray2.getString("GeplandeReisTijd"));
                }
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }openAdapter();
    }

    public void setStringArrays(){

        vertrek = new String[filterOverstap.length()];
        aankomst = new String[filterOverstap.length()];
        reistijd = new String[filterOverstap.length()];
        vertrekVertraging = new String[filterOverstap.length()];
        aankomstVertraging = new String[filterOverstap.length()];
        reistijdVertraging = new String[filterOverstap.length()];
        statusReis = new String[filterOverstap.length()];
        arrayList = new String[filterOverstap.length()];
    }

    public void checkVertraging(){

        for (int i = 0; i < filterOverstap.length(); i++) {
            try {
                JSONObject jsonCheck = new JSONObject(filterOverstap.getString(i));
                if(!jsonCheck.getString("GeplandeVertrekTijd").substring(11,16)
                        .equals((jsonCheck.getString("ActueleVertrekTijd").substring(11,16)))){
                    vertrekVertraging[i] = (jsonCheck.getString("ActueleVertrekTijd").substring(11,16));
                }if(!jsonCheck.getString("GeplandeAankomstTijd").substring(11,16)
                        .equals((jsonCheck.getString("ActueleAankomstTijd").substring(11,16)))){
                    aankomstVertraging[i] = (jsonCheck.getString("ActueleAankomstTijd").substring(11,16));
                }if(!jsonCheck.getString("GeplandeReisTijd").equals((jsonCheck.getString("ActueleReisTijd")))){
                    reistijdVertraging[i] = (jsonCheck.getString("ActueleReisTijd"));
                }statusReis[i] = jsonCheck.getString("Status");
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void openAdapter(){

        if ( vertrek != null) {

            TijdListAdapter adapter = new TijdListAdapter(this, vertrek, aankomst, reistijd,
                    vertrekVertraging, aankomstVertraging, reistijdVertraging,statusReis);
            ListView list = findViewById(R.id.tijden);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    goToRoute(position);
                }
            });
        }
    }

    public void goToRoute(int pos){

        Intent i = new Intent(this, RouteActivity.class);
        i.putExtra("name", naam);
        i.putExtra("van", van);
        i.putExtra("naar", naar);
        i.putExtra("tijd", tijd);
        i.putExtra("data", arrayList[pos]);
        i.putExtra("ritnummer", ritNummer[pos]);
        if (statusReis[pos].equals("NIET-MOGELIJK") || statusReis[pos].equals("GEANNULEERD") || statusReis[pos].equals("OVERSTAP-NIET-MOGELIJK")){
            i.putExtra("status", statusReis[pos]);
        }
        if (overstap != null) {
            i.putExtra("overstap", overstap);
        }
        startActivity(i);
        finish();
    }
}
