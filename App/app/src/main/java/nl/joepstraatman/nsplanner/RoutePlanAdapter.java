package nl.joepstraatman.nsplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private List<String> stationlijst;
    private static List<Integer> hoofdspoorposition = new ArrayList<Integer>();

    public RoutePlanAdapter(Activity Context,List<String> Stationlijst){
        super(Context, R.layout.list_layout_route,Stationlijst);
        this.context=Context;
        this.stationlijst = Stationlijst;
            }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.list_layout_route, null,true);
        TextView vertrekStation = rowView.findViewById(R.id.vertrekStation);
        vertrekStation.setText(stationlijst.get(position));
        if (containsInt(hoofdspoorposition, position)){
            vertrekStation.setTextColor(Color.parseColor("#C50FDA"));
        }
        return rowView;
    }

    public boolean containsInt(final List<Integer> array, final int key) {
        return array.contains(key);
    }

    public void addStationPosition(int pos){
        hoofdspoorposition.add(pos);
    }

    public int getStationMaxPosition(){
        return Collections.max(hoofdspoorposition);
    }
}
