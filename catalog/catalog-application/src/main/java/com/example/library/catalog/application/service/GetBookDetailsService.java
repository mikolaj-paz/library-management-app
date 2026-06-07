package com.example.library.catalog.application.service;

import com.example.library.catalog.application.port.in.IGetBookDetails;
import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.query.BookDetails;
import com.example.library.catalog.application.query.GetBookDetails;
import com.example.library.catalog.domain.exception.BookNotFoundException;

public class GetBookDetailsService implements IGetBookDetails {
  private final CatalogQueryPort catalogQueryPort;

  public GetBookDetailsService(CatalogQueryPort catalogQueryPort) {
    this.catalogQueryPort = catalogQueryPort;
  }

  @Override
  public BookDetails bookDetails(GetBookDetails query) {
    return catalogQueryPort
        .getBookDetails(query.bookId())
        .orElseThrow(() -> new BookNotFoundException(query.bookId()));
  }
}
