import { request } from "../core/api.js";
import { bindForm, setOutput } from "../core/dom.js";

async function reserveBook(values) {
  setOutput("reserve-book-output", "Loading...");

  const response = await request("/reservations", {
    method: "POST",
    body: {
      readerId: values.readerId,
      bookId: values.bookId
    }
  });

  setOutput("reserve-book-output", response.data, response.status || "NETWORK");
}

async function joinWaitingQueue(values) {
  setOutput("join-waiting-queue-output", "Loading...");

  const response = await request("/reservations/queue", {
    method: "POST",
    body: {
      readerId: values.readerId,
      bookId: values.bookId
    }
  });

  setOutput("join-waiting-queue-output", response.data, response.status || "NETWORK");
}

export function initReservationsFeature() {
  bindForm("reserve-book-form", reserveBook);
  bindForm("join-waiting-queue-form", joinWaitingQueue);
}
