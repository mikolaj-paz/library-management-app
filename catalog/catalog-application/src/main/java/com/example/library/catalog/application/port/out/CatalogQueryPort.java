package com.example.library.catalog.application.port.out;

import com.example.library.catalog.application.query.BookDetails;
import com.example.library.catalog.application.query.BookSearchResult;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.List;
import java.util.Optional;

public interface CatalogQueryPort {
  List<BookSearchResult> searchBooks(String phrase);

  Optional<BookDetails> getBookDetails(BookId bookId);
}
