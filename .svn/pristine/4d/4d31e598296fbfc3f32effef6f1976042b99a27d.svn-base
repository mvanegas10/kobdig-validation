package kobdig.urbanSimulation;

import org.postgis.PGgeometry;

/**
 * Created by Meili on 7/26/16.
 */
public class TransportNetwork {

    // ATTRIBUTES

    /**
     * The id
     */
    private String id;

    /**
     * The type of equipment
     */
    private String level;

    /**
     * Division which it belongs
     */
    private AdministrativeDivision division;

    /**
     * The geometry reference
     */
    private PGgeometry geom;

    /**
     * The equipment constructor
     * @param id The id
     * @param level The level
     * @param geom The geom reference
     */
    public TransportNetwork(String id, String level, PGgeometry geom){
        this.id = id;
        this.level = level;
        this. geom = geom;
    }

    public String getId() {
        return id;
    }

    public AdministrativeDivision getDivision() {
        return division;
    }

    public void setDivision(AdministrativeDivision division) {
        this.division = division;
    }
}
