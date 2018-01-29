package nl.joepstraatman.nsplanner;

/**
 * Created by Gebruiker on 25-1-2018.
 */

public class RouteData {

    public String Ritnummer;
    public String TijdDatum;
    public String Van;
    public String Naar;


    public RouteData(){
    }

    public RouteData(String Ritnummer, String TijdDatum, String Van, String Naar){

        this.Ritnummer = Ritnummer;
        this.TijdDatum = TijdDatum;
        this.Van = Van;
        this.Naar = Naar;
    }
}
