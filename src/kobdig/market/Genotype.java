/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

import java.util.Random;

/**
 * A genotype encoding inherited traits for evolutionary traders.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Genotype
{
    /** The length of the genotype. */
    public static final int NUM_GENES = 33;
    
    /** A random number generator for genetic operators. */
    protected static Random rnd = new Random();
    
    /** The genes. */
    protected short[] gene = new short[NUM_GENES];
    
    /**
     * Creates a new random genotype.
     */
    public Genotype()
    {
        for(int i = 0; i<gene.length; i++)
            gene[i] = (short) rnd.nextInt(256);
    }
    
    /**
     * Copy constructor
     */
    public Genotype(Genotype that)
    {
        for(int i = 0; i<NUM_GENES; i++)
            gene[i] = that.gene[i];
    }

    /**
     * Crossover constructor.
     * Creates a new genotype as a recombination of two given genotypes.
     * The recombination operator used is uniform crossover.
     */
    public Genotype(Genotype mom, Genotype dad)
    {
        for(int i = 0; i<NUM_GENES; i++)
            if(rnd.nextBoolean())
                gene[i] = mom.gene[i];
            else
                gene[i] = dad.gene[i];
    }
    
    /**
     * Returns the value of the <var>i</var>th gene,
     * a rational number in [0, 1].
     */
    public double gene(int i)
    {
        return ((double) gene[i])/255.0;
    }
    
    /**
     * Randomly perturbs the genotype with the given mutation rate.
     */
    public void mutate(double rate)
    {
        for(int i = 0; i<NUM_GENES; i++)
            for(int b = 1; b<256; b *= 2)
                if(rnd.nextDouble()<rate)
                    gene[i] ^= b;
    }
    
    /**
     * Returns a string representation of the genotype.
     */
    @Override
    public String toString()
    {
        String s = "[";
        for(int i = 0; i<NUM_GENES; i++)
        {
            s += " " + gene[i];
            if(i<NUM_GENES - 1)
                s += ",";
        }
        return s + "]";
    }
}
