package com.example.library.lending.application.command;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public record JoinWaitingQueue(ReaderId readerId, BookId bookId) {}
