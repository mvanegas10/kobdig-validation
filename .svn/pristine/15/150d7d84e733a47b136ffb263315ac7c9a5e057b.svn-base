/*
 * Interpretation.java
 *
 * Created on April 3, 2008, 6:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

import java.util.Iterator;

/**
 * This class encapsulates the abstact notion of an interpretation.
 *
 * @author Andrea G. B. Tettamanzi
 */
public abstract class Interpretation
{
    /**
     * Returns the degree of truth of the given formula under this interpretation.
     */
    public TruthDegree truth(Formula f)
    {
        if(f instanceof PropositionalFormula)
            return ((PropositionalFormula) f).truth(this);
        return f.truth(this);
    }
    
    /**
     * Returns an iterator over all possible crisp interpretations with the
     * same logical constructs as this one.
     */
    public abstract Iterator<Interpretation> iterator();
    
    /**
     * Returns the generalized Hamming distance between this interpretation
     * and the given interpretation.
     * The two interpretations must be defined on the same set of atoms.
     * 
     * @param that another interpretation
     * @return the Hamming distance between the two interpretations
     */
    public abstract double distance(Interpretation that);
    
    /**
     * Returns the minterm of this interpretation.
     * 
     * @return a formula that is satisfied only by this interpretation. 
     */
    public abstract Formula minterm();
}
