export function getById(id) {
  return document.getElementById(id);
}

export function formValues(form) {
  const payload = {};
  const data = new FormData(form);

  for (const [key, value] of data.entries()) {
    payload[key] = typeof value === "string" ? value.trim() : value;
  }

  return payload;
}

export function setText(id, text) {
  const element = getById(id);
  if (!element) {
    return;
  }

  element.textContent = text;
}

function toDisplayText(result) {
  if (result == null) {
    return "";
  }

  if (typeof result === "string") {
    return result;
  }

  return JSON.stringify(result, null, 2);
}

export function setOutput(id, payload, status) {
  const content = status ? { status, payload } : payload;
  setText(id, toDisplayText(content));
}

export function bindForm(formId, onSubmit) {
  const form = getById(formId);
  if (!form) {
    return;
  }

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const values = formValues(form);
    await onSubmit(values);
  });
}
