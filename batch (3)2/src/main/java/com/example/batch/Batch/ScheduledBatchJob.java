package com.example.batch.Batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledBatchJob {

    private final JobLauncher jobLauncher;
    private final Job job;

    public ScheduledBatchJob(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void runBatchJob() {
        try {
            JobParameters jobParameters = new JobParameters();
            jobLauncher.run(job, jobParameters);
            System.out.println("Job exécuté avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du job : " + e.getMessage());
        }
    }
}
