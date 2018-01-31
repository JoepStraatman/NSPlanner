package nl.joepstraatman.nsplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gebruiker on 19-1-2018.
 */

public class RouteListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final JSONObject data;
    private String vertrek, vertrekVertraging, aankomst, aankomstVertraging;
    private TextView vertrekTijd, vertrekTijdVertraging, station, vertrekSpoor;
    private JSONArray overstappen;
    private String[] stopTijd, Station, Spoor;

    RouteListAdapter(Activity Context, String[] Stops, JSONObject Data){

        super(Context, R.layout.list_layout_route, Stops);
        this.context = Context;
        this.data = Data;
        getData();
    }

    private void getData(){

        try {vertrek = data.getString("GeplandeVertrekTijd").substring(11,16);
            getVertragingen();
            JSONObject reisdeel = new JSONObject(data.getString("ReisDeel"));
            overstappen = (reisdeel.getJSONArray("ReisStop"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        setArrays();
        for (int i = 0; i < overstappen.length(); i++){
            try {
                Log.d("overstap",overstappen.getString(i));
                stopTijd[i] = overstappen.getJSONObject(i).getString("Tijd");
                Station[i] = overstappen.getJSONObject(i).getString("Naam");
                if (i == 0 || i == overstappen.length()-1){
                    Spoor[i] = overstappen.getJSONObject(i).getJSONObject("Spoor").getString("content");
                }
            } catch (JSONException e) {
                e.printStackTrace();}}
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.list_layout_route, null,true);
        vertrekTijd = rowView.findViewById(R.id.vertrekTijd);
        vertrekTijdVertraging = rowView.findViewById(R.id.VertrekVertraging);
        station = rowView.findViewById(R.id.vertrekStation);
        vertrekSpoor = rowView.findViewById(R.id.vertrekSpoor);
        SetTextViews(position);
        if (Spoor[position] != null){
            vertrekSpoor.setText("Spoor: " + Spoor[position]);
        }
        else {
            vertrekSpoor.setText("");
        }

        return rowView;
    }

    private void getVertragingen(){

        try {
            vertrekVertraging = data.getString("ActueleVertrekTijd").substring(11,16);
            aankomstVertraging = data.getString("ActueleAankomstTijd").substring(11,16);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SetTextViews(int position){

        if (position == 0){

            vertrekTijd.setText(vertrek);
            checkVertragingVertrek();

        } else if (position == overstappen.length()-1){
            aankomst = stopTijd[position].substring(11,16);
            vertrekTijd.setText(aankomst);
            checkVertragingAankomst();

        } else{
            vertrekTijd.setText(stopTijd[position].substring(11,16));
            vertrekTijd.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
            station.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            vertrekSpoor.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);

        } station.setText(Station[position]);
    }

    private void checkVertragingVertrek() {

        if (!vertrek.equals(vertrekVertraging)) {
            vertrekTijdVertraging.setText(vertrekVertraging);
            vertrekTijd.setPaintFlags(vertrekTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void checkVertragingAankomst(){

        if (!aankomst.equals(aankomstVertraging)) {
            vertrekTijdVertraging.setText(aankomstVertraging);
            vertrekTijd.setPaintFlags(vertrekTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void setArrays(){

        stopTijd = new String[overstappen.length()];
        Station = new String[overstappen.length()];
        Spoor = new String[overstappen.length()];
    }
}
