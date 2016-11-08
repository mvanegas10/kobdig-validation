/*
 * PossibilisticFactBase.java
 *
 * Created on May 19, 2012, 01:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.agent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import kobdig.logic.FuzzySet;
import kobdig.logic.TruthDegree;

/**
 * A possibilistic fact base is a fact set, regarded as a necessity-based
 * fact set, which is a syntactic representation of a possibility distribution.
 * <p>Unlike a fact set, a possibilistic fact base provides methods to derive facts that are
 * logical consequences of the facts in the base and to check whether a given
 * fact is a logical consequence of the facts in the base.</p>
 *
 * @author Andrea G. B. Tettamanzi
 */
public class PossibilisticFactBase extends FactBase
{
    
    /** Creates a new empty possibilistic fact base */
    public PossibilisticFactBase()
    {
        super();
    }
    
    /** Creates a new possibilistic fact base from a fact set. */
    public PossibilisticFactBase(FactSet abox)
    {
        super(abox);
    }
    
    /**
     * Returns the necessity degree for the given fact according to
     * the fact base.
     * <p>The necessity degree of a fact is the greatest truth degree
     * &alpha; such that the &alpha;-cut of the base entails the fact.</p>
     * 
     * @param fact a fact
     * @return the necessity degree of the given fact
     */
    public TruthDegree necessity(Fact fact)
    {
        // first of all, handle constant-truth formulas correctly:
        if(fact.formula().isConstant())
            return new TruthDegree(Double.parseDouble(fact.formula().toString()));
        
        Iterator<TruthDegree> i = levelSet().iterator();
        TruthDegree t = TruthDegree.FALSE;
        while(i.hasNext())
        {
            TruthDegree alpha = i.next();
            FactBase alphaCut = new FactBase(cut(alpha));
            if(alphaCut.models(fact).isTrue())
                t = alpha;
            else break;
        }
        // try also alpha = 1:
        if(models(fact).isTrue())
            t = TruthDegree.TRUE;
        return t;
    }
    
    /**
     * Returns the possibility degree for the given fact according to
     * the fact base.
     * <p>A fact is possible to the extent that its negation is not
     * necessary</p>
     * 
     * @param fact a fact
     * @return the possibility degree of the given fact
     */
    public TruthDegree possibility(Fact fact)
    {
        // first of all, handle constant-truth formulas correctly:
        if(fact.formula().isConstant())
            return new TruthDegree(Double.parseDouble(fact.formula().toString()));
        
        TruthDegree t = TruthDegree.FALSE;
        return necessity(fact.negated()).negated();
    }
    
    /**
     * Put the base in normal form, by removing redundant facts that are
     * a logical consequence of the other facts in the base.
     * <p>The algorithm is the following: for each fact in the base,
     * try removing it from the base and check whether it may still
     * inferred. If that's the case, drop it, and begin all over,
     * until no fact may be removed.</p>
     */
    void simplify()
    {
        // First of all, prepare a list of facts in the base:
        List<Fact> factList = new LinkedList<Fact>();
        Iterator<Fact> i = factIterator();
        while(i.hasNext()) factList.add(i.next());
        
        // Now, use the list of facts to check them one by one:
        i = factList.listIterator();
        while(i.hasNext())
        {
            Fact fact = i.next();
            TruthDegree t = membership(fact);
            untell(fact);
            if(necessity(fact).isAtLeastAsTrueAs(t))
            {
                // The fact is a logical consequence of the rest of the base
                // and may be safely dropped.
                // Now, we get a smaller base, which must be checked again:
                simplify();
                break;
            }
            // The fact is not a logical consequence and should be kept:
            tell(fact, t);
        }
    }
}
