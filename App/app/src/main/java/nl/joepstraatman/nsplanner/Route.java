package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Route extends AppCompatActivity{

    private FirebaseAuth authTest;
    String naam;
    String van;
    String naar;
    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        authTest = FirebaseAuth.getInstance();
        laadDataIn();
        openAdapter();
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

    public void laadDataIn(){
        Bundle extras = getIntent().getExtras();
        naam = extras.getString("name");
        van = extras.getString("van");
        naar = extras.getString("naar");
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

}
