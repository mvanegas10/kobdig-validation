/*
 * PropositionalFormula.java
 *
 * Created on April 3, 2008, 6:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

import java.io.IOException;
import java.util.Set;
import kobdig.agent.AplTokenizer;

/**
 * A well-formed formula in the language of propositional logic
 *
 * @author Andrea G. B. Tettamanzi
 */
public class PropositionalFormula extends Formula
{
    /**
     * Creates a stub propositional formula with the given operator.
     * This constructor is declared as <code>protected</code>
     * because it can be called only from within this class.
     */
    protected PropositionalFormula(Operator o)
    {
        int arity = o.arity();
        op = o;
        atom = null;
        child = new Formula[arity];
    }
    
    /** Creates a new atomic propositional formula */
    public PropositionalFormula(PropositionalAtom a)
    {
        super(a);
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
    public PropositionalFormula(Operator o, Formula... args)
    {
        int arity = o.arity();
        if(args.length < arity)
            throw new IllegalArgumentException("Not enough arguments for an operator of arity " + arity);
        op = o;
        atom = null;
        child = new Formula[arity];
        for(int i = 0; i<arity; i++)
            child[i] = new PropositionalFormula(args[i]);
    }
    
    /**
     * Copy constructor: creates a new formula identical to the one provided
     * as parameter, without sharing any of its objects.
     * In other words, the copy operation is "deep".
     *
     * @param that a formula
     */
    public PropositionalFormula(Formula that)
    {
        op = that.op;
        atom = that.atom;
        if(op!=null)
        {
            int arity = op.arity();
            child = new Formula[arity];
            for(int i = 0; i<arity; i++)
                child[i] = new PropositionalFormula(that.child[i]);
        }
    }
    
    /**
     * Parses a propositional formula from a source file.
     */
    public PropositionalFormula(AplTokenizer source) throws IOException
    {
        PropositionalFormula that = parseDisjunction(source);
        op = that.op;
        atom = that.atom;
        child = that.child;
    }
    
    /**
     * Parses a disjunction of one or more subformulas.
     * 
     * @param source a tokenizer of the source file from which the disjunction
     *   is being parsed
     * @return the parsed formula
     * @throws java.io.IOException
     */
    protected static PropositionalFormula parseDisjunction(AplTokenizer source) throws IOException
    {
        PropositionalFormula phi = null;
        do
        {
            if(phi==null)
                phi = parseConjunction(source);
            else
            {
                source.nextToken();
                phi = new PropositionalFormula(Operator.OR, parseConjunction(source), phi);
            }
        }
        while(source.has("or"));
        return phi;
    }
    
    /**
     * Parses a conjunction of one or more subformulas (whose top-level
     * logical connective is guaranteed not to be a disjunction).
     * 
     * @param source a tokenizer of the source file from which the conjunction
     *   is being parsed
     * @return the parsed formula
     * @throws java.io.IOException
     */
    protected static PropositionalFormula parseConjunction(AplTokenizer source) throws IOException
    {
        PropositionalFormula phi = null;
        do
        {
            if(phi==null)
                phi = parseSubFormula(source);
            else
            {
                source.nextToken();
                phi = new PropositionalFormula(Operator.AND, parseSubFormula(source), phi);
            }
        }
        while(source.has("and"));
        return phi;
    }
    
    /**
     * Parses a subformula, which can consist either of a literal or
     * of a positive or negated subformula surrounded by parentheses.
     * 
     * @param source a tokenizer of the source file from which the subformula
     *   is being parsed
     * @return the parsed formula
     * @throws java.io.IOException
     */
    protected static PropositionalFormula parseSubFormula(AplTokenizer source) throws IOException
    {
        if(source.ttype==AplTokenizer.TT_WORD)
        {
            if(source.has("not"))
            {
                source.nextToken();
                return new PropositionalFormula(Operator.NOT, parseSubFormula(source));
            }
            else 
            {
                PropositionalAtom a = new PropositionalAtom(source.sval);
                source.nextToken();
                return new PropositionalFormula(a);
            }
        }
        else
        {
            source.require('(');
            source.nextToken();
            PropositionalFormula phi = parseDisjunction(source);
            source.require(')');
            source.nextToken();
            return phi;
        }
    }
    
    /**
     * Returns the degree of truth of this formula under the given interpretation.
     */
    @Override
    public TruthDegree truth(Interpretation itp)
    {
        if(!(itp instanceof PropositionalInterpretation))
            throw new IllegalArgumentException("Propositional interpretation required");
        
        PropositionalInterpretation interpretation = (PropositionalInterpretation) itp;
        if(op==null)
            // Base of recursion:
            return interpretation.truth(atom);
        else
        {
            // Recursion Step:
            // calculate the truth for each of the arguments
            int arity = op.arity();
            TruthDegree[] t = new TruthDegree[arity];
            for(int i = 0; i<arity; i++)
                t[i] = child[i].truth(itp);
            return op.truth(t);
        }
    }
    
    /**
     * Returns the negation of this formula.
     */
    @Override
    public Formula negated()
    {
        if(op!=Operator.NOT)
            return new PropositionalFormula(Operator.NOT, this);
        else
            return child[0];
    }
    
    /**
     * Generates a random propositional formulas using atoms from the
     * provided atom set.
     * 
     * @param p a parameter controlling the expected depth of the formula,
     *   0 &lt; p &lt; 1.
     * @param atoms a set of propositional atoms that can be used to construct
     *   the random formula
     * @return a random propositional formula
     */
    public static PropositionalFormula random(double p, PropositionalAtom[] atoms)
    {
        if(Math.random()<p)
        {
            switch((int) Math.floor(Math.random()*3.0))
            {
                case 0:
                    return new PropositionalFormula(Operator.NOT, random(p, atoms));
                case 1:
                    return new PropositionalFormula(Operator.AND,
                            random(p, atoms), random(p, atoms));
                case 2:
                    return new PropositionalFormula(Operator.OR,
                            random(p, atoms), random(p, atoms));
            }            
        }
        return new PropositionalFormula(atoms[(int) Math.floor(Math.random()*atoms.length)]);
    }
}

