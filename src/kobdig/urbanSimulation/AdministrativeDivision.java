package kobdig.urbanSimulation;

import org.postgis.PGgeometry;

import java.util.ArrayList;

/**
 * Created by Meili on 7/25/16.
 */
public class AdministrativeDivision {

    /**
     * The id
     */
    private String id;

    /**
     * The id
     */
    private int code;

    /**
     * The group of lands in this division
     */
    private ArrayList<Land> lands;

    /**
     * The geometry reference
     */
    private PGgeometry geom;

    /**
     * On sale properties
     */
    private double onSaleProperties;

    /**
     * Rented properties
     */
    private double rentedProperties;

    /**
     * The equipment constructor
     * @param id The id
     * @param geom The geom reference
     */
    public AdministrativeDivision(String id, int code, PGgeometry geom){
        this.id = id;
        this.code = code;
        this. geom = geom;
        this.lands = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public double getOnSaleProperties() {
        return onSaleProperties;
    }

    public void setOnSaleProperties(double onSaleProperties) {
        this.onSaleProperties = onSaleProperties;
    }

    public double getRentedProperties() {
        return rentedProperties;
    }

    public void setRentedProperties(double rentedProperties) {
        this.rentedProperties = rentedProperties;
    }

    public ArrayList<Land> getLands() {
        return lands;
    }

    public void addLand(Land land) {
        lands.add(land);
    }
}
