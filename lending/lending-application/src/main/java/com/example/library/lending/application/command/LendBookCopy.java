package com.example.library.lending.application.command;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;

public record LendBookCopy(BookCopyId bookCopyId, ReaderId readerId) {}
