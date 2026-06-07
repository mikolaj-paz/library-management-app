package com.example.library.lending.application.command;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public record ReserveBook(ReaderId readerId, BookId bookId) {}
