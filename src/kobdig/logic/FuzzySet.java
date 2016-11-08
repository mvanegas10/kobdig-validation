/*
 * FuzzySet.java
 *
 * Created on April 3, 2008, 12:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import kobdig.agent.Fact;

/**
 * A fuzzy set over a discrete universe of discourse. 
 *
 * @author Andrea G. B. Tettamanzi
 */
public class FuzzySet<E>
{
    /** The elements that make up the fuzzy set, physically stored in a HashSet. */
    protected Map<E, TruthDegree> elements;
    
    /** Creates a new fuzzy set */
    public FuzzySet()
    {
        elements = new HashMap<E, TruthDegree>();
    }

    /** Copy constructor. */
    public FuzzySet(FuzzySet<E> that)
    {
        elements = new HashMap<E, TruthDegree>();
        elements.putAll(that.elements);
    }
    
    /**
     * Returns the cardinality of the support of the fuzzy set, i.e.,
     * the number of elements whose degree of membership in the set
     * is greater than zero.
     * 
     * @return the cardinality of the support of the fuzzy set. 
     */
    public int size()
    {
        return elements.size();
    }

    /**
     * Returns the membership degree of the given element.
     */
    public TruthDegree member(E element)
    {
        TruthDegree membership = elements.get(element);
        if(membership==null)
            membership = TruthDegree.FALSE;
        return membership;
    }
    
    /**
     * Sets the membership degree of the given element.
     */
    public void member(E element, TruthDegree membership)
    {
        if(membership.isFalse())
            elements.remove(element);
        else
            elements.put(element, membership);
    }
    
    /**
     * Returns an iterator on the elements of the fuzzy set.
     */
    public Iterator<E> iterator()
    {
        return elements.keySet().iterator();
    }
    
    /**
     * Returns the level set of the fuzzy set, i.e.,
     * the set of truth values used in the set.
     */
    public SortedSet<TruthDegree> levelSet()
    {
        return new TreeSet<TruthDegree>(elements.values());
    }
    
    /**
     * Checks whether this fuzzy set equals another fuzzy set.
     * Two fuzzy sets are equal if and only if their membership
     * degrees are equal for all elements.
     * 
     * @param o an object
     * @return true if o is a fuzzy set identical to this one
     */
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof FuzzySet))
            return false;
        FuzzySet that = (FuzzySet) o;
        return elements.equals(that.elements);
    }

    /**
     * Returns a hash code for this fuzzy set.
     * 
     * @return the hash code for this fuzzy set.
     */
    @Override
    public int hashCode()
    {
        return this.elements.hashCode();
    }

    /**
     * Return an &alpha;-cut of this fuzzy set.
     * 
     * @param alpha the value of &alpha;
     * @return the &alpha;-cut of this fuzzy set
     */
    public FuzzySet<E> cut(TruthDegree alpha)
    {
        FuzzySet<E> aCut = new FuzzySet<E>();
        Iterator<E> i = iterator();
        while(i.hasNext())
        {
            E element = i.next();
            if(member(element).compareTo(alpha)>=0)
                aCut.member(element, TruthDegree.TRUE);
        }
        return aCut;
    }
}
