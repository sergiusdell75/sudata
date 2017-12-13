/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */

public class TraceClassLoader extends ClassLoader{
    byte[] classData;
    public TraceClassLoader(ClassLoader parent) {
        super(parent);
    }

    void setClassData(byte[] classData){
         this.classData= classData;
    }
        /**
         *
         * @param name
         * @return 
         * @throws ClassNotFoundException
         */
        @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        if(!"Trace.MyObject".equals(name))
                return super.loadClass(name);
        return defineClass("Trace.MyObject",
                classData, 0, classData.length);
    }
}