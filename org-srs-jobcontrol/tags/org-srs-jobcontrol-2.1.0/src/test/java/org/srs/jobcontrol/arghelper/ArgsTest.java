/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.srs.jobcontrol.arghelper;

import junit.framework.TestCase;

/**
 *
 * @author bvan
 */
public class ArgsTest extends TestCase {
    
    String expected0001 = "#BSUB -q glastdataq\n"
            + "#BSUB -sp 75\n"
            + "#BSUB -R \"select[scratch>1]\"\n"
            + "#BSUB -r \n";
    
    String expected0002 = "#BSUB -q glastdataq\n"
            + "#BSUB -sp ${standardPriority}\n"
            + "#BSUB -R \"select[${standardSelect}] rusage[${standardRusage}]\"\n"
            + "#BSUB -H \n"
            + "#BSUB -G \n"
            + "#BSUB -rn \n";
    
    String expected0003 = "#PBS -q xfer\n"
            + "#PBS -l walltime=2:00:00\n"
            + "#PBS -N my_job\n"
            + "#PBS -j oe\n"
            + "#PBS -V \n";
    
    public ArgsTest(String testName){
        super( testName );
    }
    
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testSomeMethod(){
        LSFArgs args = new LSFArgs();
        try{
            String actual0001 = args.getScriptDirectives( " -q glastdataq -sp 75  -R \"select[scratch>1]\" -r" );
        String actual0002 = args.getScriptDirectives( " -q glastdataq -sp ${standardPriority} "
                + "-R \"select[${standardSelect}] rusage[${standardRusage}]\" -H -G -rn" );
        TorqueArgs tArgs = new TorqueArgs();
        String actual0003 = tArgs.getScriptDirectives( "-q xfer -l walltime=2:00:00 -N my_job -j oe -V" );
        assertEquals("LSF test 1", expected0001, actual0001);
        assertEquals("Torque test 2", expected0002, actual0002);
        assertEquals("Torque test 3", expected0003, actual0003);
        } catch(Exception e){ fail("Should have parsed options"); }
        
    }
    
}
