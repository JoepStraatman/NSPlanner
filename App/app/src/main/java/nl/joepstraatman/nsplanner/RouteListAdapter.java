package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.graphics.Paint;
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
    private final String[] stops;
    String vertrek;
    String vertrekVertraging;
    String aankomst;
    String aankomstVertraging;
    TextView vertrekTijd;
    TextView vertrekTijdVertraging;
    TextView station;
    TextView vertrekSpoor;
    JSONArray overstappen;
    String[] stopTijd;
    String[] Station;
    String[] Spoor;

    public RouteListAdapter(Activity Context, String[] Stops, JSONObject Data){
        super(Context, R.layout.list_layout_route, Stops);
        this.context=Context;
        this.stops=Stops;
        this.data=Data;
        getData();
    }

    public void getData(){
        try {vertrek = data.getString("GeplandeVertrekTijd").substring(11,16);
            getVertragingen();
            JSONObject reisdeel = new JSONObject(data.getString("ReisDeel"));
            overstappen = (reisdeel.getJSONArray("ReisStop"));
        } catch (JSONException e) {e.printStackTrace();} setArrays();
        for (int i = 0; i < overstappen.length(); i++){
            try {
                Log.d("overstap",overstappen.getString(i));
                stopTijd[i] = overstappen.getJSONObject(i).getString("Tijd");
                Station[i] = overstappen.getJSONObject(i).getString("Naam");
                if (i == 0 || i == overstappen.length()-1){
                Spoor[i] = overstappen.getJSONObject(i).getJSONObject("Spoor").getString("content");}
            } catch (JSONException e) {
                e.printStackTrace();}}
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_layout_route, null,true);
        vertrekTijd = rowView.findViewById(R.id.vertrekTijd);
        vertrekTijdVertraging = rowView.findViewById(R.id.VertrekVertraging);
        station = rowView.findViewById(R.id.vertrekStation);
        vertrekSpoor = rowView.findViewById(R.id.vertrekSpoor);
        SetTextViews(position);
        if (Spoor[position] != null){
        vertrekSpoor.setText("Spoor: " + Spoor[position]);}else{vertrekSpoor.setText("");}
        return rowView;
    }

    public void getVertragingen(){
        try {
            vertrekVertraging = data.getString("ActueleVertrekTijd").substring(11,16);
            aankomstVertraging = data.getString("ActueleAankomstTijd").substring(11,16);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void SetTextViews(int position){
        if (position == 0){
            vertrekTijd.setText(vertrek);
            checkVertragingVertrek();
        }else if (position == overstappen.length()-1){
            aankomst = stopTijd[position].substring(11,16);
            vertrekTijd.setText(aankomst);
            checkVertragingAankomst();
        } else{
            vertrekTijd.setText(stopTijd[position].substring(11,16));
            vertrekTijd.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
            station.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
            vertrekSpoor.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
        }station.setText(Station[position]);
    }

    public void checkVertragingVertrek() {
        if (!vertrek.equals(vertrekVertraging)) {
            vertrekTijdVertraging.setText(vertrekVertraging);
            vertrekTijd.setPaintFlags(vertrekTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void checkVertragingAankomst(){
        if (!aankomst.equals(aankomstVertraging)) {
            vertrekTijdVertraging.setText(aankomstVertraging);
            vertrekTijd.setPaintFlags(vertrekTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void setArrays(){
        stopTijd = new String[overstappen.length()];
        Station = new String[overstappen.length()];
        Spoor = new String[overstappen.length()];
    }
}
