package com.jw.stockprocessor.configuration;

import com.jw.stockprocessor.model.DailyStockChange;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jms.JmsDestinationPollingSource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;
import java.util.UUID;

@Configuration
@EnableIntegration
@EnableBatchProcessing
@EnableJms
public class StockProcessorConfiguration {

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DailyStockChangeItemWriter dailyStockChangeItemWriter;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    JobLauncher jobLauncher;

    @Bean
    public IntegrationFlow inboundFlow(Job persistStockJob) {
        return IntegrationFlows.from(jmsDestinationPollingSource(),
                c -> c.poller(Pollers.fixedRate(5000, 2000)))
                .transform(payload -> stockPersistJobLaunchRequest(payload, persistStockJob))
                .handle(jobLaunchingGateway())
                .channel("nullChannel")
                .get();
    }

    @Bean
    public JobLaunchingGateway jobLaunchingGateway() {
        return new JobLaunchingGateway(jobLauncher);
    }

    @Bean
    public JmsDestinationPollingSource jmsDestinationPollingSource() {
        JmsDestinationPollingSource jmsDestinationPollingSource = new JmsDestinationPollingSource(jmsTemplate);
        jmsDestinationPollingSource.setDestinationName("amq.outbound");
        return jmsDestinationPollingSource;
    }

    public JobLaunchRequest stockPersistJobLaunchRequest(Object payload, Job persistStockJob) {
        if (payload instanceof byte[]) {
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                    .addParameter("file", new FileJobParameter((byte[]) payload));
            return new JobLaunchRequest(persistStockJob, jobParametersBuilder.toJobParameters());
        } else {
            throw new RuntimeException("Wrong payload type");
        }
    }

    static class FileJobParameter extends JobParameter {
        private byte[] file;

        public FileJobParameter(byte[] file) {
            super(UUID.randomUUID().toString());//This is to avoid duplicate JobInstance error
            this.file = file;
        }

        public byte[] getValue() {
            return file;
        }
    }

    @Bean
    public Job persistStockJob(Step persistStockStep) {
        return jobBuilderFactory
                .get("persistStockJob")
                .incrementer(new RunIdIncrementer())
                .start(persistStockStep)
                .build();
    }

    @Bean
    public Step persistStockStep(ItemReader<DailyStockChange> excelStockReader) {
        return stepBuilderFactory.get("persistStockStep")
                .<DailyStockChange, DailyStockChange>chunk(100)
                .reader(excelStockReader)
                .writer(dailyStockChangeItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public PoiItemReader<DailyStockChange> excelStockReader(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        Object fileParam = jobParameters.get("file");
        if (fileParam instanceof byte[]) {
            PoiItemReader<DailyStockChange> reader = new PoiItemReader<>();
            reader.setLinesToSkip(1);
            reader.setResource(new ByteArrayResource((byte[]) fileParam));
            reader.setRowMapper(new StockRowMapper());
            return reader;
        } else {
            throw new RuntimeException("Could not find file job parameter");
        }
    }
}
