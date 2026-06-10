package com.example.library.notifications.infrastructure.in.events;

import com.example.library.notifications.application.command.InformCommand;
import com.example.library.notifications.application.port.in.IInform;
import com.example.library.notifications.domain.notification.NotificationType;
import com.example.library.sharedkernel.event.BookCopyLoaned;
import com.example.library.sharedkernel.event.BookCopyReserved;
import com.example.library.sharedkernel.event.BookCopyReturned;
import com.example.library.sharedkernel.event.LoanExtended;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.springframework.context.event.EventListener;

public class LendingEventListener {

  private final IInform informService;

  public LendingEventListener(IInform informService) {
    this.informService = informService;
  }

  private void inform(ReaderId readerId, NotificationType type, String message) {
    informService.inform(
        new InformCommand(readerId, type, "Message for " + readerId + ": " + message));
  }

  @EventListener
  public void on(BookCopyReserved event) {
    inform(
        event.readerId(),
        NotificationType.BOOK_COPY_RESERVED,
        "Your reservation for "
            + event.bookCopyId()
            + " is ready for pickup. Collect within 48 hours. Reservation number: "
            + event.reservationId()
            + ".");
  }

  @EventListener
  public void on(BookCopyLoaned event) {
    inform(
        event.readerId(),
        NotificationType.BOOK_COPY_LOANED,
        "You have borrowed "
            + event.bookCopyId()
            + ". Congratulations! Your due date: "
            + event.loanDueDate()
            + ". Loan number: "
            + event.loanId()
            + ".");
  }

  @EventListener
  public void on(BookCopyReturned event) {
    if (event.isOverdue()) {
      inform(
          event.readerId(),
          NotificationType.BOOK_COPY_RETURNED_OVERDUE,
          "You have returned "
              + event.bookCopyId()
              + ". Please note that because the return was overdue, an account remains blocked until the fine is paid.");
    } else {
      inform(
          event.readerId(),
          NotificationType.BOOK_COPY_RETURNED,
          "You have returned " + event.bookCopyId() + ". Thank you for being on time!");
    }
  }

  @EventListener
  public void on(LoanExtended event) {
    inform(
        event.readerId(),
        NotificationType.BOOK_COPY_LOANED,
        "Your loan "
            + event.loanId()
            + " for "
            + event.bookCopyId()
            + " has been extended. Your new due date: "
            + event.newDueDate()
            + ".");
  }
}
