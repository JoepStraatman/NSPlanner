package nl.joepstraatman.nsplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * De activiteit waar de reis of overstap gepland kan worden.
 * Door Joep Straatman
 */

public class ReisActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private FirebaseAuth authTest;
    private EditText station1, station2, naam;
    public JSONArray ja_data = null;
    public String[] countryNameList;
    private String name;
    private Boolean overstap;
    private Button soort, tijd, datum;
    private String url = "http://webservices.ns.nl/ns-api-stations-v2?_ga=2.101005377.1354008381.1516190474-680113843.1477057522";
    private int day, month, year, hour, minute;
    private int dayfinal, monthfinal, yearfinal, hourfinal, minutefinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis);
        laadDataIn();
        makeviews();
        authTest = FirebaseAuth.getInstance();

        Button zoek = findViewById(R.id.zoek);
        tijd = findViewById(R.id.tijd);
        datum = findViewById(R.id.datum);
        soort = findViewById(R.id.soort);

        tijd.setOnClickListener(this);
        datum.setOnClickListener(this);
        zoek.setOnClickListener(this);
        soort.setOnClickListener(this);

        getCurrentTime();
        getCurrentDate();
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
     *  Als de terugknop in android zelf wordt ingedrukt, ga dan terug naar de HomeActivity.
     */

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }

    /**
     *  De onclick listener van de buttons.
     */

    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.zoek):
                caseZoek();
                break;

            case (R.id.soort):
                caseSoort();
                break;

            case (R.id.datum):
                caseDatum();
                break;

            case (R.id.tijd):
                caseTijd();
                break;
        }
    }

    /**
     *  Zoek button event.
     */

    private void caseZoek(){

        if (naam.getText().toString().equals("") || station1.getText().toString().equals("")
                || station2.getText().toString().equals("")) {
            Toast.makeText(this, "Er mist een veld!", Toast.LENGTH_SHORT).show();
        } else if (!Arrays.asList(countryNameList).contains(station1.getText().toString()) ||
                !Arrays.asList(countryNameList).contains(station2.getText().toString())){
            Toast.makeText(this, "Dit is geen geldig station", Toast.LENGTH_SHORT).show();
        }

        else {
            goToTijd();
        }
    }

    /**
     *  Vertrek/aankomst button event.
     */

    private void caseSoort(){

        if (soort.getText().equals("Vertrek: ")){
            soort.setText("Aankomst:");
        }
        else {
            soort.setText("Vertrek: ");
        }
    }

    /**
     *  Datum button event.
     */

    private void caseDatum(){

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateD = new DatePickerDialog(this, this, year, month, day);
        dateD.show();
    }

    /**
     *  Tijd button event.
     */

    private void caseTijd(){

        Calendar t = Calendar.getInstance();
        hour = t.get(Calendar.HOUR_OF_DAY);
        minute = t.get(Calendar.MINUTE);

        TimePickerDialog timeD = new TimePickerDialog(this, this, hour, minute, true);
        timeD.show();
    }

    /**
     *  Functie waar intent worden ingeladen en gebruikt om bijvoorbeeld de overstap te checken.
     */

    public void laadDataIn(){

        Intent extra = getIntent();
        Bundle extras = getIntent().getExtras();
        naam = findViewById(R.id.naam);
        if (extra.hasExtra("name")) {
            name = extras.getString("name");
            naam.setText(name.toString());
        }
        if (extra.hasExtra("overstap")) {
            overstap = extras.getBoolean("overstap");
            naam.setVisibility(View.INVISIBLE);
            TextView textNaam = findViewById(R.id.textnaam);
            textNaam.setVisibility(View.INVISIBLE);
        }
    }

    /**
     *  Ga naar de TijdActivity en stuur de intents mee.
     */

    public void goToTijd(){

        Intent i = new Intent(this, TijdActivity.class);
        i.putExtra("name", naam.getText().toString());
        i.putExtra("van", station1.getText().toString());
        i.putExtra("naar", station2.getText().toString());
        i.putExtra("tijd",sendTimeDate());
        i.putExtra("departure", soort.getText());
        if (overstap != null) {
            i.putExtra("overstap", overstap);
        }
        startActivity(i);
        finish();
    }

    /**
     *  Haal de huidige tijd op uit het androidsysteem.
     *  Zet de tijdbutton naar die tijd.
     */

    public void getCurrentTime(){

        Calendar c = Calendar.getInstance();
        hourfinal = c.get(Calendar.HOUR_OF_DAY);
        minutefinal = c.get(Calendar.MINUTE);
        if (minutefinal < 10) {
            tijd.setText(hourfinal + ":0" + minutefinal);
        }
        else{
            tijd.setText(hourfinal + ":" + minutefinal);
        }
    }

    /**
     *  Haal de huidige datum op uit het androidsysteem.
     *  Zet de datumbutton naar die datum.
     */

    public void getCurrentDate(){

        Calendar c = Calendar.getInstance();
        yearfinal = c.get(Calendar.YEAR);
        monthfinal = (c.get(Calendar.MONTH)+1);
        dayfinal = c.get(Calendar.DAY_OF_MONTH);
        datum.setText(dayfinal + "-" + monthfinal + "-" + yearfinal);
    }

    /**
     *  Creeer datum/tijd code zoals die in de NS API gebruikt moet worden.
     */

    public String sendTimeDate(){
        String dat = yearfinal + "-" + monthfinal + "-" + dayfinal;
        String tijd = hourfinal + ":" + minutefinal;
        return dat + "T" + tijd;
    }

    /**
     *  Set views voor begin en eindstation.
     */

    public void makeviews(){

        station1 = findViewById(R.id.station1);
        station2 = findViewById(R.id.station2);
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
        }){

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
            String reisString = newjsonObj.getString("Stations");
            JSONObject reisObj = new JSONObject(reisString);
            ja_data = reisObj.getJSONArray("Station");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }getData();
    }

    /**
     *  Converteer de XML response van de API naar JSON.
     */

    public String printResponse(String response) {

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

    /**
     *  Haal alle stationsnamen uit de response en zet ze in een lijst.
     */

    public void getData(){

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

    /**
     *  Open de adapter die automatisch de 2 editteksten aanvult met stationsnamen.
     */

    public void setAutoAdapter(){

        if (countryNameList != null){
            AutoCompleteTextView auto1 = findViewById(R.id.station1);
            AutoCompleteTextView auto2 = findViewById(R.id.station2);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, countryNameList);
            auto1.setAdapter(adapter);
            auto1.setThreshold(1);
            auto1.setAdapter(adapter);
            auto2.setAdapter(adapter);
            auto2.setThreshold(1);
            auto2.setAdapter(adapter);
        }
    }

    /**
     *  Als de datum popup wordt geopend, sla het op in variabelen.
     *  Zet de buttontext naar de gekozen datum.
     */

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearfinal = i;
        monthfinal = i1 +1;
        dayfinal = i2;
        datum.setText(dayfinal + "-" + monthfinal + "-" + yearfinal);
    }

    /**
     *  Als de tijd popup wordt geopend, sla het op in variabelen.
     *  Zet de buttontext naar de gekozen tijd.
     */

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourfinal = i;
        minutefinal = i1;
        if (minutefinal < 10) {
            tijd.setText(hourfinal + ":0" + minutefinal);
        }
        else{
            tijd.setText(hourfinal + ":" + minutefinal);
        }
    }
}
