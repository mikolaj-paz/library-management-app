const statusEl = document.getElementById("health-status");
const checkButton = document.getElementById("check-health");

async function checkHealth() {
  statusEl.textContent = "Checking backend health...";

  try {
    const response = await fetch("/actuator/health", {
      headers: { Accept: "application/json" }
    });

    if (!response.ok) {
      statusEl.textContent = `Health endpoint returned HTTP ${response.status}.`;
      return;
    }

    const payload = await response.json();
    statusEl.textContent = `Backend is ${payload.status || "UP"}.`;
  } catch (error) {
    statusEl.textContent = "Backend health endpoint is not available yet.";
  }
}

checkButton.addEventListener("click", checkHealth);
checkHealth();
