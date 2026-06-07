package com.example.library.catalog.infrastructure.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.library.catalog.application.port.in.IGetBookDetails;
import com.example.library.catalog.application.port.in.ISearchCatalog;
import com.example.library.catalog.application.query.BookDetails;
import com.example.library.catalog.domain.book.ISBN;
import com.example.library.catalog.domain.exception.BookNotFoundException;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

  @Mock private ISearchCatalog searchCatalogService;

  @Mock private IGetBookDetails getBookDetailsService;

  @Test
  void should_return_book_details_when_book_exists() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var details =
        new BookDetails(
            bookId, "Domain-Driven Design", "Eric Evans", new ISBN("978-0321125217"), 3, 2);
    when(getBookDetailsService.bookDetails(any())).thenReturn(details);

    var response =
        new CatalogController(searchCatalogService, getBookDetailsService)
            .getBookDetails(bookId.value().toString());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo(details);
  }

  @Test
  void should_return_not_found_when_book_does_not_exist() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    when(getBookDetailsService.bookDetails(any())).thenThrow(new BookNotFoundException(bookId));

    var response =
        new CatalogController(searchCatalogService, getBookDetailsService)
            .getBookDetails(bookId.value().toString());

    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(response.getBody().toString()).contains("error");
  }
}
