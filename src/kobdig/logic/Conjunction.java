/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.logic;

/**
 * A conjunction of literals.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Conjunction
{
    /** A set of atoms, required as a key to the literals */
    protected Atom[] atoms;
    protected short[] literals;

    /**
     * Creates a new conjunction on the given set of atoms.
     * 
     * @param a
     */
    public Conjunction(Atom[] a)
    {
        atoms = a;
        literals = new short[atoms.length];
    }
    
    /**
     * Add the given literal to this conjunction.
     * 
     * @param phi a formula that must be a literal (@see Formula#isLiteral)
     */
    public void add(Formula phi)
    {
        if(!phi.isLiteral())
            throw new IllegalArgumentException("Literal required");
        Atom a;
        short sign;
        if(phi.isAtomic())
        {
            a = phi.atom;
            sign = 1;
        }
        else
        {
            a = phi.child[0].atom;
            sign = -1;
        }
        for(int i = 0; i<atoms.length; i++)
            if(atoms[i].equals(a))
            {
                literals[i] = sign;
                break;
            }
    }
    
    /**
     * Returns the logical AND of this conjunction with the given
     * conjunction. If the result is inconsistent, <code>null</code>
     * is returned.
     */
    public Conjunction and(Conjunction that)
    {
        if(atoms.length!=that.atoms.length)
            return null;
        Conjunction conjunction = new Conjunction(atoms);
        for(int i = 0; i<atoms.length; i++)
        {
            int product = literals[i]*that.literals[i];
            if(product<0)
            {
                // the conjunction of the two clauses is inconsistent:
                conjunction = null;
                break;
            }
            if((conjunction.literals[i] = literals[i])==0)
                conjunction.literals[i] = that.literals[i];
        }
        return conjunction;
    }
    
    /**
     * Returns the conjunction as a formula.
     * If the conjunction is empty, <code>null</code> is returned.
     * 
     * @return a formula that represents this conjunction.
     */
    public Formula formula()
    {
        Formula phi = null;
        for(int j = 0; j<literals.length; j++)
        {
            if(literals[j]!=0)
            {
                Formula literal = new Formula(atoms[j]);
                if(literals[j]<0)
                    literal = new Formula(Operator.NOT, literal);
                if(phi==null)
                    phi = literal;
                else
                    phi = new Formula(Operator.AND, literal, phi);
            }
        }
        return phi;
    }
    
    /**
     * Returns the set of atomic propositions on which the
     * conjunction is defined.
     * 
     * @return the set of atomic propositions on which the
     * conjunction is defined
     */
    public Atom[] atoms()
    {
        return atoms;
    }
    
    /**
     * Returns the sign of the given atom.
     */
    public short sign(Atom a)
    {
        for(int i = 0; i<atoms.length; i++)
            if(a.equals(atoms[i]))
                return literals[i];
        return 0;
    }
    
    /**
     * Checks whether this conjunction equals the given object.
     * 
     * @param o
     * @return true if the conjunction equals the given object
     */
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof Conjunction))
            return false;
        Conjunction that = (Conjunction) o;
        if(atoms.length!=that.atoms.length)
            return false;
        for(int i = 0; i<atoms.length; i++)
            if(literals[i]!=that.literals[i])
                return false;
        return true;
    }
    
    /**
     * Returns a hash code value for the object.
     * This method guarantees that different conjunctions have
     * distinct hash codes, and that equivalent conjunctions
     * have the same hash code.
     * 
     * @return the hash code value for this object
     */
    @Override
    public int hashCode()
    {
        int h = 0;
        int p = 1;
        for(int i = 0; i<atoms.length; i++, p *= 3)
            h += (literals[i] + 1)*p;
        return h;
    }
    
    /**
     * Returns a printable version of this clause.
     * 
     * @return a string representation of this clause
     */
    @Override
    public String toString()
    {
       return formula().toString(); 
    }
}
