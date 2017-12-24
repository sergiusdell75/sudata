import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import static java.lang.Math.ceil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public class SUdata {

    private int delrt, dt, ntr, nt;
    private float ds;
    private final String fname;
    private FileInputStream fin = null;
    private FileOutputStream fout = null;
    private FileChannel inputChannel;
    private long nbytesRead;
    private final  long tracesToDump;
    private int nbytesPerTrace;
    
    List<Trace> traces;
    /*package*/ static final int LEN_REEL_HDR = 3200;
    /*package*/ static final int LEN_BINARY_HDR = 400;
    /*package*/ static final int NBYTES_PER_HDR = 240;
    /*package*/ static final int NINTS_PER_HDR = 60;
   
    
    public SUdata(String fname){
        this.tracesToDump = (int) -1L;
        this.nbytesPerTrace = 0;
        this.nbytesRead = 0L;
        this.fname =fname;     
    }
    
    public int get_dt(){return this.dt;}
    public int get_nt(){return this.nt;}
    public int get_ntr(){return this.ntr;}
    public float get_ds(){return this.ds;}
     
    public static int sizeof(Class dataType){
        if (dataType == null) throw new NullPointerException();
        if (dataType == int.class    || dataType == Integer.class)   return 4;
        if (dataType == short.class  || dataType == Short.class)     return 2;
        if (dataType == byte.class   || dataType == Byte.class)      return 1;
        if (dataType == char.class   || dataType == Character.class) return 2;
        if (dataType == long.class   || dataType == Long.class)      return 8;
        if (dataType == float.class  || dataType == Float.class)     return 4;
        if (dataType == float.class || dataType == Float.class)    return 8;
        return 4; // 32-bit memory pointer... 
        // to use int size = numFloat * sizeof(float.class) + numInt * sizeof(int.class);
    }
    
    
    public void ConvertTraceHeader(TraceHeader h, Trace oneTr){
        oneTr.dt=h.dt;
        oneTr.delrt=h.delrt;
        oneTr.dt=h.dt;
        oneTr.f1=h.f1;
        oneTr.f2=h.f2;
        oneTr.d1=h.d1;
        oneTr.d2=h.d2;
        oneTr.sx=h.sx;
        oneTr.sy=h.sy;
        oneTr.gx=h.gx;
        oneTr.gy=h.gy;
        oneTr.cdp=h.cdp;
        oneTr.fldr=h.fldr;
        oneTr.nt=h.ns;
    }
    
    public float [] ExtractData(byte [] dbuffer){
        float [] data=null;
        int i=0;
        ByteBuffer bb = ByteBuffer.wrap(dbuffer);
        FloatBuffer db = ((ByteBuffer) bb.rewind()).asFloatBuffer();
        while (db.hasRemaining())
            data[i]= db.get();
        return data;
    }
    
  public void readFile() throws ClassNotFoundException {
        int rhsize = LEN_REEL_HDR;
        int bsize = LEN_BINARY_HDR;
        int hsize = NBYTES_PER_HDR;
        int dsize = 0;
        traces = new ArrayList<>();
        Trace oneTr=null;        
        short scalco;
        try 
        {
            fin=new FileInputStream(fname);
            inputChannel = fin.getChannel();
            ByteBuffer reelHdrBuffer = ByteBuffer.allocate(LEN_REEL_HDR);
            int nRead = inputChannel.read(reelHdrBuffer);
            nbytesRead += nRead;
            if (nRead != LEN_REEL_HDR)  
                throw new IOException("Error reading SEG-Y reel header: " + nRead + "!=" + LEN_REEL_HDR);
            ByteBuffer binaryHdrBuffer = ByteBuffer.allocate(LEN_BINARY_HDR);
            nRead = inputChannel.read(binaryHdrBuffer);
            nbytesRead += nRead;
            if (nRead != LEN_BINARY_HDR)
                throw new IOException("Error reading SEG-Y binary header: " + nRead + "!=" + LEN_BINARY_HDR);
    // This magically makes all of the value come out properly when retrieved.  Nice!
            binaryHdrBuffer.order(ByteOrder.BIG_ENDIAN);
            ntr = binaryHdrBuffer.getShort(12);
            System.out.println("Number of traces = " + ntr);
            dt= (int)binaryHdrBuffer.getShort(16);
            ds = dt/ 1000.0F;
            System.out.println("Sampling interval= " + ds);
            nt = binaryHdrBuffer.getShort(20);
            System.out.println("Sample per trace= " + nt);
            nbytesPerTrace = nt * 4;
            delrt = binaryHdrBuffer.getShort(24);
            
            ByteBuffer hdrByteBuffer = ByteBuffer.allocateDirect(NBYTES_PER_HDR);
            hdrByteBuffer.order(ByteOrder.BIG_ENDIAN);
            ByteBuffer trcByteBuffer = ByteBuffer.allocateDirect(nbytesPerTrace);
            trcByteBuffer.order(ByteOrder.BIG_ENDIAN);
            IntBuffer hdrBuffer = hdrByteBuffer.asIntBuffer();
            FloatBuffer trcBuffer = trcByteBuffer.asFloatBuffer();

            boolean finished = false;
            long traceInFileCount = 1L;
            int traceInCount = 0;
            int frameKeyLast = Integer.MIN_VALUE;
            oneTr = new Trace(nt);
            while (!finished) {

                hdrByteBuffer.position(0);
                nRead = inputChannel.read(hdrByteBuffer);
                nbytesRead += nRead;
                if (nRead == -1) {
	// We have reached EOF.
                    finished = true;
                } else {
                    if (nRead != NBYTES_PER_HDR)
                        throw new IOException("Error reading SEG-Y trace header " + traceInFileCount
		+ ": " + nRead + "!=" + NBYTES_PER_HDR);
                    trcByteBuffer.position(0);
                    nRead = inputChannel.read(trcByteBuffer);
                    nbytesRead += nRead;
                    if (nRead != nbytesPerTrace)
                        throw new IOException("Error reading SEG-Y trace " + traceInFileCount
				+ ": " + nRead + "!=" + nbytesPerTrace);

                    try {
                        //oneTr.fldr = hdrByteBuffer.getShort(16);
                        oneTr.fldr = hdrByteBuffer.getInt(8);  
                        oneTr.cdp = hdrByteBuffer.getInt(20);
                        oneTr.nt = nt;
                        oneTr.dt = ds;
                        oneTr.pos = traceInCount;
                        oneTr.delrt = hdrByteBuffer.getInt(72);
	                oneTr.sx = hdrByteBuffer.getInt(72);
	                oneTr.sy = hdrByteBuffer.getInt(76);
                        oneTr.gx = hdrByteBuffer.getInt(80);
                        oneTr.gy = hdrByteBuffer.getInt(84);
                        oneTr.f1 = hdrByteBuffer.getInt(180);
                        oneTr.f2 = hdrByteBuffer.getInt(184);
                        oneTr.d1 = hdrByteBuffer.getInt(188);
                        oneTr.d2 = hdrByteBuffer.getInt(192);
                    } catch (RuntimeException re) {
                        System.out.println("Key headers are not recognized");
                     }

	// Fill the next trace and header.
	hdrBuffer.position(0);
	trcBuffer.position(0);
        trcBuffer.get(oneTr.data);
        boolean addtr = traces.add(oneTr);
        if (addtr!= true )
            throw new IOException("Failed to add trace: " + traceInCount);
	traceInCount++;

      }

      if (tracesToDump >= 0  &&  traceInFileCount >= tracesToDump) finished = true;

    }  // End of loop over all read statements.

    fin.close();
    
        } catch (IOException e) {}

  }
 
  
  public void readHeader( byte [] hbuffer){
      
  }
 
  public TraceHeader returnTraceHeader(byte [] buffer){
        TraceHeader trh=null;

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
    public static byte[] convertToByteArray(float [] value, int nt) {
      byte[] bytes = new byte[8*nt];
      ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
      for (int i=0; i<nt; i++)
           buffer.putFloat(value[i]);
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