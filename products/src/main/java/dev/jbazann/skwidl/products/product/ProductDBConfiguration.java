package dev.jbazann.skwidl.products.product;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "dev.jbazann.skwidl.products.product",
        entityManagerFactoryRef = "productEntityManagerFactory",
        transactionManagerRef = "productTransactionManager"
)
public class ProductDBConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.product")
    public DataSource productDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean productEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("productDataSource") @NotNull DataSource productDataSource) {
        return builder
                .dataSource(productDataSource)
                .packages("dev.jbazann.skwidl.products.product")
                .persistenceUnit("productPU")//TODO
                .build();
    }

    @Bean
    public PlatformTransactionManager categoryTransactionManager(
            @Qualifier("productEntityManagerFactory") LocalContainerEntityManagerFactoryBean productEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(productEntityManagerFactory.getObject()));
    }

}
