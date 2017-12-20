
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright Sergius Dell DevLab
 */

/**
 * <h1> Main to launch all. </h1>
 * @author Sergius Dell DevLab
 *
 */
public class SerDellDevLabMain {

   /**
 * 
 * @param args: 
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
 */          
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length < 2) {
            System.out.println("Please provide processing tag and the parameter file.");
            System.exit(0);
        }
        //encode the tag and paramter list
        String procTag=args[0];
        String progParam=null;
        String encoding="US-ASCII";
        FileInputStream fis=new FileInputStream(args[1]);
        progParam=getFileContent(fis,encoding);
        //main loop over processing options
        switch (procTag) {
            case "msel":
                System.out.println(procTag + "Executing ");
                MeanFilterMain MFM = new MeanFilterMain(progParam);
        {
            try {
                MFM.myRun();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SerDellDevLabMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            break;
            default:
                System.out.println("Default ");
        }
        
    }
    
    public static String getFileContent(FileInputStream fis,
                                        String encoding ) throws IOException{
        try( BufferedReader br =
           new BufferedReader( new InputStreamReader(fis, encoding ))){
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
        return sb.toString();
        }
    }
}
