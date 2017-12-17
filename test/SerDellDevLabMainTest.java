/*
 * Copyright Sergius Dell DevLab
 */

import java.io.FileInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author emil
 */
public class SerDellDevLabMainTest {
    
    public SerDellDevLabMainTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class SerDellDevLabMain.
     * @throws java.lang.Exception
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = null;
        SerDellDevLabMain.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFileContent method, of class SerDellDevLabMain.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetFileContent() throws Exception {
        System.out.println("getFileContent");
        FileInputStream fis = null;
        String encoding = "";
        String expResult = "";
        String result = SerDellDevLabMain.getFileContent(fis, encoding);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
