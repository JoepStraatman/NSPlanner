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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * De activiteit waar je altijd op begint, en waar je een nieuwe reis kan maken of een
 * recente/favoriete reis kan kiezen.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth authTest;
    private List<String> lijst = new ArrayList<>();
    private DatabaseReference mDatabase;
    private HomeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        authTest = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Button fav = findViewById(R.id.Favoriet);
        Button nieuw = findViewById(R.id.Nieuw);
        fav.setOnClickListener(this);
        nieuw.setOnClickListener(this);
        getRouteFromFirebase();
        openAdapter();
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
     *  De onclick listener voor de buttons.
     */

    public void onClick( View v ) {

        if ( v.getId() == R.id.Favoriet) {
            startActivity( new Intent(HomeActivity.this, FavorietenActivity.class));finish();
        } else if ( v.getId() == R.id.Nieuw) {
            startActivity( new Intent(HomeActivity.this, ReisActivity.class));finish();
        }
    }

    /**
     *  Open de adapter voor de listview.
     */

    public void openAdapter(){

        adapter = new HomeListAdapter(this, lijst);
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

    /**
     *  Ga naar de RouteActivity.
     */

    public void goToRoute(int pos){

        Intent i = new Intent(this, RoutePlanActivity.class);
        i.putExtra("name", lijst.get(pos));
        startActivity(i);
        finish();
    }

    /**
     *  Haal data uit firebase en zet het in de adapter.
     */

    public void getRouteFromFirebase(){

        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = authTest.getCurrentUser();
                DataSnapshot naamsnapshot = dataSnapshot.child("Onlangs/"+user.getUid());
                Iterable<DataSnapshot> naamChildren = naamsnapshot.getChildren();

                for (DataSnapshot naam : naamChildren) {
                    lijst.add(naam.getKey());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("canceledload", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

    }

}
