/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */
import SOM.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        OutS.traces = new ArrayList<>();
        boolean appendTr =false;
        boolean addCheck=true;
        Trace itr=null;    
        itr=new Trace(ns);
        int i=0;
        int trsize=InS.traces.size();
        for (i=0; i< trsize; i++){
            itr=InS.traces.get(i);
            System.arraycopy(itr.data, 0, indata[i], 0, indata[i].length);
        }    
        
        
        myPCA.setData(indata);
        myPCA.setNumComps(numIter);
        indata=myPCA.getScores();
        for (i=0; i< trsize; i++){
            Trace otr =null;
            otr=new Trace(ns);
            itr=InS.traces.get(i);       
            otr.nt=ns;
            otr.fldr=i+1;
            otr.gx = itr.gx;
            otr.gy = itr.sy;
            otr.sx = itr.sx;
            otr.sy = itr.gy;
            otr.cdp=itr.cdp;
            otr.delrt=itr.delrt;
            otr.f1=itr.f1;
            otr.f2=itr.f2;           
            otr.dt = (int)(dt*1000);
            otr.d1=itr.d1;
            otr.d2=itr.d2;          	   
            if (i%50==0) System.out.println("INFO: Processing  current trace : " + String.valueOf(i+1)
                   + " from " + String.valueOf(trsize) + " trace  is done");
            System.arraycopy(indata[i], 0, otr.data, 0, ns);

            if (appendTr)
                try{OutS.appendTrace(Outfile,otr);} catch (IOException ex) {}
            else
                 addCheck = OutS.traces.add(otr); 
        }    
        if (appendTr) {
            System.out.println("Data set is in scratch");
        } else {
            OutS.set_maxTrace(OutS.traces.size());
            OutS.set_minTrace(1);
            OutS.set_incTrace(1);
            OutS.set_dtOut((int)(dt*1000));
            OutS.set_ntOut(ns);
            try {
                OutS.write();
            } catch (IOException ex) {
                Logger.getLogger(MeanFilterMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
    }
}
