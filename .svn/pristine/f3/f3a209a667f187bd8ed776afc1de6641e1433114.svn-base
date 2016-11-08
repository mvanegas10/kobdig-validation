/*
 * FactBase.java
 *
 * Created on April 3, 2008, 12:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.agent;

import java.util.Iterator;
import java.util.Set;
import kobdig.Main;
import kobdig.logic.Atom;
import kobdig.logic.FuzzySet;
import kobdig.logic.Interpretation;
import kobdig.logic.PropositionalInterpretationIterator;
import kobdig.logic.TruthDegree;

/**
 * A fact base is a fact set which constitutes a deductive base under some
 * deductive systems.
 * <p>Unlike a fact set, a fact base provides methods to derive facts that are
 * logical consequences of the facts in the base and to check whether a given
 * fact is a logical consequence of the facts in the base.</p>
 *
 * @author Andrea G. B. Tettamanzi
 */
public class FactBase extends FactSet
{
    
    /** Creates a new empty fact base */
    public FactBase()
    {
        super();
    }
    
    /** Creates a new fact base from a fact set. */
    public FactBase(FactSet abox)
    {
        facts = new FuzzySet<Fact>(abox.facts);
    }
    
    /**
     * Calculates the degree to which the given fact is a logical consequence
     * of the fact base.
     * <p>A fact is a logical consequence of a fact base to the extent that
     * every interpretation that satisfies the fact base also satisfies the
     * given fact.</p>
     * <p>Therefore, the way this method checks whether the base <i>B</i>
     * models the provided fact &phi;, <i>B</i> |= &phi;, is to
     * iterate over all interpretations &omega;, and check to what
     * extent &omega; |= <i>B</i> &sup; &omega; |= &phi;.
     * The returned degree is the minimum, over all &omega;,
     * of the degrees of truth of
     * "&omega; |&ne; <i>B</i> &or; &omega; |= &phi;".</p>
     * 
     * @param fact a fact
     * @return the degree to which the given fact is a consequence of the fact base
     */
    public TruthDegree models(Fact fact)
    {
        // Increment the global entailment check counter:
        Main.entailmentChecks++;
        
        // 1. Construct an interpretation with all the atoms from the
        //    fact base and the fact as well
        Set<Atom> atoms = atomSet();
        atoms.addAll(fact.formula().atomSet());
        if(atoms.isEmpty())
            return TruthDegree.TRUE;
        
        // Iterator<Interpretation> itps = interpretation().iterator();
        Iterator<Interpretation> itps = new PropositionalInterpretationIterator(atoms);
        
        // 2. For all interpretations, test whether it is the case that,
        //    whenever the base is satisfied, so is the fact:
        TruthDegree t = TruthDegree.TRUE;
        while(itps.hasNext())
        {
            Interpretation itp = itps.next();
            TruthDegree sat = fact.formula.truth(itp);
            t = TruthDegree.tnorm(t, TruthDegree.snorm(truth(itp).negated(), sat));
            if(t.isFalse()) break;
        }
        return t;
    }    
}
