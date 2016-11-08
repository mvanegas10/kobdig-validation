/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A virtual trading pit, where a given security, commodity, or contract
 * is traded by performing an call auction.
 * Market participants trade by sending orders to the pit where the asset
 * they are interested in is traded.
 * 
 * @author Andrea G. B. Tettamanzi
 */
public class Pit
{
    /** The name of the traded contract. */
    protected String ticker;
    
    /** The bid order book. */
    protected Book bids;
    
    /** The offer book. */
    protected Book asks;
    
    /** The last clearing price. */
    protected int price;
    
    /** The volume traded during the last auction. */
    protected int volume;
    
    /**
     * Creates a new trading pit for the contract with the given ticker.
     * The initial price is arbitrarily set to 1.
     */
    public Pit(String contract)
    {
        ticker = contract;
        bids = new Book(Order.BUY);
        asks = new Book(Order.SELL);
        price = 100;
    }

    /**
     * Gets the last clearing price of the contract traded.
     * 
     * @return the current market price in this pit.
     */
    public int price()
    {
        return price;
    }
    
    /**
     * Gets the volume traded during the last auction.
     * 
     */
    public int volume()
    {
        return volume;
    }
    
    /**
     * Checks whether the given market participant has the right to
     * submit orders to this trading pit.
     */
    public boolean recognize(Participant trader)
    {
        // For the time being, being a valid participant is enough...
        return trader!=null;
    }
    
    /**
     * Sends an order to this trading pit.
     * Only orders with a recognized issuer are accepted.
     * 
     * @param o an order
     * @return if the order has been accepted
     */
    public boolean send(Order o)
    {
        if(!recognize(o.issuer()))
            return false;
        if(o.sign()==Order.BUY)
        {
            // Market.log.println("Received from " + o.issuer().name() +
            //         " BUY " + o.qty() + " " + ticker + " @" + o.price());
            return bids.insert(o);
        }
        else
        {
            // Market.log.println("Received from " + o.issuer().name() +
            //         " SELL " + o.qty() + " " + ticker + " @" + o.price());
            return asks.insert(o);
        }
    }
    
    /**
     * Deletes a previously submitted order.
     *
     * @param o an existing order
     */
    public void delete(Order o)
    {
        if(o==null)
            return;
        if(o.sign()==Order.BUY)
            bids.delete(o);
        else
            asks.delete(o);
    }
    
    /**
     * Performs a single-price auction to clear as many orders as possible.
     * 
     * @return the volume traded
     */
    int clear()
    {
        Order bid, ask;
        LinkedList<Trade> trades = new LinkedList<Trade>();
        volume = 0;
        
        // Match orders and arrange trades:
        bid = bids.best();
        ask = asks.best();
        if(bid==null || ask==null)
            return volume;
        while(ask.price()<=bid.price())
        {
            int qty = Math.min(ask.qty(), bid.qty());
            // Arrange a new trade, leaving the price provisionally blank
            Trade trade = new Trade(ask, bid, qty, 0);
            trades.add(trade);
            volume += qty;
            
            // tentatively set clearing price as an average of bid and ask:
            int askprice = ask.price();
            if(askprice==0) askprice = bid.price();
            int bidprice = bid.price();
            if(bidprice==Integer.MAX_VALUE) bidprice = ask.price();
            if(askprice<=bidprice)
               price = (askprice + bidprice)/2;
            // otherwise, both seller and buyer are trading at market,
            // and the previously fixed price is still valid.
                    
            ask.fill(qty);
            if(ask.qty()==0)
            {
                asks.delete(ask);
                ask = asks.best();
            }
            bid.fill(qty);
            if(bid.qty()==0)
            {
                bids.delete(bid);
                bid = bids.best();
            }
            if(ask==null || bid==null) break;
        }
        // N.B.: the last price is the clearing price!
        
        // Settle all the trades:
        Iterator<Trade> i = trades.iterator();
        while(i.hasNext())
        {
            Trade trade = i.next();
            trade.price = price;
            trade.ask.issuer().executed(trade.ask, -trade.qty, price);
            trade.bid.issuer().executed(trade.bid, trade.qty, price);
            Market.log.println(trade.ask.issuer().name() +
                    " -> " + trade.bid.issuer().name() +
                    " " + trade.qty + " " + ticker + " @" + trade.price);
        }
        return volume;
    }

    /**
     * Returns the ticker symbol of the security traded in this pit.
     * 
     * @return a ticker symbol
     */
    public String ticker()
    {
        return ticker;
    }
}
