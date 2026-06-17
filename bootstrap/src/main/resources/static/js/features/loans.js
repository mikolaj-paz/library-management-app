import { request } from "../core/api.js";
import { bindForm, setOutput } from "../core/dom.js";

async function lendBookCopy(values) {
  setOutput("lend-book-copy-output", "Loading...");

  const response = await request("/loans", {
    method: "POST",
    body: {
      bookCopyId: values.bookCopyId,
      readerId: values.readerId,
    }
  });

  setOutput("lend-book-copy-output", response.data, response.status || "NETWORK");
}

async function returnBookCopy(values) {
  setOutput("return-book-copy-output", "Loading...");

  const response = await request(`/loans/return/${encodeURIComponent(values.bookCopyId || "")}`, {
    method: "POST"
  });

  setOutput("return-book-copy-output", response.data, response.status || "NETWORK");
}

async function extendLoan(values) {
  setOutput("extend-loan-output", "Loading...");

  const response = await request(`/loans/extend/${encodeURIComponent(values.loanId || "")}`, {
    method: "POST",
    body: {
      readerId: values.readerId
    }
  });

  setOutput("extend-loan-output", response.data, response.status || "NETWORK");
}

async function listLoans(values) {
  setOutput("list-loans-output", "Loading...");

  const response = await request("/loans/list", {
    method: "POST",
    query: {
      readerId: values.readerId
    }
  });

  setOutput("list-loans-output", response.data, response.status || "NETWORK");
}

export function initLoansFeature() {
  bindForm("lend-book-copy-form", lendBookCopy);
  bindForm("return-book-copy-form", returnBookCopy);
  bindForm("extend-loan-form", extendLoan);
  bindForm("list-loans-form", listLoans);
}
