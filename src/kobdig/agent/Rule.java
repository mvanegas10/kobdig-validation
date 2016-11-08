/*
 * Rule.java
 *
 * Created on April 3, 2008, 11:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.agent;

import java.io.IOException;
import kobdig.logic.Atom;
import kobdig.logic.Formula;
import kobdig.logic.Interpretation;
import kobdig.logic.PropositionalFormula;
import kobdig.logic.TruthDegree;

/**
 * A rule encapsulates the concept of a construct that can be used to generate
 * new facts based on conditions on existing facts.
 *
 * <p>Rules contrast with {@link Fact facts}.</p>
 *
 * <p>A rule is made of a consequent part, a fact, and an antecedent part,
 * consisting of a fact for each modality.
 * The currently available modalities are:
 * knowledge (<strong>K</strong>), obligation (<strong>O</strong>),
 * belief (<strong>B</strong>), and desire (<strong>D</strong>).</p>
 *
 * <p>A rule with an empty antecedent is interpreted as unconditional, i.e.,
 * of the form <code>IF TRUE THEN ...</code>.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Rule
{
    /** The consequent of the rule */
    protected Fact consequent;
    
    /**
     * The antecedents of the rule for each modality.
     * The currently available modalities are
     * knowledge (<strong>K</strong>), obligation (<strong>O</strong>),
     * belief (<strong>B</strong>), and desire (<strong>D</strong>).
     */
    protected Fact[] antecedent;

    /** The index of the knowledge antecedent in the array of antecedents */
    protected static final int K = 0;
    
    /** The index of the obligation antecedent in the array of antecedents */
    protected static final int O = 1;
    
    /** The index of the belief antecedent in the array of antecedents */
    protected static final int B = 2;
    
    /** The index of the desire antecedent in the array of antecedents */
    protected static final int D = 3;
    
    /** The total number of modalities provided for in the array of antecedents */
    protected static final int NMOD = 4;
    
    /**
     * Creates a new rule with an empty antecedent.
     */
    public Rule(Fact fact)
    {
        consequent = fact;
        antecedent = new Fact[NMOD];
        for(int i = 0; i<NMOD; i++)
            antecedent[i] = Fact.TRUE;
    }
    
    /**
     * Create a new rule from a source file.
     */
    public Rule(AplTokenizer source) throws IOException
    {
        antecedent = new Fact[NMOD];
        for(int i = 0; i<NMOD; i++)
            antecedent[i] = Fact.TRUE;
        
        source.require("if");
        while(source.nextToken()!=AplTokenizer.TT_EOF)
        {
            if(source.ttype==AplTokenizer.TT_WORD)
            {
                if(source.sval.length()==1)
                {
                    char modality = source.sval.charAt(0);
                    source.nextToken();
                    source.require('(');
                    source.nextToken();
                    PropositionalFormula phi = new PropositionalFormula(source);
                    source.require(')');
                    source.nextToken();
                    switch(modality)
                    {
                        case 'K':
                            antecedent[K] = new Fact(phi);
                            break;
                        case 'B':
                            antecedent[B] = new Fact(phi);
                            break;
                        case 'D':
                            antecedent[D] = new Fact(phi);
                            break;
                        case 'O':
                            antecedent[O] = new Fact(phi);
                            break;
                        default:
                            source.require("one of {K, B, D, O}");
                    }
                }
                else
                {
                    source.require("true");
                    source.nextToken();
                }
            }
            else if(source.ttype==AplTokenizer.TT_NUMBER)
            {
                Formula phi = Formula.getConstantFormula(new TruthDegree(source.nval));
                antecedent[0] = new Fact(phi);
                source.nextToken();
            }
            if(!source.has("and"))
                break;
        }
        source.require("then");
        source.nextToken();
        PropositionalFormula phi = new PropositionalFormula(source);
        consequent = new Fact(phi);
    }
    
    /**
     * Determines the degree of truth of the antecedent of this rule.
     *
     * @param agent the agent in whose context the rule is evaluated.
     */
    public TruthDegree activation(Agent agent)
    {
        TruthDegree t = agent.knows(antecedent[K]);
        t = TruthDegree.tnorm(t, agent.believes(antecedent[B]));
        t = TruthDegree.tnorm(t, agent.desires(antecedent[D]));
        t = TruthDegree.tnorm(t, agent.must(antecedent[O]));
        return t;
    }
    
    /**
     * Returns the consequent of this rule.
     */
    public Fact consequent()
    {
        return consequent;
    }
    
    /**
     * Returns a string representation of this rule.
     */
    @Override
    public String toString()
    {
        return "if K(" + antecedent[K].formula() +
            ") and B(" + antecedent[B].formula() +
            ") and D(" + antecedent[D].formula() +
            ") and O(" + antecedent[O].formula() +
            ") then " + consequent.formula();
    }
}
