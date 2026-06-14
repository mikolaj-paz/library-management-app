import { request } from "../core/api.js";
import { bindForm, setOutput } from "../core/dom.js";

async function searchCatalog(values) {
  setOutput("catalog-search-output", "Loading...");

  const phrase = values.phrase || "";
  const response = await request(`/catalog/search`, {
    method: "GET",
    query: { phrase }
  });

  setOutput("catalog-search-output", response.data, response.status);
}

async function bookDetails(values) {
  setOutput("book-details-output", "Loading...");

  const response = await request(`/catalog/${encodeURIComponent(values.bookId || "")}`);
  setOutput("book-details-output", response.data, response.status);
}

export function initCatalogFeature() {
  bindForm("catalog-search-form", searchCatalog);
  bindForm("book-details-form", bookDetails);
}
