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

/**
 * De activiteit waarin de tijden van de mogelijke reizen worden weergegeven.
 * Door Joep Straatman
 */

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
        doeVolleyRequest();
    }

    /**
     * Maak de logout button rechts boven in de titelbalk.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Zorgt ervoor dat de gebruiker wordt uitgelogd als er op de button geklikt wordt.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.mybutton) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  De logout functie die de status van de user veranderd, en door gaat naar de LoginActivity.
     */

    public void logout(){

        authTest.signOut();
        Log.d("Signout", "onAuthStateChanged:signed_out2");
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *  Als de terugknop in android zelf wordt ingedrukt, ga dan terug naar de ReisActivity.
     */

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ReisActivity.class));finish();
        super.onBackPressed();
    }

    /**
     *  Functie waar intent worden ingeladen en gebruikt om variabelen te veranderen.
     */

    public void laadDataIn(){

        Bundle extras = getIntent().getExtras();
        Intent extra = getIntent();

        if ( extras.getString("van") != null){
            naam = extras.getString("name");
        }
        soort = extras.getString("departure");
        van = extras.getString("van");
        naar = extras.getString("naar");

        TextView station1 = findViewById(R.id.van);
        TextView station2 = findViewById(R.id.naar);
        station1.setText(van.toString());
        station2.setText(naar.toString());

        tijd = extras.getString("tijd");

        if ( extra.hasExtra("overstap")) {
            overstap = extras.getBoolean("overstap");
        }
    }

    /**
     *  Maak een API URL uit de verschillende opgehaalde data.
     */

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

    /**
     *  Doe een volley GET request.
     */

    public void doeVolleyRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                jsonparser(response);
            }}, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);}
        }) {

            // Login op de API.

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                String creds = String.format("%s:%s","straatmanjoep@gmail.com","bnjf-LP20gR1WNnYeZ2RYyDvYItt-PtN2Go1ulSipoWfrY42SIuhHA");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;}
            };

        // Stuur de GET request naar de wachtlijst.

        queue.add(stringRequest);
    }

    /**
     *  Parse de json data en haal de benodigde dat eruit.
     */

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

    /**
     *  Haal de ritnummers uit de data.
     */

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

    /**
     *  Filter de binnengehaalde routes op het hebben van geen overstappen.
     *  Stop de routes zonder overstap in een nieuwe lijst.
     */

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

    /**
     *  Converteer de XML response van de API naar JSON.
     */

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

    /**
     *  Haal de benodigde route informatie uit de gefilterde routes.
     */

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

    /**
     *  Initialiseer de stringarays die nodig zijn voor de adapter.
     */

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

    /**
     *  Check of een route vertraging heeft en sla die op.
     */

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

    /**
     *  Open de adapter voor de listview.
     *  Geef alle benodigde route informatie mee aan de adapter.
     */

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

    /**
     *  Ga naar de RouteActivity.
     */

    public void goToRoute(int pos){

        Intent i = new Intent(this, RouteActivity.class);

        startActivity(putExtraIntent(i, pos));
        finish();
    }

    /**
     *  Geef data aan intent mee.
     */

    private Intent putExtraIntent(Intent i, int pos){

        i.putExtra("name", naam);
        i.putExtra("van", van);
        i.putExtra("naar", naar);
        i.putExtra("tijd", tijd);
        i.putExtra("data", arrayList[pos]);
        i.putExtra("ritnummer", ritNummer[pos]);
        if (statusReis[pos].equals("NIET-MOGELIJK") || statusReis[pos].equals("GEANNULEERD") ||
                statusReis[pos].equals("OVERSTAP-NIET-MOGELIJK")){
            i.putExtra("status", statusReis[pos]);
        }
        if (overstap != null) {
            i.putExtra("overstap", overstap);
        }
        return i;
    }
}
