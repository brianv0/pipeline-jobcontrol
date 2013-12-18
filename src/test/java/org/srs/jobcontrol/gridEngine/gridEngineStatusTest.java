/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.srs.jobcontrol.gridEngine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import junit.framework.TestCase;
import org.srs.jobcontrol.JobStatus;
import org.srs.jobcontrol.JobStatus.Status;
import org.srs.jobcontrol.common.CommonJobStatus;
import org.srs.jobcontrol.gridEngine.qstat.JobInfo;
import org.srs.jobcontrol.gridEngine.qstat.JobListT;
import org.srs.jobcontrol.gridEngine.qstat.QueueInfoT;

/**
 *
 * @author bvan
 */
public class gridEngineStatusTest extends TestCase {

    public gridEngineStatusTest(String testName){
        super( testName );
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    String validXml = 
            "<job_info  xmlns:xsd=\"https://github.com/gridengine/gridengine/raw/master/source/dist/util/resources/schemas/qstat/qstat.xsd\">\n"
            + "  <queue_info>\n"
            + "    <job_list state=\"running\">\n"
            + "      <JB_job_number>392667</JB_job_number>\n"
            + "      <JAT_prio>0.50050</JAT_prio>\n"
            + "      <JAT_ntix>0.00000</JAT_ntix>\n"
            + "      <JB_name>ge_script</JB_name>\n"
            + "      <JB_owner>glastpro</JB_owner>\n"
            + "      <JB_project>P_glast_prod</JB_project>\n"
            + "      <JB_department>defaultdepartment</JB_department>\n"
            + "      <state>r</state>\n"
            + "      <cpu_usage>110669.00000</cpu_usage>\n"
            + "      <mem_usage>15566.12109</mem_usage>\n"
            + "      <io_usage>0.42582</io_usage>\n"
            + "      <tickets>0</tickets>\n"
            + "      <JB_override_tickets>0</JB_override_tickets>\n"
            + "      <JB_jobshare>0</JB_jobshare>\n"
            + "      <otickets>0</otickets>\n"
            + "      <ftickets>0</ftickets>\n"
            + "      <stickets>0</stickets>\n"
            + "      <JAT_share>0.00000</JAT_share>\n"
            + "      <queue_name>long@ccwsge0453.in2p3.fr</queue_name>\n"
            + "      <jclass_name></jclass_name>\n"
            + "      <slots>1</slots>\n"
            + "    </job_list>\n"
            + "  </queue_info>\n"
            + "  <job_info>\n"
            + "    <job_list state=\"pending\">\n"
            + "      <JB_job_number>404991</JB_job_number>\n"
            + "      <JAT_prio>0.00000</JAT_prio>\n"
            + "      <JAT_ntix>0.00000</JAT_ntix>\n"
            + "      <JB_name>ge_script</JB_name>\n"
            + "      <JB_owner>glastpro</JB_owner>\n"
            + "      <JB_project>P_glast_prod</JB_project>\n"
            + "      <JB_department>defaultdepartment</JB_department>\n"
            + "      <state>qw</state>\n"
            + "      <tickets>0</tickets>\n"
            + "      <JB_override_tickets>0</JB_override_tickets>\n"
            + "      <JB_jobshare>0</JB_jobshare>\n"
            + "      <otickets>0</otickets>\n"
            + "      <ftickets>0</ftickets>\n"
            + "      <stickets>0</stickets>\n"
            + "      <JAT_share>0.00000</JAT_share>\n"
            + "      <queue_name></queue_name>\n"
            + "      <jclass_name></jclass_name>\n"
            + "      <slots>1</slots>\n"
            + "    </job_list>\n"
            + "    <job_list state=\"zombie\">\n"
            + "      <JB_job_number>397513</JB_job_number>\n"
            + "      <JAT_prio>0.00000</JAT_prio>\n"
            + "      <JAT_ntix>0.00000</JAT_ntix>\n"
            + "      <JB_name>ge_script</JB_name>\n"
            + "      <JB_owner>glastpro</JB_owner>\n"
            + "      <JB_project>P_glast_prod</JB_project>\n"
            + "      <JB_department>defaultdepartment</JB_department>\n"
            + "      <state>qw</state>\n"
            + "      <queue_name></queue_name>\n"
            + "      <jclass_name></jclass_name>\n"
            + "      <slots>1</slots>\n"
            + "    </job_list>\n"
            + "  </job_info>\n"
            + "</job_info>";

    public void testSomeMethod() throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance( "org.srs.jobcontrol.gridEngine.qstat" );
        Unmarshaller um = jc.createUnmarshaller();
        InputStream is = new ByteArrayInputStream(validXml.getBytes());
        JobInfo ji = (JobInfo) um.unmarshal( is );
        HashMap<String, JobStatus> statii = new HashMap<String, JobStatus>();
        
        ji.getQueueInfo().get( 0 ).getJobList();
        ji.getJobInfo().get( 0 ).getJobList();
        gridEngineStatus stat = new gridEngineStatus();
        stat.evaluateList( (List<JobListT>) ji.getQueueInfo().get( 0 ).getJobList(), statii );
        stat.evaluateList( ji.getJobInfo().get( 0 ).getJobList(), statii );
        assertTrue(statii.get( "404991" ).getStatus() == Status.PENDING);
        assertTrue(statii.get( "392667" ).getStatus() == Status.RUNNING);
        assertTrue(statii.get( "397513" ).getStatus() == Status.DONE);
    }

}
