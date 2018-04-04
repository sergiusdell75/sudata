
import java.io.IOException;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import SOM.*;
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
     }
   
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
        boolean appendTr =false;
        boolean addCheck;
        int   ix,iy, i;
        int   ny,nx,nz;
        float [] data;
        float [][][] dataTmp;
        double [][] Tslice;
        int trsize; 
        int maxiz=(int)(zmax/dz);  //determine the number of the depth points
        
        nz=maxiz+1;
        OutS.traces = new ArrayList<>();
        data = new float[ns];
        Trace itr=null;    
        itr=new Trace(ns);
        //omp parallel for
        i=0;
        trsize=InS.traces.size(); //rectangle for tests
        ny=(int)sqrt(trsize);
        nx=ny;
        dataTmp= new float[ns][nx][ny];  
        Tslice = new double[nx][ny]; 
        // for now create 1) empty output in the scratch and 2) 3D temporary matrix
        // 
        i=0;
        for (iy=0; iy<ny;iy++) { 
            for (ix=0; ix<nx;ix++) {
                i=ix+iy*nx; //  limited to trsize
                Trace otr =null;
                otr=new Trace(ns);
                itr=InS.traces.get(i);
                Gx = itr.gx;
                Sy = itr.sy;
                Sx = itr.sx;
                Gy = itr.gy;
           
                otr.nt=ns;
                otr.fldr=i+1;
                otr.gx = (int)Gx;
                otr.gy = (int)Gy;
                otr.sx = (int)Sx;
                otr.sy = (int)Sy;
                otr.cdp=itr.cdp;
                otr.delrt=itr.delrt;
                otr.f1=itr.f1;
                otr.f2=itr.f2;           
                otr.dt = (int)(dt*1000);
                otr.d1=itr.d1;
                otr.d2=itr.d2;          	   
                if (i%50==0) System.out.println("INFO: Processing  current trace : " + String.valueOf(i+1)
                   + " from " + String.valueOf(trsize) + " trace  is done");
//Loop over all depth points
                System.arraycopy(itr.data, 0, data, 0, ns);
                for (int ii=0; ii<ns; ii++) {
                    otr.data[ii]=0.0f;
                    dataTmp[ii][ix][iy]=data[ii];
                }
                if (appendTr)
                    try{OutS.appendTrace(Outfile,otr);} catch (IOException ex) {}
                else
                     addCheck = OutS.traces.add(otr);     
            }
        }
        // start PCA and 
        for (int ii=0; ii<ns; ii++) {     
            for (iy=0; iy<ny;iy++) { 
                for (ix=0; ix<nx;ix++) {
                    Tslice[ix][iy]=(double)dataTmp[ii][ix][iy];
                }
            }
            PCALight tPCA=new PCALight(false,false);
            tPCA.setNs(nx,nz);
            tPCA.setData(Tslice);
            tPCA.setNumComps(1);
            tPCA.process();
            Tslice=tPCA.getScores();
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
