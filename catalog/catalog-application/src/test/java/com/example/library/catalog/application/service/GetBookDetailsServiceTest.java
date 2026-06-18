package com.example.library.catalog.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.query.BookDetails;
import com.example.library.catalog.application.query.GetBookDetails;
import com.example.library.catalog.domain.exception.BookNotFoundException;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetBookDetailsServiceTest {

  @Mock private CatalogQueryPort catalogQueryPort;

  @Test
  void should_return_book_details_when_book_exists() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var details =
        new BookDetails(
            bookId,
            "Domain-Driven Design",
            "Eric Evans",
            new ISBN("978-0321125217"),
            "Addison-Wesley",
            LocalDate.of(2003, 8, 30),
            3,
            2);
    when(catalogQueryPort.getBookDetails(bookId)).thenReturn(Optional.of(details));
    var service = new GettingBookDetails(catalogQueryPort);

    var result = service.getBookDetails(new GetBookDetails(bookId));

    assertThat(result).isEqualTo(details);
  }

  @Test
  void should_throw_when_book_does_not_exist() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    when(catalogQueryPort.getBookDetails(bookId)).thenReturn(Optional.empty());
    var service = new GettingBookDetails(catalogQueryPort);

    assertThatThrownBy(() -> service.getBookDetails(new GetBookDetails(bookId)))
        .isInstanceOf(BookNotFoundException.class);
  }
}
