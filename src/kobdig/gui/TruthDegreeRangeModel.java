/*
 * InputVariableRangeModel.java
 *
 * Created on March 3, 2006, 3:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig.gui;

import javax.swing.*;
import javax.swing.event.*;

/**
 * An ancillary class for storing the range of the input slider
 * used by the GUI to allow the user to interactively enter a truth degree.
 * The true range of the variable is mapped onto a fictitious integer range
 * [0, 100].
 *
 * @author Andrea G. B. Tettamanzi
 */
public class TruthDegreeRangeModel implements BoundedRangeModel
{
    /** The width of the range. */
    protected static final int RANGE = 100;
    
    /**
     * A change event that will be used to notify change listeners of
     * changes in state of the data model.
     */
    protected ChangeEvent changeEvent;
    
    /** A list of event listeners for this object. */
    protected EventListenerList listenerList;

    /**
     * The minimum of the actual slider range.
     * This is set once and for all in the constructor. Any attempt to
     * change its value is ignored by this implementation.
     */
    protected double minimum;
    
    /**
     * The maximum of the actual slider range
     * This is set once and for all in the constructor. Any attempt to
     * change its value is ignored by this implementation.
     */
    protected double maximum;
    
    /**
     * This would be the amount of the actual "jump" allowed when the user clicks
     * on the slider bar instead of dragging the knob.
     * However, this implementation sets the extent to zero and ignores
     * any attempts to change its value.
     */
    protected double extent;
    
    /**
     * The actual value corresponding to the knob position.
     * The initial value is set by the constructor to the midrange,
     * i.e., value = (minimum + maximum)/2.
     */
    protected double value;
    
    /**
     * The number of integer increments which correspond to a unit double
     * increment. Used to translate the position of the slider knob from
     * its internal integer representation to the actual double value and
     * <em>vice versa</em>.
     */
    protected double multiplier;
    
    /**
     * Set by swing components when a dragging action is underway.
     */
    protected boolean isAdjusting = false;
    
    /**
     * Construct a new data model corresponding to the given range.
     * The range is assigned here once and for all; any subsequent attempt to
     * modify it is ignored by this implementation.
     * Of course, it must be <code>min</code> &lt; <code>max</code>,
     * otherwise a runtime exception is thrown, with an "Invalid range"
     * explanation.
     *
     * @param min the left boundary of the range.
     * @param max the right boundary of the range.
     */
    public TruthDegreeRangeModel(double min, double max)
    {
        if(min>=max)
            throw new RuntimeException("Invalid range");
        changeEvent = new ChangeEvent(this);
        listenerList = new EventListenerList();
        minimum = min;
        maximum = max;
        value = (min + max)/2;
        extent = 0.0;
        multiplier = (double) (max - min)/RANGE;
        fireStateChanged();
    }

    /**
     * Sets the model's extent.  The <I>newExtent</I> is forced to 
     * be greater than or equal to zero and less than or equal to
     * maximum - value.   
     * <p>
     * When a BoundedRange model is used with a scrollbar the extent
     * defines the length of the scrollbar knob (aka the "thumb" or
     * "elevator").  The extent usually represents how much of the 
     * object being scrolled is visible. When used with a slider,
     * the extent determines how much the value can "jump", for
     * example when the user presses PgUp or PgDn.
     * <p>
     * Notifies any listeners if the model changes.
     * 
     * 
     * @param newExtent the model's new extent
     * @see #getExtent
     * @see #setValue
     */
    @Override
    public void setExtent(int newExtent)
    {
        // Do nothing.
    }

    /**
     * Sets the model's maximum to <I>newMaximum</I>. The other 
     * three properties may be changed as well, to ensure that
     * <pre>
     * minimum <= value <= value+extent <= maximum
     * </pre>
     * <p>
     * Notifies any listeners if the model changes.
     * This implementation just ignores invokations to this method.
     * 
     * @param newMaximum the model's new maximum
     * @see #getMaximum
     * @see #addChangeListener
     */
    @Override
    public void setMaximum(int newMaximum)
    {
        // Do nothing
    }

    /**
     * Sets the model's minimum to <I>newMinimum</I>.   The 
     * other three properties may be changed as well, to ensure 
     * that:
     * <pre>
     * minimum <= value <= value+extent <= maximum
     * </pre>
     * <p>
     * Notifies any listeners if the model changes.
     * This implementation just ignores invokations to this method.
     * 
     * @param newMinimum the model's new minimum
     * @see #getMinimum
     * @see #addChangeListener
     */
    @Override
    public void setMinimum(int newMinimum)
    {
        // Do nothing.
    }

    /**
     * Sets the model's current value to <code>newValue</code> if <code>newValue</code>
     * satisfies the model's constraints. Those constraints are:
     * <pre>
     * minimum <= value <= value+extent <= maximum
     * </pre>
     * Otherwise, if <code>newValue</code> is less than <code>minimum</code> 
     * it's set to <code>minimum</code>, if its greater than 
     * <code>maximum</code> then it's set to <code>maximum</code>, and 
     * if it's greater than <code>value+extent</code> then it's set to 
     * <code>value+extent</code>.
     * <p>
     * When a BoundedRange model is used with a scrollbar the value
     * specifies the origin of the scrollbar knob (aka the "thumb" or
     * "elevator").  The value usually represents the origin of the 
     * visible part of the object being scrolled.
     * <p>
     * Notifies any listeners if the model changes.
     * This implementation just ignores invokations to this method.
     * 
     * @param newValue the model's new value
     * @see #getValue
     */
    @Override
    public void setValue(int newValue)
    {
        setActualValue(minimum + newValue*multiplier);
    }
    
    /**
     * Sets the model's actual value to <code>v</code> if <code>v</code>
     * satisfies the model's constraints. Those constraints are:
     * <pre>
     * minimum <= value <= value + extent <= maximum
     * </pre>
     * Otherwise, if <code>v</code> is less than <code>minimum</code> 
     * it is set to <code>minimum</code>, if its greater than 
     * <code>maximum</code> then it is set to <code>maximum</code>, and 
     * if it is greater than <code>value+extent</code> then it is set to 
     * <code>value + extent</code>.
     * <p>
     * Notifies any listeners if the model changes.
     * 
     * @param v the model's new value
     * @see #getActualValue
     */
    public void setActualValue(double v)
    {
        value = Math.floor(v*100.0)/100.0;
        if(value<minimum)
            value = minimum;
        if(value>maximum)
            value = maximum;
        fireStateChanged();
    }

    /**
     * This attribute indicates that any upcoming changes to the value
     * of the model should be considered a single event. This attribute
     * will be set to true at the start of a series of changes to the value,
     * and will be set to false when the value has finished changing.  Normally
     * this allows a listener to only take action when the final value change in
     * committed, instead of having to do updates for all intermediate values.
     * <p>
     * Sliders and scrollbars use this property when a drag is underway.
     * 
     * @param b true if the upcoming changes to the value property are part of a series
     */
    @Override
    public void setValueIsAdjusting(boolean b)
    {
        isAdjusting = b;
        fireStateChanged();
    }

    /**
     * Adds a ChangeListener to the model's listener list.
     * 
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    @Override
    public void addChangeListener(ChangeListener l)
    {
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * Removes a ChangeListener from the model's listener list.
     * 
     * @param l the ChangeListener to remove
     * @see #addChangeListener
     */
    @Override
    public void removeChangeListener(ChangeListener l)
    {
        listenerList.remove(ChangeListener.class, l);
    }

    /**
     * This method sets all of the model's data with a single method call.
     * The method results in a single change event being generated. This is
     * convenient when you need to adjust all the model data simultaneously and
     * do not want individual change events to occur.
     * <p>
     * This implementation actually changes the value and isAdjusting property
     * only; all other arguments are simply ignored.
     * 
     * @param value  an int giving the current value
     * @param extent an int giving the amount by which the value can "jump" (ignored)
     * @param min    an int giving the minimum value (ignored)
     * @param max    an int giving the maximum value (ignored)
     * @param adjusting a boolean, true if a series of changes are in
     *                    progress
     * @see #setValue
     * @see #setExtent
     * @see #setMinimum
     * @see #setMaximum
     * @see #setValueIsAdjusting
     */
    @Override
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting)
    {
        setValue(value);
        setValueIsAdjusting(adjusting);
    }

    /**
     * Returns the model's extent, the length of the inner range that
     * begins at the model's value.  
     * 
     * @return the value of the model's extent property
     * @see #setExtent
     * @see #setValue
     */
    @Override
    public int getExtent()
    {
        return (int) Math.floor(extent/multiplier);
    }

    /**
     * Returns the model's maximum.  Note that the upper
     * limit on the model's value is (maximum - extent).
     * 
     * @return the value of the maximum property.
     * @see #setMaximum
     * @see #setExtent
     */
    @Override
    public int getMaximum()
    {
        return RANGE;
    }

    /**
     * Returns the minimum acceptable value.
     * 
     * @return the value of the minimum property
     * @see #setMinimum
     */
    @Override
    public int getMinimum()
    {
        return 0;
    }

    /**
     * Returns the model's current value.  Note that the upper
     * limit on the model's value is <code>maximum - extent</code> 
     * and the lower limit is <code>minimum</code>.
     * 
     * @return the model's value
     * @see #setValue
     */
    @Override
    public int getValue()
    {
        return (int) Math.floor((value - minimum)/multiplier);
    }
    
    /**
     * Returns the model's current actual value. Note that the upper
     * limit on the model's value is <code>maximum - extent</code> 
     * and the lower limit is <code>minimum</code>.
     * 
     * @return the model's value.
     * @see #setActualValue
     */
    public double getActualValue()
    {
        return value;
    }

    /**
     * Returns true if the current changes to the value property are part 
     * of a series of changes.
     * 
     * @return the valueIsAdjustingProperty.
     * @see #setValueIsAdjusting
     */
    @Override
    public boolean getValueIsAdjusting()
    {
        return isAdjusting;
    }
    
    /**
     * Notify all change listeners registered with this data model
     * that there has been a change in the data model.
     * Typically, a change means that the user has moved the
     * knob of an associated slider control to change the value
     * of an input variable.
     */
    protected void fireStateChanged()
    {
        Object[] listeners = listenerList.getListenerList();
        for(int i = listeners.length - 2; i >= 0; i -=2)
            if(listeners[i]==ChangeListener.class)
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
    }
}
