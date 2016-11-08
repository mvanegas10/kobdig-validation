/*
 * PropositionalInterpretation.java
 *
 * Created on April 3, 2008, 6:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An assignment of truth degrees to propositional atoms.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class PropositionalInterpretation extends Interpretation
{
    protected Map<PropositionalAtom,TruthDegree> truth;

    /** Creates a new propositional interpretation */
    public PropositionalInterpretation()
    {
        truth = new HashMap<PropositionalAtom,TruthDegree>();
    }
    
    /**
     * Assigns a truth degree to the given a propositional atom.
     */
    public void assign(PropositionalAtom a, TruthDegree t)
    {
        truth.put(a, t);
    }
    
    /**
     * Assigns a Boolean truth value to the given propositional atom.
     */
    public void assign(PropositionalAtom a, boolean t)
    {
        assign(a, new TruthDegree(t));
    }
    
    /**
     * Returns the truth degree assigned by the interpretation to
     * the given propositional atom. If the given atom is unknown
     * to the interpretation, the returned truth degree is 0.5,
     * i.e., neither true nor false.
     */
    public TruthDegree truth(Atom atom)
    {
        TruthDegree t = truth.get((PropositionalAtom) atom);
        if(t==null)
            t = TruthDegree.NEUTRAL;
        return t;
    }
    
    /**
     * Returns the set of all atomic propositions to which this interpretation
     * assigns truth values.
     */
    public Set<PropositionalAtom> atoms()
    {
        return truth.keySet();
    }

    /**
     * Returns the generalized Hamming distance between this interpretation
     * and the given interpretation.
     * The two interpretation must be defined on the same set of atoms.
     * 
     * @param interpretation another interpretation
     * @return the Hamming distance between the two interpretation
     */
    @Override
    public double distance(Interpretation interpretation)
    {
        double d = 0.0;
        PropositionalInterpretation that = (PropositionalInterpretation) interpretation;
        Set<PropositionalAtom> atoms = atoms();
        if(!atoms.equals(that.atoms()))
            throw new IllegalArgumentException("Cannot calculate distance between interpretations not on the same universe of discourse");
        Iterator<PropositionalAtom> i = atoms.iterator();
        while(i.hasNext())
        {
            Atom atom = i.next();
            d += truth(atom).distance(that.truth(atom));
        }
        return d;
    }

    /**
     * Returns a human-readable string representation of the
     * interpretation.
     * 
     * @return a string representation of the interpretation. 
     */    
    @Override
    public String toString()
    {
        String str = "(\n";
        Iterator<PropositionalAtom> i = truth.keySet().iterator();
        while(i.hasNext())
        {
            PropositionalAtom atom = i.next();
            str += "  " + atom + " --> " + truth.get(atom) + "\n";
        }
        return str + ")";
    }

    /**
     * Returns an iterator over all the crisp interpretations that can be
     * constructed on this propositional language.
     */
    @Override
    public Iterator<Interpretation> iterator()
    {
        return new PropositionalInterpretationIterator(this);
    }

    @Override
    public Formula minterm()
    {
        PropositionalFormula phi = null;
        Iterator<PropositionalAtom> i = truth.keySet().iterator();
        while(i.hasNext())
        {
            PropositionalAtom atom = i.next();
            PropositionalFormula literal = truth.get(atom).isTrue() ?
                    new PropositionalFormula(atom) :
                    new PropositionalFormula(Operator.NOT, new PropositionalFormula(atom));
            if(phi==null)
                phi = literal;
            else
                phi = new PropositionalFormula(Operator.AND, literal, phi);
        }
        return phi;
    }
}
