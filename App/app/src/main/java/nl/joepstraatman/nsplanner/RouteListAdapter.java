package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gebruiker on 19-1-2018.
 */

public class RouteListAdapter extends ArrayAdapter<String> {

    private final Activity context;

    public RouteListAdapter(Activity context){
        super(context, R.layout.list_layoutTijd);
        this.context=context;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_layoutRoute, null,true);
        return rowView;
    }
}
