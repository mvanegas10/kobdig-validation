/*
 * PropositionalAtom.java
 *
 * Created on April 3, 2008, 6:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

/**
 * An atomic proposition in the language of propositional logic.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class PropositionalAtom extends Atom
{
    
    /**
     * Creates a new propositional atom with the given name.
     *
     * @param n the name of the new atom
     */
    public PropositionalAtom(String n)
    {
        super(n);
    }
}
