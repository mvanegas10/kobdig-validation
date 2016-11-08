/*
 * Agent.java
 *
 * Created on April 3, 2008, 9:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import kobdig.logic.Atom;
import kobdig.logic.BooleanFormula;
import kobdig.logic.BooleanTerm;
import kobdig.logic.Formula;
import kobdig.logic.Interpretation;
import kobdig.logic.Operator;
import kobdig.logic.PossibilityDistribution;
import kobdig.logic.PropositionalAtom;
import kobdig.logic.PropositionalFormula;
import kobdig.logic.PropositionalInterpretation;
import kobdig.logic.PropositionalInterpretationIterator;
import kobdig.logic.TruthDegree;

/**
 * A KOBDIG deliberating agent, equipped with knowledge and belief bases,
 * desire and obligation-generation rules, and obligation, desire, and goal sets.
 * These element constitute the mental state of the agent.
 * <p>More precisely,</p>
 * <ul>
 * <li>the <strong>knowledge base</strong> contains things the agent knows
 *     about the world;</li>
 * <li>the <strong>belief base</strong> contains things the agent believes
 *     about the world;</li>
 * <li>the <strong>desire-generation rule base</strong> contains rules which
 *     determine which desires the agent adopts given its knowledge, beliefs,
 *     obligations, and current desires;</li>
 * <li>the <strong>obligation-generation rule base</strong> contains rules which
 *     determine which obligations are active given the agent's knowledge,
 *     beliefs, current obligations, and desires;</li>
 * <li>the <strong>obligation set</strong> contains laws and regulations
 *     the agent has resolved to abide by;</li>
 * <li>the <strong>desire set</strong> contains states of the world the agent
 *     desires to achieve;</li>
 * <li>the <strong>goal set</strong> is a consistent subset of the desires which
 *     the agent has committed to and plans on achieving.</li>
 * </ul>
 * <p>The two rule bases are, in a sense, the <em>program</em> of the agent, in
 * that they encode the "business logic" and the bahavior of the agent, whereas
 * the knowledge and belief bases change dynamically (based on information coming
 * from sensors or told by other agents) to reflect the world the agent operates
 * in; finally, the three sets (obligations, desires, and goals) result from
 * the agent's deliberations and determine its actions.</p>
 * 
 * <p>This is the type of agent described in
 * C&eacute;lia da Costa Pereira and Andrea G. B. Tettamanzi.
 * "An Integrated Possibilistic Framework for Goal Generation
 * in Cognitive Agents".
 * In Wiebe van der Hoek, Gal Kaminka, Yves Lesp&eacute;rance, Michael Luck,
 * and Sandip Sen (Editor(s)).
 * <em>Proceedings of the 9th International conference on autonomous agents
 * and multiagent systems (AAMAS&nbsp;2010)</em>, pages 1239&ndash;1246,
 * International foundation for autonomous agents and multiagent systems, 2010.
 * <a href="http://www.ifaamas.org/Proceedings/aamas2010/pdf/01 Full Papers/25_05_FP_0632.pdf"><strong>[Full text]</strong></a>
 * </p>
 * 
 * @author Andrea G. B. Tettamanzi
 */
public class Agent
{
    /**
     * The agent's name.
     */
    protected String name;
    
    /**
     * The <strong>knowledge base</strong> contains things the agent knows
     * about the world.
     *
     * <p>In our AAMAS 2010 paper, we have assumed that the knowledge base is
     * a possibility distribution; however, that assumption does not lends
     * itself to an efficient implementation; therefore, we use
     * an equivalent syntactic representation by means of a possibilistic
     * knowledge base.</p>
     */
    protected PossibilisticFactBase knowledge;

    /**
     * The <strong>belief base</strong> contains things the agent believes
     * about the world.
     *
     * <p>In our AAMAS 2010 paper, we have assumed that the belief base is
     * a possibility distribution; however, that assumption does not lends
     * itself to an efficient implementation; therefore, we use
     * an equivalent syntactic representation by means of a possibilistic
     * belief base.</p>
     */
    protected PossibilisticFactBase beliefs;
    
    /**
     * The <strong>desire-generation rule base</strong> contains rules which
     * determine which desires the agent adopts given its knowledge, beliefs,
     * obligations, and current desires.
     */
    protected RuleBase desRules;
    
    /**
     * The <strong>obligation-generation rule base</strong> contains rules which
     * determine which obligations are active given the agent's knowledge,
     * beliefs, current obligations, and desires.
     */
    protected RuleBase oblRules;
    
    /**
     * The <strong>obligation set</strong> contains laws and regulations
     * the agent has resolved to abide by.
     */
    protected FactSet obligations;
    
    /**
     * The desires of an agent are determined by a
     * <strong>qualitative utility distribution</strong>,
     * formally a possibility distribution <i>u</i> over the
     * set of interpretations of the logical language used to
     * express states of the world.
     */
    PossibilityDistribution utility;
    
    /**
     * The <strong>desire set</strong> contains states of the world the agent
     * desires to achieve. These are what is called the <em>justified</em>
     * desires da Costa Pereira's and Tettamanzi's papers.
     * 
     * This set has been replaced by a qualitative utility,
     * formally a possibility distribution.
     * 
     * @deprecated 
     */
    protected FactSet desires;
    
    /**
     * The <strong>goal set</strong> is a consistent subset of the desires which
     * the agent has committed to and plans to achieve.
     */
    protected FactSet goals;
    
    /**
     * Creates an empty KOBDIG deliberating agent.
     */
    public Agent()
    {
        name = "New Agent";
        knowledge = new PossibilisticFactBase();
        beliefs = new PossibilisticFactBase();
        desRules = new RuleBase();
        oblRules = new RuleBase();
        obligations = new FactSet();
        utility = new PossibilityDistribution();
        desires = new FactSet();
        goals = new FactSet();
    }
    
    /**
     * Creates a KOBDIG deliberating agent from a file.
     * <p>A KOBDIG agent program has the following syntax:</p>
     * 
     * <p>
     * <code>agent(</code><var>name</var><code>)</code><br/>
     * <code>{</code><br/>
     * &nbsp;&nbsp;<code>knowledge</code><br/>
     * &nbsp;&nbsp;<code>{</code><br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; a set of comma-separated propositional formulas<br/>
     * &nbsp;&nbsp;<code>}</code><br/>
     * &nbsp;&nbsp;<code>beliefs</code><br/>
     * &nbsp;&nbsp;<code>{</code><br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; a set of comma-separated propositional formulas<br/>
     * &nbsp;&nbsp;<code>}</code><br/>
     * &nbsp;&nbsp;<code>desires</code><br/>
     * &nbsp;&nbsp;<code>{</code><br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; a set of comma-separated rules<br/>
     * &nbsp;&nbsp;<code>}</code><br/>
     * &nbsp;&nbsp;<code>obligations</code><br/>
     * &nbsp;&nbsp;<code>{</code><br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; a set of comma-separated rules<br/>
     * &nbsp;&nbsp;<code>}</code><br/>
     * <code>}</code>
     * </p>
     */
    public Agent(InputStream is) throws IOException
    {
        FactBase k = new FactBase();
        FactBase b = new FactBase();
        desRules = new RuleBase();
        oblRules = new RuleBase();
        obligations = new FactSet();
        utility = new PossibilityDistribution();
        desires = new FactSet();
        goals = new FactSet();
        
        // Use a buffered reader with default buffer size
        Reader r = new BufferedReader(new InputStreamReader(is));
        // Initialize the agent program source tokenizer:
        AplTokenizer source = new AplTokenizer(r);
        source.nextToken();
        source.require("agent");
        source.nextToken();
        source.require('(');
        source.nextToken();
        source.requireWord("agent identifier");
        name = source.sval;
        source.nextToken();
        source.require(')');
        
        source.nextToken();
        source.require('{');
        
        while(source.nextToken()==AplTokenizer.TT_WORD)
        {
            if(source.has("knowledge"))
            {
                k.parse(source);
                knowledge = new PossibilisticFactBase(k);
            }
            else if(source.has("beliefs"))
            {
                b.parse(source);
                beliefs = new PossibilisticFactBase(b);
            }
            else if(source.has("desires"))
                desRules.parse(source);
            else if(source.has("obligations"))
                oblRules.parse(source);
        }
        source.require('}');
        
        // Initialize the obligations, desires, and goals:
        updateDesires();
        updateObligations();
        updateGoals();
    }
    
    /**
     * Returns the name of this agent.
     * 
     * @return the agent's name
     */
    public String name()
    {
        return name;
    }
    
    /**
     * Returns the agent's knowledge base.
     */
    public PossibilisticFactBase knowledge()
    {
        return knowledge;
    }
    
    /**
     * Returns the agent's belief base.
     */
    public PossibilisticFactBase beliefs()
    {
        return beliefs;
    }
    
    /**
     * Returns the agent's obligation set.
     */
    public FactSet obligations()
    {
        return obligations;
    }
    
    /**
     * Returns the agent's qualitative utility, which is the
     * semantic representation of the agent's desires.
     */
    public PossibilityDistribution utility()
    {
        return utility;
    }
    
    /**
     * Returns the agent's desire set (not used at the moment).
     */
    public FactSet desires()
    {
        return desires;
    }
    
    /**
     * Returns the agent's goal set.
     */
    public FactSet goals()
    {
        return goals;
    }
    
    /**
     * Updates the agent's atomic goal set.
     * This is an ad hoc method that will work if you only have
     * atomic goals, in which case method "updateGoals" needs not
     * be called.
     */
    public void updateAtomicGoals()
    {
        goals = new FactSet();
        
        SortedSet<TruthDegree> levels = beliefs.levelSet();
        while(!levels.isEmpty())
        {
            // Take the highest remaining degree in the belief base:
            TruthDegree gamma = levels.last();
            
            // Compute G_gamma by a simplified version of Algorithm 2:
            TruthDegree bestUtility = TruthDegree.FALSE;

            // We consider the set of atoms that occur in the consequents
            // of the desire-generation rules:
            Set<Atom> rhsAtoms = desRules.consequentAtomSet();

            // Find the most desired among the gamma-possible atoms:
            Iterator<Atom> i = rhsAtoms.iterator();
            while(i.hasNext())
            {
                PropositionalFormula phi = new PropositionalFormula((PropositionalAtom) i.next());
                Fact candidateGoal = new Fact(phi);
                if(beliefs.possibility(candidateGoal).isAtLeastAsTrueAs(gamma))
                {
                    TruthDegree d = desires(candidateGoal);
                    if(d.isAtLeastAsTrueAs(bestUtility))
                    {                     
                        if(!d.equals(bestUtility))
                        {
                            goals = new FactSet();
                            bestUtility = d;
                        }
                        goals.tell(candidateGoal);
                    }
                }
            }
            if(goals.size()>0) break;
            levels = levels.headSet(gamma);
        }
    }
    
    /**
     * Returns the agent's obligation-generation rules.
     */
    public RuleBase obligationRules()
    {
        return oblRules;
    }
    
    /**
     * Returns the agent's desire-generation rules.
     */
    public RuleBase desireRules()
    {
        return desRules;
    }
    
    /**
     * Returns the degree to which the agent knows the given fact is true.
     */
    public TruthDegree knows(Fact fact)
    {
        return knowledge.necessity(fact);
    }
    
    /**
     * Returns the the degree to which the agent believes the given fact is true.
     */
    public TruthDegree believes(Fact fact)
    {
        return beliefs.necessity(fact);
    }
    
    /**
     * This is an ad hoc way of computing the degree of truth of a fact
     * based on a fact set which might not be consistent.
     * This method is an attempt to extend a semantics defined when the
     * fact set consists of literals only. Therefore, it is guaranteed
     * to work as expected when the factset contains literals only, but
     * it might not work otherwise.
     *
     * @param factset a set of facts
     * @param fact a fact
     * @return the degree to which the fact set justifies the given fact 
     */
    protected TruthDegree justify(FactSet factset, Fact fact)
    {
        // first of all, handle constant-truth formulas correctly:
        if(fact.formula().isConstant())
            return new TruthDegree(Double.parseDouble(fact.formula().toString()));
        TruthDegree mu = factset.membership(fact);
        if(mu.isFalse())
        {
            // Determine by structural recursion on the fact formula:
            Formula phi = fact.formula();
            if(phi.isLiteral())
                // Nothing can be said about a literal which is not in the set
                return TruthDegree.FALSE;
            Operator op = phi.operator();
            int arity = op.arity();
            TruthDegree[] t = new TruthDegree[arity];
            for(int i = 0; i<arity; i++)
                t[i] = justify(factset, new Fact(phi.term(i)));
            return op.truth(t);
        }
        else
            return mu;
    }
    
    /**
     * Returns the the degree to which the given fact is a justified desire of
     * the agent.
     */
    public TruthDegree desires(Fact fact)
    {
        // first of all, handle constant-truth formulas correctly:
        if(fact.formula().isConstant())
            return new TruthDegree(Double.parseDouble(fact.formula().toString()));
        
        return utility.guaranteedPossibility(fact.formula());
    }
    
    /**
     * Returns the the degree to which the given fact is an obligation for
     * the agent.
     */
    public TruthDegree must(Fact fact)
    {
        return justify(obligations, fact);
    }
    
    /**
     * Performs belief revision according to a new piece of information
     * (a fact) told by a source with given degree of trust.
     * This is the syntactic belief-change operator described in
     * C&eacute;lia da Costa Pereira and Andrea G. B. Tettamanzi.
     * <a href="http://dx.doi.org/10.1109/WI-IAT.2011.54">"A Syntactic
     * Possibilistic Belief Change Operator for Cognitive Agents".
     * </a>In Olivier Boissier, Jeffrey Bradshaw, Longbing Cao,
     * Klaus Fischer, and Mohand-Sa&iuml;d Hacid (Editor(s)).
     * <em>WI-IAT 2011: 2011 IEEE/WIC/ACM International Conference
     * on Web Intelligence  and Intelligent Agent Technology, Lyon,
     * France, 22&ndash;27 August 2011, volume 2: IAT 2011</em>
     * (ISBN: 978-1-4577-1373-6), pages 38&ndash;45,
     * IEEE Computer Society, Los Alamitos, CA, 2011.
     * <br/>DOI: <a href="http://dx.doi.org/10.1109/WI-IAT.2011.54">10.1109/WI-IAT.2011.54</a>
     * 
     * <p>Algorithmically, the belief-change operator may be decribed as follows:</p>
     * <ol>
     * <li>remove from belief base <i>B</i> all formulas &psi; such that
     *     <i>B</i>(&psi;) &le; <b>B</b>(&not;&phi;);
     * <li>redistribute the degrees of membership of all extant formulas &psi;,
     *     now ranging in [<b>B</b>(&not;&phi;), 1], by remapping them to the
     *     full (0, 1] interval: <i>B</i>'(&psi;) =
     *     1 - (1 - <i>B</i>(&psi;))/(1 - <b>B</b>(&not;&phi;));
     * <li>add formula &phi; with membership degree &tau; to the
     *     resulting contracted belief base;
     * <li>finally, for all formula &psi;,
     *     add &phi; &or; &psi; with membership degree <i>B</i>(&psi;);
     *     of course, only formulas with a non-zero mebership degree
     *     in the initial belief base will have to be considered.
     * </ol>
     * 
     * @param fact the new incoming fact
     * @param trust the degree to which the source of the incoming fact
     *              is trusted
     */
    public void updateBeliefs(Fact fact, TruthDegree trust)
    {
        // First of all, compute the degree to which the incoming
        // fact contradicts the agent's current beliefs:
        TruthDegree contradiction = beliefs.necessity(fact.negated());
        
        // Save a copy of the original belief base:
        PossibilisticFactBase revisedBeliefs = new PossibilisticFactBase();

        // 1. Remove from the base all facts whose degree of membership
        //    is not greater than the "contradiction", i.e.
        //    insert into the revised base only those facts whose degree
        //    of membership is greater than the "contradiction":
        Iterator<Fact> i = beliefs.factIterator();
        while(i.hasNext())
        {
            Fact psi = i.next();
            TruthDegree t = beliefs.membership(psi);
            if(!contradiction.isAtLeastAsTrueAs(t))
            {
        // 2. Redistribute the degrees of membership of all extant formulas,
        //    now ranging in [contradiction, 1],
        //    by remapping them to the full [0, 1] interval:
                TruthDegree tcond = new TruthDegree(1.0 -
                    t.negated().doubleValue() /
                    contradiction.negated().doubleValue());
                revisedBeliefs.tell(psi, tcond);
            }
        }
        
        // 3. for all fact in the base, add a new fact whose formula
        //    is the disjunction of the existing fact and the incoming
        //    fact:
        i = beliefs.factIterator();
        while(i.hasNext())
        {
            Fact psi = i.next();
            TruthDegree t = beliefs.necessity(psi);
            Fact disjunction = new Fact(new PropositionalFormula(Operator.OR, psi.formula(), fact.formula()));
            if(!revisedBeliefs.necessity(disjunction).isAtLeastAsTrueAs(t))
                revisedBeliefs.tell(disjunction, t);
        }
        
        // 4. Add the incoming fact to the base
        //    with the same membership degree as its degree of trust
        if(!revisedBeliefs.necessity(fact).isAtLeastAsTrueAs(trust))
           revisedBeliefs.tell(fact, trust);
        
        // Done! Now, replace the original beliefs:
        beliefs = revisedBeliefs;
        // The belief base must be simplified because, even if we checked
        // not to include redundant facts, depending on the order they were
        // inserted, some redundant facts may still be there:
        beliefs.simplify();
        
        // 5. Finally, update the mental state to reflect the
        //    changes in beliefs:
        updateDesires();
        updateObligations();
        updateGoals();
    }
    
    /**
     * Update the knowledge base with a new piece of knowledge
     * (a fact) with a given degree of truth.
     */
    public void tell(Fact fact, TruthDegree truth)
    {
        // TO DO
        
        // Finally, update the mental state to reflect the
        // changes in knowledge:
        updateDesires();
        updateObligations();
        updateGoals();
    }
    
    /**
     * Applies the rules in the given rule base to update the given fact set.
     * This is, in essence, the algorithm shown in Figure&nbsp;1 of
     * C&eacute;lia da Costa Pereira and Andrea G. B. Tettamanzi,
     * "Goal Generation and Adoption from Partially Trusted Beliefs", in
     * Proceedings of the European Conference on Artificial Intelligence
     * (ECAI 2008), Patras, Greece.
     */
    protected FactSet applyRules(RuleBase rb, FactSet factset)
    {
        // TO DO: test correctness
        FactSet result = new FactSet();
        Iterator<Rule> rit = rb.iterator();
        while(rit.hasNext())
        {
            Rule r = rit.next();
            TruthDegree t = r.activation(this);
            Fact cons = r.consequent();
            result.tell(cons, TruthDegree.snorm(t, justify(result, cons)));
        }
        return result;
    }
    
    /**
     * Updates the desire set.
     * <p> The algorithm used is the deliberation algorithm presented in
     * C&eacute;lia da Costa Pereira and Andrea G. B. Tettamanzi.
     * "An Integrated Possibilistic Framework for Goal Generation
     * in Cognitive Agents".
     * In Wiebe van der Hoek, Gal Kaminka, Yves Lesp&eacute;rance, Michael Luck,
     * and Sandip Sen (Editor(s)).
     * <em>Proceedings of the 9th International conference on autonomous agents
     * and multiagent systems (AAMAS&nbsp;2010)</em>, pages 1239&ndash;1246,
     * International foundation for autonomous agents and multiagent systems, 2010.
     * <a href="http://www.ifaamas.org/Proceedings/aamas2010/pdf/01 Full Papers/25_05_FP_0632.pdf"><strong>[Full text]</strong></a>
     * </p>
     */
    public void updateDesires()
    {
        boolean changed;

        Set<Atom> rhsAtoms = desRules.consequentAtomSet();
        utility = new PossibilityDistribution(rhsAtoms, TruthDegree.FALSE);
        
        do
        {
            // The following is what is called Deg in the AAMAS 2010 paper
            Map<Rule,TruthDegree> activations = new HashMap<Rule,TruthDegree>();
            
            // 1. Pre-compute rule activations, for the sake of efficiency.
            Iterator<Rule> rit = desRules.iterator();
            while(rit.hasNext())
            {
                Rule r = rit.next();
                activations.put(r, r.activation(this));
            }
            
            // 2. For all interpretations, update the qualitative utility:
            Iterator<Interpretation> i = new PropositionalInterpretationIterator(rhsAtoms);
            changed = false;
            while(i.hasNext())
            {
                Interpretation itp = i.next();
                
                // Consider the rules whose consequent is satisfied by the
                // interpretation, and compute the maximum of their activations:
                rit = desRules.iterator();
                TruthDegree t = TruthDegree.FALSE;
                while(rit.hasNext() && !t.isTrue())
                {
                    Rule r = rit.next();
                    if(r.consequent().formula().truth(itp).isTrue())
                        t = TruthDegree.snorm(t, activations.get(r));
                }
                changed = !t.equals(utility.possibility(itp));
                utility.possibility(itp, t);
            }
        }
        while(changed);
    }
    
    /**
     * Updates the obligation set.
     * This method is obsolete and should be replaced.
     * 
     * This is, in essence, the algorithm shown in Figure&nbsp;1 of
     * C&eacute;lia da Costa Pereira and Andrea G. B. Tettamanzi,
     * "Goal Generation and Adoption from Partially Trusted Beliefs", in
     * Proceedings of the European Conference on Artificial Intelligence
     * (ECAI 2008), Patras, Greece.
     */
    public void updateObligations()
    {
        // TO DO: test correctness
        boolean changed;
        
        do
        {
            FactSet updatedObligations = applyRules(oblRules, obligations);
            changed = !updatedObligations.equals(obligations);
            obligations = updatedObligations;
        }
        while(changed);
    }
    
    /**
     * Updates the goal set.
     * This method implements the <em>goal election function</em>
     * of the agent.
     * The goal set is the maximal consistent subset of the desire set.
     * 
     * <p>The implementation of this method follows Algorithms 2 and 3 in
     *  C&eacute;lia da Costa Pereira and Andrea G. B. Tettamanzi.
     * <a href="http://dx.doi.org/10.3233/978-1-60750-606-5-641">"Belief-Goal
     * Relationships in Possibilistic Goal Generation"</a>.
     * In Helder Coelho, Rudi Studer, and Michael Wooldridge (Editor(s)).
     * <em>ECAI 2010 &ndash; 19th European Conference on
     * Artificial Intelligence,  Lisbon, Portugal, August 16-20, 2010,
     * Proceedings</em> (ISBN: 978-1-60750-605-8), volume 215 of
     * <em>Frontiers in Artificial Intelligence and Applications</em>,
     * pages 641&ndash;646, IOS Press, 2010.</p>
     * 
     * <p>A known issue with this implementation is that either
     * no simplification of the goal formula is performed,
     * in which case the goal may be exceedingly
     * hard to read, although correct, or simplification is performed,
     * but then the method becomes too slow to be used in applications
     * where performance is an issue.</p>
     */
    public void updateGoals()
    {
        goals = new FactSet();
        
        SortedSet<TruthDegree> levels = beliefs.levelSet();
        while(!levels.isEmpty())
        {
            // Take the highest remaining degree in the belief base:
            TruthDegree gamma = levels.last();
            
            // Compute G_gamma by Algorithm 2:
            
            SortedSet<TruthDegree> uLevels = utility.levelSet();
            while(!uLevels.isEmpty())
            {
                // Take the highest remaining utility degree:
                TruthDegree delta = uLevels.last();
                
                // Determine the least specific formula phi
                // such that J(phi) >= delta: this is the disjunction
                // of all minterms of interpretations whose utility
                // is greater then or equal to delta:
                List<BooleanTerm> termList = new ArrayList<BooleanTerm>();
                Iterator<Interpretation> i = utility.interpretations();
                while(i.hasNext())
                {
                    Interpretation itp = i.next();
                    if(utility.possibility(itp).isAtLeastAsTrueAs(delta))
                        termList.add(new BooleanTerm((PropositionalInterpretation) itp));
                }
                BooleanFormula f = new BooleanFormula(termList);
                // Simplify the formula:
                f.simplify();
                PropositionalFormula phi = f.toPropositionalFormula();
            
                // If the possibility of phi is at least gamma,
                // terminate with G_gamma = {phi}:
                if(phi!=null)
                {
                    Fact goal = new Fact(phi);
                    if(beliefs.possibility(goal).isAtLeastAsTrueAs(gamma))
                    {
                        goals.tell(goal);
                        break;
                    }
                }
            
                // Otherwise, consider the next value of delta:
                uLevels = uLevels.headSet(delta);
            }
            if(goals.size()>0) break;
            levels = levels.headSet(gamma);
        }
    }
    
    /**
     * Returns a string representation of this agent.
     */
    @Override
    public String toString()
    {
        String s = "agent(" + name + ")\n{";
        s += "\n  knowledge\n  "   + knowledge;
        s += "\n  beliefs\n  "     + beliefs;
        s += "\n  obligation rules\n  " + oblRules;
        s += "\n  desire rules\n  " + desRules;
        s += "\n  obligations\n  " + obligations;
        s += "\n  desires\n  "     + desires;
        s += "\n  goals\n  "       + goals;
        return s + "\n}";
    }
}
