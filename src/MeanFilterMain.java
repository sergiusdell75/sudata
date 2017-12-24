
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import java.util.ListIterator;
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
        float Value;
        float Value_temp, Val_down, Val_up;
        float sembl_temp, sembl_n, maxz = 0;
        float tt;
        int   itemp, iup, idown, isembl, i, iz;
        float x,y,z;
        float dtnew=(float) (dt/1000000.);  ///change from sample intervall in SU form (msec) to the normal form in sec
        int maxiz=(int)(zmax/dz);        ///determine the number of the depth points
        int nz=maxiz+1;
        ListIterator<Trace> litr = InS.traces.listIterator();
        Trace otr = new Trace(ns);
        //omp parallel for
        // Xcord, Ycord, data
        i=0;
        while(litr.hasNext()){
         /// Output preparing: ///////////////////////////////////////////////////////////
           Gx = litr.next().gx;
           Gy = litr.next().gy;
	   otr.nt=nz;
	   otr.gx = (int)Gx;
           otr.gy = (int)Gy;
	   otr.dt = (int)(dz*1000);
           System.out.println("INFO: Processing  current trace : " + String.valueOf(i)
                   + " from " + String.valueOf(ntr) + " trace  is done");

///Loop over all depth points: /////////////////////////////////////////////////
            z=(float) 0.0;
            for (iz=0; iz < nz; iz++){
               double ValueMax = 0.;

///Loop over all samples: /////////////////////////////////////////////////
	      for (int ii=0; ii<ns; ii++) {
                ListIterator<Trace> itr = InS.traces.listIterator();
                Value=(float) 0.;
                sembl_temp=(float) 0.;
                isembl=0;        
		while( itr.hasNext()) {
                        x= Gx - itr.next().gx; 
                        y= Gy - itr.next().gy; 
			tt=(float) (ii*dtnew+sqrt(z*z + x*x + y*y)/vel); 
			idown=(int)(tt/dtnew); 
                        if (idown >= ns) {
                            idown=ns-1;
                        } else {}
                        iup=1+idown;
                        Val_down=(float) itr.next().data[idown]; 
			Val_up=(float) itr.next().data[iup];
                        Value_temp=Val_down + 
                                (Val_up-Val_down)*(tt - dtnew*idown)/(dtnew*iup - dtnew*idown);
                        Value+=Value_temp;
                     }
                    Value=abs(Value);
                    if (Value>ValueMax) ValueMax=Value;
                }
                z+=dz;
                otr.data[iz] = (float) ValueMax; /// write value for z
            }
            try {
                OutS.appendTrace(Outfile,otr,true);
            } catch (IOException ex) {
                Logger.getLogger(MeanFilterMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
    }
}
