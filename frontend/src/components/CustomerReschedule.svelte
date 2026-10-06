<script>
  let appointment = {
    service: "Plumbing Repair",
    date: "October 15, 2026",
    time: "14:00",
    professional: "Giorgi",
    price: "50 GEL"
  };

  let status = "idle"; // idle, loading, error, success
  let errorMessage = "";

  async function cancelAppointment() {
    status = "loading";
    errorMessage = "";

    // Simulate network failure
    setTimeout(() => {
      status = "error";
      errorMessage = "Network failed. Please try again.";
    }, 1000);
  }
</script>

<style>
  /* Base reset and text variables according to design principles */
  :global(body) {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
    background-color: #f9f9f9;
    color: #1a1a1a; /* High contrast */
    margin: 0;
    padding: 0;
  }

  .container {
    max-width: 600px;
    margin: 2rem auto;
    padding: 1.5rem;
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  h1 {
    font-size: 1.5rem;
    margin-bottom: 1rem;
    color: #111111; /* Contrast ratio well above 4.5:1 */
  }

  .appointment-details {
    margin-bottom: 1.5rem;
    padding: 1rem;
    background-color: #f0f0f0;
    border-radius: 4px;
    border: 1px solid #d1d1d1;
  }

  .appointment-details p {
    margin: 0.5rem 0;
    font-size: 1rem;
    line-height: 1.5;
  }

  .label {
    font-weight: 600;
  }

  button {
    padding: 0.75rem 1.5rem;
    font-size: 1rem;
    font-weight: 600;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  /* Visible focus for WCAG 2.1 AA */
  button:focus-visible {
    outline: 3px solid #0056b3;
    outline-offset: 2px;
  }

  .cancel-btn {
    background-color: #d32f2f; /* Dark red for good contrast */
    color: #ffffff;
  }

  .cancel-btn:hover {
    background-color: #b71c1c;
  }

  .cancel-btn:disabled {
    background-color: #9e9e9e;
    cursor: not-allowed;
  }

  .retry-btn {
    background-color: #1976d2;
    color: #ffffff;
    margin-top: 1rem;
  }

  .retry-btn:hover {
    background-color: #1565c0;
  }

  .error-message {
    color: #d32f2f;
    font-weight: 600;
    margin-top: 1rem;
    padding: 0.5rem;
    background-color: #ffebee;
    border-left: 4px solid #d32f2f;
  }

  .status-message {
    margin-top: 1rem;
    font-style: italic;
    color: #555555;
  }
</style>

<div class="container">
  <h1>Appointment Details</h1>

  <div class="appointment-details">
    <p><span class="label">Service:</span> {appointment.service}</p>
    <p><span class="label">Professional:</span> {appointment.professional}</p>
    <p><span class="label">Date:</span> {appointment.date}</p>
    <p><span class="label">Time:</span> {appointment.time}</p>
    <p><span class="label">Price:</span> {appointment.price}</p>
  </div>

  {#if status === 'idle' || status === 'error'}
    <button
      class="cancel-btn"
      on:click={cancelAppointment}
      aria-disabled={status === 'loading'}
    >
      Cancel Appointment
    </button>
  {/if}

  {#if status === 'loading'}
    <p class="status-message" aria-live="polite">Cancelling appointment...</p>
  {/if}

  {#if status === 'error'}
    <div class="error-message" aria-live="assertive" role="alert">
      {errorMessage}
    </div>
    <button class="retry-btn" on:click={cancelAppointment}>
      Retry Cancellation
    </button>
  {/if}
</div>