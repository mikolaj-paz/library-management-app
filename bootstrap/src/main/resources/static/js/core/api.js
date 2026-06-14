function buildQueryString(query) {
  if (!query) {
    return "";
  }

  const params = new URLSearchParams();
  Object.entries(query).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      params.append(key, String(value));
    }
  });

  const raw = params.toString();
  return raw ? `?${raw}` : "";
}

async function tryParseBody(response) {
  const text = await response.text();
  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text);
  } catch (_error) {
    return text;
  }
}

export async function request(path, options = {}) {
  const method = options.method || "GET";
  const queryString = buildQueryString(options.query);
  const url = `${path}${queryString}`;

  const headers = {
    Accept: "application/json",
    ...(options.body ? { "Content-Type": "application/json" } : {})
  };

  try {
    const response = await fetch(url, {
      method,
      headers,
      body: options.body ? JSON.stringify(options.body) : undefined
    });

    const data = await tryParseBody(response);

    return {
      ok: response.ok,
      status: response.status,
      data
    };
  } catch (_error) {
    return {
      ok: false,
      status: 0,
      data: { error: "Network error or backend unavailable." }
    };
  }
}
