package com.example.library.lending.application.port.in;

import com.example.library.sharedkernel.identifier.ReaderId;

public interface IHandleOverdueBookReturn {

  void handleOverdueBookReturn(ReaderId readerId);
}
