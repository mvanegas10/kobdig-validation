package kobdig.gui;

import kobdig.agent.AplTokenizer;
import kobdig.agent.Fact;
import kobdig.logic.Formula;
import kobdig.logic.PropositionalFormula;
import kobdig.logic.TruthDegree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * A fact parser. Parses a fact from a string as follows: <belief>:<trust degree>
 *
 *
 * Created by Meili on 7/5/16.
 */
public class FactParser {

    /**
     * The resultant fact
     */
    private Fact fact;

    /**
     * The trust degree for the fact
     */
    private TruthDegree trust;

    /**
     * FactParser's Constructor
     * @param string The string to be parsed
     */
    public FactParser(String string){
        String[] data = string.split(":");
        this.fact = null;
        this.setFact(data[0]);
        this.trust = new TruthDegree(Double.parseDouble(data[1]));
    }

    /**
     * Set's the respective fact from a given string
     * @param string The string to be parsed
     */
    public void setFact(String string){
        try
        {
            Reader r = new BufferedReader(new StringReader(string));
            AplTokenizer st = new AplTokenizer(r);
            st.nextToken();
            Formula phi = new PropositionalFormula(st);
            fact = new Fact(phi);
        }
        catch(IOException e)
        {
            fact = null;
            System.out.println("Couldn't parse string");
            e.printStackTrace();
        }
    }

    /**
     * Returns the resultant fact
     * @return the resultant fact
     */
    public Fact getFact() {
        return fact;
    }

    /**
     * Returns the resultant truth degree
     * @return the resultant truth degree
     */
    public TruthDegree getTrust() {
        return trust;
    }
}
