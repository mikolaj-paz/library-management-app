package com.example.library.catalog.infrastructure.config;

import com.example.library.catalog.application.port.in.IGetBookDetails;
import com.example.library.catalog.application.port.in.ISearchCatalog;
import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.service.GetBookDetailsService;
import com.example.library.catalog.application.service.SearchCatalogService;
import com.example.library.catalog.infrastructure.out.persistence.JdbcCatalogQueryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CatalogConfig {

  @Bean
  CatalogQueryPort catalogQueryPort(JdbcTemplate jdbc) {
    return new JdbcCatalogQueryPort(jdbc);
  }

  @Bean
  ISearchCatalog searchCatalog(CatalogQueryPort catalogQueryPort) {
    return new SearchCatalogService(catalogQueryPort);
  }

  @Bean
  IGetBookDetails getBookDetails(CatalogQueryPort catalogQueryPort) {
    return new GetBookDetailsService(catalogQueryPort);
  }
}
