package com.example.library.lending.domain.reader;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.identifier.ReaderId;
import org.junit.jupiter.api.Test;

class ReaderTest {

  @Test
  void should_not_be_blocked_when_reader_is_active() {
    var reader = new ReaderFactoryImpl().reconstitute(ReaderId.create(), ReaderStatus.ACTIVE);

    assertThat(reader.isBlocked()).isFalse();
  }

  @Test
  void should_be_blocked_when_reader_status_is_blocked() {
    var reader = new ReaderFactoryImpl().reconstitute(ReaderId.create(), ReaderStatus.BLOCKED);

    assertThat(reader.isBlocked()).isTrue();
  }
}
