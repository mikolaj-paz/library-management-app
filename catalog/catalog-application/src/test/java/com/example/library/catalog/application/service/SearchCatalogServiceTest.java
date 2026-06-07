package com.example.library.catalog.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.query.BookSearchResult;
import com.example.library.catalog.application.query.SearchCatalog;
import com.example.library.catalog.domain.book.ISBN;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchCatalogServiceTest {

  @Mock private CatalogQueryPort catalogQueryPort;

  @Test
  void should_delegate_search_phrase_to_catalog_query_port() {
    var service = new SearchCatalogService(catalogQueryPort);
    var result =
        new BookSearchResult(
            BookId.of(UUID.randomUUID().toString()),
            "Domain-Driven Design",
            "Eric Evans",
            new ISBN("978-0321125217"),
            true);
    when(catalogQueryPort.searchBooks("domain")).thenReturn(List.of(result));

    var results = service.search(new SearchCatalog("domain"));

    assertThat(results).containsExactly(result);
    verify(catalogQueryPort).searchBooks("domain");
  }
}
