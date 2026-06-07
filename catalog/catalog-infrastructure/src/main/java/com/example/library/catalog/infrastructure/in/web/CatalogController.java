package com.example.library.catalog.infrastructure.in.web;

import com.example.library.catalog.application.port.in.IGetBookDetails;
import com.example.library.catalog.application.port.in.ISearchCatalog;
import com.example.library.catalog.application.query.GetBookDetails;
import com.example.library.catalog.application.query.SearchCatalog;
import com.example.library.catalog.domain.exception.BookNotFoundException;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

  private final ISearchCatalog searchCatalogService;
  private final IGetBookDetails getBookDetailsService;

  public CatalogController(
      ISearchCatalog searchCatalogService, IGetBookDetails getBookDetailsService) {
    this.searchCatalogService = searchCatalogService;
    this.getBookDetailsService = getBookDetailsService;
  }

  @GetMapping
  public ResponseEntity<?> searchCatalog(@PathVariable String phrase) {
    var results = searchCatalogService.search(new SearchCatalog(phrase));
    if (results.isEmpty()) {
      return ResponseEntity.ok(Map.of("message", "No books found.", "results", List.of()));
    }
    return ResponseEntity.ok(results);
  }

  @GetMapping("/{bookId}")
  public ResponseEntity<?> getBookDetails(@PathVariable String bookId) {
    try {
      var details = getBookDetailsService.bookDetails(new GetBookDetails(BookId.of(bookId)));
      return ResponseEntity.ok(details);
    } catch (BookNotFoundException e) {
      return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    }
  }
}
