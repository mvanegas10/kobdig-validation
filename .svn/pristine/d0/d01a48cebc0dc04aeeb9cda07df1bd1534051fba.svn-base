/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

/**
 * An order submitted or for submission to the market.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Order
{
    public static final int BUY = 1;
    public static final int SELL = -1;
        
    /**
     * The quantity of the order, i.e., the number of contracts to trade.
     * A positive quantity means the order is a <em>buy</em>, a
     * negative quantity means the order is a <em>sell</em>.
     */
    protected int quantity;
    
    /**
     * The limit price of the order: market orders may be simulated by
     * setting a limit price of 0 for sell orders or 2<sup>31</sup> - 1
     * for buy orders.
     */
    protected int price;
    
    /** The market participant who has issued the order. */
    Participant owner;
    
    /**
     * Creates a new order with the specified sign, quantity, and limit price.
     * A limit price of zero is interpreted as no limit (i.e., market price).
     * 
     * @param issuer the market participant issuing this order
     * @param sign the sign of the order, <code>BUY</code> or <code>SELL</code>
     * @param qty the quantity of contracts to trade
     * @param limit the limit price, or zero if the order is at market
     */
    public Order(Participant issuer, int sign, int qty, int limit)
    {
        if(sign!=SELL && sign!=BUY)
            throw new IllegalArgumentException("Order sign must be +1 or -1.");
        if(qty<=0)
            throw new IllegalArgumentException("Quantity of an order must be greater than zero.");
        if(limit<0)
            throw new IllegalArgumentException("Price of an order cannot be negative.");
        if(issuer==null)
            throw new IllegalArgumentException("Order must have an issuer.");
        owner = issuer;
        quantity = sign*qty;
        price = limit;
        if(price==0 && sign==BUY)
            price = Integer.MAX_VALUE;
    }
    
    /**
     * Returns the issuer of this order.
     */
    Participant issuer()
    {
        return owner;
    }

    /**
     * Returns the limit price of this order.
     */
    int price()
    {
        return price;
    }

    int sign()
    {
        if(quantity>0)
            return BUY;
        else
            return SELL;
    }
    
    int qty()
    {
        return Math.abs(quantity);
    }
    
    /**
     * Fill the given quantity of this order.
     */
    void fill(int qty)
    {
        if(quantity>0)
            quantity -= qty;
        else
            quantity += qty; 
    }
}
