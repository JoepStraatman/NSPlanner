package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
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
    private final String[] vertrekV;
    private final String[] aankomstV;
    private final String[] reisV;
    public TijdListAdapter(Activity context, String[] Begin, String[] Eind, String[] ReisT, String[] VertrekV, String[] AankomstV, String[] ReisV) {
        super(context, R.layout.list_layout,Begin);
        this.context=context;
        this.begin=Begin;
        this.eind=Eind;
        this.reisT=ReisT;
        this.vertrekV=VertrekV;
        this.aankomstV=AankomstV;
        this.reisV=ReisV;
    }
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_layout, null,true);
        ImageView arrow = rowView.findViewById(R.id.arrow);
        arrow.setImageResource(R.drawable.arrow);
        TextView beginTijd = rowView.findViewById(R.id.begin);
        TextView eindTijd = rowView.findViewById(R.id.eind);
        TextView reisTijd = rowView.findViewById(R.id.reistijd);
        TextView beginVertragingTijd = rowView.findViewById(R.id.vertrekVertraging);
        TextView eindVertragingTijd = rowView.findViewById(R.id.aankomstVertraging);
        TextView reisVertragingTijd = rowView.findViewById(R.id.reistijdVertraging);

        beginTijd.setText(begin[position]);
        eindTijd.setText(eind[position]);
        reisTijd.setText("Reistijd: " + reisT[position]);
        if (vertrekV[position]!= null){
            beginVertragingTijd.setText(vertrekV[position]);
            beginTijd.setPaintFlags(beginTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }if (aankomstV[position]!= null){
            eindVertragingTijd.setText(aankomstV[position]);
            eindTijd.setPaintFlags(eindTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }if (reisV[position]!= null){
            reisVertragingTijd.setText(reisV[position]);
            reisTijd.setPaintFlags(reisTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return rowView;
    };
}
