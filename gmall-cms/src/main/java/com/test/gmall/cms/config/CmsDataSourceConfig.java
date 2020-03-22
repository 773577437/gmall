package com.test.gmall.cms.config;

import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 配置mysql主从分离访问
 */
@Configuration
public class CmsDataSourceConfig {

    /**
     * 注册 sharding-jdcb.yml 数据源配置
     * @return
     * @throws IOException
     * @throws SQLException
     */
    @Bean
    public DataSource dataSource() throws IOException, SQLException {

        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(ResourceUtils.getFile("classpath:sharding-jdbc.yml"));
        return dataSource;
    }
}
