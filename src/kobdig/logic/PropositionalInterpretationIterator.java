/*
 * PropositionalInterpretationIterator.java
 *
 * Created on April 4, 2008, 6:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

import java.util.Set;

/**
 * An iterator over all possible crisp truth assignments to the atomic
 * propositions of a given propositional language.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class PropositionalInterpretationIterator extends InterpretationIterator
{
    /** A set of propositional atoms that make up the vocabulary of the interpretations. */
    protected PropositionalAtom[] atom;
    
    /** Truth values for all atoms. */
    protected boolean[] value;
    
    /** The size of the interpretations. */
    protected int size;
    
    /** True is there is another interpretation left. */
    protected boolean left;
    
    /** Creates a new instance of PropositionalInterpretationIterator */
    public PropositionalInterpretationIterator(PropositionalAtom[] a)
    {
        atom = a;
        size = atom.length;
        value = new boolean[size];
        for(int i = 0; i<size; i++)
            value[i] = false;
        left = true;
    }

    /** Creates a new instance of PropositionalInterpretationIterator */
    public PropositionalInterpretationIterator(PropositionalInterpretation itp)
    {
        this(itp.atoms().toArray(new PropositionalAtom[1]));
    }

    /**
     * Creates a PropositionalInterpretation iterator to iterate over
     * all propositional interpretations constructed on the provided
     * set of atomic propositions.
     * 
     * @param atoms a set of atomic propositions on which the interpretations
     *              will be constructed.
     */
    public PropositionalInterpretationIterator(Set<Atom> atoms)
    {
        this(atoms.toArray(new PropositionalAtom[1]));
    }

    /**
     * Returns <code>true</code> if there are more crisp interpretations to
     * visit.
     */
    @Override
    public boolean hasNext()
    {
        return left;
    }

    /**
     * Returns the next crisp interpretation.
     * 
     * @return the next crisp interpretation.
     */
    @Override
    public Interpretation next()
    {
        PropositionalInterpretation itp = new PropositionalInterpretation();
        for(int i = 0; i<size; i++)
        {
            itp.assign(atom[i], value[i]);
        }
        for(int i = 0; i<size; i++)
        {
            value[i] = !value[i];
            if(value[i])
                break;
            left = left && (i < size - 1);
        }
        return itp;
    }
    
}
