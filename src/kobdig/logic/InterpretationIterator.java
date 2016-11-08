/*
 * InterpretationIterator.java
 *
 * Created on April 4, 2008, 6:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.logic;

import java.util.Iterator;

/**
 * An interpretation iterator is an iterator over the set of all possible
 * crisp interpretations over a given set of logical constructs.
 *
 * @author Andrea G. B. Tettamanzi
 */
public abstract class InterpretationIterator implements Iterator<Interpretation>
{    
    /**
     * The remove operation is not meaningful.
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove operation not supported");
    }
    
}
