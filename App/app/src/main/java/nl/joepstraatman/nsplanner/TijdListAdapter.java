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
 * De adapter voor de TijdActivity om de verschillende reizentijden weer te geven.
 */

public class TijdListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] begin, eind, reisT, vertrekV, aankomstV, reisV, statusR;

    private TextView beginTijd, eindTijd, reisTijd, beginVertragingTijd, eindVertragingTijd, reisVertragingTijd;
    private ImageView statusView;


    public TijdListAdapter(Activity context, String[] Begin, String[] Eind, String[] ReisT,
                           String[] VertrekV, String[] AankomstV, String[] ReisV, String[] StatusR) {

        super(context, R.layout.list_layout_tijd,Begin);
        this.context=context;
        this.begin=Begin;
        this.eind=Eind;
        this.reisT=ReisT;
        this.vertrekV=VertrekV;
        this.aankomstV=AankomstV;
        this.reisV=ReisV;
        this.statusR=StatusR;
    }

    public View getView(int position,View view,ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_layout_tijd, null,true);
        ImageView arrow = rowView.findViewById(R.id.arrow);
        arrow.setImageResource(R.drawable.arrow);
        findViews(rowView);
        beginTijd.setText(begin[position]);
        eindTijd.setText(eind[position]);
        reisTijd.setText("Reistijd: " + reisT[position]);

        // Check op vertragingen, zoja laat die zien en streep de geplande tijd door. ( *3 )

        if (vertrekV[position]!= null){
            beginVertragingTijd.setText(vertrekV[position]);
            beginTijd.setPaintFlags(beginTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }if (aankomstV[position]!= null){
            eindVertragingTijd.setText(aankomstV[position]);
            eindTijd.setPaintFlags(eindTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }if (reisV[position]!= null){
            reisVertragingTijd.setText(reisV[position]);
            reisTijd.setPaintFlags(reisTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Check of de route wel mogelijk is. Zo niet Weergeef waarom niet.

        }if (statusR[position].equals("NIET-MOGELIJK") || statusR[position].equals("GEANNULEERD") ||
                statusR[position].equals("OVERSTAP-NIET-MOGELIJK")){
            statusView.setVisibility(View.VISIBLE);
        }
        return rowView;
    }

    /**
     *  Initialiseer alle views.
     */

    private void findViews(View rowView){

        beginTijd = rowView.findViewById(R.id.begin);
        eindTijd = rowView.findViewById(R.id.eind);
        reisTijd = rowView.findViewById(R.id.reistijd);
        beginVertragingTijd = rowView.findViewById(R.id.vertrekVertraging);
        eindVertragingTijd = rowView.findViewById(R.id.aankomstVertraging);
        reisVertragingTijd = rowView.findViewById(R.id.reistijdVertraging);
        statusView = rowView.findViewById(R.id.uitval);
    }
}
