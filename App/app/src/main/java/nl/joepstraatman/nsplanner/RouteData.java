package nl.joepstraatman.nsplanner;

/**
 *  De data class waar data uit firebase makkelijk in kan worden opgehaald.
 *  Door Joep Straatman
 */

public class RouteData {

    public String Ritnummer, TijdDatum, Van, Naar;

    public RouteData(){
    }

    public RouteData(String Ritnummer, String TijdDatum, String Van, String Naar){

        this.Ritnummer = Ritnummer;
        this.TijdDatum = TijdDatum;
        this.Van = Van;
        this.Naar = Naar;
    }
}
