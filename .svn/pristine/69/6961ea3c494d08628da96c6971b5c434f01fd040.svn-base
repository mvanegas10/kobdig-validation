/*
 * Formula.java
 *
 * Created on April 3, 2008, 2:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class encapsulates the abstract notion of formula in a logical language.
 * <p>A formula is a tree whose root is a logical operator, like &not;, &and;, and &or;,
 * and whose children nodes are formulas; if the arity of the root is zero, the
 * formula is an {@link Atom Atom} and its "root" is the name of the atom, e.g., an atomic
 * proposition in propositional logic or a predicate in predicate logic.
 *
 * <p>It is stipulated that an atomic formula whose root can be parsed as a
 * truth degree in [0, 1] has, by definition, the truth degree indicated
 * by its name in all interpretations.</p>
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Formula implements Cloneable
{
    /**
     * The topmost operator of the formula.
     * If <code>op==null</code>, the formula is atomic.
     */
    protected Operator op;
    
    /** The atom, if the formula is atomic, otherwise ignored/not used. */
    protected Atom atom;
    
    /** The children formulas. */
    protected Formula[] child;
    
    /**
     * The pre-defined formula T (for Tautology), whose truth degree is always 1.
     */
    public static final Formula T = new Formula(new Atom("1.0"))
    {
        @Override
        public TruthDegree truth(Interpretation interpretation)
        {
            return TruthDegree.TRUE;
        }
    };
    
    /**
     * The pre-defined formula F (for False), whose truth degree is always 0.
     */
    public static final Formula F = new Formula(new Atom("0.0"))
    {
        @Override
        public TruthDegree truth(Interpretation interpretation)
        {
            return TruthDegree.FALSE;
        }
    };
    
    /**
     * Creates an empty formula. Only for use by subclasses.
     */
    protected Formula()
    {  
    }
    
    /**
     * Creates a new atomic formula, consisting of the given atom.
     */
    public Formula(Atom a)
    {
        op = null;
        atom = a;
    }
    
    /**
     * Creates a new atomic formula with the given truth value.
     */
    public static Formula getConstantFormula(TruthDegree t)
    {
        return new Formula(new Atom(t.toString()))
        {
            @Override
            public TruthDegree truth(Interpretation interpretation)
            {
                return new TruthDegree(Double.parseDouble(atom.toString()));
            }
        };
    }
    
    /**
     * Creates a new compound formula, with the given logical
     * operator and arguments.
     *
     * @param o the operator
     * @param args the formulas that constitute the arguments of the operator.
     *        If there are more arguments than the arity of the operator,
     *        the extra arguments are ignored. The number of arguments is variable
     *        but cannot be less than the arity of the operator.
     *        The maximum number of arguments is limited by the maximum dimension
     *        of a Java array as defined by the
     *        <a href="http://java.sun.com/docs/books/vmspec/">Java Virtual Machine
     *        Specification</a>.
     */
    public Formula(Operator o, Formula... args)
    {
        int arity = o.arity();
        if(args.length < arity)
            throw new IllegalArgumentException("Not enough arguments for an operator of arity " + arity);
        op = o;
        atom = null;
        child = new Formula[arity];
        for(int i = 0; i<arity; i++)
            child[i] = new Formula(args[i]);
    }
    
    /**
     * Copy constructor: creates a new formula identical to the one provided
     * as parameter, without sharing any of its objects.
     * In other words, the copy operation is "deep".
     *
     * @param that a formula
     */
    public Formula(Formula that)
    {
        op = that.op;
        atom = that.atom;
        if(op!=null)
        {
            int arity = op.arity();
            child = new Formula[arity];
            for(int i = 0; i<arity; i++)
                child[i] = new Formula(that.child[i]);
        }
    }

    /**
     * Indicates whether some other formula is "equal to" this one.
     * <p>Two formula are considered equal if they are syntactically
     * identical. For example, in the case of propositional logic formulas,
     * <var>p</var> and &not;&not;<var>p</var> are different formulas,
     * although logically equivalent.</p>
     */
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof Formula))
            return false;
        Formula that = (Formula) o;
        if(op!=null)
        {
            int arity = op.arity();
            if(arity!=that.op.arity())
                return false;
            if(!op.toString().equals(that.op.toString()))
                return false;
            for(int i = 0; i<arity; i++)
                if(!child[i].equals(that.child[i]))
                    return false;
            return true;
        }
        else
            return atom.toString().equals(that.atom.toString());
    }

    /**
     * Returns a hash code for this formula.
     * The hash code returned is the same as the hash code for
     * the string representation of the formula.
     * 
     * @return the hash code for this formula
     */
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }
    
    /**
     * Returns the set of all atoms occurring in the formula.
     * 
     * @return the set of all atoms occurring in the formula
     */
    public Set<Atom> atomSet()
    {
        Set<Atom> atoms = new HashSet<Atom>();
        if(op==null)
            atoms.add(atom);
        else
            for(int i = 0; i<op.arity(); i++)
                atoms.addAll(child[i].atomSet());
        return atoms;
    }
    
    /**
     * Creates and returns a copy of this formula.
     * This clone method performs a "deep copy" operation.
     */
    @Override
    public Object clone()
    {
        return new Formula(this);
    }
    
    /**
     * Returns the top operator of the formula.
     * If the formula is atomic, the returned operator is null.
     */
    public Operator operator()
    {
        return op;
    }
    
    /**
     * Returns the i-th term of the formula.
     */
    public Formula term(int i)
    {
        if(i<0 || i>=op.arity())
            throw new IllegalArgumentException("Term " + i + " does not exist for an operator of arity " + op.arity());
        return child[i];
    }
    
    /**
     * Tells whether the formula is atomic.
     */
    public boolean isAtomic()
    {
        return op==null;
    }
    
    /**
     * Tells whether the formula has a constant truth value which
     * does not depend on a particular interpretation.
     * A formula is a constant if it is atomic and the name of its
     * atom is a truth degree in [0.0, 1.0].
     */
    public boolean isConstant()
    {
        if(op!=null)
            return false;
        try
        {
            TruthDegree t = new TruthDegree(Double.parseDouble(atom.toString()));
            return atom.toString().equals(t.toString());
        }
        catch(IllegalArgumentException e)
        {
            return false;
        }
    }
    
    /**
     * Tells whether the formula is a literal.
     */
    public boolean isLiteral()
    {
        if(isAtomic())
            return true;
        return op==Operator.NOT && child[0].isAtomic();
    }
    
    /**
     * Tells whether the formula is a conjunction of literals.
     */
    public boolean isConjunction()
    {
        if(isLiteral())
            return true;
        return op==Operator.AND && child[0].isConjunction() && child[1].isConjunction();
    }
    
    /**
     * Returns the negation of this formula.
     */
    public Formula negated()
    {
        if(op!=Operator.NOT)
            return new Formula(Operator.NOT, this);
        else
            return child[0];
    }
    
    /**
     * Applies the De Morgan's law to this formula and removes double
     * negation.
     * If the formula's root operator is not a negation,
     * this method has no effect.
     * 
     * @return the transformed formula
     */
    public Formula deMorgan()
    {
        if(op!=Operator.NOT)
            return this;
        if(child[0].op==Operator.NOT)
            return child[0].child[0];
        if(child[0].op==Operator.AND)
            return new Formula(Operator.OR,
                    new Formula(Operator.NOT, child[0].child[0]),
                    new Formula(Operator.NOT, child[0].child[1]));
        else if(child[0].op==Operator.OR)
            return new Formula(Operator.AND,
                    new Formula(Operator.NOT, child[0].child[0]),
                    new Formula(Operator.NOT, child[0].child[1]));
        else
            return this;
    }
    
    /**
     * Right-associate this formula.
     * This transforms, e.g., (<var>f</var><sub>1</sub> &and; <var>f</var><sub>2</sub>)
     * &and; <var>f</var><sub>3</sub> into <var>f</var><sub>1</sub> &and; (<var>f</var><sub>2</sub>
     * &and; <var>f</var><sub>3</sub>).
     * If the operator of the left subformula is different from the
     * root operator, or the root operator is &not;, no transformation
     * is made.
     */
    public Formula rightAssociate()
    {
        if(op==Operator.NOT || isLiteral())
            return this;
        if(op==child[0].op)
            return new Formula(op,
                    child[0].child[0],
                    new Formula(op, child[0].child[1], child[1]).rightAssociate());
        return this;
    }
    
    /**
     * Returns the negation normal form (NNF) of this formula.
     * Distributes negation (NOT) over any conjunctions or disjunctions
     * according to deMorgan's laws until negation is only applied
     * to atomic propositions (propositional variables) and
     * removes double negations where these arise.
     */
    public Formula nnf()
    {        
        Formula phi = deMorgan();
        if(phi.isLiteral())
            return phi;
        // At this point, the root connective can only be AND or OR:
        return new Formula(phi.op, phi.child[0].nnf(), phi.child[1].nnf());
    }
    
    /**
     * Returns the disjunctive normal form of this formula
     * as a set of conjunctions, where each conjunction is
     * a set of literals.
     * This method uses the basic algorithm described in
     * <a href="http://answers.google.com/answers/threadview?id=452083">
     * this page</a> of Google Answers.
     * 
     * @param atoms a set of atoms, required as a key for clause representation
     */
    private Set<Conjunction> setDNF(Atom[] atoms)
    {
        Set<Conjunction> disjunction = new DisjunctiveNormalForm();
        
        Formula phi = deMorgan();
        // DNF(literal) = literal
        if(phi.isLiteral())
        {
            Conjunction conjunction = new Conjunction(atoms);
            conjunction.add(phi);
            disjunction.add(conjunction);
        }
        // DNF(P OR Q) -> DNF(P) OR DNF(Q)
        else if(phi.op==Operator.OR)
        {
            disjunction.addAll(phi.child[0].setDNF(atoms));
            disjunction.addAll(phi.child[1].setDNF(atoms));
        }
        else
        {
            // The case DNF(P AND Q) is more complicated.
            // Start by finding DNF(P) = P_1 OR P_2 OR ... OR P_m
            // and the DNF(Q) = Q_1 OR Q_2 OR ... OR Q_n,
            // where each P_i, Q_j is a conjunction of literals.
            Set<Conjunction> l = phi.child[0].setDNF(atoms);
            Set<Conjunction> r = phi.child[1].setDNF(atoms);

            // Then DNF(P AND Q) is obtained as the disjunction of all possible
            // pairs of distributed conjunctions: OR_{ij} ( P_i AND Q_j ).
            Iterator<Conjunction> i = l.iterator();
            while(i.hasNext())
            {
                Iterator<Conjunction> j = r.iterator();
                Conjunction leftClause = i.next();
                while(j.hasNext())
                {
                    Conjunction rightClause = j.next();
                    Conjunction conjunction = rightClause.and(leftClause);
                    
                    // Remove "inner" conjunctions which happen to contain both
                    // an atomic proposition and its negation as these are "false".
                    if(conjunction!=null)
                        disjunction.add(conjunction);
                }
            }
        }

        /* TO DO
         Further pruning of the DNF is useful to obtain a "canonical"
         form of a formula, i.e., to recognize when two formulas are
         equivalent (in particular when a formula is tautologous).
         */
        
        return disjunction;
    }

    /**
     * Returns a set of the conjunctive clauses that make up the
     * Disjunctive Normal Form of this formula.
     * This method uses the basic algorithm described in
     * <a href="http://answers.google.com/answers/threadview?id=452083">
     * this page</a> of Google Answers.
     * 
     * @return a set of disjoint conjunctions
     */
    public Set<Conjunction> getDNFClauses()
    {
        Atom[] atoms = atomSet().toArray(new Atom[1]);
        return setDNF(atoms);
    }
    
    /**
     * Transforms this formula into disjunctive normal form.
     */
    public Formula dnf()
    {
        // Obtain the set of disjoint conjunctions:
        Set<Conjunction> disjunction = getDNFClauses();
        
        // Construct a formula from the set of disjoint conjunctions:
        Formula dnf = null;
        Iterator<Conjunction> i = disjunction.iterator();
        while(i.hasNext())
        {
            Formula clause = i.next().formula();
            if(dnf==null)
                dnf = clause;
            else if(clause!=null)
                dnf = new Formula(Operator.OR, clause, dnf);
        }
        
        // An empty disjunction is considered "false", by
        // analogy with an empty sum being 0.  This is consistent with the
        // simplification rules above, as removing an obvious
        // "false" term from a disjunction should be interpreted as finally
        // "false" if all terms are removed.
        if(dnf==null)
            dnf = F;
        return dnf;
    }

//    public Formula dnf()
//    {
//        Formula phi = deMorgan();
//        // DNF(literal) = literal
//        if(phi.isLiteral())
//            return phi;
//        // DNF(P OR Q) -> DNF(P) OR DNF(Q)
//        if(phi.op==Operator.OR)
//            return new Formula(phi.op, phi.child[0].dnf(), phi.child[1].dnf()).rightAssociate();
//
//        // The case DNF(P AND Q) is more complicated.
//        // Start by finding DNF(P) = P_1 OR P_2 OR ... OR P_m
//        // and the DNF(Q) = Q_1 OR Q_2 OR ... OR Q_n,
//        // where each P_i, Q_j is a conjunction of elementary propositions.
//        Formula l = phi.child[0].dnf().rightAssociate();
//        Formula r = phi.child[1].dnf().rightAssociate();
//        Formula dnf = null;
//        
//        // Then DNF(P AND Q) is obtained as the disjunction of all possible
//        // pairs of distributed conjunctions: OR_{ij} ( P_i AND Q_j ).
//        Formula i = l;
//        while(true)
//        {
//            Formula j = r;
//            while(true)
//            {
//                Formula left = i.op==Operator.OR ? i.child[0] : i;
//                Formula right = j.op==Operator.OR ? j.child[0] : j;
//                
//                Formula c = new Formula(Operator.AND, left, right).rightAssociate();
//                if(dnf==null)
//                    dnf = c;
//                else
//                    dnf = new Formula(Operator.OR, c, dnf);
//                if(j.op!=Operator.OR) break;
//                j = j.child[1];
//            }
//            if(i.op!=Operator.OR) break;
//            i = i.child[1];
//        }
//
//        /* TO DO
//         3. Remove "inner" conjunctions which happen to contain both
//         an atomic proposition and its negation as these are "false".
//         Also remove duplicates of elementary propositions in an inner
//         conjunction.  Finally, if the exact same "inner" conjunction
//         appears more than once in the "outer" disjunction, that
//         duplication can be eliminated by removing one copy.
//         */
//
//        /* TO DO
//         Further pruning of the DNF is useful to obtain a "canonical"
//         form of a formula, i.e., to recognize when two formulas are
//         equivalent (in particular when a formula is tautologous).
//         */
//        
//        // An empty disjunction is considered "false", by
//        // analogy with an empty sum being 0.  This is consistent with the
//        // simplification rules above, as removing an obvious
//        // "false" term from a disjunction should be interpreted as finally
//        // "false" if all terms are removed.
//        if(dnf==null)
//            dnf = F;
//        return dnf;
//    }
    
    /**
     * Returns a string representation of the formula.
     *
     * @return a string representation of the formula
     */
    @Override
    public String toString()
    {
        if(op==null)
            return atom.toString();
        else if(op.arity()==1)
            return op.toString() + child[0];
        else if(op.arity()==2)
            return "(" + child[0].toString() + " " + op + " " + child[1] + ")";
        else
        {
            String str = op.toString() + "(";
            for(int i = 0; i<op.arity(); i++)
            {
                if(i>0) str += ", ";
                str += child[i];
            }
            return str + ")";
        }
    }

    /**
     * Returns the degree of truth of this formula under the given interpretation.
     * All concrete subclasses must override this method.
     */
    public TruthDegree truth(Interpretation interpretation)
    {
        throw new UnsupportedOperationException("Impossible to calculate the truth of an abstract formula");
    }
}
