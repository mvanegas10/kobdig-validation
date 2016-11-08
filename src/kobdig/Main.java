/*
 * Main.java
 *
 * Created on April 3, 2008, 9:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kobdig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;
import kobdig.agent.Agent;
import kobdig.agent.Fact;
import kobdig.gui.MainFrame;
import kobdig.logic.*;
import kobdig.market.Market;
import kobdig.market.Simulation;

/**
 * Main class for the KOBDIG application.
 * KOBDIG stands for Knowledge, Obligations, Beliefs, Desires, Intentions, Goals. 
 *
 * @author Andrea G. B. Tettamanzi
 */
public class Main
{
    /** The main application window. */
    public static MainFrame mainframe;
    
    /** Version information. */
    public static final String version = "1.0";
    
    /** Counter of entailment checks performed. */
    public static int entailmentChecks;
    
    /** This class cannot have instances. */
    private Main()
    {
    }
    
    /**
     * This provisional method contains test code.
     * It should be replaced with appropriate JUnit testing
     * classes in the Test Packages branch of the application.
     */
    public static void test()
    {
        double beta = 0.5;
        System.out.print("n\ti\tchecks\tcard\n");
        // for(int trial = 0; trial<20; trial++)
        for(int n = 1; n<=12; n++)
        {
            PropositionalAtom[] language = new PropositionalAtom[n];
            for(int a = 0; a<n; a++)
                language[a] = new PropositionalAtom(String.format("%c", 'a' + a));
            Agent agent = new Agent();
            for(int i = 0; i<20; i++)
            {
                PropositionalFormula phi = PropositionalFormula.random(beta, language);
                TruthDegree t = new TruthDegree(0.1*Math.floor(Math.random()*10.0));
                // System.out.printf("n = " + n + ", i = " + i + ", B <- B * " +
                //         t + "/" + phi + ";\n");
                entailmentChecks = 0;
                agent.updateBeliefs(new Fact(phi), t);
                System.out.printf("%d\t%d\t%d\t%d\n", n, i, entailmentChecks, agent.beliefs().size());
            }
            // System.out.printf("B = " + agent.beliefs());
        }
    }
    
    public static final int INTERVAL = 1;
    
    /**
     * The Market Simulator main.
     */
    public static void marketSimulator()
    {
        System.out.println("Market Simulator...");
        
        Simulation sim = new Simulation(100);
        long hour = System.currentTimeMillis()/3600000;
        File logFile = new File("market_log_" + INTERVAL + "-" + hour + ".txt");
        File graphFile = new File("price_vol_" + INTERVAL + "-" + hour + ".txt");
        File histFile = new File("histogram_" + INTERVAL + "-" + hour + ".txt");
        PrintStream graph, histogram;
        try
        {
            Market.log = new PrintStream(logFile);
            graph = new PrintStream(graphFile);
            histogram = new PrintStream(histFile);
        }
        catch(FileNotFoundException e)
        {
            System.err.println("Could not open log file, appending to System.out.");
            Market.log = System.out;
            graph = System.out;
            histogram = System.out;
        }
        Market.log.println("At Generation 0");
        Market.log.println(sim);
        
        for(int t = 1; t<1000; t++)
        {
            sim.act();
            Market.step();
            System.out.println("" + t + "\t" + Market.instrument("XYZ").price() +
                    "\t" + Market.instrument("XYZ").volume());
            if(t % INTERVAL == 0)
            {
                sim.generation();
                Market.log.println("\nAfter " + t + " periods:");
                Market.log.println(sim);
                // System.err.println("Free memory: " + Runtime.getRuntime().freeMemory());
                // System.gc();
                // System.err.println("Free memory after GC: " + Runtime.getRuntime().freeMemory());
            }
        }
        sim.histogram(histogram);
        Market.log.println("\nEnd of Simulation:");
        Market.log.println(sim);
        
        Iterator<Integer> p = Market.prices().iterator();
        Iterator<Integer> v = Market.volumes().iterator();
        graph.println("Price\tVolume");
        while(p.hasNext())
            graph.println("" + p.next() + "\t" + v.next());
    }
    
    /**
     * Launch the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        if(args.length!=0)
            marketSimulator();
        else
            java.awt.EventQueue.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    String title = "KOBDIG v. " + version;
                    System.out.println(title);
                    mainframe = new MainFrame();
                    mainframe.setTitle(title);
                    mainframe.setVisible(true);
                }
            });
    }
    
}
