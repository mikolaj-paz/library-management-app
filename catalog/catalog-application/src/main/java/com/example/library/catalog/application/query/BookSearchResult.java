package com.example.library.catalog.application.query;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;

public record BookSearchResult(
    BookId bookId, String title, String author, ISBN isbn, boolean hasAvailableCopies) {}
