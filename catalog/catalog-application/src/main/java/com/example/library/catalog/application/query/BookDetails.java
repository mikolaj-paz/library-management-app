package com.example.library.catalog.application.query;

import com.example.library.catalog.domain.book.BookId;
import com.example.library.catalog.domain.book.ISBN;

public record BookDetails(
    BookId bookId, String title, String author, ISBN isbn, int totalCopies, int availableCopies) {}
