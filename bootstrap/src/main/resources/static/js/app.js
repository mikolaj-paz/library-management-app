import { initCatalogFeature } from "./features/catalog.js";
import { initBookCopiesFeature } from "./features/bookCopies.js";
import { initLoansFeature } from "./features/loans.js";
import { initReservationsFeature } from "./features/reservations.js";
import { initBooksFeature } from "./features/books.js";
import { initUsersFeature } from "./features/users.js";

function initApp() {
  initCatalogFeature();
  initBooksFeature();
  initBookCopiesFeature();
  initLoansFeature();
  initReservationsFeature();
  initUsersFeature();
}

initApp();
