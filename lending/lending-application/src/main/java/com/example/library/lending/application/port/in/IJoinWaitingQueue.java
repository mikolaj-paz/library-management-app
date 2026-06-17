package com.example.library.lending.application.port.in;

import com.example.library.lending.application.command.JoinWaitingQueue;

public interface IJoinWaitingQueue {

  void joinWaitingQueue(JoinWaitingQueue command);
}
