/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * A market simulation using evolving traders.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Simulation
{
    /** A population of evolutionary traders. */
    protected List<EvoTrader> pop;
    
    /** Random Number Generator. */
    protected static Random rnd = new Random();

    /**
     * Creates a new simulation with the given number of traders.
     */
    public Simulation(int size)
    {
        pop = new Vector<EvoTrader>(size);
        for(int i = 0; i<size; i++)
            pop.add(new EvoTrader(Trader.uniqueId(), 100000, 1000, new Genotype()));
    }

    /**
     * Let the agents in the simulation perform their actions.
     */
    public void act()
    {
        Iterator<EvoTrader> i = pop.iterator();
        while(i.hasNext())
            i.next().trade();
    }
    
    /**
     * Select an individual proportionately to fitness.
     * 
     * @param pool
     * @param cumulativeFitness
     * @return
     */
    protected EvoTrader select(List<EvoTrader> pool)
    {
        Iterator<EvoTrader> i = pop.iterator();
        long cumulativeNAV = 0;
        while(i.hasNext())
            cumulativeNAV += i.next().netAssetValue();
        
        long n = rnd.nextLong() % cumulativeNAV;
        long sum = 0;
        i = pool.iterator();
        while(i.hasNext())
        {
            EvoTrader t = i.next();
            sum += t.netAssetValue();
            if(sum>=n)
                return t;
        }
        // This statement should never be reached:
        return null;
    }
    
    /**
     * Performs a generation of the evolutionary algorithm.
     */
    public void generation()
    {
        long cumulativeNAV = 0;
        // Cash and asset for redistribution:
        long cash = 0;
        long asset = 0;
        
        Collections.sort(pop);
        
        // The worst 30% of the population becomes extinct:
        int worst30pct = 3*pop.size()/10;
        for(int i = 0; i<worst30pct; i++)
        {
            EvoTrader t = pop.remove(0);
            t.cancelAll();
            asset += t.asset();
            cash += t.cash();
        }
        
        Iterator<EvoTrader> i = pop.iterator();
        while(i.hasNext())
            cumulativeNAV += i.next().netAssetValue();
        
        i = pop.iterator();
        int assignedCash = 0;
        int assignedAsset = 0;
        while(i.hasNext())
        {
            EvoTrader t = i.next();
            
            int c = (int) (cash*t.netAssetValue()/cumulativeNAV);
            t.cash(c);
            assignedCash += c;
            
            int a = (int) (asset*t.netAssetValue()/cumulativeNAV);
            t.asset(a);
            assignedAsset += a;
        }
        // Assign the rest to the best individual:
        pop.get(pop.size() - 1).cash(cash - assignedCash);
        pop.get(pop.size() - 1).asset(asset - assignedAsset);
        
        List<EvoTrader> offspring = new LinkedList<EvoTrader>();
        for(int count = 0; count<worst30pct; count++)
        {
            EvoTrader mom, dad, baby;
            
            mom = select(pop);
            do dad = select(pop);
            while(dad==mom);
            baby = new EvoTrader(mom, dad);
            offspring.add(baby);
        }
        
        pop.addAll(offspring);
    }
    
    public void histogram(PrintStream out)
    {
        Collections.sort(pop);
        
        Iterator<EvoTrader> i = pop.iterator();
        while(i.hasNext())
            out.println(i.next().netAssetValue());
    }
    
    @Override
    public String toString()
    {
        String s = "Population {\n";
        Iterator<EvoTrader> i = pop.iterator();
        while(i.hasNext())
            s += "\t" + i.next() + "\n";
        return s + "}";
    }
}
