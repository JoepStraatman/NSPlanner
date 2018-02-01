package nl.joepstraatman.nsplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by joeps on 25-1-2018.
 */

public class RoutePlanAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private List<String> stationlijst, vertreklijst, spoorlijst, vertraginglijst;
    private List<Integer> hoofdspoorposition = new ArrayList<>();
    private TextView vertrekStation, vertrekTijd, vertrekSpoor, vertraging;

    public RoutePlanAdapter(Activity Context, List<String> Stationlijst, List<String> Vertreklijst,
                            List<String> Spoorlijst, List<String> Vertraginglijst){

        super(Context, R.layout.list_layout_route,Stationlijst);
        this.context=Context;
        this.stationlijst = Stationlijst;
        this.vertreklijst = Vertreklijst;
        this.spoorlijst = Spoorlijst;
        this.vertraginglijst = Vertraginglijst;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.list_layout_route, null,true);
        getTextviews(rowView, position);

        // Als het een begin of eindpunt is, laat het spoor zien.

        if (!spoorlijst.get(position).equals("")){
            vertrekSpoor.setText("Spoor: " + spoorlijst.get(position));
        } else {
            vertrekSpoor.setVisibility(View.INVISIBLE);
        }

        // Als het station een tussenstop is.

        if (!containsInt(hoofdspoorposition, position)){
            vertrekTijd.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
            vertrekStation.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            vertrekSpoor.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        }
        else{
            checkvertraging(position);
        }
        return rowView;
    }

    /**
     *  Initialiseer textviews.
     */

    private void getTextviews(View rowView, int position){

        vertrekStation = rowView.findViewById(R.id.vertrekStation);
        vertrekTijd = rowView.findViewById(R.id.vertrekTijd);
        vertrekSpoor = rowView.findViewById(R.id.vertrekSpoor);
        vertraging = rowView.findViewById(R.id.VertrekVertraging);
        vertrekStation.setText(stationlijst.get(position));
        vertrekTijd.setText(vertreklijst.get(position));
    }

    /**
     *  Check of er vertraging is in de route, zoja laat die zien.
     */

    private void checkvertraging(int position){

        if (!vertreklijst.get(position).equals(vertraginglijst.get(position))){
            vertraging.setText(vertraginglijst.get(position));
            vertrekTijd.setPaintFlags(vertrekTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    /**
     *  Check of een station een begin/eind station is.
     */

    public boolean containsInt(final List<Integer> array, final int key) {

        return array.contains(key);
    }

    /**
     *  Add een begin/eindstation posititie aan de lijst.
     */

    public void addStationPosition(int pos){

        hoofdspoorposition.add(pos);
    }

}
