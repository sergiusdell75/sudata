
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import static java.lang.Math.ceil;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;




public class SUdata {

    int delrt, dt, nt;
    private final String fname;
   
    
     public SUdata(String fname){
         this.fname =fname;
         
}

    public  FileInputStream fin = null;
    public  FileOutputStream fout = null;

    Trace [] traces;
    
    public static int sizeof(Class dataType){
        if (dataType == null) throw new NullPointerException();
        if (dataType == int.class    || dataType == Integer.class)   return 4;
        if (dataType == short.class  || dataType == Short.class)     return 2;
        if (dataType == byte.class   || dataType == Byte.class)      return 1;
        if (dataType == char.class   || dataType == Character.class) return 2;
        if (dataType == long.class   || dataType == Long.class)      return 8;
        if (dataType == float.class  || dataType == Float.class)     return 4;
        if (dataType == double.class || dataType == Double.class)    return 8;
        return 4; // 32-bit memory pointer... 
        // to use int size = numDouble * sizeof(double.class) + numInt * sizeof(int.class);
    }
    
  public void readHeader() throws ClassNotFoundException {
        int hsize = 0;
        int dsize = 0;
        TraceHeader header = null;
        try {
            hsize=convertTraceToByteArray(header).length;
        } catch (IOException ex) {
            Logger.getLogger(SUdata.class.getName()).log(Level.SEVERE, null, ex);
        }
        short scalco;
        try {
            fin=new FileInputStream(fname);
            byte[] hbuffer= new byte[hsize];
            int read = fin.read(hbuffer);
            header=returnTraceHeader(hbuffer);
            fin.close(); //close and reopen file. It's fast in this case!
            //
            fin=new FileInputStream(fname);
            dsize=header.ns*sizeof(double.class);
            int bsize=dsize+hsize;
            byte[] buffer = new byte[bsize];
            delrt = (int) (header.delrt);
            dt = (int) (header.dt);
            nt = (int) (header.ns); 
            while (fin.read(buffer) != -1) { 
            //read header
            //read trace
                //System.arraycopy(tr.data, 0, this.data, 0, nt)
            }
        } catch (IOException ex){
           System.out.println("ERROR: could not open file"+fname);
           Logger.getLogger(SUdata.class.getName()).log(Level.SEVERE, null, ex);
        } 

  }
  
  public void readFile(){
      
  }
 
  public TraceHeader returnTraceHeader(byte [] buffer){
        TraceHeader trh=null;
        try {
            ByteArrayInputStream ins = new ByteArrayInputStream(buffer) ;
        @SuppressWarnings("UnusedAssignment")
            ObjectInputStream in = new ObjectInputStream(ins);
            trh=(TraceHeader)in.readObject();
        }catch (IOException | ClassNotFoundException i){
            System.out.println("Trace object wasn't deserializid.");
        }
        return trh;
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
        fout = new FileOutputStream(fname);
        byte[] buffer;
        for (Trace it : traces) {
            writeHeader( it );
            buffer=convertToByteArray(it.data,it.nt);
            fout.write(buffer);
        }
    } catch (IOException ex){
        System.out.println("Error writing file '" + fname + "'");
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
            f= new FileOutputStream(fname, true);
        else
	    f= new FileOutputStream(fname, true);
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