package com.example.library.domain.factory;

import com.example.library.domain.model.BookCopy;
import com.example.library.domain.model.CopyId;
import com.example.library.domain.model.CopyStatus;

public class BookCopyFactory {

  public BookCopy create(CopyId id) {
    // This should also register the event in the domain event system
    return new BookCopy(id, CopyStatus.AVAILABLE);
  }
}
