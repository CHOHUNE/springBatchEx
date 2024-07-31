package com.example.springbatchex.batch;

import com.example.springbatchex.entity.WinEntity;
import com.example.springbatchex.repository.WinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecondBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final WinRepository winRepository;


    @Bean
    public Job secondJob() {
        return new JobBuilder("secondJob", jobRepository)
                .start(secondStep())
                .build();
    }

    // job -> Step ( Reader -> Processor -> Writer 와 읽어들일 Chunk 사이즈를 정의 )
    @Bean
    public Step secondStep() {

        return new StepBuilder("secondStep", jobRepository)
                // chunk 단위는 너무 작으면 I/O 가 많아지고 너무 크면 적재 및 자원 사용에 대한 비용과 실패시 부담이 커지므로
                // 벤치마크 후에 적정 값을 설정해야 한다.
                .<WinEntity, WinEntity>chunk(10, platformTransactionManager)
                .reader(winReader())
                .processor(winProcessor())
                .writer(winWriter())
                .build();

    }

    @Bean
    public RepositoryItemReader<WinEntity> winReader() {

        return new RepositoryItemReaderBuilder<WinEntity>()
                .name("winReader")
                .pageSize(10)
                .methodName("findByWinGreaterThanEqual")
                .arguments(Collections.singletonList(10L)) //정수 10보다 크거나 같은 경우
                .repository(winRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    private RepositoryItemWriter<WinEntity> winWriter() {

        return new RepositoryItemWriterBuilder<WinEntity>()
                .repository(winRepository)
                .methodName("save")
                .build();
    }

    private ItemProcessor<WinEntity, WinEntity> winProcessor() {

        return item -> {
            item.setReward(true);

            return item;
        };
    }


}
