package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by Gebruiker on 23-1-2018.
 */

public class HomeListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] lijst;

    public HomeListAdapter(Activity context, String[] Lijst) {
        super(context, R.layout.list_layout_home,Lijst);
        this.context=context;
        this.lijst=Lijst;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_layout_home, null,true);
        return rowView;
    };
}
