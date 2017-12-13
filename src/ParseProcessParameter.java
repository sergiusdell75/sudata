/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */
public class ParseProcessParameter {
    
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
     public static String[] parserAllValues(String [] strArray, String [] pstr){
        String [] strA;
        boolean flag=false;
        int length = strArray.length;
        String [] tstr= new String [pstr.length];
        //check if the to parse argument is in the parse stream
        for (int j=0; j< pstr.length;j++){
            for (int i=0; i< length;i++){
                flag=strArray[i].matches("(.*)"+pstr[j]+"(.*)");
                if (flag) {
                    strA=strArray[i].split("=");
                    tstr[j]=(pstr[j].matches("(.*)"+strA[0]+"(.*)"))?strA[1]:null;
                    break;
                }
            }
            flag=false;
        }
        return tstr; 
    }
}
