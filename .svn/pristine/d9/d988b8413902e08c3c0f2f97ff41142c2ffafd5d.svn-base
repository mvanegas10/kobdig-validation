/*
 * RuleBase.java
 *
 * Created on April 3, 2008, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.agent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import kobdig.logic.Atom;

/**
 * A set of rules, that generate new facts based on conditions on existing facts.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class RuleBase
{
    protected Set<Rule> rules;
    
    /** Creates an empty rule base. */
    public RuleBase()
    {
        rules = new HashSet<Rule>();
    }
    
    /**
     * Parse the rules of this rule base from an agent program source file.
     */
    public void parse(AplTokenizer source) throws IOException
    {
        source.nextToken();
        source.require('{');
        
        while(source.nextToken()!='}')
        {
            Rule rule = new Rule(source);
            // System.out.println("Parsed rule = " + rule);
            add(rule);
            if(source.ttype!=',')
                break;
        }
        source.require('}');
    }
    
    /**
     * Add a rule to the rule base.
     */
    public void add(Rule r)
    {
        rules.add(r);
    }
    
    /**
     * Remove a rule from the rule base.
     */
    public void remove(Rule r)
    {
        rules.remove(r);
    }
    
    /**
     * Returns an iterator over the rules in the rule base.
     */
    public Iterator<Rule> iterator()
    {
        return rules.iterator();
    }
    
    /**
     * Returns a string representation of this rule base.
     */
    @Override
    public String toString()
    {
        String s = "{\n";
        Iterator<Rule> i = iterator();
        while(i.hasNext())
        {
            if(s.length()>2)
                s += ",\n";
            s += "  " + i.next();
        }
        return s + "\n}";
    }

    /**
     * Return the set of atomic propositions that occur in formulas
     * on the right-hand side of the rules in this rule base.
     * 
     * @return a set of atomic formulas
     */
    Set<Atom> consequentAtomSet()
    {
        Set<Atom> atoms = new HashSet<Atom>();
        Iterator<Rule> i = iterator();
        while(i.hasNext())
            atoms.addAll(i.next().consequent().formula().atomSet());
        return atoms;
    }
}
