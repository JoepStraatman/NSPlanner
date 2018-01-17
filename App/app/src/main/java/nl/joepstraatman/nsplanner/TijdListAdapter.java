package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gebruiker on 17-1-2018.
 */

public class TijdListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] begin;
    private final String[] eind;
    private final String[] reisT;
    public TijdListAdapter(Activity context, String[] Begin, String[] Eind, String[] ReisT) {
        super(context, R.layout.list_layout,Begin);
        this.context=context;
        this.begin=Begin;
        this.eind=Eind;
        this.reisT=ReisT;
    }
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_layout, null,true);
        ImageView arrow = rowView.findViewById(R.id.arrow);
        arrow.setImageResource(R.drawable.arrow);
        TextView beginTijd = rowView.findViewById(R.id.begin);
        TextView eindTijd = rowView.findViewById(R.id.eind);
        TextView reisTijd = rowView.findViewById(R.id.reistijd);

        beginTijd.setText(begin[position]);
        eindTijd.setText(eind[position]);
        reisTijd.setText("Reistijd: " + reisT[position]);
        return rowView;

    };
}
