package com.example.library.catalog.infrastructure.in.web;

import com.example.library.catalog.application.command.AddBook;
import com.example.library.catalog.application.port.in.IAddBook;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

  private final IAddBook addBookService;

  public BookController(IAddBook addBookService) {
    this.addBookService = addBookService;
  }

  @PostMapping
  public ResponseEntity<?> addBook(@RequestBody AddBookRequest request) {
    try {
      var command =
          new AddBook(
              request.title(),
              request.author(),
              new ISBN(request.isbn()),
              request.publisher(),
              LocalDate.parse(request.publicationDate()));
      var book = addBookService.addBook(command);
      return ResponseEntity.ok(Map.of("bookId", book.id().value().toString()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  record AddBookRequest(
      String title, String author, String isbn, String publisher, String publicationDate) {}
}
