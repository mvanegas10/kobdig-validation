/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An order book.
 * A book is a set of order queues, one for each limit price level.
 * 
 * @author Andrea G. B. Tettamanzi
 */
public class Book
{
    /** The price levels in the book */
    protected SortedMap<Integer, Queue<Order>> levels;
    
    /** The type of book: bids (+1) or asks (-1). */
    int sign;
    
    /**
     * Creates a new empty book.
     */
    public Book(int type)
    {
        levels = new TreeMap<Integer, Queue<Order>>();
        sign = type;
    }
    
    /**
     * Inserts an order.
     * 
     * @return if the order was successfully inserted
     */
    public boolean insert(Order o)
    {
        if(o.sign()!=sign)
            throw new IllegalArgumentException("Order has wrong sign: " + o.sign());
        Queue<Order> level = levels.get(o.price());
        if(level==null)
        {
            level = new LinkedList<Order>();
            levels.put(o.price(), level);
        }
        return level.offer(o);
    }

    /**
     * Deletes an order.
     * 
     * @param o an existing order, previously inserted in the book.
     */
    void delete(Order o)
    {
        Queue<Order> level = levels.get(o.price());
        if(level!=null)
            if(level.remove(o))
                if(level.isEmpty())
                    levels.remove(o.price());
    }
    
    /**
     * Returns the first order according to the book type.
     * For a bid book, the first order is the first order at the
     * highest price level. For an offer book, the first order is the
     * first order at the lowest price level.
     */
    Order best()
    {
        if(levels.isEmpty())
            return null;
        int head = sign>0 ? levels.lastKey() : levels.firstKey();
        Queue<Order> level = levels.get(head);
        return level.peek();
    }
}
