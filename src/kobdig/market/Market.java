/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

import java.io.PrintStream;
import java.util.Vector;

/**
 * Singleton class containing the market simulator.
 * This class has all the facilities for recording the time series
 * of prices and volumes.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Market
{
    /** The market time. */
    static protected int time = 0;

    /** The virtual pit for trading XYZ, the one and only asset in the system. */
    static protected Pit pit = new Pit("XYZ");
    
    /** The time series of prices. */
    static protected Vector<Integer> prices = new Vector<Integer>();
    
    /** The financial time series of prices used for calculating technical indicators. */
    static protected Series series = new Series();
    
    /** The time series of volumes. */
    static protected Vector<Integer> volumes = new Vector<Integer>();
    
    /** A file for detailed logging of market operation. */
    public static PrintStream log;

    /**
     * This singleton class cannot have instances.
     */
    private Market()
    {        
    }
    
    /**
     * Resets the market for a new simulation.
     */
    public static void reset()
    {
        time = 0;
        prices = new Vector<Integer>();
        volumes = new Vector<Integer>();
    }
    
    /**
     * Performs a simulation step.
     */
    public static void step()
    {
        volumes.add(pit.clear());
        prices.add(pit.price());
        log.println(pit.ticker() + "(" + time + ") = " + prices.elementAt(time) +
                ", vol = " + volumes.elementAt(time));
        series = new Series(prices);
        time++;
    }
    
    /**
     * Returns a reference to the trading pit for the given instrument.
     */
    public static Pit instrument(String ticker)
    {
        if(ticker.equals("XYZ"))
            return pit;
        return null;
    }
    
    /**
     * Returns the value of the <var>i</var>th technical indicator signal.
     * A value of 1 means "buy", a value of -1 means "sell", whereas
     * a zero value means "no signal" or "neutral".
     * 
     * @param i the index of the indicator
     */
    public static int signal(int i)
    {
        final double SMALL = 0.005;
        
        int sig = 0;
        int last = series.size - 1;
        try
        {
            switch(i)
            {
                case 1: // Price went up this period
                    if(series.getLogReturn(last)>SMALL)
                        sig = 1;
                    else if(series.getLogReturn(last)<-SMALL)
                        sig = -1;
                    break;
                case 2: // Price went up one period ago
                    if(series.getLogReturn(last - 1)>SMALL)
                        sig = 1;
                    else if(series.getLogReturn(last - 1)<-SMALL)
                        sig = -1;                    
                    break;
                case 3: // Price went up two periods ago
                    if(series.getLogReturn(last - 2)>SMALL)
                        sig = 1;
                    else if(series.getLogReturn(last - 2)<-SMALL)
                        sig = -1;
                    break;
                case 4: // Price went up three periods ago
                    if(series.getLogReturn(last - 3)>SMALL)
                        sig = 1;
                    else if(series.getLogReturn(last - 3)<-SMALL)
                        sig = -1;
                    break;
                case 5: //  Price went up four periods ago
                    if(series.getLogReturn(last - 4)>SMALL)
                        sig = 1;
                    else if(series.getLogReturn(last - 4)<-SMALL)
                        sig = -1;
                    break;
                case 6: //  Price > 5-period SMA
                    if(series.getQuote(last)>series.movingAverage(5, last) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last)<series.movingAverage(5, last) - SMALL)
                        sig = -1;
                    break;
                case 7: //  Price > 10-period SMA
                    if(series.getQuote(last)>series.movingAverage(10, last) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last)<series.movingAverage(10, last) - SMALL)
                        sig = -1;
                    break;
                case 8: //  Price > 20-period SMA
                    if(series.getQuote(last)>series.movingAverage(20, last) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last)<series.movingAverage(20, last) - SMALL)
                        sig = -1;
                    break;
                case 9: //  Price > 5-period EMA
                    if(series.getQuote(last)>series.expMovingAverage(5, last) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last)<series.expMovingAverage(5, last) - SMALL)
                        sig = -1;
                    break;
                case 10: //  Price > 10-period EMA
                    if(series.getQuote(last)>series.expMovingAverage(10, last) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last)<series.expMovingAverage(10, last) - SMALL)
                        sig = -1;
                    break;
                case 11: //  Price > 20-period EMA
                    if(series.getQuote(last)>series.expMovingAverage(20, last) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last)<series.expMovingAverage(20, last) - SMALL)
                        sig = -1;
                    break;
                case 12: //  Price / 5-period SMA > 20-period SMA
                    if(series.movingAverage(5, last)>series.movingAverage(20, last) + SMALL)
                        sig = 1;
                    else if(series.movingAverage(5, last)<series.movingAverage(20, last) - SMALL)
                        sig = -1;
                    break;
                case 13: //  Price / 5-period SMA > 50-period SMA
                    if(series.movingAverage(5, last)>series.movingAverage(50, last) + SMALL)
                        sig = 1;
                    else if(series.movingAverage(5, last)<series.movingAverage(50, last) - SMALL)
                        sig = -1;
                    break;
                case 14: //  Price / 5-period EMA > 20-period EMA
                    if(series.expMovingAverage(5, last)>series.expMovingAverage(20, last) + SMALL)
                        sig = 1;
                    else if(series.expMovingAverage(5, last)<series.expMovingAverage(20, last) - SMALL)
                        sig = -1;
                    break;
                case 15: //  Price / 5-period EMA > 50-period EMA
                    if(series.expMovingAverage(5, last)>series.expMovingAverage(50, last) + SMALL)
                        sig = 1;
                    else if(series.expMovingAverage(5, last)<series.expMovingAverage(50, last) - SMALL)
                        sig = -1;
                    break;
                case 16: //  Price / 5-period SMA went up this period
                    if(series.getQuote(last)/series.movingAverage(5, last)>series.getQuote(last - 1)/series.movingAverage(5, last - 1) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last)/series.movingAverage(5, last)<series.getQuote(last - 1)/series.movingAverage(5, last - 1) - SMALL)
                        sig = -1;
                    break;
                case 17: //  Price / 5-period SMA went up one period ago
                    if(series.getQuote(last - 1)/series.movingAverage(5, last - 1)>series.getQuote(last - 2)/series.movingAverage(5, last - 2) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last - 1)/series.movingAverage(5, last - 1)<series.getQuote(last - 2)/series.movingAverage(5, last - 2) - SMALL)
                        sig = -1;
                    break;
                case 18: //  Price / 5-period SMA went up two periods ago
                    if(series.getQuote(last - 2)/series.movingAverage(5, last - 2)>series.getQuote(last - 3)/series.movingAverage(5, last - 3) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last - 2)/series.movingAverage(5, last - 2)<series.getQuote(last - 3)/series.movingAverage(5, last - 3) - SMALL)
                        sig = -1;
                    break;
                case 19: //  Price / 10-period EMA went up this period
                    if(series.getQuote(last)/series.expMovingAverage(5, last)>series.getQuote(last - 1)/series.expMovingAverage(5, last - 1) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last)/series.expMovingAverage(5, last)<series.getQuote(last - 1)/series.expMovingAverage(5, last - 1) - SMALL)
                        sig = -1;
                    break;
                case 20: //  Price / 10-period EMA went up one period ago
                    if(series.getQuote(last - 1)/series.expMovingAverage(5, last - 1)>series.getQuote(last - 2)/series.expMovingAverage(5, last - 2) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last - 1)/series.expMovingAverage(5, last - 1)<series.getQuote(last - 2)/series.expMovingAverage(5, last - 2) - SMALL)
                        sig = -1;
                    break;
                case 21: //  Price / 10-period EMA went up two periods ago
                    if(series.getQuote(last - 2)/series.expMovingAverage(5, last - 2)>series.getQuote(last - 3)/series.expMovingAverage(5, last - 3) + SMALL)
                        sig = 1;
                    else if(series.getQuote(last - 2)/series.expMovingAverage(5, last - 2)<series.getQuote(last - 3)/series.expMovingAverage(5, last - 3) - SMALL)
                        sig = -1;
                    break;
                case 22: //  MACD
                    if((series.MACDSignal(last)>0.0 && series.MACDSignal(last - 1)<0.0)
                            || (series.MACD(last)>0.0 && series.MACD(last - 1)<0.0))
                        sig = 1;
                    else if((series.MACDSignal(last)<0.0 && series.MACDSignal(last - 1)>0.0)
                            || (series.MACD(last)<0.0 && series.MACD(last - 1)>0.0))
                        sig = -1;
                    break;
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            sig = 0;
        }
        return sig;
    }
    
    /**
     * Returns the prices of the simulation up to now.
     */
    public static Vector<Integer> prices()
    {
        return prices;
    }
    
    /**
     * Returns the volumes of the simulation up to now.
     */
    public static Vector<Integer> volumes()
    {
        return volumes;
    }
    
    /**
     * Test main.
     * 
     * @param args not used
     */
    public static void main(String[] args)
    {
        System.out.println("Testing the Market Simulator...");
        Trader[] pop = new Trader[100];
        
        for(int i = 0; i<100; i++)
            pop[i] = new RandomTrader("Trader #" + i, 100000, 1000);

        for(int t = 0; t<100; t++)
        {
            for(int i = 0; i<100; i++)
                pop[i].trade();
            step();
        }
    
        for(int i = 0; i<100; i++)
           System.out.println(pop[i]);
    }
}
