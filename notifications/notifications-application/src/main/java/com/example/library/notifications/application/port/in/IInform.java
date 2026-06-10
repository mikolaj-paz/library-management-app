package com.example.library.notifications.application.port.in;

import com.example.library.notifications.application.command.InformCommand;

public interface IInform {

  void inform(InformCommand data);
}
