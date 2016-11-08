/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kobdig.market;

import java.util.Random;

/**
 * A zero-information trader, who buys and sells the XYZ asset at random.
 * This class has been written mainly as a mock-up implementation
 * of the Trader abstract class for testing purposes.
 *
 * @author Andrea G. B. Tettamanzi
 */
public class RandomTrader extends Trader
{
    /**
     * Creates a new random trader with the given unique identifier, initial
     * cash balance, and asset balance.
     */
    public RandomTrader(String id, int c, int a)
    {
        super(id, c, a);
    }
    
    @Override
    public void trade()
    {
        // Mock-up implementation:
        Random rand = new Random();
        
        if(rand.nextDouble() < ((double) cash - resCash)/((double) netAssetValue()))
        {
            // buy some XYZ
            int maxqty = (int) (cash/(Market.instrument("XYZ").price() + 1)/10);
            if(maxqty<1)
                maxqty = 1;
            int qty = 1 + rand.nextInt(maxqty);
            int price = Math.abs(Market.instrument("XYZ").price() + rand.nextInt(100) - 50);
            buy(qty, price);
        }
        else
        {
            // sell some XYZ
            if(asset - resAsset>9)
            {
                int qty = 1 + rand.nextInt((int)(asset - resAsset)/10);
                int price = Math.abs(Market.instrument("XYZ").price() + rand.nextInt(100) - 50);
                sell(qty, price);
            }
        }
    }
}
