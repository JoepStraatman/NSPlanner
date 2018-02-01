package nl.joepstraatman.nsplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
 * De activiteit waar de favorieten worden opgehaald en worden laten zien.
 * Door Joep Straatman
 */

public class FavorietenActivity extends AppCompatActivity {

    private FirebaseAuth authTest;
    private List<String> lijst = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FavorietenListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorieten);
        authTest = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
     *  Als de terugknop in android zelf wordt ingedrukt.
     */

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, HomeActivity.class));finish();
        super.onBackPressed();
    }

    /**
     *  Open de adapter voor de listview.
     */

    public void openAdapter(){

        adapter = new FavorietenListAdapter(this, lijst);
        ListView list = findViewById(R.id.favoriet);
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
                DataSnapshot naamsnapshot = dataSnapshot.child("Favorieten/"+user.getUid());
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
