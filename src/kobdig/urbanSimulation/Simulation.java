package kobdig.urbanSimulation;

import kobdig.agent.Agent;
import kobdig.agent.Fact;
import kobdig.agent.PossibilisticFactBase;
import org.postgis.PGgeometry;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Meili on 20/06/16.
 */
public class Simulation {

    // CONSTANTS

    /**
     * Temporally income gap
     */
    public static final double INCOME_GAP = 0.3;

    /**
     * Temporally income gap
     */
    public static final String NETWORK = "network";

    /**
     * Invest
     */
    public static final String INVEST = "invest";

    /**
     * Buy or rent
     */
    public static final String BUY_OR_RENT = "buy_or_rent";

    /**
     * Temporally income gap
     */
    public static final String EQUIPMENT = "equipment";

    // VARIABLES

    /**
     * The household agents involved in the simulation
     */
    protected static ArrayList<Household> households;

    /**
     * The investors agents involved in the simulation
     */
    protected static ArrayList<Investor> investors;

    /**
     * The promoters agents involved in the simulation
     */
    protected static ArrayList<Promoter> promoters;

    /**
     * The properties involved in the simulation
     */
    protected static Map<AdministrativeDivision,ArrayList<Property>> properties;

    /**
     * The properties involved in the simulation
     */
    protected static ArrayList<Property> freeProperties;

    /**
     * The properties involved in the simulation
     */
    protected static ArrayList<Property> forRentProperties;

    /**
     * The for sale land
     */
    protected static ArrayList<Land> forSaleLand;

    /**
     * Equipments
     */
    protected static Map<AdministrativeDivision,ArrayList<Equipment>> equipments;

    /**
     * TransportNetwork
     */
    protected static Map<AdministrativeDivision,ArrayList<TransportNetwork>> primaryNetwork;

    /**
     * The land
     */
    protected static Map<AdministrativeDivision,ArrayList<Land>> lands;

    /**
     * The divisions
     */
    protected static AdministrativeDivision[] divisions;

    /**
     * The household's agent
     */
    protected static Agent householdAgent;

    /**
     * Equipments length
     */
    protected static int equipmentsLength;

    /**
     * Network length
     */
    protected static int networkLength;

    /**
     * The investor's agent
     */
    protected static Agent investorAgent;

    /**
     * The promoter's agent
     */
    protected static Agent promoterAgent;

    /**
     * The time reference in the simulation
     */
    protected static int time;

    /**
     * The number of simulations to generate
     */
    protected static int numSim;

    /**
     * The ID manager
     */
    protected static int[] idManager;

    /**
     * Connection
     */
    protected static Connection conn;

    /**
     * Filtered equipments
     */
    protected static String filteredEquipments;

    /**
     * Filtered network
     */
    protected static String filteredNetwork;

    /**
     * Generates a household step in the simulation for an specific household
     * @param household The household
     * @param time The time in the simulation
     */
    public static void householdUpdateBeliefs(Household household, int time) {

        household.step(time);
        Property cheapestProperty = null;
        double cheapestPrice = Double.POSITIVE_INFINITY;

        // Updates de affordBuying and affordRenting beliefs and gets the cheapest property
        int purchFound = 0;
        int rentFound = 0;

        for (Property property : freeProperties) {
            if (property.getCurrentPrice() < cheapestPrice) {
                cheapestPrice = property.getCurrentPrice();
                cheapestProperty = property;
            }

            if (household.getCurrentPurchasingPower() >= property.getCurrentPrice()) {
                PropertyUtility prop = new PropertyUtility(property,household);
                household.addPurchasableProperty(prop);
                purchFound++;
            }

        }

        for (Property property : forRentProperties) {
            if(household.getCurrentNetMonthlyIncome() >= property.getCurrentCapitalizedRent()){
                PropertyUtility prop = new PropertyUtility(property,household);
                household.addRentableProperty(prop);
                rentFound++;
            }
        }
        if(purchFound > 0){
            household.updateBelief("ab:" + Double.toString(purchFound/(0.0 + freeProperties.size())));
        }
        else{
            household.updateBelief("not ab:1");
        }
        if(rentFound > 0){
            household.updateBelief("ar:" + Double.toString(rentFound/(0.0 + forRentProperties.size())));
        }
        else{
            household.updateBelief("not ar:1");
        }

        // Updates the buyingRentable belief
        // TODO: Improve this approach
        if(cheapestPrice < Double.POSITIVE_INFINITY){
            if(household.getCurrentPurchasingPower() > cheapestPrice){
                double rnd = Math.random();
                if (rnd < 0.5){
                    household.updateBelief("br:" + cheapestProperty.getCurrentCapitalizedRent()/(0.0 +
                            cheapestProperty.getCurrentPotentialRent()));
                }
                else household.updateBelief("not br:1");
            }
        }
    }

    /**
     * Generates a household intention step in the simulation
     * @param household The household
     */
    public static void householdIntentionStep(Connection conn, Household household) {

        Iterator<Fact> iter = household.getAgent().goals().factIterator();
        while(iter.hasNext()) {
            String goal = iter.next().formula().toString();
            // If the goal is to buy
            if (goal.contains(Household.BUY) && goal.contains(Household.OWNER)){
                PropertyUtility taken = buyProperty(household);
                if (taken != null) {
                    freeProperties.remove(taken.getProperty());
                    taken.getProperty().setState(Property.OCCUPIED);
                    household.setOwnerOccupied(true);
                }
            }

            // If the goal is to invest
            else if (goal.contains(Household.BUY) && !goal.contains(Household.NOT_LANDLORD) && goal.contains(Household.LANDLORD)){
                PropertyUtility taken = invest(household);
                if (taken != null) {
                    taken.getProperty().setState(Property.SEEKING_TENANT);
                    freeProperties.remove(taken.getProperty());
                    forRentProperties.add(taken.getProperty());
                    taken.setUpdated(false);
                    Investor newInvestor = new Investor(investorAgent, household,taken.getProperty());
                    investors.add(newInvestor);
                    household.setProperty(null);
                }
            }

            // If the goal is to rent
            if (goal.contains(Household.RENT)){
                PropertyUtility taken = rentProperty(household);
                if (taken != null) {
                    forRentProperties.remove(taken.getProperty());
                    taken.getProperty().setState(Property.RENTED);
                    household.setProperty(null);
                    household.setRenting(true);
                }
            }

            // If the goal is to change and either sell or invest
            if (goal.contains(Household.CHANGE)){

                PropertyUtility taken = buyProperty(household);
                if (taken != null) {
                    freeProperties.remove(taken.getProperty());
                    taken.getProperty().setState(Property.OCCUPIED);
                }

                if (goal.contains(Household.LANDLORD)){
                    household.invest(household.getProperty());
                    freeProperties.remove(household.getProperty());
                    forRentProperties.add(household.getProperty());
                    Investor newInvestor = new Investor(investorAgent, household,household.getProperty());
                    newInvestor.getProperty().setState(Property.SEEKING_TENANT);
                    investors.add(newInvestor);
                    household.setProperty(null);
                }
                else if (goal.contains(Household.SELL)){
                    //TODO: Implement the seller part of the property
                    if (household.getProperty() != null) {
                        freeProperties.add(household.getProperty());
                        household.getProperty().setState(Property.FOR_SALE);
                        household.setProperty(null);
                    }
                }

            }

        }
    }

    /**
     * Generates a investor step in the simulation for an specific investor
     * @param investor The investor
     * @param time The time in the simulation
     */
    public static void investorUpdateBeliefs(Investor investor, int time) {

        investor.step(time);
        Property cheapestProperty = null;
        double cheapestPrice = Double.POSITIVE_INFINITY;

        // Updates de affordBuying and affordRenting beliefs and gets the cheapest property
        int purchFound = 0;

        for (Property property : freeProperties) {
            if (property.getCurrentPrice() < cheapestPrice) {
                cheapestPrice = property.getCurrentPrice();
                cheapestProperty = property;
            }

            if (investor.getCurrentPurchasingPower() >= property.getCurrentPrice()) {
                PropertyUtility prop = new PropertyUtility(property,null);
                investor.addPurchasableProperty(prop);
                purchFound++;
            }
        }

        if(purchFound > 0) investor.updateBelief("ab:" + Double.toString(purchFound/(0.0 + freeProperties.size())));

        else investor.updateBelief("not ab:1");

        // Updates the buyingRentable belief
        // TODO: Improve this approach
        if(cheapestPrice < Double.POSITIVE_INFINITY){
            if(investor.getCurrentPurchasingPower() > cheapestPrice){
                investor.updateBelief("br:" + cheapestProperty.getCurrentCapitalizedRent()/(0.0 +
                        cheapestProperty.getCurrentPotentialRent()));
            }
        }


        // Updates the sellingRentable belief
        // TODO: Improve this approach
        if (investor.getProperty() != null) {
            if (cheapestPrice < Double.POSITIVE_INFINITY) {
                if (investor.getCurrentPurchasingPower() > cheapestPrice) {
                    double sellingRentability = investor.getProperty().getCurrentCapitalizedRent() /
                            (0.0 + investor.getProperty().getCurrentCapitalizedRent() +
                                    cheapestProperty.getCurrentCapitalizedRent());

                    investor.updateBelief("sr:" + sellingRentability);
                }
            }
        }
    }

    /**
     * Generates a investor intention step in the simulation
     * @param investor The investor
     */
    public static void investorIntentionStep(Connection conn, Investor investor) {

        Iterator<Fact> iter = investor.getAgent().goals().factIterator();
        while(iter.hasNext()) {
            String goal = iter.next().formula().toString();

            // If the goal is to invest
            if (goal.contains(Investor.BUY) && goal.contains(Investor.LANDLORD)){
                if (investor.getProperty() != null) {
                    PropertyUtility taken = invest(investor);
                    if (taken != null) {
                        freeProperties.remove(taken.getProperty());
                        forRentProperties.add(taken.getProperty());
                        taken.getProperty().setState(Property.SEEKING_TENANT);
                        Investor newInvestor = new Investor(investorAgent, investor, taken.getProperty());
                        investors.add(newInvestor);
                    }
                }
                else{
                    PropertyUtility taken = invest(investor);
                    if (taken != null) {
                        freeProperties.remove(taken.getProperty());
                        forRentProperties.add(taken.getProperty());
                        taken.getProperty().setState(Property.SEEKING_TENANT);
                    }
                }
            }

            // If the goal is to sell
            // TODO: Improve this approach
//            if (!goal.contains(Investor.BUY) && !goal.contains(Investor.LANDLORD) && !goal.contains(Investor.NOT_SELL)
//                    && goal.contains(Investor.SELL)){
//                if (investor.getProperty() != null) {
//                    freeProperties.add(investor.getProperty());
//                    investor.getProperty().setState(Property.FOR_SALE);
//                }
//            }

        }
    }

    /**
     * Generates a promoter step in the simulation for an specific promoter
     * @param promoter The promoter
     * @param time The time in the simulation
     */
    public static void promoterUpdateBeliefs(Promoter promoter, int time) {

        promoter.step(time);
        int purchFound = 0;

        // Updates affordBuyingLand
        for (Land land : forSaleLand) {
            if(promoter.getPurchasingPower() >= land.getPrice()){
                promoter.addPurchasableLand(land);
                purchFound++;
            }
        }

        if(purchFound > 0) promoter.updateBelief("abl:" + Double.toString(purchFound/(0.0 + forSaleLand.size())));

        else promoter.updateBelief("not abl:1");

        // Updates ac
        // TODO: Improve this approach

        double rnd = Math.random();
        if (rnd < 0.5) promoter.updateBelief("ac:" + 1);
        else promoter.updateBelief("not ac:" + 1);
    }

    /**
     * Generates a promoter intention step in the simulation
     * @param promoter The promoter
     */
    public static void promoterIntentionStep(Connection conn, Promoter promoter) {

        Iterator<Fact> iter = promoter.getAgent().goals().factIterator();
        while(iter.hasNext()) {
            String goal = iter.next().formula().toString();
            if (goal.contains(Promoter.BUY_LAND) && goal.contains(Promoter.SELL_OFF_PLANS)){
                Land taken = buyLand(conn, promoter);
                if (taken != null){
                    forSaleLand.remove(taken);
                    Property construction = null;
                    try {
                        int id = idManager[0]++;
                        construction = new Property(Integer.toString(id),taken.getLatitude(),
                                taken.getLongitude(),(taken.getPrice() + 150), taken.getPrice()/10, taken.getPrice(),
                                taken.getGeom());
                        construction.setDivision(taken.getDivision());
                        properties.get(taken.getDivision()).add(construction);
                        construction.setState(Property.FOR_SALE);
                        construction.setLand(taken);
                        freeProperties.add(construction);
                    }
                    catch (Exception e) {}
                }
            }
        }
    }

    /**
     *
     * Instantiates all the households in the file
     * @param conn The connection from the database
     * @throws SQLException
     */
    public static void createHouseholds(Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("select * from households limit 60");
        while( r.next() ) {
            int id = r.getInt(1);
            String lastname = r.getString(2);
            double purchasingPower = r.getDouble(3);
            double netMonthlyIncome = r.getDouble(4);
            Household household = new Household(Integer.toString(id), householdAgent,lastname,purchasingPower,
                    netMonthlyIncome);
            household.updateBelief("not r:1");
            household.updateBelief("not o:1");
            households.add(household);
        }
        r.close();
        s.close();
    }

    /**
     * Instantiates all the investors in the file
     * @param conn The connection from the database
     * @throws IOException If error I/O Error
     */
    public static void createInvestors(Connection conn) throws SQLException{
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("select * from investors");
        while( r.next() ) {
            int id = r.getInt(1);
            double purchasingPower = r.getDouble(2);
            Investor investor = new Investor(Integer.toString(id), investorAgent, purchasingPower);
            investors.add(investor);
        }
        r.close();
        s.close();
    }

    /**
     * Instantiates all the promoters in the file
     * @param conn The connection from the database
     * @throws IOException If error I/O Error
     */
    public static void createPromoters(Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("select * from promoters");
        while( r.next() ) {
            int id = r.getInt(1);
            double purchasingPower = r.getDouble(2);
            Promoter promoter = new Promoter(Integer.toString(id), promoterAgent, purchasingPower);
            promoters.add(promoter);
        }
        r.close();
        s.close();
    }

    /**
     * Instantiates all the properties in the database
     * @param conn The connection from the database
     * @throws SQLException
     */
    public static void createProperties(Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("select * from properties");
        while( r.next() ) {
            int id = r.getInt(1);
            double lat = r.getDouble(3);
            double lon = r.getDouble(4);
            double price = r.getDouble(5);
            double rent = r.getDouble(6);
            double value = r.getDouble(7);
            PGgeometry geom = (PGgeometry)r.getObject(8);
            Property property = new Property(Integer.toString(id),lat, lon, price, rent, value, geom);
            for (AdministrativeDivision division : divisions) {
                if(division != null){
                    Statement s1 = conn.createStatement();
                    String query = "SELECT ST_Contains(upzs.geom,land.geom) FROM (SELECT geom FROM upz WHERE gid = " +
                            division.getId() + ") AS upzs, (SELECT geom FROM land WHERE gid = " + id + ") AS land;";
                    ResultSet r1 = s1.executeQuery(query);
                    String c = null;
                    if(r1.next()) c = r1.getString(1);
                    if (c != null && c.equals("t")) {
                        property.setDivision(division);
                        properties.get(division).add(property);
                        break;
                    }
                    s1.close();
                    r1.close();
                }
            }
            property.setState("For sale");
            freeProperties.add(property);
        }
        r.close();
        s.close();
    }


    /**
     * Instantiates all the land in the database
     * @param conn The connection from the database
     * @throws SQLException
     */
    public static void createLand(Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("select * from land");
        while( r.next() ) {
            int id = r.getInt(1);
            double lat = r.getDouble(2);
            double lon = r.getDouble(3);
            double price = r.getDouble(4);
            PGgeometry geom = (PGgeometry)r.getObject(5);
            int codigo_upz = r.getInt(6);
            Land land = new Land(Integer.toString(id), lat, lon, price, geom);
            if (codigo_upz != 0) {
                land.setDivision(divisions[codigo_upz]);
                lands.get(divisions[codigo_upz]).add(land);
                forSaleLand.add(land);
            }
////            Get divisions for each land
//            for (AdministrativeDivision division : divisions) {
//                if (division != null) {
//                    Statement s1 = conn.createStatement();
//                    String query = "SELECT ST_Contains(upzs.geom,land.geom) FROM (SELECT geom FROM upz WHERE gid = " +
//                            division.getId() + ") AS upzs, (SELECT geom FROM land WHERE gid = " + id + ") AS land;";
//                    ResultSet r1 = s1.executeQuery(query);
//                    String c = null;
//                    if (r1.next()) c = r1.getString(1);
//                    if (c != null && c.equals("t")) {
//                        land.setDivision(division);
//                        lands.get(division).add(land);
//                        Statement s2 = conn.createStatement();
//                        s2.executeUpdate("UPDATE land SET codigo_upz = '" + division.getCode() + "' WHERE gid = '" +
//                                land.getId() + "';");
//                        break;
//                    }
//                    s1.close();
//                    r1.close();
//                }
//            }
//            forSaleLand.add(land);
        }
        r.close();
        s.close();
    }

    /**
     * Instantiates all the divisions in the database
     * @param conn The connection from the database
     * @throws SQLException
     */
    public static void createDivisions(Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("select gid,codigo_upz,geom from upz");
        while( r.next() ) {
            int id = r.getInt(1);
            int code = r.getInt(2);
            PGgeometry geom = (PGgeometry)r.getObject(3);
            AdministrativeDivision division = new AdministrativeDivision(Integer.toString(id),code, geom);
            equipments.put(division, new ArrayList<>());
            properties.put(division, new ArrayList<>());
            lands.put(division, new ArrayList<>());
            primaryNetwork.put(division, new ArrayList<>());
            divisions[code] = division;
        }
        r.close();
        s.close();
    }

    /**
     * Instantiates the transport network from the database
     * @param conn The connection from the database
     * @throws SQLException
     */
    public static void createTransportNetwork(Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("select a.gid,a.geom from " + filteredNetwork + ")) a");
        while( r.next() ) {
            networkLength++;
            int id = r.getInt(1);
            PGgeometry geom = (PGgeometry)r.getObject(2);
            TransportNetwork network = new TransportNetwork(Integer.toString(id), "primary", geom);
            Statement s1 = conn.createStatement();
            String query = "SELECT codigo_upz FROM interseccion_upz_redprimaria where id_redprimaria = '" + id + "';";
            ResultSet r1 = s1.executeQuery(query);
            while(r1.next()) {
                int codigo_upz = r1.getInt(1);
                if (codigo_upz != 0) {
                    network.setDivision(divisions[codigo_upz]);
                    primaryNetwork.get(divisions[codigo_upz]).add(network);
                }
            }
            s1.close();
            r1.close();
//            Get divisions for each network
//            for (AdministrativeDivision division : divisions) {
//                if (division != null) {
//                    Statement s1 = conn.createStatement();
//                    String query = "SELECT ST_Intersects(upzs.geom,network.geom) FROM (SELECT geom FROM upz WHERE gid = " +
//                            division.getId() + ") AS upzs, (SELECT geom FROM red_primaria WHERE gid = " + id + ") AS network;";
//                    ResultSet r1 = s1.executeQuery(query);
//                    String c = null;
//                    if (r1.next()) c = r1.getString(1);
//                    if (c != null && c.equals("t")) {
//                        network.setDivision(division);
//                        primaryNetwork.get(division).add(network);
//                        Statement s2 = conn.createStatement();
//                        s2.executeUpdate("INSERT INTO interseccion_upz_redprimaria (\"id_redprimaria\",\"codigo_upz\") " +
//                                "VALUES ('" + network.getId() + "','" + division.getCode() + "');");
//                    }
//                    r1.close();
//                    s1.close();
//                }
//            }
        }
        System.out.println(networkLength);
        r.close();
        s.close();
    }

    /**
     * Instantiates all the equipments in the database
     * @param conn The connection from the database
     * @throws SQLException
     */
    public static void createEquipments(Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet r = s.executeQuery("select a.id,a.codigo_upz,a.geom,a.nombre,a.tipo from " + filteredEquipments + ")) a");
        while( r.next() ) {
            int id = r.getInt(1);
            int codigo_upz = r.getInt(2);
            PGgeometry geom = (PGgeometry)r.getObject(3);
            String nombre = r.getString(4);
            String tipo = r.getString(5);
            Equipment equip = new Equipment(Integer.toString(id), tipo, geom);
            int codigoUPZ = r.getInt(2);
            if(codigoUPZ != 0 && codigoUPZ  <= 117) {
                equip.setDivision(divisions[codigoUPZ]);
                equipments.get(divisions[codigoUPZ]).add(equip);
                equipmentsLength++;
            }
        }
        r.close();
        s.close();
    }

    /**
     * Purchases a land
     * @return The land purchased
     */
    public static PropertyUtility rentProperty(Household investor){
        double maxUtility = 0.0;
        PropertyUtility selection = null;
        for (PropertyUtility purchasable : investor.getRentableProperties()) {
            createPropertyUtility(purchasable, BUY_OR_RENT);
            if(purchasable.getUtility() > maxUtility){
                maxUtility = purchasable.getUtility();
                selection = purchasable;
            }
        }
        investor.setRenting(selection != null);
        if(investor.isRenting()){
            investor.setPreviousNetMonthlyIncome(investor.getCurrentNetMonthlyIncome());
            investor.setCurrentNetMonthlyIncome(investor.getPreviousNetMonthlyIncome() - selection.getProperty().getCurrentCapitalizedRent());
        }
        return selection;
    }

    /**
     * Purchases a land
     * @return The land purchased
     */
    public static PropertyUtility buyProperty(Household investor){
        double maxUtility = 0.0;
        PropertyUtility selection = null;
        for (PropertyUtility purchasable : investor.getRentableProperties()) {
            createPropertyUtility(purchasable, BUY_OR_RENT);
            if(purchasable.getUtility() > maxUtility){
                maxUtility = purchasable.getUtility();
                selection = purchasable;
            }
        }
        investor.setRenting(selection != null);
        if(investor.isRenting()){
            investor.setPreviousNetMonthlyIncome(investor.getCurrentNetMonthlyIncome());
            investor.setCurrentNetMonthlyIncome(investor.getPreviousNetMonthlyIncome() - selection.getProperty().getCurrentCapitalizedRent());
        }
        return selection;
    }

    /**
     * Purchases a land
     * @return The land purchased
     */
    public static PropertyUtility invest(Household investor) {
        double maxUtility = 0.0;
        PropertyUtility selection = null;
        for (PropertyUtility purchasable : investor.getRentableProperties()) {
            createPropertyUtility(purchasable, INVEST);
            if (purchasable.getUtility() > maxUtility) {
                maxUtility = purchasable.getUtility();
                selection = purchasable;
            }
        }
        investor.setRenting(selection != null);
        if (investor.isRenting()) {
            investor.setPreviousNetMonthlyIncome(investor.getCurrentNetMonthlyIncome());
            investor.setCurrentNetMonthlyIncome(investor.getPreviousNetMonthlyIncome() - selection.getProperty().getCurrentCapitalizedRent());
        }
        return selection;
    }

    /**
     * Purchases a land
     * @return The land purchased
     */
    public static PropertyUtility invest(Investor investor){
        double maxUtility = 0.0;
        PropertyUtility selection = null;
        for (PropertyUtility purchasable : investor.getPurchasableProperties()) {
            createPropertyUtility(purchasable, INVEST);
            if(purchasable.getUtility() > maxUtility){
                maxUtility = purchasable.getUtility();
                selection = purchasable;
            }
        }
        if (selection != null){
            investor.setPreviousPurchasingPower(investor.getCurrentPurchasingPower());
            investor.setCurrentPurchasingPower(investor.getPreviousPurchasingPower() - selection.getProperty().getCurrentPrice());
        }
        return selection;
    }

    /**
     * Purchases a land
     * @return The land purchased
     */
    public static Land buyLand(Connection conn, Promoter promoter){
        double maxUtility = 0.0;
        Land selection = null;
        for (Land purchasable : promoter.getPurchasableLand()) {
//            try {
                if (purchasable.getDivision() != null && !purchasable.isUpdated()) {
//                    double equipUtility = 0.0;
//                    double transportUtility = 0.0;
//
//                    Statement s1 = conn.createStatement();
//                    String query_equipments = "SELECT COUNT(a.*) FROM " + filteredEquipments + ")) a INNER JOIN buffer b ON ST_Intersects(a.geom, b.geom) WHERE b.id_land = " + purchasable.getId();
//                    ResultSet r1 = s1.executeQuery(query_equipments);
//                    if(r1.next()) {
//                        equipUtility = r1.getInt(1);
//                    }
//                    s1.close();
//                    r1.close();
//
//
//                    Statement s2 = conn.createStatement();
//                    String query_transport = "SELECT COUNT(a.*) FROM " + filteredNetwork + ")) a INNER JOIN buffer b ON ST_Intersects(a.geom, b.geom) WHERE b.id_land = " + purchasable.getId();
//                    ResultSet r2 = s2.executeQuery(query_transport);
//                    if(r2.next()) {
//                        transportUtility = r2.getInt(1);
//                    }
//                    s2.close();
//                    r2.close();
//                    purchasable.setUtility(0.4*(equipUtility/(double)equipmentsLength) + 0.6*(transportUtility/(double)networkLength));
////                    purchasable.setUtility(0.0*(equipUtility/(double)equipmentsLength) + 1.0*(transportUtility/(double)networkLength));
                    purchasable.setUtility(Math.random());
                    purchasable.setUpdated(true);
                }
                if(purchasable.getUtility() > maxUtility){
                    maxUtility = purchasable.getUtility();
                    selection = purchasable;
                }
//            }
//            catch (SQLException e){
//                e.printStackTrace();
//            }
        }
        if (selection != null){
            promoter.setPurchasingPower(promoter.getPurchasingPower() - selection.getPrice());
        }
        return selection;
    }
    
    public static void createPropertyUtility(PropertyUtility property, String type) {

        // Sets transport utility and equipments utility

        if (property.getProperty().getDivision() != null) {
            try {
                double equipUtility = 0.0;
                double transportUtility = 0.0;

                Statement s1 = conn.createStatement();
                String query_equipments = "SELECT COUNT(a.*) FROM " + filteredEquipments + ")) a INNER JOIN buffer b " +
                        "ON ST_Intersects(a.geom, b.geom) WHERE b.id_land = " + property.getProperty().getId();
                ResultSet r1 = s1.executeQuery(query_equipments);
                if (r1.next()) {
                    equipUtility = r1.getInt(1);
                }
                s1.close();
                r1.close();


                Statement s2 = conn.createStatement();
                String query_transport = "SELECT COUNT(a.*) FROM " + filteredNetwork + ")) a INNER JOIN buffer b " +
                        "ON ST_Intersects(a.geom, b.geom) WHERE b.id_land = " + property.getProperty().getId();
                ResultSet r2 = s2.executeQuery(query_transport);
                if (r2.next()) {
                    transportUtility = r2.getInt(1);
                }
                s2.close();
                r2.close();

                property.setEquipments(equipUtility/(double)equipmentsLength);
                property.setTransport(transportUtility/(double)networkLength);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        if (type.equals(BUY_OR_RENT)) {
            // Creates property utility

            String sqlCreate = "INSERT INTO properties_utility (property_id,household_id,class) VALUES (" +
                    property.getProperty().getId() + "," + property.getHousehold().getId() + ",'" + property.getHousehold().getCurrentPurchasingPower() + "');";

            String sqlCreateGeom = "UPDATE properties_utility SET geom = (SELECT ST_SetSRID(ST_MakePoint(" + property.getProperty().getLongitude()
                    + ", " + property.getProperty().getLand().getLatitude() + "),4326)) WHERE property_id = " + property.getProperty().getId()
                    + " AND household_id = " + property.getHousehold().getId()  + ";";

            String sqlGetSimilar = "SELECT COUNT(*) FROM properties_utility prop INNER JOIN (SELECT ST_Buffer(geom, 0.008) AS geom " +
                    "FROM properties_utility WHERE property_id = " + property.getProperty().getId() + " AND household_id = " +
                    property.getHousehold().getId() + ") buffer ON ST_INTERSECTS (prop.geom,buffer.geom) WHERE class BETWEEN (prop.class - 5) AND (prop.class + 15);";

            int neighbors = 0;

            try {
                Statement s = conn.createStatement();
                ResultSet r = s.executeQuery(sqlCreate);
                s.close();
                r.close();

                s = conn.createStatement();
                r = s.executeQuery(sqlCreateGeom);
                s.close();
                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Sets closeness

            try {
                Statement s = conn.createStatement();
                ResultSet r = s.executeQuery(sqlGetSimilar);
                if (r.next())  neighbors = r.getInt(1);
                s.close();
                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            property.setCloseness(neighbors);
        }

        property.calculateUtility(type);

    }


    /**
     * Writes the resultant data indicators in the database
     * @param conn The connection to the database
     * @throws SQLException
     */
    public static void writeIndicators(Connection conn, int time) throws SQLException{
        double countRent = 0.0;
        double countSale = 0.0;
        for (AdministrativeDivision division : divisions) {
            if (division != null) {
                double rentInDivision = 0.0;
                double saleInDivision = 0.0;
                for (Property property : properties.get(division)) {
                    if (property.getState().equals(Property.OCCUPIED)) {
                        countSale++;
                        saleInDivision++;
                    } else if (property.getState().equals(Property.RENTED)) {
                        countRent++;
                        saleInDivision++;
                    }
                }
                division.setOnSaleProperties(saleInDivision);
                division.setRentedProperties(rentInDivision);
            }
        }
        for (AdministrativeDivision division : divisions) {
            if (division != null) {
                double sale = (countSale > 0.0) ? division.getOnSaleProperties() / countSale : 0;
                double rent = (countRent > 0.0) ? division.getRentedProperties() / countRent : 0;
                double si = Math.abs(sale - rent) / 2.0;
                Statement s = conn.createStatement();
                String query = "INSERT INTO indicator2 (\"step\",\"si\",\"idUPZ\") VALUES ('" + time + "','" + si + "','" +
                        division.getId() + "')";
                s.executeUpdate(query);
                s.close();
            }
        }
        double rop = ((countRent + countSale) > 0.0)? countRent/(countRent + countSale): 0.0;
        Statement s = conn.createStatement();
        String query = "INSERT INTO indicator1 (\"step\",\"ROP\") VALUES ('" + time + "','" + rop + "')";
        s.executeUpdate(query);
        s.close();

    }

    /**
     * Writes the resultant data in the database
     * @param conn The connection to the database
     * @throws SQLException
     */
    public static void writeResults(Connection conn, int time) throws SQLException{
        for (AdministrativeDivision division : divisions) {
            if (division != null) {
                for (Property property : properties.get(division)) {
                    Statement s = conn.createStatement();
                    String query = "INSERT INTO properties_state (\"step\",\"idProperty\",\"price\",\"rent\",\"value\",\"state\"" +
                            ",\"geom\",\"codigo_upz\") VALUES ('" + time + "','" + property.getId() + "','" + property.getCurrentPrice() + "','" +
                            property.getCurrentCapitalizedRent() + "','" + property.getCurrentValue() + "','" + property.getState() +
                            "','" + property.getGeom() + "','" + division.getCode() + "')";
                    s.executeUpdate(query);
                    s.close();
                }
            }
        }
    }

    /**
     * Creates a connection to the database
     * @param conn The resultant connection
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection createConnection(Connection conn) throws ClassNotFoundException, SQLException {
        //  Load the JDBC driver and establish a connection.
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/validation";
        conn = DriverManager.getConnection(url, "Meili", "");
        //  Add the geometry types to the connection.
        ((org.postgresql.PGConnection)conn).addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
        ((org.postgresql.PGConnection)conn).addDataType("box3d",Class.forName("org.postgis.PGbox3d"));

        try{
            Statement s1 = conn.createStatement();
            s1.executeUpdate("DROP TABLE properties_state");
            s1.close();
        } catch (SQLException e) {
            System.out.println("Creating results table...");
        }

        try{
            Statement s1 = conn.createStatement();
            s1.executeUpdate("DROP TABLE indicator1");
            s1.close();
        } catch (SQLException e) {
            System.out.println("Creating indicator1 table...");
        }

        try{
            Statement s1 = conn.createStatement();
            s1.executeUpdate("DROP TABLE indicator2");
            s1.close();
        } catch (SQLException e) {
            System.out.println("Creating indicator1 table...");
        }

        try{
            Statement s1 = conn.createStatement();
            s1.executeUpdate("DELETE FROM properties_utility;");
            s1.close();
        } catch (SQLException e) {
            System.out.println("Creating utility table...");
        }

        Statement s2 = conn.createStatement();
        String query = "CREATE TABLE \"properties_state\" (gid serial,\"step\" numeric,\"idProperty\" numeric,\"price\""+
                "numeric,\"rent\" numeric,\"value\" numeric,\"state\" varchar(200),\"codigo_upz\" numeric)";
        s2.executeUpdate(query);
        s2.close();

        Statement s3 = conn.createStatement();
        query = "ALTER TABLE \"properties_state\" ADD PRIMARY KEY (gid)";
        s3.executeUpdate(query);
        s3.close();

        Statement s4 = conn.createStatement();
        query = "SELECT AddGeometryColumn('','properties_state','geom','0','POINT',2)";
        s4.executeQuery(query);
        s4.close();

        Statement s5 = conn.createStatement();
        query = "CREATE TABLE \"indicator1\" (gid serial,\"step\" numeric,\"ROP\" numeric)";
        s5.executeUpdate(query);
        s5.close();

        Statement s6 = conn.createStatement();
        query = "CREATE TABLE \"indicator2\" (gid serial,\"step\" numeric,\"idUPZ\" numeric,\"si\" numeric)";
        s6.executeUpdate(query);
        s6.close();

        return conn;
    }

    public static void main(String[] args){
        System.out.println("Testing the urbanSimulation Simulator...");
        households = new ArrayList<>();
        investors = new ArrayList<>();
        promoters = new ArrayList<>();
        properties = new HashMap<>();
        freeProperties = new ArrayList<>();
        forRentProperties = new ArrayList<>();
        forSaleLand = new ArrayList<>();
        lands = new HashMap<>();
        divisions = new AdministrativeDivision[200];
        equipments = new HashMap<>();
        primaryNetwork = new HashMap<>();
        idManager = new int[5];

        filteredEquipments = "(SELECT * FROM equipamentos WHERE codigo_upz IN (85,81,80,46,112,116,31,30,29,28,27";
        filteredNetwork = "(SELECT * FROM red_primaria WHERE gid IN (176,784,794,793,798,796,822,819,856,852,849,885,894," +
                "891,937,932,938,984,990,986,1029,1028,1076,1077,1113,1114,1117,1165,1164,1218,1221,1220,1280,1281,1284," +
                "1332,1330,1373,1368,1374,1418,1416,1455,1453,1487,1533,1527,51,48,52,64,63,76,94,91,90,96,102,101,106," +
                "110,109,114,113,118,117,122";

        try {
            conn = createConnection(conn);
            numSim = 30;

            // Get initializer files
            File householdAgentFile = new File("/Users/Meili/kobdig/docs/householdAgent.apl");
            File investorAgentFile = new File("/Users/Meili/kobdig/docs/investorAgent.apl");
            File promoterAgentFile = new File("/Users/Meili/kobdig/docs/promoterAgent.apl");

            // Instantiates all the classes

            try {
                householdAgent = new Agent(new FileInputStream(householdAgentFile));
                investorAgent = new Agent(new FileInputStream(investorAgentFile));
                promoterAgent = new Agent(new FileInputStream(promoterAgentFile));

                createDivisions(conn);
//                createTransportNetwork(conn);
//                createEquipments(conn);
//                createProperties(conn);
                createHouseholds(conn);
                createInvestors(conn);
                createPromoters(conn);
                createLand(conn);

                writeIndicators(conn, 0);
                writeResults(conn, 0);

                Statement s = conn.createStatement();
                ResultSet r = s.executeQuery("SELECT MAX(gid) FROM properties;");
                if(r.next())  idManager[0] = r.getInt(1) + 1;
                s.close();
                r.close();

                for (time = 1; time <= numSim; time++) {

                    // Updates market values

//                    if ((time -1) == 10) {
//                        updateEquipmentsOrNetworkToConsider(NETWORK, "985,974,973,1022,1020,1018,1015,1014,1013,1011," +
//                                "1002,1001,1006,1005,996,994,442,443,443,435,434,427,425,420,419,417,416,415,412,411,487," +
//                                "483,472,471,468,463,530,578");
//                    }

                    for (AdministrativeDivision division : divisions) {
                        if (division != null) {
                            ArrayList<Land> landDiv = lands.get(division);
                            for (Land land : landDiv) land.step(time - 1);
                            for (Property property : properties.get(division)) property.step(time - 1);
                        }
                    }

                    for (Promoter promoter : promoters) {
                        // Updates promoter's beliefs
                        promoterUpdateBeliefs(promoter, time-1);
                        // Updates promoter's new intentions
                        promoterIntentionStep(conn, promoter);
                    }

                    for (Investor investor : investors) {
                        // Updates household's beliefs
                        investorUpdateBeliefs(investor, time-1);
                        // Generates household's new intentions
                        investorIntentionStep(conn, investor);
                    }

                    for (Household household : households) {
                        // Updates household's beliefs
                        householdUpdateBeliefs(household, time-1);
                        // Generates household's new intentions
                        householdIntentionStep(conn, household);
                    }

                    System.err.println(time-1 + ". - free " + freeProperties.size() + " for rent " + forRentProperties.size() + " total " +
                            (freeProperties.size() + forRentProperties.size()) );

                    writeIndicators(conn, time-1);
                    writeResults(conn, time-1);

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
