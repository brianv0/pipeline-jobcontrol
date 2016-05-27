package org.srs.jobcontrol.slurm;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobSubmissionException;
import org.srs.jobcontrol.common.JobControlService.DeleteFile;

/**
 * Slurm job builder.
 * @author bvan
 */
public class SlurmScriptJobBuilder {
    private static final String SHEBANG = "#!/bin/bash\n";
    private final StringBuilder script;
    private final List<Runnable> undoList;
    private final static Logger LOGGER = 
            Logger.getLogger("org.srs.jobcontrol.slurm.SlurmScriptJobBuilder");

    SlurmScriptJobBuilder(){
        script = new StringBuilder(SHEBANG);
        undoList = new ArrayList<>();
    }

    public void build(Job job) throws JobSubmissionException{
        try {
            if(LOGGER.isLoggable(Level.FINER)){
                LOGGER.log(Level.FINER, "Building Job");
            }
            processWorkingDirectory(job);
            processArchiveOldWorkingDir(job);
            processName(job);
            processLogFile(job);
            processMaxCPU(job);
            processMaxMemory(job);
            processExtraOptions(job);
            addSlurmDirective("-rn"); // Not re-runnable
            processEnv(job);
            script.append("\n");
            script.append("bash pipeline_wrapper\n");
            job.getFiles().put("slurm_pilot", script.toString());
            processFiles(job);
        } catch (IOException ex){
            LOGGER.log(Level.INFO, "Error building job, removing files...", ex);
            for(Runnable undoItem: undoList){
                undoItem.run();
            }
            throw new JobSubmissionException("Unable to build job", ex);
        }
    }

    public void processArchiveOldWorkingDir(Job job) throws IOException{
        if(LOGGER.isLoggable(Level.FINER)){
            LOGGER.log(Level.FINER, "Checking if we need to archive...");
        }
        if(job.getArchiveOldWorkingDir() != null){
            doArchiveOldWorkingDir(job);
        }
    }

    public void processEnv(Job job){
        if(LOGGER.isLoggable(Level.FINER)){
            LOGGER.log(Level.FINER, "Building Environment");
        }
        Map<String, String> env = job.getEnv();
        for(String key: env.keySet()){
            script.append(String.format("export %s=\"%s\"\n", key, env.get(key)));
        }
    }

    public void processExtraOptions(Job job){
        if(job.getExtraOptions() != null){
            for(String s: tokenizeExtraOption(job.getExtraOptions())){
                addSlurmDirective(s);
            }
        }
    }

    public void processFiles(Job job) throws IOException{
        Map<String, String> files = job.getFiles();
        Path basePath = Paths.get(job.getWorkingDirectory());
        for(String name: files.keySet()){
            Path target = basePath.resolve(name);
            if(LOGGER.isLoggable(Level.FINER)){
                LOGGER.log(Level.FINER, "Processing file " + target.toString());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(target, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW)){
                writer.write(files.get(name));
                undoList.add(new DeleteFile(target.toFile()));
            }
        }
    }

    public void processLogFile(Job job){
        String logFileName = job.getLogFile() == null ? "logFile.log" : sanitize(job.getLogFile());
        addSlurmDirective(String.format("-o %s", logFileName));
        job.getEnv().put("JOBCONTROL_LOGFILE", logFileName);
    }

    public void processMaxCPU(Job job){
        if(job.getMaxCPU() != 0){
            int minutes = job.getMaxCPU() / 60;
            int seconds = job.getMaxCPU() % 60;
            addSlurmDirective(String.format("-t %d:%d", minutes, seconds));
        }
    }

    public void processMaxMemory(Job job){
        if(job.getMaxMemory() != 0){
            addSlurmDirective(String.format("--mem=%d", job.getMaxMemory()));
        }
    }

    public void processName(Job job){
        if(job.getName() != null){
            addSlurmDirective(String.format("-J %s", sanitize(job.getName())));
        }
    }

    public void processWorkingDirectory(Job job) throws IOException{
        if(job.getWorkingDirectory() != null){
            String dir = job.getWorkingDirectory();
            Path dirPath = Paths.get(dir);
            Files.createDirectories(dirPath);
            addSlurmDirective(String.format("-D %s", job.getWorkingDirectory()));
        }
    }

    private String sanitize(String option){
        return option.replaceAll("\\s+", "_");
    }
    
    private void doArchiveOldWorkingDir(Job job) throws IOException{
        if(LOGGER.isLoggable(Level.FINER)){
            LOGGER.log(Level.FINER, "Archiving previous working directory");
        }
        Path workingDir = Paths.get(job.getWorkingDirectory());
        Path archiveDir = Paths.get(job.getArchiveOldWorkingDir());
        Files.createDirectories(archiveDir);
        for(Path file : Files.newDirectoryStream(workingDir)){
            if(LOGGER.isLoggable(Level.FINEST)){
                LOGGER.log(Level.FINEST, String.format("Checking " + file.toString()));
            }
            // Don't move archive root
            if(!file.equals(archiveDir.getParent())){
                if(LOGGER.isLoggable(Level.FINEST)){
                    LOGGER.log(Level.FINEST, 
                            String.format("Archiving %s to %s", file.toString(), 
                                    archiveDir.resolve(file.getFileName()))
                    );
                }
                Files.move(file, archiveDir.resolve(file.getFileName()));
            }
        }        
    }

    protected List<String> tokenizeExtraOption(String string){
        return Arrays.asList(string.split("\\|"));
    }

    public void addSlurmDirective(String opt){
        script.append(String.format("#SBATCH %s\n", opt));
    }

}
