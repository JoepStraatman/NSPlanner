package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth authTest;
    private String[] lijst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        authTest = FirebaseAuth.getInstance();
        Button fav = findViewById(R.id.Favoriet);
        Button nieuw = findViewById(R.id.Nieuw);
        fav.setOnClickListener(this);
        nieuw.setOnClickListener(this);
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
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.Favoriet) {
            startActivity(new Intent(HomeActivity.this, FavorietenActivity.class));finish();
        } else if (v.getId() == R.id.Nieuw) {
            startActivity(new Intent(HomeActivity.this, ReisActivity.class));finish();
        }
    }

    public void openAdapter(){
        lijst = new String[5];
        HomeListAdapter adapter = new HomeListAdapter(this, lijst);
        ListView list = findViewById(R.id.onlangs);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
            goToRoute(position);
                }
            });
        }

    public void goToRoute(int pos){
        //Intent i = new Intent(this, RouteActivity.class);
        //startActivity(i);
        //finish();
        Toast.makeText(this, "Wordt nog aangewerkt" ,Toast.LENGTH_SHORT).show();
    }
}
