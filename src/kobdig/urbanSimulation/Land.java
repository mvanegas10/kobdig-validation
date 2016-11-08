package kobdig.urbanSimulation;

import org.postgis.PGgeometry;

/**
 * Created by Meili on 7/19/16.
 */
public class Land {

    // ATTRIBUTES

    /**
     * ID
     */
    private String id;

    /**
     * Latitude reference
     */
    private double latitude;

    /**
     * Longitude reference
     */
    private double longitude;

    /**
     * Division to which it belongs
     */
    private AdministrativeDivision division;

    /**
     * The land's price
     */
    private double price;

    /**
     * The land's utility
     */
    private double utility;

    /**
     * The geometry reference
     */
    private PGgeometry geom;

    /**
     * True if the utility function is updated
     */
    private boolean updated;

    public Land(String id, double latitude, double longitude, double price, PGgeometry geom) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.division = null;
        this.utility = Double.NEGATIVE_INFINITY;
        this.geom = geom;
        this.updated = false;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public AdministrativeDivision getDivision() {
        return division;
    }

    public void setDivision(AdministrativeDivision division) {
        this.division = division;
    }

    public double getUtility() {
        return utility;
    }

    public void setUtility(Double utility) {
        this.utility = utility;
    }

    public PGgeometry getGeom() {
        return geom;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    // METHODS

    public void step(int time) {
        price = (price - Math.exp(time) < 0) ? 0.0 : price - Math.exp(time);
    }

}
