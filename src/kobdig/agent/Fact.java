/*
 * Fact.java
 *
 * Created on April 3, 2008, 11:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.agent;

import kobdig.logic.Formula;

/**
 * A fact encapsulates and generalizes the concept of proposition, relation,
 * formula, or anything that can be predicated about reality or the world.
 * Facts contrast with {@link Rule rules}.
 *
 * @see FactSet
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Fact
{
    /** The formula that represents this fact. */
    Formula formula;
    
    /** The true fact. */
    public static final Fact TRUE = new Fact(Formula.T);
    
    /** Creates a new instance of Fact */
    public Fact(Formula f)
    {
        formula = f;
    }
    
    /**
     * Indicates whether some other fact is "equal to" this one.
     * <p>Two facts are considered equal if they are syntactically
     * identical. For example, in the case of facts represented by
     * propositional logic formulas, <var>p</var> and
     * &not;&not;<var>p</var> are different facts, although logically
     * equivalent. An alternative option would be to consider them
     * equal if they are logically equivalent; however, that would
     * be computationally more intensive. The choice has been made
     * to emphasize efficiency.</p>
     */
    @Override
    public boolean equals(Object that)
    {
        if(!(that instanceof Fact))
            return false;
        return formula.equals(((Fact) that).formula);
    }
    
    /**
     * Returns the hash code of the formula representing this fact.
     * This is consistent with the way <code>equals</code> is
     * implemented.
     * 
     * @return the hash code for this fact
     */
    @Override
    public int hashCode()
    {
        return formula.hashCode();
    }
    
    /**
     * Returns the formula which represents this fact.
     */
    public Formula formula()
    {
        return formula;
    }
    
    /**
     * Returns the negation of this fact.
     */
    public Fact negated()
    {
        return new Fact(formula.negated());
    }
}
