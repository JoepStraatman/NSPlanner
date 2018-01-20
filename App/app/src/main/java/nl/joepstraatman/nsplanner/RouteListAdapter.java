package nl.joepstraatman.nsplanner;

import android.app.Activity;
import android.graphics.Paint;
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
    JSONArray overstappen;
    String[] stopTijd;

    public RouteListAdapter(Activity Context, String[] Stops, JSONObject Data){
        super(Context, R.layout.list_layout_route, Stops);
        this.context=Context;
        this.stops=Stops;
        this.data=Data;
        getData();
    }

    public void getData(){
        try {
            vertrek = data.getString("GeplandeVertrekTijd").substring(11,16);
            vertrekVertraging = data.getString("ActueleVertrekTijd").substring(11,16);
            JSONObject reisdeel = new JSONObject(data.getString("ReisDeel"));
            overstappen = (reisdeel.getJSONArray("ReisStop"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < overstappen.length(); i++){

        }
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_layout_route, null,true);
        vertrekTijd = rowView.findViewById(R.id.vertrekTijd);
        vertrekTijdVertraging = rowView.findViewById(R.id.VertrekVertraging);
        checkVertraging();

        TextView station = rowView.findViewById(R.id.vertrekStation);
        TextView vertrekSpoor = rowView.findViewById(R.id.vertrekSpoor);
        if (position == 0){
            vertrekTijd.setText(vertrek);
        }else{
            vertrekTijd.setText(stopTijd[position]);
        }
        return rowView;
    }

    public void checkVertraging() {
        if (!vertrek.equals(vertrekVertraging)) {
            vertrekTijdVertraging.setText(vertrekVertraging);
            vertrekTijd.setPaintFlags(vertrekTijd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
