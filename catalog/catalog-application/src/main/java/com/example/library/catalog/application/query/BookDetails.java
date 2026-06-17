package com.example.library.catalog.application.query;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;

public record BookDetails(
    BookId bookId,
    String title,
    String author,
    ISBN isbn,
    String publisher,
    LocalDate publicationDate,
    int totalCopies,
    int availableCopies) {}
