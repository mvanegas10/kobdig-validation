/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

/**
 * The recording of a trade between two market participants.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Trade
{
    public Order ask;
    public Order bid;
    public int qty;
    public int price;
    
    /**
     * Creates an arranged trade.
     * 
     * @param a the ask order
     * @param b the bid order
     * @param q the quantity traded
     * @param p the price of the trade
     */
    public Trade(Order a, Order b, int q, int p)
    {
        ask = a;
        bid = b;
        qty = q;
        price = p;
    }
}
