package com.example.library.catalog.application.service;

import com.example.library.catalog.application.port.in.ISearchCatalog;
import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.query.BookSearchResult;
import com.example.library.catalog.application.query.SearchCatalog;
import java.util.List;

public class SearchCatalogService implements ISearchCatalog {

  private final CatalogQueryPort catalogQueryPort;

  public SearchCatalogService(CatalogQueryPort catalogQueryPort) {
    this.catalogQueryPort = catalogQueryPort;
  }

  @Override
  public List<BookSearchResult> search(SearchCatalog query) {
    return catalogQueryPort.searchBooks(query.phrase());
  }
}
