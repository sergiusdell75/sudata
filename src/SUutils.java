/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */
public class SUutils {
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
}
