package com.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    /* We accept the file that we want to read as a job parameter. */
    @Bean
    @StepScope // recreates a new reader each time our job runs
    FlatFileItemReader<Person> flatFileItemReader(@Value("#{jobParameters[file]}") File file) {
        FlatFileItemReader<Person> r = new FlatFileItemReader<>();
        r.setResource(new FileSystemResource(file));
        r.setLineMapper(new DefaultLineMapper<Person>() {
            {
                this.setLineTokenizer(new DelimitedLineTokenizer(",") {
                    {
                        this.setNames("first", "last", "email");
                    }
                });
                this.setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
                    {
                        this.setTargetType(Person.class);
                    }
                });
            }
        });
        return r;
    }

    /* We write our people in a H2 database */
    @Bean
    JdbcBatchItemWriter<Person> jdbcBatchItemWriter(DataSource h2) {
        JdbcBatchItemWriter<Person> w = new JdbcBatchItemWriter<>();
        w.setDataSource(h2);
        w.setSql("insert into PEOPLE( first, last, email) values ( :first, :last, :email )");
        w.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return w;
    }

    /* A job that reads Person data from a file and writes them in a database.
    * Note that Spring executes all batch jobs by default, but we disabled that in `application.properties` */
    @Bean
    Job personEtl(JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            FlatFileItemReader<Person> reader,
            JdbcBatchItemWriter<Person> writer
    ) {

        Step step = stepBuilderFactory.get("file-to-database")
                .<Person, Person>chunk(5)
                .reader(reader)
                .writer(writer)
                .build();

        return jobBuilderFactory.get("etl")
                .start(step)
                .build();
    }

    /* This bean runs our job. We have to execute the app with `--file=people.csv` */
    @Bean
    CommandLineRunner runner(JobLauncher launcher,
                             Job job,
                             @Value("${file}") File in, // we defined this prop here and we'll pass it through command-line
                             JdbcTemplate jdbcTemplate) {
        return args -> {

            // launches our job, passing the file parameter
            JobExecution execution = launcher.run(job,
                    new JobParametersBuilder()
                            .addString("file", in.getAbsolutePath()) // our Reader requires this param
                            .toJobParameters());

            System.out.println("execution status: " + execution.getExitStatus().toString());

            // queries the DB to assert that the people are there
            List<Person> personList = jdbcTemplate.query("select * from PEOPLE", (resultSet, i) -> new Person(
                    resultSet.getString("first"),
                    resultSet.getString("last"),
                    resultSet.getString("email")));

            personList.forEach(System.out::println);

        };

    }

}
