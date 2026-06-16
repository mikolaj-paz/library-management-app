package com.example.library.catalog.application.command;

import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;

public record AddBook(
    String title, String author, ISBN isbn, String publisher, LocalDate publicationDate) {}
