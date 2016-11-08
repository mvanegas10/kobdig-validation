/*
 * Atom.java
 *
 * Created on April 3, 2008, 2:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

/**
 * This class encapsulates the abstract notion of an atomic formula.
 * <p>Depending on the particular logical language, the atom may be
 * an atomic proposition (propositional logic),
 * a predicate (predicate logic),
 * an atomic concept (description logic), etc.</p>
 *
 * <p>It is stipulated that an atom whose name can be parsed as a
 * truth degree in [0, 1] has, by definition, the truth degree indicated
 * by its name in all interpretations.</p>
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Atom implements Comparable
{
    /** The name of the atom, i.e., its symbol or, in general, its textual representation. */
    protected String name;
    
    /** Creates a new atom. */
    public Atom(String n)
    {
        name = n;
    }

    /**
     * Indicates whether some other atom is "equal to" this one.
     * <p>Two atoms are considered equal if they have the same name.</p>
     */
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof Atom))
            return false;
        Atom that = (Atom) o;
        return name.equals(that.name);
    }
    
    /**
     * Use the hashcode of the name as hashcode of the atom.
     * Otherwise, the HashMap will not work.
     */
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
    
    /**
     * Returns a string representation of the atom.
     * In this general class, the string representation of an atom is its name.
     */
    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public int compareTo(Object o)
    {
        return name.compareTo(((Atom) o).name);
    }
    
    
}
