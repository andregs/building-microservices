package com.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* To test this, put a CSV file in ${user.home}/Desktop/in dir and check the console output.
* Then, call `GET /files?file=valid/path/to/file.csv` and check the console output.
*
* The exercise here is to demonstrate that we can use MessageChannels to connect different
* flows explicitly. We can call our job via an event in our filesystem or by calling a REST
* endpoint. */
@Configuration
public class IntegrationConfiguration {

    @Bean
    MessageChannel files() {
        return MessageChannels.direct().get();
    }

    @RestController
    public static class FileNameRestController {

        private final MessageChannel files;

        @RequestMapping(method = RequestMethod.GET, value = "/files")
        void triggerJobForFile(@RequestParam String file) {

            Message<File> fileMessage = MessageBuilder.withPayload(new File(file))
                    .build();
            this.files.send(fileMessage);
        }

        @Autowired
        public FileNameRestController(MessageChannel files) {
            this.files = files;
        }
    }

    /* Listen to the message channel and whenever we get an incoming file:
    * 1. prints debug info
    * 2. creates a job launch request
    * 3. triggers the batch job to process that file
    * 4. prints out the job execution results */
    @Bean
    IntegrationFlow batchJobFlow(Job job,
                                 JdbcTemplate jdbcTemplate,
                                 JobLauncher launcher,
                                 MessageChannel files) {

        return IntegrationFlows.from(files)
                .transform(file -> {
                    System.out.println(file.toString());
                    System.out.println(file.getClass());
                    return file ;
                })
                .transform((GenericTransformer<File, JobLaunchRequest>) file -> {
                    JobParameters jp = new JobParametersBuilder()
                            .addString("file", file.getAbsolutePath())
                            .toJobParameters();
                    return new JobLaunchRequest(job, jp);
                })
                .handle(new JobLaunchingGateway(launcher))
                .handle(JobExecution.class, (payload, headers) -> {
                    System.out.println("job execution status: " + payload.getExitStatus().toString());

                    List<Person> personList = jdbcTemplate.query("select * from PEOPLE",
                            (resultSet, i) -> new Person(resultSet.getString("first"),
                                    resultSet.getString("last"),
                                    resultSet.getString("email")));

                    personList.forEach(System.out::println);
                    return null;
                })
                .get();
    }

    /* Monitor the filesystem `dir` and send new incoming files through our message channel. */
    @Bean
    IntegrationFlow incomingFiles(@Value("${user.home}/Desktop/in") File dir, MessageChannel files) {
        return IntegrationFlows.from(
                Files.inboundAdapter(dir)
                        .preventDuplicates(true)
                        .autoCreateDirectory(true),
                poller -> poller.poller(spec -> spec.fixedRate(1, TimeUnit.SECONDS)))
                .channel(files)
                .get();
    }
}
