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
     * The utility
     */
    private double utility;

    /**
     * The constructor of the class
     * @param property
     * @param utility
     */
    public PropertyUtility(Property property, double utility) {
        this.property = property;
        this.utility = utility;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }
}
