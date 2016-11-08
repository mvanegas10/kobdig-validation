package kobdig.urbanSimulation;

/**
 * Created by Meili on 11/7/16.
 */
public class PropertyUtility {

    /**
     * The property
     */
    private Property property;

    /**
     * The household
     */
    private Household household;

    /**
     * The closeness utility
     */
    private Double closeness;

    /**
     * The transport utility
     */
    private Double transport;

    /**
     * The equipments utility
     */
    private Double equipments;

    /**
     * True if the utility is updated
     */
    private boolean updated;

    /**
     * The utility
     */
    private Double utility;

    /**
     * The constructor of the class
     *
     * @param property
     * @param household
     */
    public PropertyUtility(Property property, Household household) {
        this.property = property;
        this.household = household;
        this.updated = false;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public double getCloseness() {
        return closeness;
    }

    public void setCloseness(double closeness) {
        this.closeness = closeness;
    }

    public double getTransport() {
        return transport;
    }

    public void setTransport(double transport) {
        this.transport = transport;
    }

    public double getEquipments() {
        return equipments;
    }

    public void setEquipments(double equipments) {
        this.equipments = equipments;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public Double getUtility() {
        return utility;
    }

    public void calculateUtility(String type) {
        if(type.equals(Simulation.INVEST)) utility = 0.3 * equipments + 0.3 * transport + 0.4 * closeness;
        else utility = 0.4 * equipments + 0.6 * transport;
    }
}
