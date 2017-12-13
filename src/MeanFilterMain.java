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
        toParse= str.split("\n");
        toParam=new String[]{"input","output","vel","zmax","dz"};
     };
   
    String [] toParse;
    String [] toParam;
    
        float Gx;	
        float Gy;
        float v;
        double starttime;
        double endtime;
        double vel = 0;
        float Value;
        float Value_temp, Val_down, Val_up;
        float ValueMax, ValueMaxM;
        float sembl_temp, sembl_n, zmax = 0, dz = 0, maxz = 0;
        float dt, tt, dtnew;
        int ns, itemp, iup, idown, isembl, iz, maxiz, time, nz;
        float x, z;
        String str;
        String Infile = null;
        String Outfile = null;
        String Velfile = null;
        
        ParseProcessParameter Par =new ParseProcessParameter();
        
        SUdata InS = new SUdata(Infile);
        SUdata OutS = new SUdata(Outfile);
        
        //Infile.readFile();
        void setParams(){
            String [] parResolve= ParseProcessParameter.parserAllValues(toParse,toParam);
             
            dt = (float)(InS.dt);         ///determine the sampling intervall
            ns = (int)(InS.nt);           ///determine amount of the samples at the input trace
            dtnew=(float) (dt/1000000.);  ///change from sample intervall in SU form (msec) to the normal form in sec
            maxiz=(int)(maxz/dz);        ///determine the number of the depth points
            nz=maxiz+1;                  ///determine amount of the samples at the output trace
        }
  
        void process(){
            System.out.println("Input: " + Infile);
            System.out.println("Output: "+ Outfile);
            System.out.println("Velocity: " + String.valueOf(vel));
            System.out.println("Depth discretization: "+ String.valueOf(zmax));
            System.out.println("Depth dampling: "+ String.valueOf(dz));
        }
}
