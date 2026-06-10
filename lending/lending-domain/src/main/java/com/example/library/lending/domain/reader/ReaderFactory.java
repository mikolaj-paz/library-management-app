package com.example.library.lending.domain.reader;

import com.example.library.sharedkernel.identifier.ReaderId;

public interface ReaderFactory {

  Reader reconstitute(ReaderId id, ReaderStatus status);
}
