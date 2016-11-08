/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

/**
 * A participant in the market, i.e., a trader.
 *
 * @author Andrea G. B. Tettamanzi
 */
public interface Participant
{
    /**
     * Returns a unique identifier of the market participant.
     * 
     * @return
     */
    public String name();

    /**
     * Notify the participant that the given order has been
     * (partially) executed for the given quantity at the given price.
     * 
     * @param o an order
     * @param qty the quantity filled, positive for a purchase, negative for a sale
     * @param price price at which the order was (partially) filled
     */
    public void executed(Order o, int qty, int price);
    
    /**
     * Method called by the market simulator to let the market participant
     * evaluate market data and take action accordingly.
     */
    public void trade();

}
