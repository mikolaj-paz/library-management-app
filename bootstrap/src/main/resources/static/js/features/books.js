import { request } from "../core/api.js";
import { bindForm, setOutput } from "../core/dom.js";

async function createBook(values) {
  setOutput("create-book-output", "Loading...");

  const response = await request("/books", {
    method: "POST",
    body: {
      title: values.title,
      author: values.author,
      isbn: values.isbn,
      publisher: values.publisher,
      publicationDate: values.publicationDate
    }
  });

  setOutput("create-book-output", response.data, response.status || "NETWORK");
}

export function initBooksFeature() {
  bindForm("create-book-form", createBook);
}