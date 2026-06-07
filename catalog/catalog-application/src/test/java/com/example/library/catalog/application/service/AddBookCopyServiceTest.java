package com.example.library.catalog.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.catalog.application.command.AddBookCopy;
import com.example.library.catalog.application.port.out.BookCopyRepository;
import com.example.library.catalog.application.port.out.BookRepository;
import com.example.library.catalog.domain.book.Book;
import com.example.library.catalog.domain.book.ISBN;
import com.example.library.catalog.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddBookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private BookRepository bookRepository;

  @Mock private DomainEventPublisher eventPublisher;

  @Test
  void should_create_book_copy_when_book_exists() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var book =
        Book.create(bookId, "Clean Architecture", "Robert C. Martin", new ISBN("978-0134494166"));
    when(bookRepository.find(bookId)).thenReturn(Optional.of(book));
    var service = new AddBookCopyService(bookCopyRepository, bookRepository, eventPublisher);

    var copyId = service.add(new AddBookCopy(bookId));

    assertThat(copyId).isNotNull();
    var copyCaptor = ArgumentCaptor.forClass(BookCopy.class);
    verify(bookCopyRepository).create(copyCaptor.capture());
    assertThat(copyCaptor.getValue().id()).isEqualTo(copyId);
    assertThat(copyCaptor.getValue().bookId()).isEqualTo(bookId);
  }

  @Test
  void should_throw_when_book_does_not_exist() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    when(bookRepository.find(bookId)).thenReturn(Optional.empty());
    var service = new AddBookCopyService(bookCopyRepository, bookRepository, eventPublisher);

    assertThatThrownBy(() -> service.add(new AddBookCopy(bookId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("not found");

    verify(bookCopyRepository, never()).create(any());
  }
}
