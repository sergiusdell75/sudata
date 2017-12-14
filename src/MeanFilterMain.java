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
    
    float vel;
    float dt;
    float dz;
    float zmax;
    int ns;
    
    void run(){
        setParams();
        print();
        process();
    }
    
    void setParams(){
        Infile=parResolve[0];   
        Outfile=parResolve[1];
        vel=Float.parseFloat(parResolve[2]);
        zmax=Float.parseFloat(parResolve[3]); 
        dz=Float.parseFloat(parResolve[4]);
        
        SUdata InS = new SUdata(Infile);
        SUdata OutS = new SUdata(Outfile);
        dt = (float)(InS.dt);         ///determine the sampling intervall
        ns = (int)(InS.nt); 
        //Infile.readFile();
                 ///determine amount of the samples at the output trace
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
        float v;
        double starttime;
        double endtime;
        float Value;
        float Value_temp, Val_down, Val_up;
        float sembl_temp, sembl_n, maxz = 0;
        float tt;
        int   itemp, iup, idown, isembl, iz;
        float x, z;
        float dtnew=(float) (dt/1000000.);  ///change from sample intervall in SU form (msec) to the normal form in sec
        int maxiz=(int)(zmax/dz);        ///determine the number of the depth points
        int nz=maxiz+1; 
    }
        
        

}
