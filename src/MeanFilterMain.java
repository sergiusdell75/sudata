/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */
class RunnableDemo implements Runnable {
   private Thread t;
   private final String threadName;
   
   RunnableDemo( String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
   }
   
   @Override
   public void run() {
      System.out.println("Running " +  threadName );
      try {
         for(int i = 4; i > 0; i--) {
            System.out.println("Thread: " + threadName + ", " + i);
            // Let the thread sleep for a while.
            Thread.sleep(50);
         }
      } catch (InterruptedException e) {
         System.out.println("Thread " +  threadName + " interrupted.");
      }
      System.out.println("Thread " +  threadName + " exiting.");
   }
   
   public void start () {
      System.out.println("Starting " +  threadName );
      if (t == null) {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
}


public class MeanFilterMain {
    
    public static class ParserException extends Exception
{
      // Parameterless Constructor
      public ParserException() {}

      // Constructor that accepts a message
      public ParserException(String message)
      {
         super(message);
      }
 }
    public static String parserValue(String str){
        String [] strA= str.split("=");
        String tstr;
        boolean flag;
        flag=str.matches("(.*)"+strA[0]+"(.*)");
        tstr=(flag)?strA[1]:null;
        return tstr;
}
    public static String parserValues(String [] strArray, String str){
        String [] strA;
        boolean flag=false;
        int length = strArray.length;
        String tstr=null;
        //check if the to parse argument is in the parse stream
        for (int i=0; i< length;i++){
            flag=strArray[i].matches("(.*)"+str+"(.*)");
            if (flag) {
                strA=strArray[i].split("=");
                tstr=(str.matches("(.*)"+strA[0]+"(.*)"))?strA[1]:null;
                break;
            }
        } 
        return tstr; 
    }
    
    public static void main(String[] args) {
        // TODO code application logic here    
        if (args.length < 1) {
            System.out.println("Please provide processing parameters");
            System.exit(0);
        }
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
        String str;
        String Infile = null;
        String Outfile = null;
        String Velfile = null;
        float x, z;
        try {//parserValue(args[0]);
            str= parserValues(args, "input");
            if (str!=null) {
                Infile=str;
            } 
            else {
                throw new ParserException();
            }
        } catch (ParserException ex) {
            System.out.println("Input file argument is missing. Use input=");
        }
        try {
            str=parserValues(args,"output");
            if (str!=null) {
                Outfile=str;
            } 
            else {
                throw new ParserException();
            }
        } catch (ParserException ex) {
            System.out.println("Output file argument is missing. Use output=");
        }
        try {
            str=parserValues(args,"vel");
            if (str!=null) {
                vel=Double.parseDouble(str);
            } 
            else {
                throw new ParserException();
            }
        } catch (ParserException ex) {
            System.out.println("Velocity argument is missing. Use vel=");
        }
        try {
            str=parserValues(args,"zmax");
            if (str!=null) {
                zmax=Float.parseFloat(str);
            } 
            else {
                throw new ParserException();
            }
        } catch (ParserException ex) {
            System.out.println("Maximum depth argument is missing. Use zmax=");
        }
        try {
            str=parserValues(args,"dz");
            if (str!=null) {
                dz=Float.parseFloat(str);
            } 
            else {
                throw new ParserException();
            }
        } catch (ParserException ex) {
            System.out.println("Depth sampling argument is missing. Use dz=");
        }
        
        SUdata InS = new SUdata(Infile);
        SUdata OutS = new SUdata(Outfile);
        //Infile.readFile();
        
        dt = (float)(InS.dt);         ///determine the sampling intervall
        ns = (int)(InS.nt);           ///determine amount of the samples at the input trace
        dtnew=(float) (dt/1000000.);  ///change from sample intervall in SU form (msec) to the normal form in sec
        maxiz=(int)(maxz/dz);    ///determine the number of the depth points
        nz=maxiz+1;                  ///determine amount of the samples at the output trace

        System.out.println("INFO: multithreaded 3D localization of passive seismic events");
        System.out.println("INFO: Copiright (C), Stefan Duemmong & Sergius Dell");
        System.out.println("INFO: Using"); 
        System.out.println("Input: " + Infile);
        System.out.println("Output: "+ Outfile);
        System.out.println("Velocity: " + String.valueOf(vel));
        System.out.println("Depth discretization: "+ String.valueOf(zmax));
        System.out.println("Depth dampling: "+ String.valueOf(dz));
    }  

}
