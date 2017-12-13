
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
        if (args.length < 1) {
            System.out.println("Please provide processing parameters");
            System.exit(0);
        }
        String procTag=args[0];
        String progParam=null;
        String encoding="UTF-8";
        FileInputStream fis=new FileInputStream(args[1]);
        progParam=getFileContent(fis,encoding);
        switch (procTag) {
            case "":
                System.out.println("Execution " + procTag);
                
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
