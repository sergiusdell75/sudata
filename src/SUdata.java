
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import static java.lang.Math.ceil;
import java.nio.ByteBuffer;
/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */

public class SUdata {
    char [] fname;
    int delrt, dt, nt;
    private final String inname;
    private final String outname;
    
     public SUdata(String inname, String outname){
         this.inname =inname;
         this.outname =outname;
}

    public  FileInputStream fin = null;
    public  FileOutputStream fout = null;

    Trace [] traces;

  public void readHeader(){
  }
  public void readFile(){
  }
 

  public void writeHeader( Trace tr ) throws IOException{
    TraceHeader header = new TraceHeader();
    header.sy = tr.sy;
    header.sx = tr.sx;
    header.gy = tr.gy;
    header.gx = tr.gx;
    header.f1 = tr.f1;
    header.f2 = tr.f2;
    header.d1 = tr.d1;
    header.d2 = tr.d2;
    header.cdp = tr.cdp;
    header.delrt = (short) (tr.delrt);
    header.dt = (short)(tr.dt);
    header.ns = (short) (tr.nt);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    @SuppressWarnings("UnusedAssignment")
    ObjectOutput out = null;
    try {
        out = new ObjectOutputStream(bos);   
        out.writeObject(header);
        out.flush();
        byte[] buffer = bos.toByteArray();
        fout.write(buffer);
    } finally {
        try {
        bos.close();
        } catch (IOException ex) {
    // ignore close exception
        }
    }
   
  }
    public static byte[] convertToByteArray(double [] value, int nt) {
      byte[] bytes = new byte[8*nt];
      ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
      for (int i=0; i<nt; i++)
           buffer.putDouble(value[i]);
      return buffer.array();

  }
    
    public static byte[] convertTraceToByteArray(TraceHeader h) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        @SuppressWarnings("UnusedAssignment")
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);   
            out.writeObject(h);
            out.flush();
            byte[] buffer = bos.toByteArray();
            return buffer;
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
    // ignore close exception
            }
        }
 }
  public void write(){
    try {
        fout = new FileOutputStream(outname);
        byte[] buffer;
        for (Trace it : traces) {
            writeHeader( it );
            buffer=convertToByteArray(it.data,it.nt);
            fout.write(buffer);
        }
    } catch (IOException ex){
        System.out.println("Error writing file '" + outname + "'");
    }
  }
  
  public void append( Trace tr) throws IOException{ 
    
 // fout.open( fname, ios::app );
  writeHeader( tr );

 
  }
  
  public void writeTrace(String name, Trace tr, boolean append) throws IOException{
     if(tr.data!=null)
    {
        FileOutputStream f=null;
        TraceHeader h= new TraceHeader();
        if(append)
            f= new FileOutputStream(outname, true);
        else
	    f= new FileOutputStream(outname, true);
        h.cdp=tr.cdp;
	h.sx=(int)(tr.sx);
	h.gx=(int)(tr.gx);
        h.ns=(short)(tr.nt);
        h.dt=(short)(ceil(tr.dt*1000000.f));
        h.delrt=(short)(ceil(tr.delrt*1000.f));
        h.f1=tr.f1;
        h.d1=tr.d1;
        h.f2=tr.f2;
        h.d2=tr.d2;
        byte [] buffer1=convertTraceToByteArray( h );
        byte [] buffer2=convertToByteArray(tr.data,tr.nt);
        f.write(buffer1);
        f.write(buffer2);
    }
  }

}