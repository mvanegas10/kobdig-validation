/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A virtual trader.
 *
 * @author Andrea G. B. Tettamanzi
 */
public abstract class Trader implements Participant
{
    private static final String[] V = {"a", "e", "i", "o", "u"};
    private static final String[] T = {"h", "b", "d", "j", "g", "p", "t", "ch", "k", "m"};
    private static final String[] R = {"", "n", "l", "r", "s"};
    private static int nextId = 0;
    
    /** The name of this trader. */
    protected String name;
    
    /** The trader's cash balance. */
    protected long cash;
    
    /** The trader's reserved cash. */
    protected long resCash;
    
    /** The trader's asset balance in contract XYZ. */
    protected long asset;
    
    /** The trader's reserved XYZ contracts. */
    protected long resAsset;
    
    /** The set of pending orders. */
    protected Set<Order> orders;
    
    /**
     * Creates a new unique ID for a trader.
     */
    public static String uniqueId()
    {
        int n = nextId++;
        String s = "";
        
        do
        {
            int r = n % R.length;
            n /= R.length;
            int v = n % V.length;
            n /= V.length;
            int t = n % T.length;
            n /= T.length;
            s += T[t] + V[v] + R[r];
        }
        while(n!=0);
        return s;
    }

    /**
     * Creates a new trader with the given unique identifier, initial
     * cash balance, and asset balance.
     */
    public Trader(String id, int c, int a)
    {
        name = id;
        cash = c;
        resCash = 0;
        asset = a;
        resAsset = 0;
        orders = new HashSet<Order>();
    }
    
    @Override
    public String name()
    {
        return name;
    }

    @Override
    public void executed(Order o, int qty, int price)
    {
        asset += qty;
        cash -= qty*price;
        if(qty>0)
        {
            resCash -= qty*o.price();
            if(resCash<0)
                throw new RuntimeException("Negative cash reservation!");
        }
        else
            resAsset += qty;
        if(o.qty()==0)
            orders.remove(o);
    }
    
    /**
     * Returns this trader's cash balance.
     */
    public long cash()
    {
        return cash;
    }
    
    /**
     * Changes this trader's cash balance by the specified amount.
     */
    public void cash(long change)
    {
        cash += change;
    }
    
    /**
     * Returns this trader's asset balance.
     */
    public long asset()
    {
        return asset;
    }
    
    /**
     * Changes this trader's asset balance by the specified number of contracts.
     */
    public void asset(long change)
    {
        asset += change;
    }
    
    @Override
    public String toString()
    {
        return name + " with " + cash + " cents and " + asset + " XYZ" +
                ", NAV = " + netAssetValue() + ", " + orders.size() + " unfilled orders";
    }
    
    /**
     * Returns the NAV of the trader.
     */
    public long netAssetValue()
    {
        return cash + asset*Market.instrument("XYZ").price();
    }
    
    /**
     * Buy the given quantity of asset at the given limit price.
     * 
     * @param qty the quantity
     * @param price the limit price
     */
    protected void buy(int qty, int price)
    {
        if(qty*price>cash - resCash)
            throw new IllegalArgumentException("Not enough cash.");
        Order o = new Order(this, Order.BUY, qty, price);
        orders.add(o);
        Market.instrument("XYZ").send(o);
        resCash += qty*price;
    }

    /**
     * Sell the given quantity of asset at the given limit price.
     * 
     * @param qty the quantity
     * @param price the limit price
     */
    protected void sell(int qty, int price)
    {
        if(qty>asset - resAsset)
            throw new IllegalArgumentException("Short selling is not permitted.");
        Order o = new Order(this, Order.SELL, qty, price);
        orders.add(o);
        Market.instrument("XYZ").send(o);
        resAsset += qty;
    }
    
    /**
     * Cancels the given order, previously issued by the trader,
     * or anything remains of it.
     * 
     * @param o an existing order
     */
    protected void cancel(Order o)
    {
        if(o.sign()==Order.BUY)
        {
            resCash -= o.qty()*o.price();
            if(resCash<0)
                throw new RuntimeException("Negative cash reservation!");
        }
        else
            resAsset -= o.qty();
        Market.instrument("XYZ").delete(o);
        orders.remove(o);
    }

    /**
     * Cancels all the trader's pending orders.
     */
    public void cancelAll()
    {
        Iterator<Order> i = orders.iterator();
        while(i.hasNext())
            cancel(i.next());
    }
    
}
