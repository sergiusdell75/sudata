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

    /**
     * @param str
     * @return strA
     */
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
    
    public static void main(String[] args) {
        // TODO code application logic here
        float Gx;	
        float Gy;
        float v;
        double starttime;
        double endtime;
        float Value;
        float Value_temp, Val_down, Val_up;
        float ValueMax, ValueMaxM;
        float sembl_temp, sembl_n,  zmax, xmax, dz, maxz;
        float dt, tt, dtnew;
        int ns, itemp, iup, idown, isembl, iz, maxiz, time, nz;
        String str;
        String Inputfile;
        String Outputfile;
        String Velfile;
        String Output_name;
        float x, z;
        try {
            str=parserValue(args[0]);
            if (str==null) {
                throw new ParserException();
            }
        } catch (ParserException ex) {
            System.out.println("Input argument$" + "is missing");
        }
    }

    
}
