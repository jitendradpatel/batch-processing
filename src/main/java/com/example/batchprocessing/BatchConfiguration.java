package com.example.batchprocessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public JdbcPagingItemReader<Account> reader(DataSource dataSource, PagingQueryProvider pagingQueryProvider) {
		return new JdbcPagingItemReaderBuilder<Account>()
				.name("accountItemReader")
				.fetchSize(10000)
				.pageSize(1000)
				.dataSource(dataSource)
				.queryProvider(pagingQueryProvider)
				.beanRowMapper(Account.class)
				.build();
	}

	@Bean
	public SqlPagingQueryProviderFactoryBean queryProvider(DataSource dataSource) {
		SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
		provider.setDatabaseType(DatabaseType.POSTGRES.name());
		provider.setDataSource(dataSource);
		provider.setSelectClause("select aid, bid, abalance, filler");
		provider.setFromClause("from pgbench_accounts");
		provider.setSortKey("aid");
		return provider;
	}

	@Bean
	public JdbcBatchItemWriter<Account> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Account>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO pgbench_accounts2 (aid, bid, abalance, filler) VALUES (:aid, :bid, :abalance, :filler)")
			.dataSource(dataSource)
			.build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob")
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}

	@Bean
	public Step step1(JdbcPagingItemReader<Account> reader, JdbcBatchItemWriter<Account> writer) {
		ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
		threadPoolExecutor.setCorePoolSize(4);
		threadPoolExecutor.setMaxPoolSize(4);
		threadPoolExecutor.afterPropertiesSet();
		return stepBuilderFactory.get("step1")
			.<Account, Account> chunk(1000)
			.reader(reader)
			.writer(writer)
			.taskExecutor(threadPoolExecutor)
			.build();
	}
}
