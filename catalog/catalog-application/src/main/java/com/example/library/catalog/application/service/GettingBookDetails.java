package com.example.library.catalog.application.service;

import com.example.library.catalog.application.port.in.IGetBookDetails;
import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.query.BookDetails;
import com.example.library.catalog.application.query.GetBookDetails;
import com.example.library.catalog.domain.exception.BookNotFoundException;

public class GettingBookDetails implements IGetBookDetails {
  private final CatalogQueryPort catalogQueryPort;

  public GettingBookDetails(CatalogQueryPort catalogQueryPort) {
    this.catalogQueryPort = catalogQueryPort;
  }

  @Override
  public BookDetails getBookDetails(GetBookDetails query) {
    return catalogQueryPort
        .getBookDetails(query.bookId())
        .orElseThrow(() -> new BookNotFoundException(query.bookId()));
  }
}
