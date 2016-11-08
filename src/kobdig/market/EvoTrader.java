/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

import java.util.Iterator;

/**
 * An evolutionary trader, whose trading decisions are motivated by
 * genetically inherited traits.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class EvoTrader extends Trader implements Comparable<EvoTrader>
{
    public static final int gb1 = 23;
    public static final int gb2 = 24;
    public static final int gb3 = 25;
    public static final int gb4 = 26;
    public static final int gb5 = 27;
    public static final int ga1 = 28;
    public static final int ga2 = 29;
    public static final int ga3 = 30;
    public static final int ga4 = 31;
    public static final int ga5 = 32;
    
    /** The trader's genotype. */
    protected Genotype genotype;
    
    /**
     * The trader's sentiment. A sentiment of 1 is definitely <em>bullish</em>,
     * while a sentiment of -1 is definitely <em>bearish</em>; a value of
     * zero corresponds to a neutral sentiment. 
     */
    double sentiment;
    
    /** The last bid price concession used by the agent. */
    double bidConcession;
    
    /** The last ask price concession used by the agent. */
    double askConcession;
    
    /**
     * Creates a new evolutionary trader with the given unique identifier,
     * initial cash and asset balance, and genotype.
     */
    public EvoTrader(String id, int c, int a, Genotype g)
    {
        super(id, c, a);
        genotype = g;
        // The initial sentiment is genetically determined:
        sentiment = 2.0*genotype.gene(0) - 1.0;
    }
    
    /**
     * Recombination constructor.
     */
    public EvoTrader(EvoTrader mom, EvoTrader dad)
    {
        super(uniqueId(), 0, 0);
        Market.log.println(name + " = " + mom.name + " * " + dad.name);
        genotype = new Genotype(mom.genotype, dad.genotype);
        genotype.mutate(0.0038);
        long mc = mom.cash/4;
        long ma = mom.asset/4;
        long dc = dad.cash/4;
        long da = dad.asset/4;
        mom.cash(-mc);
        mom.asset(-ma);
        dad.cash(-dc);
        dad.asset(-da);
        cash = mc + dc;
        asset = ma + da;
        sentiment = 2.0*genotype.gene(0) - 1.0;
    }

    /**
     * Update the agent's sentiment.
     * The new sentiment is based on an average of the values of
     * the technical indicators, weighted by the genetically determined
     * degrees to which the agents trusts them.
     * A kind of mental inertia is accounted for by taking the previous
     * mental state as a term of the average.
     */
    private void updateSentiment()
    {
        double sum = sentiment;
        double totalWeight = 1.0;
        
        for(int i = 1; i<=22; i++)
        {
            double signal = (double) Market.signal(i);
            sum += signal*genotype.gene(i);
            totalWeight += genotype.gene(i);
        }
        sentiment = sum/totalWeight;
        if(sentiment<-1.0 || sentiment>1.0)
            throw new RuntimeException("sentiment = " + sentiment);
    }

    /**
     * Take action based on the current sentiment and the inherited
     * traits of the agent.
     * The genes involved are the following:
     * <ol>
     *   <li value="23">gb1 Fraction of its available cash the
     *     agent is willing to invest at each period;</li>
     *   <li value="24">gb2 Minimum threshold for asset;</li>
     *   <li value="25">gb3 Incentive to buy if asset below threshold;</li>
     *   <li value="26">gb4 Parameter for price concession</li>
     *   <li value="26">gb5 Parameter for price concession adaptation</li>
     *   <li value="28">ga1 Fraction of available asset the agent is willing to
     *     divest at each period</li>
     *   <li value="29">ga2 Maximum threshold for asset</li>
     *   <li value="30">ga3 Incentive to sell if asset above threshold</li>
     *   <li value="31">ga4 Parameter for price concession</li>
     *   <li value="32">ga5 Parameter for price concession adaptation</li>
     * </ol>
     */
    @Override
    public void trade()
    {
        updateSentiment();
        
        // Check if there are unfilled orders:
        boolean firstTimeBid = true;
        boolean firstTimeAsk = true;
        Iterator<Order> i = orders.iterator();
        while(i.hasNext())
        {
            Order o = i.next();
            if(o.sign()==Order.BUY)
                firstTimeBid = false;
            else
                firstTimeAsk = false;
            cancel(o);
        }
        if(!orders.isEmpty())
            throw new RuntimeException("Orders not cancelled!");
        if(resAsset!=0 || resCash!=0)
            throw new RuntimeException("resAsset = " + resAsset + ", resCash = " + resCash);
        
        if(sentiment>0.0)
        {
            // buy
            double bidValue = cash*genotype.gene(gb1);
            if(asset*Market.instrument("XYZ").price() < genotype.gene(gb2)*netAssetValue())
                bidValue += cash*(1.0 - genotype.gene(gb1))*genotype.gene(gb3)*sentiment;
            if(firstTimeBid)
                bidConcession = sentiment + genotype.gene(gb4);
            else
                bidConcession += genotype.gene(gb5);
            int price = (int) Math.round(Market.instrument("XYZ").price()*bidConcession);
            if(price>1073741824) // 2^30
                price = 1073741824;
            if(price<1)
                price = 1;
            int qty = (int) Math.floor(bidValue/price);
            if(qty>0)
                buy(qty, price);
        }
        else if(sentiment<0.0)
        {
            // sell
            double quantity = asset*genotype.gene(ga1);
            if(asset*Market.instrument("XYZ").price() > genotype.gene(ga2)*netAssetValue())
                // sentiment<0, therefore -= actually becomes a += !
                quantity -= asset*(1.0 - genotype.gene(ga1))*genotype.gene(ga3)*sentiment;
            if(firstTimeAsk)
                askConcession = genotype.gene(ga4) - sentiment;
            else
                askConcession += genotype.gene(ga5);
            int price = (int) Math.round(Market.instrument("XYZ").price()/askConcession);
            int qty = (int) quantity;
            if(qty>0)
               sell(qty, price);
        }
    }

    @Override
    public int compareTo(EvoTrader that)
    {
        int difference = (int) (netAssetValue() - that.netAssetValue());
        return difference;
    }
    
    @Override
    public String toString()
    {
        String s = super.toString();
        return s + "; genotype: " + genotype;
    }
}
