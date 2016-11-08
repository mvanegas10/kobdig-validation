/*
 * PossibilityDistribution.java
 *
 * Created on May 20, 2012, 12:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A possibility distribution over all interpretations
 * of a given propositional language.
 * <p>A possibility distribution is a mapping
 * &pi : &Omega; &rarr; [0, 1], where &Omega; is the
 * set of all possible worlds, i.e., interpretations, &omega;.</p>
 * <p>Given a possible world &omega; &isin; &Omega;,
 * &pi;(&omega;) &isin; [0, 1] is the degree to which world &omega;
 * is possible: &pi;(&omega;) = 0 means world &omega; is outright
 * impossible, &pi;(&omega;) = 1 means world &omega; is completely
 * possible, and values in between mean world &omega; is not impossible,
 * but less likely than a completely possible world.</p>
 *
 * @author Andrea G. B. Tettamanzi
 */
public class PossibilityDistribution
{
    /** The set of propositional atoms that make up the propositional language. */
    protected PropositionalAtom[] atom;
    
    /** Degrees of possibility for each interpretation. */
    protected TruthDegree[] possibility;
    
    /**
     * Creates an empty possibility distribution.
     */
    public PossibilityDistribution()
    {
        atom = null;
        possibility = null;
    }
    
    /**
     * Creates a possibility distribution on the provided
     * set of atomic propositions, initialized with all
     * interpretations possible to the degree supplied.
     * 
     * @param atoms a set of atomic propositions
     */
    public PossibilityDistribution(Set<Atom> atoms, TruthDegree t)
    {
        atom = atoms.toArray(new PropositionalAtom[1]);
        if(atom.length>31) throw new UnsupportedOperationException("Propositional language too large");
        possibility = new TruthDegree[1 << atom.length];
        for(int w = 0; w<possibility.length; possibility[w++] = t);
    }
    
    /**
     * Creates a possibility distribution on the provided
     * set of atomic propositions, initialized with all
     * interpretations fully possible.
     * 
     * @param atoms a set of atomic propositions
     */
    public PossibilityDistribution(Set<Atom> atoms)
    {
        this(atoms, TruthDegree.TRUE);
    }
    
    /**
     * Returns the image of the possibility distribution, i.e.,
     * the set of possibility degrees to which the interpretations
     * are mapped.
     * 
     * @return a sorted set of truth degrees
     */
    public SortedSet<TruthDegree> levelSet()
    {
        TreeSet<TruthDegree> set = new TreeSet<TruthDegree>();
        for(int w = 0; w<possibility.length; set.add(possibility[w++]));
        return set;
    }
    
    /**
     * Returns an iterator over the interpretations of this
     * possibility distribution.
     * 
     * @return an iterator over the interpretations of this distribution
     */
    public Iterator<Interpretation> interpretations()
    {
        return new PropositionalInterpretationIterator(atom);
    }
    
    /**
     * Returns the index in the possibility distribution array of
     * the given propositional interpretation.
     * 
     * @param itp an interpretation
     * @return its index in the array used internally to store possibility degrees
     */
    protected int index(PropositionalInterpretation itp)
    {
        int w = 0;
        for(int i = 0; i<atom.length; i++)
            if(itp.truth(atom[i]).isTrue())
                w |= 1 << i;
        return w;
    }

    /**
     * Return the possibility degree of the given interpretation.
     * 
     * @param itp an interpretation.
     * @return the possibility degree of the given interpretation.
     */
    public TruthDegree possibility(Interpretation itp)
    {
        if(!(itp instanceof PropositionalInterpretation))
            throw new IllegalArgumentException("Propositional interpretation required");
        
        PropositionalInterpretation interpretation = (PropositionalInterpretation) itp;
        return(possibility[index(interpretation)]);
    }
    
    /**
     * Set the possibility degree of the given interpretation as indicated.
     * 
     * @param itp an interpretation
     * @param t the new possibility degree of the given interpretation
     */
    public void possibility(Interpretation itp, TruthDegree t)
    {
        if(!(itp instanceof PropositionalInterpretation))
            throw new IllegalArgumentException("Propositional interpretation required");
        
        PropositionalInterpretation interpretation = (PropositionalInterpretation) itp;
        possibility[index(interpretation)] = t;
    }
    
    /**
     * Compute the possibility degree of a formula according to the distribution.
     * <p>Given a possibility distribution &pi;,
     * the possibility measure &Pi; of a formula &phi;
     * is defined as
     * &Pi; = max<sub>&omega; |= &phi;</sub>&pi;(&omega;).</p>
     * 
     * @param f a propositional formula
     * @return its possibility measure
     */
    public TruthDegree possibility(Formula formula)
    {        
        if(!(formula instanceof PropositionalFormula))
            throw new IllegalArgumentException("Propositional formula required");
        
        PropositionalFormula f = (PropositionalFormula) formula;

        // 1. Construct an interpretation with all the atoms from the
        //    propositional language
        Iterator<Interpretation> itps =
                new PropositionalInterpretationIterator(atom);
        
        // 2. Compute the maximumm of the possibility of the models of f:
        TruthDegree t = TruthDegree.FALSE;
        while(itps.hasNext() && !t.isTrue())
        {
            Interpretation itp = itps.next();
            if(f.truth(itp).isTrue())
                t = TruthDegree.snorm(t, possibility(itp));
        }
        return t;
    }
    
    /**
     * Compute the necessity degree of a formula according to the distribution.
     * <p>Given a possibility distribution &pi;,
     * the necessity measure <i>N</i> of a formula &phi;
     * is defined as
     * <i>N</i> = 1 - max<sub>&omega; |&ne; &phi;</sub>&pi;(&omega;).</p>
     * 
     * @param f a propositional formula
     * @return its necessity measure
     */
    public TruthDegree necessity(Formula formula)
    {        
        if(!(formula instanceof PropositionalFormula))
            throw new IllegalArgumentException("Propositional formula required");
        
        PropositionalFormula f = (PropositionalFormula) formula;

        // 1. Construct an interpretation with all the atoms from the
        //    propositional language
        Iterator<Interpretation> itps =
                new PropositionalInterpretationIterator(atom);
        
        // 2. Compute the maximum possibility of the countermodels of f:
        TruthDegree t = TruthDegree.FALSE;
        while(itps.hasNext() && !t.isTrue())
        {
            Interpretation itp = itps.next();
            if(f.truth(itp).isFalse())
                t = TruthDegree.snorm(t, possibility(itp));
        }
        
        // 3. Return 1 - max possibility of countermodels:
        return t.negated();
    }
    
    /**
     * Compute the guaranteed possibility degree of a formula
     * according to the distribution.
     * <p>Given a possibility distribution &pi;,
     * the guaranteed possibility measure &Delta; of a formula &phi;
     * is defined as
     * &Delta(&phi;) = min<sub>&omega; |= &phi;</sub>&pi;(&omega;).</p>
     * 
     * @param f a propositional formula
     * @return its guaranteed possibility measure
     */
    public TruthDegree guaranteedPossibility(Formula formula)
    {        
        if(!(formula instanceof PropositionalFormula))
            throw new IllegalArgumentException("Propositional formula required");
        
        PropositionalFormula f = (PropositionalFormula) formula;

        // 1. Construct an interpretation with all the atoms from the
        //    propositional language
        Iterator<Interpretation> itps =
                new PropositionalInterpretationIterator(atom);
        
        // 2. Compute the minimumm of the possibility of the models of f:
        TruthDegree t = TruthDegree.TRUE;
        while(itps.hasNext() && !t.isFalse())
        {
            Interpretation itp = itps.next();
            if(f.truth(itp).isTrue())
                t = TruthDegree.tnorm(t, possibility(itp));
        }
        return t;
    }
    
    /**
     * Returns a human-readable string representation of the
     * possibility distribution.
     * 
     * @return a string representation of the possibility distribution. 
     */    
    @Override
    public String toString()
    {
        String str = "{\n";
        Iterator<Interpretation> itps =
                new PropositionalInterpretationIterator(atom);
        
        while(itps.hasNext())
        {
            Interpretation itp = itps.next();
            int n = index((PropositionalInterpretation) itp);
            str += "World #" + n + " = " + itp + ",\tu(" +
                    n + ") = " + possibility(itp) + "\n";
        }
        return str;
    }
}
