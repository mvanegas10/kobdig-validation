package kobdig.urbanSimulation;

import org.postgis.PGgeometry;

/**
 * Created by Meili on 21/06/2016.
 */
public class Property {

    // CONSTANTS

    /**
     * Rented
     */
    public static final String RENTED = "Rented";

    /**
     * Seeking tenant
     */
    public static final String SEEKING_TENANT = "Seeking tenant";

    /**
     * Occupied
     */
    public static final String OCCUPIED = "Occupied";

    /**
     * For sale
     */
    public static final String FOR_SALE = "For sale";

    /**
     * Probability at any particular time step that an owner-occupier household
     * will choose to move out
     */
    public final static double ABANDONMENT_FACTOR = 0.0125;

    /**
     * Probability that a tenant household will move out in a particular time step
     */
    public final static double TENANT_MOBILITY = 0.0561;

    /**
     * Loss in value applied to the physical condition variable of every location every time ste
     */
    public final static double DEPRECIATION_RATE = 0.0028;

    // ATTRIBUTES

    /**
     * Unique ID for the property
     */
    private String id;

    /**
     * Current property's value
     */
    private double latitude;

    /**
     * Current property's value
     */
    private double longitude;

    /**
     * Division which it belongs
     */
    private AdministrativeDivision division;

    /**
     * Indicates if it is for sale, not for sale, seeking tenants or rented
     */
    private String state;

    /**
     * Current property's price
     */
    private double currentPrice;

    /**
     * Current capitalized ground rent
     */
    private double currentCapitalizedRent;

    /**
     * Current potential ground rent
     */
    private double currentPotentialRent;

    /**
     * Current property's value
     */
    private double currentValue;

    /**
     * Current property's price
     */
    private double previousPrice;

    /**
     * Current capitalized ground rent
     */
    private double previousCapitalizedRent;

    /**
     * Current potential ground rent
     */
    private double previousPotentialRent;

    /**
     * Current property's value
     */
    private double previousValue;

    /**
     * Utility
     */
    private double utility;

    /**
     * The land where it was constructed
     */
    private Land land;

    /**
     * The geometry value
     */
    private PGgeometry geom;

    /**
     * True if the utility function is updated
     */
    private boolean updated;

    // CONSTRUCTOR

    /**
     * Property's constructor
     * @param id The property's id
     * @param price The property's price
     * @param rent The property's rent
     * @param value The property's value
     */
    public Property(String id, double latitude, double longitude, double price, double rent,
                    double value, PGgeometry geom){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentPrice = price;
        this.currentCapitalizedRent = rent;
        this.currentPotentialRent = rent;
        this.currentValue = value;
        this.previousPrice = price;
        this.previousCapitalizedRent = rent;
        this.previousPotentialRent = rent;
        this.previousValue = value;
        this.utility = Double.NEGATIVE_INFINITY;
        this.geom = geom;
        this.updated = false;
    }

    // GETTERS AND SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AdministrativeDivision getDivision() {
        return division;
    }

    public void setDivision(AdministrativeDivision division) {
        this.division = division;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getCurrentCapitalizedRent() {
        return currentCapitalizedRent;
    }

    public double getCurrentPotentialRent() {
        return currentPotentialRent;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }

    public Land getLand() {
        return land;
    }

    public void setLand(Land land) {
        this.land = land;
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

    /**
     * Generates a step in the simulation
     * @param time The time in the simulation
     */
    public void step(int time){

        previousPrice = currentPrice;
        previousCapitalizedRent = currentCapitalizedRent;
        previousPotentialRent = currentPotentialRent;
        previousValue = currentValue;

        currentPrice = (previousPrice - Math.exp(time) < 0)? 0.0: previousPrice - Math.exp(time);
        currentPotentialRent = (previousPotentialRent + Math.log(time + 1) < 0)? 0.0: previousPotentialRent +
                Math.log(time + 1);
        currentCapitalizedRent = (previousCapitalizedRent - Math.log(time + 1) <= 0.1)? 0.1: previousCapitalizedRent -
                Math.log(time + 1);
        currentValue = (previousValue - Math.exp(time) < 0)? 0.0: previousValue - Math.exp(time);

    }

    public String toString(){
        return id;
    }

}
