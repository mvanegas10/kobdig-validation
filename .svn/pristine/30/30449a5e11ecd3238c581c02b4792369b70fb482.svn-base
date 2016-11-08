package kobdig.urbanSimulation;

import org.postgis.PGgeometry;

/**
 * Created by Meili on 7/25/16.
 */
public class Equipment {

    // CONSTANTS

    /**
     * Bienestar
     */
    public static final String BIENESTAR = "\"bienestar\"";

    /**
     * Culto
     */
    public static final String CULTO = "\"culto\"";

    /**
     * Cultura
     */
    public static final String CULTURA = "\"cultura\"";

    /**
     * CYSF
     */
    public static final String CYSF = "\"cysf\"";

    /**
     * Deportes
     */
    public static final String DEPORTES = "\"deportes\"";

    /**
     * Educacion
     */
    public static final String EDUCACION = "\"educacion\"";

    /**
     * Edusup
     */
    public static final String EDUSUP = "\"edusup\"";

    /**
     * Recintos feriales
     */
    public static final String RECINTOS_FERIALES = "\"recintos_feriales\"";

    /**
     * Sa
     */
    public static final String SA = "\"sa\"";

    /**
     * Salones comunales
     */
    public static final String SALONES_COMUNALES = "\"salones\"";

    /**
     * Salud
     */
    public static final String SALUD = "\"salud\"";

    /**
     * Seguridad
     */
    public static final String SEGURIDAD = "\"seguridad\"";

    // ATTRIBUTES

    /**
     * The id
     */
    private String id;

    /**
     * The type of equipment
     */
    private String type;

    /**
     * The division to which it belongs
     */
    private AdministrativeDivision division;

    /**
     * The geometry reference
     */
    private PGgeometry geom;

    /**
     * The equipment constructor
     * @param id The id
     * @param type The type
     * @param geom The geom reference
     */
    public Equipment(String id, String type, PGgeometry geom){
        this.id = id;
        this.type = type;
        this.division = null;
        this. geom = geom;
    }

    public AdministrativeDivision getDivision() {
        return division;
    }

    public void setDivision(AdministrativeDivision division) {
        this.division = division;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public PGgeometry getGeom() {
        return geom;
    }
}
