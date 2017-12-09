/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author emil
 */
public class Trace {
    double [] data;
    double delrt;
    double dt;
    float f1;
    float f2;
    float d1;
    float d2;
    int sx;
    int sy;
    int gx;
    int gy;
    int cdp;
    int fldr;
    int nt;
    long pos;
    
    public Trace( TraceHeader  trheader, long apos ){
      delrt = trheader.delrt;
      dt = trheader.dt;
      nt = trheader.ns;
      f1 = trheader.f1;
      f2 = trheader.f2;
      d1 = trheader.d1;
      d2 = trheader.d2;
      sx= trheader.sx;
      sy= trheader.sy;
      gx= trheader.gx;
      gy= trheader.gy;  
      cdp = trheader.cdp;
      fldr = trheader.fldr;
      pos = apos;
      this.data=null;
    }
    public Trace( final Trace tr ){
        data = null;
        copyTrace( tr );
}

    public Trace(int nt){
        this.data=new double[nt];
          delrt = 0;
          dt  = 0;
          f1 = 0;
          f2 = 0;
          d1 = 0;
          d2 = 0;
          sx  = 0;
          sy  = 0;
          gx  = 0;
          gy  = 0;  
          cdp = 0;
          fldr= 0;
          this.nt = nt;
    }
    private void copyTrace(final Trace tr ){
         cdp = tr.cdp;
         fldr = tr.fldr;
         pos = tr.pos;
         delrt = tr.delrt;
         dt = tr.dt;
         f1 = tr.f1;
         f2 = tr.f2;
         d1 = tr.d1;
         d2 = tr.d2;
         sx= tr.sx;
         sy= tr.sy;
         gx= tr.gx;
         gy= tr.gy; 
         if ( tr.data!=null ){
             this.data=new double[tr.nt];
             System.arraycopy(tr.data, 0, this.data, 0, nt);
         }
         else {
             nt = tr.nt;
             this.data = null;
         } 
    }
    
    private void close(){
        if(this.data != null)
            this.data=null;
    }
    
};

