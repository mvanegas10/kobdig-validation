package kobdig.logic;

/* Copyright (c) 2012 the authors listed at the following URL, and/or
the authors of referenced articles or incorporated external code:
http://en.literateprograms.org/Quine-McCluskey_algorithm_(Java)?action=history&offset=20110925122251

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Quine-McCluskey_algorithm_(Java)?oldid=17357
*/

import java.util.*;
import java.io.*;

/**
 * A Boolean term, represented as an array of False (0), True (1),
 * or Don't Care (2) values.
 * 
 * <p>Adapted from George Vastianos,
 * <a href="http://en.literateprograms.org/Quine-McCluskey_algorithm_%28Java%29">Quine-McCluskey
 * algorithm (Java)</a>.</p>
 * 
 * @author George Vastianos, Andrea G. B. Tettamanzi
 */
public class BooleanTerm
{
    public static final byte False = 0;
    public static final byte True = 1;
    public static final byte DontCare = 2;
    private byte[] varVals;
    private PropositionalAtom[] vars; 

    public BooleanTerm(byte[] varVals, PropositionalAtom[] vars)
    {
        this.varVals = varVals;
        this.vars = vars;
    }
    
    /**
     * Constructs a Boolean term as the minterm of the provided
     * propositional interpretation.
     * 
     * @param itp an interpretation 
     */
    public BooleanTerm(PropositionalInterpretation itp)
    {
        List<PropositionalAtom> varList = new ArrayList<PropositionalAtom>(itp.atoms());
        Collections.sort(varList);
        vars = varList.toArray(new PropositionalAtom[varList.size()]);
        varVals = new byte[vars.length];
        for(int i = 0; i<varVals.length; i++)
            varVals[i] = itp.truth(vars[i]).isTrue() ? True : False;
    }

    public int getNumVars()
    {
        return varVals.length;
    }

    @Override
    public String toString()
    {
        String result = "{";
        for(int i=0; i<varVals.length; i++) {
            result += vars[i] + "=";
            if (varVals[i] == DontCare)
                result += "X";
            else
                result += varVals[i];
            result += " ";
        }
        result += "}";
        return result;
    }

    public BooleanTerm combine(BooleanTerm term)
    {
        int diffVarNum = -1; // The position where they differ
        for(int i=0; i<varVals.length; i++) {
            if (this.varVals[i] != term.varVals[i]) {
                if (diffVarNum == -1) {
                    diffVarNum = i;
                } else {
                    // They're different in at least two places
                    return null;
                }
            }
        }
        if (diffVarNum == -1) {
            // They're identical
            return null;
        }
        byte[] resultVars = varVals.clone();
        resultVars[diffVarNum] = DontCare;
        return new BooleanTerm(resultVars, vars);
    }
    
    public int countValues(byte value)
    {
        int result = 0;
        for(int i=0; i<varVals.length; i++) {
            if (varVals[i] == value) {
                result++;
            }
        }
        return result;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        } else if (o == null || !getClass().equals(o.getClass())) {
            return false;
        } else {
            BooleanTerm rhs = (BooleanTerm)o;
            return Arrays.equals(this.varVals, rhs.varVals);
        }
    }
    @Override
    public int hashCode()
    {
        return varVals.hashCode();
    }
    
    boolean implies(BooleanTerm term)
    {
        for(int i=0; i<varVals.length; i++) {
            if (this.varVals[i] != DontCare &&
                this.varVals[i] != term.varVals[i]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Convert this term to a conjunctive propositional formula.
     * 
     * @return 
     */
    public PropositionalFormula toPropositionalFormula()
    {
        PropositionalFormula phi = null;
        if(vars==null)
            return null;
        for(int i = 0; i<vars.length; i++)
        {
            PropositionalAtom atom = vars[i];
            if(varVals[i]!=DontCare)
            {
                PropositionalFormula literal = varVals[i]==True ?
                        new PropositionalFormula(atom) :
                        new PropositionalFormula(Operator.NOT, new PropositionalFormula(atom));
                if(phi==null)
                    phi = literal;
                else
                    phi = new PropositionalFormula(Operator.AND, literal, phi);
            }
        }
        return phi;
    }
}

