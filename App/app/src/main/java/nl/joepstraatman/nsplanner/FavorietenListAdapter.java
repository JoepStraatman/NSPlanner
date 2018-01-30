package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Gebruiker on 23-1-2018.
 */

public class FavorietenListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> lijst;


    public FavorietenListAdapter(Activity context, List<String> Lijst) {

        super( context, R.layout.list_layout_favorieten,Lijst);
        this.context = context;
        this.lijst = Lijst;
    }

    public View getView( int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_layout_favorieten, null,true);

        TextView onlangs = rowView.findViewById(R.id.favorieten);
        onlangs.setText(lijst.get(position));
        return rowView;
    }

}
