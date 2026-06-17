package com.example.library.catalog.application.port.in;

import com.example.library.catalog.application.query.BookSearchResult;
import com.example.library.catalog.application.query.SearchCatalog;
import java.util.List;

public interface ISearchCatalog {
  List<BookSearchResult> searchCatalog(SearchCatalog query);
}
