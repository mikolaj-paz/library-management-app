import { request } from "../core/api.js";
import { bindForm, setOutput } from "../core/dom.js";

async function addBookCopy(values) {
  setOutput("add-book-copy-output", "Loading...");

  const response = await request("/book-copies", {
    method: "POST",
    body: {
      bookId: values.bookId
    }
  });

  setOutput("add-book-copy-output", response.data, response.status || "NETWORK");
}

async function removeBookCopy(values) {
  setOutput("remove-book-copy-output", "Loading...");

  const response = await request("/book-copies/remove", {
    method: "POST",
    body: {
      bookCopyId: values.bookCopyId
    }
  });

  setOutput("remove-book-copy-output", response.data, response.status || "NETWORK");
}

export function initBookCopiesFeature() {
  bindForm("add-book-copy-form", addBookCopy);
  bindForm("remove-book-copy-form", removeBookCopy);
}
