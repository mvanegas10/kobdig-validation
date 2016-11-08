/*
 * Series.java, adapted from DailyQuoteSeries.java
 *
 * Created on February 11, 2006, 2:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package kobdig.market;

import java.util.*;


/** A historical series of quotes of a financial instrument.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Series
{
    /** The size of the series. */
    protected int size;
    
    /** The quotes. */
    protected double x[];
    
    /** The precomputed logarithmic returns. */
    protected double r[];
    
    /** Creates a new empty instance of DailyQuoteSeries. */
    public Series()
    {
        super();
    }
    
    /**
     * Creates a new instance of DailyQuoteSeries from a collection.
     * @param series a Collection containing daily closing quotes of a financial instrument
     */
    public Series(Collection<Integer> series)
    {
        readCollection(series);
    }
        
    /**
     * Reads the series from a collection of floating point values in double precision.
     *
     * @param series a series of daily closing quotes 
     */
    protected void readCollection(Collection<Integer> series)
    {
        size = series.size();
        if(size==0)
            return;
        x = new double[size];
        Iterator<Integer> i = series.iterator();
        for(int t = 0; i.hasNext(); t++)
            x[t] = (i.next()).doubleValue();
        if(size<2)
            return;
        r = new double[size - 1];
        for(int t = 1; t<size; t++)
            r[t - 1] = Math.log(x[t]/x[t - 1]); 
    }
    
    /**
     * Get the <VAR>t</VAR>th quote in the series.
     * @param t an index to an individual quote in the series (<CODE>0</CODE> is the index of the quote for the first day)
     * @return the requested quote
     */
    public double getQuote(int t)
    {
        if(t<0 || t>=size)
            return 0.0;
        return x[t];
    }
    
    /**
     * Get the square of the <VAR>t</VAR>th quote in the series.
     * @param t an index to an individual quote in the series (<CODE>0</CODE> is the index of the quote for the first day)
     * @return the square of the requested quote
     */
    public double getSqrQuote(int t)
    {
        if(t<0 || t>=size)
            return 0.0;
        return x[t]*x[t];
    }
    
    /**
     * Returns the log-return calculated on the <VAR>t</VAR>th element of the series.
     * The log-return of element <VAR>x</var>(<VAR>t</VAR>) of series <VAR>X</VAR>
     * is defined as log(<VAR>x</var>(<VAR>t</VAR>)/<VAR>x</var>(<VAR>t - 1</VAR>).
     * For this reason, the first element for which log-return can be calculated is
     * the one with index <CODE>1</CODE>.
     * Requesting a log-return for an out-of-range index does not cause an
     * exception to be thrown, but the result is zero.
     * @param t an index to an individual quote in the series (<CODE>0</CODE> is the index of the quote for the first day)
     * @return the log-return calculated on the <VAR>t</VAR>th element of the series
     */
    public double getLogReturn(int t)
    {
        if(t<1 || t>=size)
            return 0.0;
        return r[t - 1];
    }
    
    /**
     * Sample mean of the series.
     * @return the sample mean of the series
     */
    public double mean()
    {
        double sum = 0.0;
        for(int t = 0; t<size; t++)
            sum += x[t];
        return sum/size;
    }
    
    /**
     * Variance of the series w.r.t. a known mean.
     * @param m the (known) mean of the series.
     * @return the variance of the series w.r.t. the given mean
     */
    public double variance(double m)
    {
        double var = 0.0;
        
        for(int t = 0; t<size; t++)
        {
            double dev = x[t] - m;
            var +=  dev*dev;
        }
        return var/size;
    }
    
    /**
     * Variance of the series.
     * @return the variance of the series
     */
    public double variance()
    {
        return variance(mean());
    }

    /** Exponential smoothing backcast for GARCH variance initialization.
     *  This method is a Java porting of Matlab function <code>varbackcast</code>
     *  by TradingLab.
     *
     *  @return backcast of the long-term variance of the series.
     */
    public double varianceBackcast()
    {
        double unconditionalVariance = variance(0.0);
        // why do they compute the unconditional variance if they don't
        // use it afterwards?
        
        double lambda = .7;
        double sigsum = 0.0;
        for(int t = 0; t<size; t++)
            sigsum += Math.pow(lambda, size - t - 1.0)*getSqrQuote(size - t);
     
        // This doesn't make sense to me...
        // looks like a convex hull of sigsum and something else,
        // except lambda is raised to the n-th power...
        return Math.pow(lambda, size) + (1.0 - lambda)*sigsum;
    }
    
    /**
     * Return a moving average for the series for the given day.
     * The <var>n</var>-day moving average, MA(<var>n</var>), is a
     * very simple and popular delay technical indicator.
     *
     * @param periods the number of days back in time over which
     *   the moving average is to be computed
     * @param t the day for which the moving average is to be computed
     * @return the value of MA(<code>periods</code>) on day <code>t</code>
     */
    public double movingAverage(int periods, int t)
    {
        if(periods>t + 1 || periods<1)
            throw new IndexOutOfBoundsException("MA(" + periods + ") is undefined at time " + t);
        double sum = 0.0;
        for(int i = t - periods + 1; i<=t; i++)
            sum += x[i];
        return sum/periods;
    }
    
    /**
     * Return an exponential moving average for the series for the given day.
     * The <var>n</var>-day exponential moving average, EMA(<var>n</var>), is a
     * very popular delay technical indicator.
     *
     * @param periods the period of the exponential moving average, used to
     *                compute the smoothing factor
     *                <var>s</var> = 2/(<code>periods</code> + 1)
     * @param t the day for which the moving average is to be computed
     * @return the value of EMA(<code>periods</code>) on day <code>t</code>
     */
    public double expMovingAverage(int periods, int t)
    {
        if(periods<1)
            throw new IndexOutOfBoundsException("EMA(" + periods + ") is undefined at time " + t);
        double alpha = 2.0/(periods + 1);
        double ema = 0.0;
        for(int i = 0; i<=t; i++)
            ema += alpha*(x[i] - ema);
        return ema;
    }
    
    /**
     * Returns the "standard" MACD for the series for the given day.
     * <p>Developed by Gerald Appel, Moving Average Convergence/Divergence (MACD)
     * is one of the simplest and most reliable indicators available.
     * MACD uses moving averages, which are lagging indicators, to include some
     * trend-following characteristics. These lagging indicators are turned
     * into a momentum oscillator by subtracting the longer moving average
     * from the shorter moving average. The resulting plot forms a line that
     * oscillates above and below zero, without any upper or lower limits.</p>
     * <p>The "standard" formula for the MACD is the difference
     * between a security's 26-day and 12-day Exponential Moving Averages (EMAs).
     * This is the formula that is used in many popular technical analysis
     * programs.
     */
    public double MACD(int t)
    {
        return MACD(26, 12, t);
    }

    /**
     * Returns the MACD technical indicator for the series for the given day.
     * <p>Developed by Gerald Appel, Moving Average Convergence/Divergence (MACD)
     * is one of the simplest and most reliable indicators available.
     * MACD uses moving averages, which are lagging indicators, to include some
     * trend-following characteristics. These lagging indicators are turned
     * into a momentum oscillator by subtracting the longer moving average
     * from the shorter moving average. The resulting plot forms a line that
     * oscillates above and below zero, without any upper or lower limits.</p>
     */
    public double MACD(int slow, int fast, int t)
    {
        if(slow<=fast)
            throw new IndexOutOfBoundsException("MACD(" + slow + " - " +
                    fast + ") is undefined at time " + t);
        return expMovingAverage(slow, t) - expMovingAverage(fast, t);
    }
    
    /**
     * Calculates the signal line for the MACD (Moving Average
     * Convergence/Divergence), which is the difference between a short and a
     * long term moving average for a field. The signal line is a moving
     * average of the MACD used for generating entry/exit signals.
     * The MACD signal line is a 9-period exponential moving average of the MACD
     * (Moving Average Convergence/Divergence).
     * <p>The current implementation is quite inefficient; if this method were
     * to be called repeatedly for subsequent periods, the MACD series could be
     * computed only once.</p>
     *
     * @return the 9-period exponential moving average of the MACD
     */
    public double MACDSignal(int t)
    {
        Series macd = new Series();
        macd.x = new double[macd.size = t + 1];
        for(int i = 0; i<=t; i++)
            macd.x[i] = MACD(i);
        return macd.expMovingAverage(9, t);
    }
    
    /**
     * Computes the momentum technical indicator.
     */
    public double momentum(int periods, int t)
    {
        if(periods>t || periods<1)
            throw new IndexOutOfBoundsException("Momentum(" + periods + ") is undefined at time " + t);
        return x[t] - x[t - periods];
    }
    
    /**
     * Computes the rate of change (or ROC) technical indicator.
     */
    public double rateOfChange(int periods, int t)
    {
        if(periods>t || periods<1)
            throw new IndexOutOfBoundsException("ROC(" + periods + ") is undefined at time " + t);
        return 100.0*(x[t] - x[t - periods])/x[t - periods];
    }
    
    /**
     * Computes the relative strength index (RSI) with the given number of periods.
     */
    public double relativeStrengthIndex(int periods, int t)
    {
        double rs = upward().expMovingAverage(periods, t - 1)/downward().expMovingAverage(periods, t - 1);
        return 100.0 - 100.0/(1 + rs);
    }
    
    /**
     * Create a new historical series from this one containing the upward changes.
     * The upward change for a day <var>t</var> is defined as
     * max{0, <var>x</var><sub><var>t</var></sub> - <var>x</var><sub><var>t</var> - 1</sub>}.
     * The new series contains one day less than the series it derives from; the
     * missing day is the first.
     */
    public Series upward()
    {
        Series up = new Series();
        up.x = new double[up.size = size - 1];
        for(int t = 0; t<up.size; t++)
        {
            up.x[t] = x[t + 1] - x[t];
            if(up.x[t]<0.0)
                up.x[t] = 0.0;
        }
        return up;
    }
    
    /**
     * Create a new historical series from this one containing the downward changes.
     * The downward change for a day <var>t</var> is defined as
     * max{0, <var>x</var><sub><var>t - 1</var></sub> - <var>x</var><sub><var>t</var></sub>}.
     * The new series contains one day less than the series it derives from; the
     * missing day is the first.
     */
    public Series downward()
    {
        Series down = new Series();
        down.x = new double[down.size = size - 1];
        for(int t = 0; t<down.size; t++)
        {
            down.x[t] = x[t] - x[t + 1];
            if(down.x[t]<0.0)
                down.x[t] = 0.0;
        }
        return down;
    }
    
    /**
     * Create a new historical series from this one by removing the trend
     * by means of a moving average of the given depth.
     * Because of the way the trend is removed from the series, the first
     * <code>depth</code> quotes are lost.
     *
     * @param depth the depth of the moving average used to remove the trend.
     * @return a new daily quote series oscillating around zero
     */
    public Series removeDelayedTrend(int depth)
    {
        if(depth<1 || depth>=size)
            throw new IndexOutOfBoundsException();
        Series oscillator = new Series();
        oscillator.x = new double[oscillator.size = size - depth];
        if(oscillator.size>1)
            oscillator.r = new double[oscillator.size - 1];
        for(int t = 0; t<oscillator.size; t++)
        {
            oscillator.x[t] = x[t + depth] - movingAverage(depth, t + depth);
            if(t>0)
                oscillator.r[t - 1] = Math.log(oscillator.x[t]/oscillator.x[t - 1]);
        }
        return oscillator;
    }
        
    /**
     * Create a new historical series from this one by removing the
     * given linear trend.
     * This method is intended for use on logarithmic series only.
     * Applying it to a linear series is possible, but would be a conceptual error.
     * @param c the constant coefficient of the straight line representing the trend
     * @param r the angular coefficient of the straight line representing the trend
     * @return a new daily quote series oscillating around zero
     * @see #trend
     */
    public Series removeLinearTrend(double c, double r)
    {
        Series oscillator = new Series();
        oscillator.x = new double[oscillator.size = size];
        if(size>1)
            oscillator.r = new double[size - 1];
        for(int t = 0; t<size; t++)
        {
            oscillator.x[t] = x[t] - c - r*t;
            if(t>0)
                oscillator.r[t - 1] = this.r[t - 1] - r;
        }
        return oscillator;
    }

    /**
     * Create a new historical series from this one by taking the logarithm of prices.
     * The log returns of the new series are identical to those of the original series
     * and equal the differences for the new series.
     *
     * @return a new logarithmic daily quote series.
     */
    public Series logarithmic()
    {
        Series logarithmic = new Series();
        logarithmic.x = new double[logarithmic.size = size];
        logarithmic.r = r;
        for(int t = 0; t<size; t++)
            logarithmic.x[t] = Math.log(x[t]);
        return logarithmic;
    }    
}
