import { request } from "../core/api.js";
import { bindForm, setOutput } from "../core/dom.js";

async function registerReader(values) {
  setOutput("register-reader-output", "Loading...");

  const response = await request("/users/readers/register", {
    method: "POST",
    body: {
      name: values.name,
      surname: values.surname,
      email: values.email,
      telephone: values.telephone
    }
  });

  setOutput("register-reader-output", response.data, response.status || "NETWORK");
}

async function unblockReader(values) {
  setOutput("unblock-reader-output", "Loading...");

  const response = await request("/users/readers/unblock", {
    method: "POST",
    body: {
      readerAccountId: values.readerAccountId
    }
  });

  setOutput("unblock-reader-output", response.data, response.status || "NETWORK");
}

export function initUsersFeature() {
  bindForm("register-reader-form", registerReader);
  bindForm("unblock-reader-form", unblockReader);
}