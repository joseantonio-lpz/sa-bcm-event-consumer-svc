package mx.com.seccionamarilla.bcm.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {

	@Primary
	@Bean(name = "primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.primary")
	DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "primaryJdbcTemplate")
	JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource ds) {
		return new JdbcTemplate(ds);
	}
}
