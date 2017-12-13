/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */
public class PrintProcessInfo  {
    String [] info1, info2;
    
    PrintProcessInfo(String [] info1, String [] info2){
        this.info1=info1;
        this.info2=info2;
    }
    
    public void print(){
        System.out.println("INFO: multithreaded seismic data processing");
        System.out.println("INFO: Copiright (C), Serg Dell DevLab");
        System.out.println("INFO: Using");
        int len=info1.length;
        for (int i=0; i<len; i++) {
            System.out.println("INFO: " + info1[i] + " "+ info2[i]);
        }
}
   
}
