import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Math.ceil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class SUdata {

    private int delrt, dt, ntr, nt;
    private int dtOut, ntOut;
    private float ds;
    private final String fname;
    private FileInputStream fin = null;
    private FileOutputStream fout = null;
    private FileChannel inputChannel;
    private long nbytesRead;
    private long nbytesWritten;
    private final  long tracesToDump;
    private int nbytesPerTrace;
    private int nbytesPerTraceOut;
    private int minTrace, maxTrace,traceInc;
    
    List<Trace> traces=null;
    /*package*/ static final int LEN_REEL_HDR = 3200;
    /*package*/ static final int LEN_BINARY_HDR = 400;
    /*package*/ static final int NBYTES_PER_HDR = 240;
    /*package*/ static final int NINTS_PER_HDR = 60;
   
    
    public SUdata(String fname){
        this.nbytesPerTraceOut = 0;
        this.nbytesWritten = 0L;
        this.tracesToDump = (int) -1L;
        this.nbytesPerTrace = 0;
        this.nbytesRead = 0L;
        this.fname =fname;     
    }
    
    public int get_dt(){return this.dt;}
    public int get_nt(){return this.nt;}
    public int get_ntr(){return this.ntr;}
    public float get_ds(){return this.ds;}
    public void set_minTrace(int minTrace){this.minTrace=minTrace;}
    public void set_maxTrace(int maxTrace){this.maxTrace=maxTrace;}   
    public void set_incTrace(int traceInc){this.traceInc=traceInc;}
    public void set_dtOut(int dtOut){this.dtOut=dtOut;}
    public void set_ntOut(int ntOut){this.ntOut=ntOut;}
    
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
            System.out.println("header " + reelHdrBuffer);
            ByteBuffer binaryHdrBuffer = ByteBuffer.allocate(LEN_BINARY_HDR);
            nRead = inputChannel.read(binaryHdrBuffer);
            nbytesRead += nRead;
            if (nRead != LEN_BINARY_HDR)
                throw new IOException("Error reading SEG-Y binary header: " + nRead + "!=" + LEN_BINARY_HDR);
    // This magically makes all of the value come out properly when retrieved.  Nice!
            binaryHdrBuffer.order(ByteOrder.nativeOrder()); 
            ntr = binaryHdrBuffer.getShort(12);
            System.out.println("Number of traces = " + ntr);
            dt= (int)binaryHdrBuffer.getShort(16);
            ds = dt*1E-6F;
            System.out.println("Sampling interval= " + ds);
            nt = binaryHdrBuffer.getShort(20);
            System.out.println("Sample per trace= " + nt);
            nbytesPerTrace = nt * 4;
            delrt = binaryHdrBuffer.getShort(24);
            
            ByteBuffer hdrByteBuffer = ByteBuffer.allocateDirect(NBYTES_PER_HDR);
            hdrByteBuffer.order(ByteOrder.LITTLE_ENDIAN);//nativeOrder());
            ByteBuffer trcByteBuffer = ByteBuffer.allocateDirect(nbytesPerTrace);
            trcByteBuffer.order(ByteOrder.LITTLE_ENDIAN);//nativeOrder());
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
                if (nRead == -1) {// We have reached EOF.
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
                        oneTr.nt = hdrByteBuffer.getShort(114);
                        oneTr.dt = hdrByteBuffer.getShort(116);
                        oneTr.pos = nbytesRead;
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
	//hdrBuffer.position(0);
	trcBuffer.position(0);
        trcBuffer.get(oneTr.data);
        boolean addtr = traces.add(oneTr);
        if (addtr!= true )
            throw new IOException("Failed to add trace at: " + nbytesRead +" position");
	traceInCount++;

      }

      if (tracesToDump >= 0  &&  traceInFileCount >= tracesToDump) finished = true;

    }  // End of loop over all read statements.

    fin.close();
    
        } catch (IOException e) {}

  }
 
  public void writeHeader( Trace tr, ByteBuffer hdrByteBuffer ) throws IOException{
            hdrByteBuffer.putInt(8,tr.fldr);  
            hdrByteBuffer.putInt(20,tr.cdp);
            hdrByteBuffer.putShort(114,(short)tr.nt);
            hdrByteBuffer.putShort(116,(short)tr.dt);
            hdrByteBuffer.putInt(72,(int)tr.delrt);
	    hdrByteBuffer.putInt(72,tr.sx);
	    hdrByteBuffer.putInt(76,tr.sy);
            hdrByteBuffer.putInt(80,tr.gx);
            hdrByteBuffer.putInt(84,tr.gy);
            hdrByteBuffer.putInt(180,(int)tr.f1);
            hdrByteBuffer.putInt(184,(int)tr.f1);
            hdrByteBuffer.putInt(188,(int)tr.d1);
            hdrByteBuffer.putInt(192,(int)tr.d2);
    
  }
  public void write() throws IOException{
    try {
        fout = new FileOutputStream(fname);
        FileChannel outputChannel = fout.getChannel();
        
        ByteBuffer reelHdrBuffer = ByteBuffer.allocate(LEN_REEL_HDR);
        String reelHdrBufferStr = "This is the header"; 
        //write 3200 bytes ebdic 
        int nWritten = outputChannel.write(reelHdrBuffer);
        nbytesWritten += nWritten;
        if (nWritten != LEN_REEL_HDR)  
                throw new IOException("Error writing SEG-Y reel header: " + nWritten + "!=" + LEN_REEL_HDR);
        System.out.println("header " + reelHdrBufferStr);
        
        ByteBuffer binaryHdrBuffer = ByteBuffer.allocate(LEN_BINARY_HDR);
        //binaryHdrBuffer.order(ByteOrder.nativeOrder()); 
        binaryHdrBuffer.putShort(12, (short) ntr);
        binaryHdrBuffer.putShort(16, (short) dtOut);
        binaryHdrBuffer.putShort(20, (short) ntOut);
        binaryHdrBuffer.putShort(24, (short) delrt);
        
        nWritten = outputChannel.write(binaryHdrBuffer);
        nbytesWritten += nWritten;
        if (nWritten != LEN_BINARY_HDR)  
                throw new IOException("Error writing SEG-Y binary header: " + nWritten + "!=" + LEN_BINARY_HDR);
  
        ByteBuffer hdrByteBuffer = ByteBuffer.allocateDirect(NBYTES_PER_HDR);
        hdrByteBuffer.order(ByteOrder.nativeOrder());//LITTLE_ENDIAN);;
        IntBuffer hdrBuffer = hdrByteBuffer.asIntBuffer();
        
        nbytesPerTraceOut = ntOut * 4;
        ByteBuffer trcByteBuffer = ByteBuffer.allocateDirect(nbytesPerTraceOut);
        trcByteBuffer.order(ByteOrder.nativeOrder());//LITTLE_ENDIAN);
        FloatBuffer trcBuffer = trcByteBuffer.asFloatBuffer();
        
        long outputTraceCount = 1L;
        ListIterator<Trace> litr = traces.listIterator();
        int traceCounter;
        boolean writeThisTrace=true;       
	while (litr.hasNext()) {
          traceCounter=litr.next().fldr;
	  if (traceCounter < minTrace) writeThisTrace = false;  // Out of range.
	  if (traceCounter > maxTrace) writeThisTrace = false;  // Out of range.
	  if (minTrace == Long.MIN_VALUE) {
	    if (traceCounter%traceInc != 0) writeThisTrace = false;  // Not on the increment.
	  } else {
	    if ((traceCounter-minTrace)%traceInc != 0) writeThisTrace = false;  // Not on the increment.
	  }

	  if (writeThisTrace) {

	    // Fill the next trace and header.
	    hdrBuffer.position(0);
	    trcBuffer.position(0);
            writeHeader(litr.next(), hdrByteBuffer );
	    trcBuffer.put(litr.next().data);

	    // Write the header.
	    hdrByteBuffer.position(0);
	    nWritten = outputChannel.write(hdrByteBuffer);
	    nbytesWritten += nWritten;
	    if (nWritten != NBYTES_PER_HDR)
	      throw new IOException("For file '" + fname
				    + "' - Error writing SEG-Y trace header " + outputTraceCount
				    + ": " + nWritten + "!=" + NBYTES_PER_HDR);

	    // Write the trace.
	    trcByteBuffer.position(0);
	    trcByteBuffer.limit(nbytesPerTraceOut);
	    nWritten = outputChannel.write(trcByteBuffer);
	    nbytesWritten += nWritten;
	    if (nWritten != nbytesPerTraceOut)
	      throw new IOException("For file '" + fname
				    + "' - Error writing SEG-Y trace " + outputTraceCount
				    + ": " + nWritten + "!=" + nbytesPerTraceOut);

	  }
	  outputTraceCount++;
	}
      } catch (IOException ex) {}
  }
  
  public void appendTrace(String name, Trace tr) throws IOException{
     if(tr.data!=null)
    {
        FileOutputStream f=null;
        TraceHeader h= new TraceHeader();
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
        byte [] buffer1=null;
        byte [] buffer2=null;
        f.write(buffer1);
        f.write(buffer2);
    }
  }

}