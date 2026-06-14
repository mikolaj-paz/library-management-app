import { initCatalogFeature } from "./features/catalog.js";
import { initBookCopiesFeature } from "./features/bookCopies.js";
import { initLoansFeature } from "./features/loans.js";
import { initReservationsFeature } from "./features/reservations.js";

function initApp() {
  initCatalogFeature();
  initBookCopiesFeature();
  initLoansFeature();
  initReservationsFeature();
}

initApp();
