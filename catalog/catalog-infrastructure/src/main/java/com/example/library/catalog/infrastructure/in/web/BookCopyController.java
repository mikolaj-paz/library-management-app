package com.example.library.catalog.infrastructure.in.web;

import com.example.library.catalog.application.command.AddBookCopy;
import com.example.library.catalog.application.command.RemoveBookCopy;
import com.example.library.catalog.application.port.in.IAddBookCopy;
import com.example.library.catalog.application.port.in.IRemoveBookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book-copies")
public class BookCopyController {

  private final IAddBookCopy addBookCopyService;
  private final IRemoveBookCopy removeBookCopyService;

  public BookCopyController(
      IAddBookCopy addBookCopyService, IRemoveBookCopy removeBookCopyService) {
    this.addBookCopyService = addBookCopyService;
    this.removeBookCopyService = removeBookCopyService;
  }

  @PostMapping
  public ResponseEntity<?> addBookCopy(@RequestBody AddBookCopyRequest request) {
    try {
      var bookId = BookId.of(request.bookId());
      var result = addBookCopyService.add(new AddBookCopy(bookId));

      return ResponseEntity.ok(Map.of("bookCopyId", result.value().toString()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  record AddBookCopyRequest(String bookId) {}

  @PostMapping("/remove")
  public ResponseEntity<?> removeBookCopy(@RequestBody RemoveBookCopyRequest request) {
    try {
      var bookCopyId = BookCopyId.of(request.bookCopyId());
      removeBookCopyService.remove(new RemoveBookCopy(bookCopyId));
      return ResponseEntity.ok(Map.of("message", "Book copy removed successfully."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  record RemoveBookCopyRequest(String bookCopyId) {}
}
