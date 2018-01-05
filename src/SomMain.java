/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */
import SOM.*;

public class SomMain {
    
    SomMain(String str) {
        toParse= str.split("\n");
        toParam=new String[]{"input","output","vel","zmax","dz"};
        parResolve= ParseProcessParameter.parserAllValues(toParse,toParam);
    }
    
    String [] toParse;
    String [] toParam;
    String [] parResolve;
    
    String Infile = null;
    String Outfile = null;
   
    SUdata InS = null;
    SUdata OutS = null;
    
    float dt;
    int ns;
    int ntr;
    int numIter;
    boolean verbose;
    boolean debug;
    
    void setDebug(boolean debug){ this.debug=debug;}
    void setVerbose(boolean verbose){ this.verbose=verbose;}
    
    void setParams() throws ClassNotFoundException{
        Infile=parResolve[0];   
        Outfile=parResolve[1];
        numIter=Integer.parseInt(parResolve[2]);
        
        InS = new SUdata(Infile);
        OutS = new SUdata(Outfile);
        
        // read file
        InS.readFile();
        this.dt = (float)(InS.get_ds());         ///determine the sampling intervall
        this.ns = (int)(InS.get_nt()); 
        this.ntr = InS.get_ntr(); //traces.size();
        
        this.verbose=false;
        this.debug=false; 
    }
  
        void print(){
            System.out.println("Input: " + Infile);
            System.out.println("Output: "+ Outfile);
            System.out.println("Number of iterations: " + String.valueOf(numIter));
        }
       
        void myRun() throws ClassNotFoundException{
            setParams();
            print();
            process();
        }

    private void process() {
        double [][] indata=null;
        indata=new double[ns][ntr];
        PCALight myPCA;
        myPCA =new PCALight(verbose,debug);
        //fill data
        myPCA.setData(indata);
        myPCA.setNumComps(numIter);
        indata=myPCA.getScores();
    }
    
}
