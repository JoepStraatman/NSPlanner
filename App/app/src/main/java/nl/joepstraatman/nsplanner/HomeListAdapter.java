package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 *  De adapter voor de HomeActivity om de verschillende recente reizen weer te geven.
 *  Door Joep Straatman
 */

public class HomeListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> lijst;


    public HomeListAdapter(Activity context, List<String> Lijst) {

        super( context, R.layout.list_layout_home,Lijst);
        this.context = context;
        this.lijst = Lijst;
    }

    public View getView( int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_layout_home, null,true);
        TextView onlangs = rowView.findViewById(R.id.onlangstext);
        onlangs.setText(lijst.get(position));
        return rowView;
    }
}
