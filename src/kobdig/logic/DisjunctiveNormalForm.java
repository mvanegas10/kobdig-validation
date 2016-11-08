/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.logic;


import java.util.HashSet;
import java.util.Iterator;

/**
 * A formula in disjunctive normal form.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class DisjunctiveNormalForm extends HashSet<Conjunction>
{
    /**
     * Add a conjunction only if it is not already contained in the
     * disjunctive normal form.
     * 
     * @param e
     * @return
     */
//    @Override
//    public boolean add(Conjunction c)
//    {
//        Iterator<Conjunction> i = clauses.iterator();
//        System.out.println("Adding " + c + "...");
//        while(i.hasNext())
//            if(c.equals(i.next()))
//                return false;
//        clauses.add(c);
//        System.out.println("... Added.");
//        return true;
//    }

//    @Override
//    public boolean addAll(Collection<? extends Conjunction> c)
//    {
//        DisjunctiveNormalForm that = (DisjunctiveNormalForm) c;
//        boolean changed = false;
//        Iterator<Conjunction> i = that.iterator();
//        while(i.hasNext())
//            if(add(i.next())) changed = true;
//        return changed;
//    }

    /**
     * Returns an iterator over the conjunctions of this disjunctive
     * normal form.
     * 
     * @return
     */
//    @Override
//    public Iterator<Conjunction> iterator()
//    {
//        return clauses.iterator();
//    }

    /**
     * Returns a printable version of this disjunctive normal form.
     * @return a string representation of this DNF
     */
    @Override
    public String toString()
    {
        String s = "";
        Iterator<Conjunction> i = iterator();
        while(i.hasNext())
        {
            if(s.length()>0) s += " " + Operator.NOT + " ";
            s += i.next();
        }
        return s;
    }
}
