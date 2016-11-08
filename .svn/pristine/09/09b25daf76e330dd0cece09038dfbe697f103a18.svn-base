/*
 * FactSet.java
 *
 * Created on April 3, 2008, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.agent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import kobdig.logic.Atom;
import kobdig.logic.Formula;
import kobdig.logic.Interpretation;
import kobdig.logic.PropositionalAtom;
import kobdig.logic.PropositionalFormula;
import kobdig.logic.PropositionalInterpretation;
import kobdig.logic.TruthDegree;
import kobdig.logic.FuzzySet;
import kobdig.logic.Operator;

/**
 * A set of facts, represented by formulas of some suitable logic language.
 * A fact set may or may not be consistent.
 * <p>The semantics, or modality, of the facts is implicit and depens on the
 * context in which a fact set is used. For example, is a fact set is
 * intended to be a "desire set", the facts it contains will have to be construed
 * as "facts the agent would like to be true", i.e., <em>desires</em>; if,
 * on the other hand, the fact set is taken to be a "knowledge set", the facts
 * it ontains are understood to be true, i.e., <em>knowledge</em> items.</p>
 *
 * @see Fact
 *
 * @author Andrea G. B. Tettamanzi
 */
public class FactSet
{
    /** The fuzzy set of facts. */
    protected FuzzySet<Fact> facts;
    
    /** Creates a new empty set of facts */
    public FactSet()
    {
        facts = new FuzzySet<Fact>();
    }
    
    /** Creates a new fact set from a given fuzzy set of facts */
    protected FactSet(FuzzySet<Fact> fs)
    {
        facts = fs;
    }
    
    /**
     * Returns the cardinality of the support of the set.
     * 
     * @return the number of facts whose membership is greater than zero. 
     */
    public int size()
    {
        return facts.size();
    }
    
    /**
     * Parse the facts of this fact set from an agent program source file.
     */
    public void parse(AplTokenizer source) throws IOException
    {
        source.nextToken();
        source.require('{');
        
        while(source.nextToken()!='}')
        {
            PropositionalFormula phi = new PropositionalFormula(source);
            TruthDegree t = TruthDegree.TRUE;
            if(source.ttype==':')
            {
                source.nextToken();
                source.requireNumber("membership degree in [0, 1]");
                t = new TruthDegree(source.nval);
                source.nextToken();
            }
            // System.out.println("Parsed formula = " + phi);
            tell(new Fact(phi), t);
            if(source.ttype!=',')
                break;
        }
        source.require('}');
    }
    
    /**
     * Return the level set of this fact set, i.e., an ordered set
     * of truth degrees that are used in the set.
     */
    public SortedSet<TruthDegree> levelSet()
    {
        return facts.levelSet();
    }
    
    /**
     * Return an &alpha;-cut of this fact set.
     */
    public FactSet cut(TruthDegree alpha)
    {
        return new FactSet(facts.cut(alpha));
    }
    
    /**
     * Returns a propositional interpretation containing all the atomic symbols occurring
     * in the formulas of this fact set. The returned interpretation sets
     * the truth of all atoms that occur in a literal formula according to
     * the membership of the relevant literal, and of all the remaining
     * atoms to <code>0.5</code>.
     */
    public PropositionalInterpretation interpretation()
    {
        PropositionalInterpretation itp = new PropositionalInterpretation();
        Iterator<Atom> i = atomSet().iterator();
        while(i.hasNext())
        {
            PropositionalAtom atom = (PropositionalAtom) i.next();
            PropositionalFormula positive = new PropositionalFormula(atom);
            PropositionalFormula negative = new PropositionalFormula(Operator.NOT, positive);
            TruthDegree mupos = membership(new Fact(positive));
            TruthDegree muneg = membership(new Fact(negative)).negated();
            if((mupos.isFalse() && muneg.isTrue()) || (!mupos.isFalse() && !muneg.isTrue()))
            {
                double sum = mupos.doubleValue() + muneg.doubleValue();
                itp.assign(atom, new TruthDegree(0.5*sum));
            }
            else if(mupos.isFalse())
                itp.assign(atom, muneg);
            else
                itp.assign(atom, mupos);
        }
        return itp;
    }
    
    /**
     * Returns the propositional interpretation which satisfies this fact set
     * to the maximum degree (ideally 1.0). The returned interpretation contains
     * all the atomic symbols occurring in the formulas of this fact set and
     * sets their truth degree in such a way as to maximize the degree of
     * satisfaction of the fact set.
     *
     * Finding such an interpretation requires solving an optimization problem
     * which, in general, can be very hard.
     * The provisional implementation of this method performs an exhaustive search
     * of all fuzzy interpretations with truth degrees that are multiples of 0.1.
     * While this has a complexity of 10<sup><var>n</var></sup>, where <var>n</var>
     * is the number of atomic symbols occurring in the fact set, i.e., the
     * cardinality of the interpretation, it guarantees to return an approximated
     * solution within 0.05 from the real one.
     */
    public PropositionalInterpretation satisfyingInterpretation()
    {
        final double INCREMENT = 0.1;
        PropositionalInterpretation itp = new PropositionalInterpretation();
        Atom[] atoms = atomSet().toArray(new Atom[1]);
        double[] t = new double[atoms.length];
        double[] best = new double[atoms.length];
        for(int i = 0; i<t.length; i++)
            best[i] = t[i] = 0.0;
        TruthDegree maxsat = TruthDegree.FALSE;
        boolean done = false;
        do
        {
            // Assign the degrees t to the atoms:
            for(int i = 0; i<atoms.length; i++)
                itp.assign((PropositionalAtom) atoms[i], new TruthDegree(t[i]));
            TruthDegree sat = truth(itp);
            
            // Check whether there is an improvement and, if so, update the best so far:
            if(sat.compareTo(maxsat)>0)
            {
                for(int i = 0; i<atoms.length; i++)
                    best[i] = t[i];
                maxsat = sat;
            }
            
            // Consider the next assignment:
            for(int i = 0; i<t.length; i++)
            {
                t[i] += INCREMENT;
                done = t[i]>1.0;
                if(!done) break;
                else t[i] = 0.0;
            }
        }
        while(!done);
        
        // Assign the solution degrees t to the atoms:
        for(int i = 0; i<atoms.length; i++)
            itp.assign((PropositionalAtom) atoms[i], new TruthDegree(best[i]));
        
        return itp;
    }
    
    /**
     * Checks the degree to which this fact set is logically consistent.
     * <p>A classical fact set is consistent if it does not contain contradictions, i.e.,
     * if there exists an interpretation that satisfies it.</p>
     * <p>The consistency of a fuzzy fact set is the maximum degree to which an
     * interpretation satisfies it.</p>
     * <p>Instead of checking all infinite fuzzy interpretations, this method
     * uses all crisp interpretations, which are a finite number. Of course,
     * the maximum degree of satisfaction over all crisp interpretations is
     * but a lower bound on the consistency of the fact set.</p>
     *
     * @return the degree to which the facts contained in the fact set are logically
     *         consistent.
     */
    public TruthDegree consistency()
    {
        TruthDegree t = TruthDegree.FALSE;
        Iterator<Interpretation> itps = interpretation().iterator();
        while(itps.hasNext() && !t.isTrue())
        {
            Interpretation j = itps.next();
            t = TruthDegree.snorm(t, truth(j));
            if(t.isTrue())
                break;  // No need to contine...
        }
        return t;
    }
    
    /**
     * Add a new fact to the fact set with membership 1.
     *
     * @param newFact the new fact that has to be added.
     */
    public void tell(Fact newFact)
    {
        facts.member(newFact, TruthDegree.TRUE);
    }
    
    /**
     * Add a new fact to the fact set with the given membership.
     *
     * @param newFact the new fact that has to be added.
     * @param mu the degree of membership of the new fact.
     */
    public void tell(Fact newFact, TruthDegree mu)
    {
        facts.member(newFact, mu);
    }
    
    /**
     * Removes completely a fact from the fact set.
     * If the fact does not belong to the fact set, this method has no effect.
     *
     * @param fact the fact that has to be removed.
     */
    public void untell(Fact fact)
    {
        facts.member(fact, TruthDegree.FALSE);
    }
    
    /**
     * Returns the degree to which this fact set contains the specified fact.
     */
    public TruthDegree membership(Fact fact)
    {
        return facts.member(fact);
    }
    
    /**
     * Returns the degree to which the given interpretation satisfies this fact set.
     * The degree to which an interpretation satisfies a fact set is the minimum
     * of the degrees to which it satisfies every individual fact belonging to
     * the set. Since facts have a fuzzy membership in the set, that degree of
     * satisfaction is the maximum between the degree to which the interpretation
     * satisfies the fact and the degree to which the fact does not belong into
     * the fact set.
     */
    public TruthDegree truth(Interpretation itp)
    {
        TruthDegree t = TruthDegree.TRUE;
        Iterator<Fact> i = facts.iterator();
        while(i.hasNext())
        {
            Fact fact = i.next();
            TruthDegree mu = facts.member(fact);
            TruthDegree sat = fact.formula().truth(itp);
            t = TruthDegree.tnorm(t, TruthDegree.snorm(mu.negated(), sat));
        }
        return t;
    }

    /**
     * Returns the set of all atoms occurring in this fact set.
     */
    public Set<Atom> atomSet()
    {
        Set<Atom> atoms = new HashSet<Atom>();
        Iterator<Fact> i = facts.iterator();
        while(i.hasNext())
            atoms.addAll(i.next().formula().atomSet());
        return atoms;
    }
    
    /**
     * Returns an iterator on all facts whose membership in this
     * fact set is non null.
     */
    public Iterator<Fact> factIterator()
    {
        return facts.iterator();
    }
    
    /**
     * Tells whether this fact set equals the given object.
     * A fact set equals another fact set if the two fact sets contain
     * exactly the same facts to the same degree.
     * 
     * @param o
     * @return true if the given object is a fact set and equals this one
     */
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof FactSet))
            return false;
        FactSet that = (FactSet) o;
        return facts.equals(that.facts);
    }
    
    /**
     * Returns the hash code for this fact set.
     * 
     * @return the hash code for this fact set.
     */
    @Override
    public int hashCode()
    {
        return facts.hashCode();
    }
    
    /**
     * Returns a string representation of this fact set.
     */
    @Override
    public String toString()
    {
        String s = "{ ";
        Iterator<Fact> i = facts.iterator();
        while(i.hasNext())
        {
            Fact fact = i.next();
            if(s.length()>2)
                s += ", ";
            s += fact.formula();
            TruthDegree mu = membership(fact);
            if(!mu.isTrue())
                s += " : " + mu;
        }
        return s + " }";
    }
}
