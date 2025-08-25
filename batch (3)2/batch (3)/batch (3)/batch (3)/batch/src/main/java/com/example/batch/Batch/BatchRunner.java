package com.example.batch.Batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BatchRunner {

    @Bean
    CommandLineRunner runBatch(JobLauncher jobLauncher, Job job) {
        return args -> jobLauncher.run(job, new JobParameters());
    }
}
