
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright Sergius Dell DevLab
 */

/**
 * <h1> Passive seismic event localization. </h1>
 * The event localization is based on the diffraction stacking approach.
 * Diffraction stacking is a modified Kirchhoff amplitude summation method. 
 * The localization engine requires (constant) velocity or traveltime tables.
 * <p>
 * The localization exploits stacking operator duality which basically means
 * focusing/defocusing in order to improve the localization resolution.
 * @author Sergius Dell DevLab
 */

class MeanFilterMainThread extends Thread {
    
    // MeanFilterMainThread
    //Map
    @Override
    public void run(){
        
    }
    
}

public class MeanFilterMain {
    
    MeanFilterMain(String str){
        this.ns  = 0;
        this.vel = 0;
        this.dt  = 0;
        this.dz  = 0;
        this.zmax= 0;
        toParse= str.split("\n");
        toParam=new String[]{"input","output","vel","zmax","dz"};
        parResolve= ParseProcessParameter.parserAllValues(toParse,toParam);
     };
   
    String [] toParse;
    String [] toParam;
    String [] parResolve;
    
    String Infile = null;
    String Outfile = null;
   
    SUdata InS = null;
    SUdata OutS = null;
    
    float vel;
    float dt;
    float dz;
    float zmax;
    int ns;
    int ntr;
    
    void myRun() throws ClassNotFoundException{
        setParams();
        print();
        process();
    }
    
    void setParams() throws ClassNotFoundException{
        Infile=parResolve[0];   
        Outfile=parResolve[1];
        vel=Float.parseFloat(parResolve[2]);
        zmax=Float.parseFloat(parResolve[3]); 
        dz=Float.parseFloat(parResolve[4]);
        
        InS = new SUdata(Infile);
        OutS = new SUdata(Outfile);
        
        // read file
        InS.readFile();
        this.dt = (float)(InS.get_ds());         ///determine the sampling intervall
        this.ns = (int)(InS.get_nt()); 
        this.ntr = InS.get_ntr(); //traces.size();
         
        }
  
        void print(){
            System.out.println("Input: " + Infile);
            System.out.println("Output: "+ Outfile);
            System.out.println("Velocity: " + String.valueOf(vel));
            System.out.println("Depth discretization: "+ String.valueOf(zmax));
            System.out.println("Depth dampling: "+ String.valueOf(dz));
        }
       
        
    void process(){
        float Gx;	
        float Gy;
        float Sx;
        float Sy;
        float Value;
        float Value_temp, Val_down, Val_up;
        float sembl_temp, sembl_n, maxz = 0;
        float tt;
        boolean appendTr =false;
        boolean addCheck;
        int   itemp, iup, idown, isembl, i, iz;
        float x,y,z;
        float [] data ;
        int trsize; 
        int maxiz=(int)(zmax/dz);        ///determine the number of the depth points
        int nz=maxiz+1;

        OutS.traces = new ArrayList<>();
        Trace otr =null;
        otr=new Trace(nz);
        data = new float[ns];
        Trace itr=null;
        itr=new Trace(ns);
        //omp parallel for
        i=0;
        trsize=InS.traces.size();
        for (i=0; i< trsize; i++){
           itr=InS.traces.get(i);
           Gx = itr.gx;
           Sy = itr.sy;
           Sx = itr.sx;
           Gy = itr.gy;
           
	   otr.nt=nz;
           otr.fldr=i;
	   otr.gx = (int)Gx;
           otr.gy = (int)Gy;
           otr.sx = (int)Sx;
           otr.sy = (int)Sy;
           otr.cdp=itr.cdp;
           otr.delrt=itr.delrt;
           otr.f1=itr.f1;
           otr.f2=itr.f2;           
           otr.dt = (int)(dz*1000);
           otr.d1=itr.d1;
           otr.d2=itr.d2;          	   
           System.out.println("INFO: Processing  current trace : " + String.valueOf(i)
                   + " from " + String.valueOf(trsize) + " trace  is done");

//Loop over all depth points
            for (iz=0; iz < nz; iz++){
                double ValueMax = 0;
                System.arraycopy(itr.data, 0, data, 0, ns);
                for (int ii=0; ii<ns; ii++) ValueMax+=data[ii];
                otr.data[iz] = (float) ValueMax; /// write value for z
            }
            if (appendTr)
                try{OutS.appendTrace(Outfile,otr);} catch (IOException ex) {}
            else
                 addCheck = OutS.traces.add(otr);     
        }  
        if (appendTr) {
            System.out.println("Data is in the scratch");
        } else {
            OutS.set_maxTrace(OutS.traces.size());
            OutS.set_minTrace(1);
            OutS.set_incTrace(1);
            OutS.set_dtOut((int)(dz*1000));
            OutS.set_ntOut(nz);
            try {
                OutS.write();
            } catch (IOException ex) {
                Logger.getLogger(MeanFilterMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
