/*
 * Operator.java
 *
 * Created on April 3, 2008, 2:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

/**
 * This class encapsulates the abstract notion of a logical operator.
 * <p>A logical operator is defined by its <em>symbol</em>, or name,
 * and by an <em>arity</em>.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Operator
{
    /** The arity of the operator. Must be greater than zero. */
    protected int arity;
    
    /** The name of the operator. */
    protected String name;
    
    /** The pre-defined unary negation operator, "&not;". */
    public static final Operator NOT = new Operator("~", 1)
    {
        @Override
        public TruthDegree truth(TruthDegree... t)
        {
            return super.truth(t).negated();
        }
    };
    
    /** The pre-defined binary conjunction operator, "&and;". */
    public static final Operator AND = new Operator("&", 2)
    {
        @Override
        public TruthDegree truth(TruthDegree... t)
        {
            return TruthDegree.tnorm(t[1], super.truth(t));
        }
    };
    
    /** The pre-defined binary disjunction operator, "&or;". */
    public static final Operator OR  = new Operator("|", 2)
    {
        @Override
        public TruthDegree truth(TruthDegree... t)
        {
            return TruthDegree.snorm(t[1], super.truth(t));
        }
    };
    
    /** The pre-defined binary exclusive or operator, "&oplus;". */
    public static final Operator XOR  = new Operator("+", 2)
    {
        @Override
        public TruthDegree truth(TruthDegree... t)
        {
            return TruthDegree.snorm(
                TruthDegree.tnorm(t[1].negated(), super.truth(t)),
                TruthDegree.tnorm(t[1], super.truth(t).negated())
            );
        }
    };
    
    /** Creates an operator with the given symbol and arity. */
    public Operator(String symbol, int a)
    {
        if(a<1)
            throw new IllegalArgumentException("Illegal arity " + a);
        arity = a;
        name = symbol;
    }
    
    /** Returns the arity of the operator. */
    public int arity()
    {
        return arity;
    }
    
    /**
     * The operator's truth function.
     * Every operator must override this method; for convenience, the standard
     * implementation returns the degree of truth of the first argument.
     */
    public TruthDegree truth(TruthDegree... t)
    {
        if(t.length < arity)
            throw new IllegalArgumentException("Not enough arguments for an operator of arity " + arity);
        return t[0];
    }
    
    /**
     * Returns a string representation of the operator.
     * In this general class, the string representation of an operator is its symbol.
     */
    public String toString()
    {
        return name;
    }    
}
