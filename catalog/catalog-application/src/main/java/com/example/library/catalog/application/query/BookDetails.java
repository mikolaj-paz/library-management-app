package com.example.library.catalog.application.query;

import com.example.library.catalog.domain.book.ISBN;
import com.example.library.sharedkernel.identifier.BookId;

public record BookDetails(
    BookId bookId, String title, String author, ISBN isbn, int totalCopies, int availableCopies) {}
