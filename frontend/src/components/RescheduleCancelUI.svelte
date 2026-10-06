<script>
  import { onMount } from 'svelte';

  // Secure link token and appointment initial state
  export let secureToken = 'sec_lnk_9842_8a2f';

  let appointment = {
    bookingId: 'BK-9842',
    masterId: 'M-101',
    masterName: 'Giorgi Beridze',
    serviceName: 'Plumbing Repair (Сантехника)',
    city: 'Tbilisi',
    slotStart: '2026-10-10T14:00:00Z',
    slotEnd: '2026-10-10T15:00:00Z',
    amountGEL: 85,
    status: 'CONFIRMED' // 'CONFIRMED' | 'RESCHEDULED' | 'CANCELLED'
  };

  // Reschedule form state
  let newDate = '2026-10-15';
  let newTime = '16:00';
  let rescheduleReason = 'Client requested schedule adjustment';
  let isRescheduling = false;
  let rescheduleSuccess = false;
  let rescheduleError = '';

  // Cancellation state
  let cancelReason = 'Schedule conflict';
  let isCancelling = false;
  let cancelSuccess = false;
  let cancelError = '';

  // Network simulation toggle for manual testing/verification of network failure retry
  let simulateNetworkError = false;

  onMount(() => {
    // Read query parameter if accessed via secure link like ?token=...
    if (typeof window !== 'undefined') {
      const urlParams = new URLSearchParams(window.location.search);
      const tokenParam = urlParams.get('token');
      if (tokenParam) {
        secureToken = tokenParam;
      }
    }
  });

  function formatDisplayDate(isoString) {
    if (!isoString) return '';
    try {
      const d = new Date(isoString);
      return d.toISOString().replace('T', ' ').substring(0, 16) + ' UTC';
    } catch (e) {
      return isoString;
    }
  }

  async function handleReschedule(event) {
    if (event) event.preventDefault();
    rescheduleError = '';
    rescheduleSuccess = false;
    isRescheduling = true;

    const requestedSlotStart = `${newDate}T${newTime}:00Z`;
    const requestedSlotEnd = `${newDate}T${parseInt(newTime.split(':')[0], 10) + 1}:00:00Z`;

    try {
      if (simulateNetworkError) {
        throw new TypeError('Failed to fetch (Simulated Network Error)');
      }

      const response = await fetch(`/api/v1/bookings/${appointment.bookingId}/reschedule`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          bookingId: appointment.bookingId,
          requestedSlotStart: requestedSlotStart,
          requestedSlotEnd: requestedSlotEnd,
          reason: rescheduleReason
        })
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({ message: 'Failed to reschedule appointment.' }));
        throw new Error(errData.message || `Server error ${response.status}`);
      }

      const data = await response.json();
      appointment.slotStart = data.slotStart || requestedSlotStart;
      appointment.slotEnd = data.slotEnd || requestedSlotEnd;
      appointment.status = data.status || 'RESCHEDULED';
      rescheduleSuccess = true;
    } catch (err) {
      if (err.name === 'TypeError' || err.message.includes('Failed to fetch')) {
        rescheduleError = 'Network connection failed while rescheduling. Please verify your connection and try again.';
      } else {
        rescheduleError = `Reschedule error: ${err.message}`;
      }
    } finally {
      isRescheduling = false;
    }
  }

  async function handleCancel(event) {
    if (event) event.preventDefault();
    cancelError = '';
    cancelSuccess = false;
    isCancelling = true;

    try {
      if (simulateNetworkError) {
        throw new TypeError('Failed to fetch (Simulated Network Error)');
      }

      const response = await fetch(`/api/v1/bookings/${appointment.bookingId}/cancel`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          bookingId: appointment.bookingId,
          reason: cancelReason
        })
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({ message: 'Failed to cancel appointment.' }));
        throw new Error(errData.message || `Server error ${response.status}`);
      }

      const data = await response.json();
      appointment.status = data.status || 'CANCELLED';
      cancelSuccess = true;
    } catch (err) {
      if (err.name === 'TypeError' || err.message.includes('Failed to fetch') || err.message.includes('Network Error')) {
        cancelError = 'Cancellation failed due to network error. Please check your connection and try again.';
      } else {
        cancelError = `Cancellation error: ${err.message}`;
      }
    } finally {
      isCancelling = false;
    }
  }

  function getStatusBadgeClass(status) {
    switch (status) {
      case 'CONFIRMED': return 'badge-confirmed';
      case 'RESCHEDULED': return 'badge-rescheduled';
      case 'CANCELLED': return 'badge-cancelled';
      default: return 'badge-default';
    }
  }
</script>

<section class="reschedule-card" id="reschedule-cancel-section" aria-labelledby="reschedule-heading">
  <header class="card-header">
    <h2 id="reschedule-heading">Customer Appointment Management</h2>
    <p class="subtitle">View, reschedule, or cancel your booked service in Georgia</p>
    <div class="secure-token-badge">
      <span class="token-label">Secure Access Link:</span>
      <code class="token-value">{secureToken}</code>
    </div>
  </header>

  <!-- Current Appointment Details -->
  <div class="appointment-details" aria-label="Current Appointment Information">
    <h3 class="section-subtitle">Current Appointment Overview</h3>
    <div class="details-grid">
      <div class="detail-row">
        <span class="detail-label">Booking Reference:</span>
        <span class="detail-value font-mono"><strong>{appointment.bookingId}</strong></span>
      </div>
      <div class="detail-row">
        <span class="detail-label">Master:</span>
        <span class="detail-value">{appointment.masterName} ({appointment.city})</span>
      </div>
      <div class="detail-row">
        <span class="detail-label">Service:</span>
        <span class="detail-value">{appointment.serviceName}</span>
      </div>
      <div class="detail-row">
        <span class="detail-label">Scheduled Time:</span>
        <span class="detail-value"><strong>{formatDisplayDate(appointment.slotStart)}</strong></span>
      </div>
      <div class="detail-row">
        <span class="detail-label">Price:</span>
        <span class="detail-value price-text">{appointment.amountGEL} ₾ (GEL)</span>
      </div>
      <div class="detail-row">
        <span class="detail-label">Appointment Status:</span>
        <span class={`status-badge ${getStatusBadgeClass(appointment.status)}`}>
          {appointment.status}
        </span>
      </div>
    </div>
  </div>

  <!-- Network failure simulation toggle for offline/retry verification -->
  <div class="network-sim-box">
    <label for="simulate-net-err-checkbox" class="sim-label">
      <input
        id="simulate-net-err-checkbox"
        type="checkbox"
        bind:checked={simulateNetworkError}
        class="sim-checkbox"
      />
      Simulate Network Failure (To verify error state & retry flow)
    </label>
  </div>

  {#if appointment.status !== 'CANCELLED'}
    <!-- Reschedule Form -->
    <form on:submit={handleReschedule} class="reschedule-form" aria-labelledby="reschedule-form-title">
      <h3 id="reschedule-form-title" class="section-subtitle">Reschedule Appointment</h3>

      {#if rescheduleError}
        <div class="status-banner error" role="alert" aria-live="polite">
          <p class="banner-title"><strong>Reschedule Request Failed</strong></p>
          <p class="banner-text">{rescheduleError}</p>
          <button type="button" class="retry-btn" on:click={handleReschedule}>
            Retry Reschedule
          </button>
        </div>
      {/if}

      {#if rescheduleSuccess}
        <div class="status-banner success" role="alert" aria-live="polite">
          <p class="banner-title"><strong>✓ Appointment Rescheduled Successfully!</strong></p>
          <p class="banner-text">Your appointment with {appointment.masterName} is now set to <strong>{formatDisplayDate(appointment.slotStart)}</strong>.</p>
        </div>
      {/if}

      <div class="form-group">
        <label for="new-date-input" class="form-label">
          Select New Date <span class="required-asterisk">*</span>
        </label>
        <input
          id="new-date-input"
          type="date"
          bind:value={newDate}
          required
          class="form-control"
          aria-describedby="new-date-help"
        />
        <span id="new-date-help" class="help-text">Choose an available future date.</span>
      </div>

      <div class="form-group">
        <label for="new-time-select" class="form-label">
          Select New Time Slot <span class="required-asterisk">*</span>
        </label>
        <select
          id="new-time-select"
          bind:value={newTime}
          required
          class="form-control"
        >
          <option value="10:00">10:00 AM</option>
          <option value="12:00">12:00 PM</option>
          <option value="14:00">02:00 PM</option>
          <option value="16:00">04:00 PM</option>
          <option value="18:00">06:00 PM</option>
        </select>
      </div>

      <div class="form-group">
        <label for="reschedule-reason-input" class="form-label">
          Reason for Rescheduling
        </label>
        <input
          id="reschedule-reason-input"
          type="text"
          bind:value={rescheduleReason}
          class="form-control"
          placeholder="e.g. Schedule conflict or travel plans"
        />
      </div>

      <button
        type="submit"
        class="primary-btn"
        disabled={isRescheduling}
      >
        {isRescheduling ? 'Updating Schedule...' : 'Confirm New Time Slot'}
      </button>
    </form>

    <hr class="divider" />

    <!-- Cancellation Form -->
    <form on:submit={handleCancel} class="cancel-form" aria-labelledby="cancel-form-title">
      <h3 id="cancel-form-title" class="section-subtitle cancel-title">Cancel Appointment</h3>
      <p class="cancel-warning-text">
        If you no longer need this service, you can cancel your appointment. Master {appointment.masterName} will be notified immediately.
      </p>

      {#if cancelError}
        <div class="status-banner error" role="alert" aria-live="polite" id="cancellation-error-banner">
          <p class="banner-title"><strong>Cancellation Failed</strong></p>
          <p class="banner-text">{cancelError}</p>
          <button type="button" class="retry-btn" on:click={handleCancel} id="retry-cancel-btn">
            Retry Cancellation
          </button>
        </div>
      {/if}

      <div class="form-group">
        <label for="cancel-reason-input" class="form-label">
          Reason for Cancellation
        </label>
        <input
          id="cancel-reason-input"
          type="text"
          bind:value={cancelReason}
          class="form-control"
          placeholder="e.g. No longer needed"
        />
      </div>

      <button
        type="submit"
        class="danger-btn"
        disabled={isCancelling}
        id="cancel-appointment-btn"
      >
        {isCancelling ? 'Processing Cancellation...' : 'Cancel Appointment'}
      </button>
    </form>
  {:else}
    <!-- Cancelled Appointment Status View -->
    <div class="status-banner cancelled-banner" role="alert">
      <h3 class="banner-title">Appointment Cancelled</h3>
      <p class="banner-text">
        This booking ({appointment.bookingId}) has been cancelled. If you need a new appointment, please browse masters or contact support.
      </p>
    </div>
  {/if}
</section>

<style>
  .reschedule-card {
    background: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 8px;
    padding: 24px;
    max-width: 600px;
    margin: 0 auto 32px auto;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    color: #1f2328;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .card-header {
    border-bottom: 2px solid #0969da;
    padding-bottom: 12px;
    margin-bottom: 20px;
  }

  .card-header h2 {
    margin: 0 0 4px 0;
    font-size: 1.5rem;
    color: #0969da;
  }

  .subtitle {
    margin: 0 0 8px 0;
    font-size: 0.95rem;
    color: #424a53;
  }

  .secure-token-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background-color: #f6f8fa;
    border: 1px solid #d0d7de;
    border-radius: 4px;
    padding: 4px 8px;
    font-size: 0.85rem;
  }

  .token-label {
    color: #424a53;
    font-weight: 600;
  }

  .token-value {
    background-color: #e1e4e8;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: monospace;
    color: #0969da;
  }

  .appointment-details {
    background-color: #f6f8fa;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 16px;
    margin-bottom: 20px;
  }

  .section-subtitle {
    margin-top: 0;
    margin-bottom: 12px;
    font-size: 1.1rem;
    color: #0969da;
  }

  .details-grid {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .detail-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.95rem;
  }

  .detail-label {
    color: #424a53;
  }

  .detail-value {
    color: #1f2328;
  }

  .font-mono {
    font-family: monospace;
  }

  .price-text {
    font-weight: 600;
    color: #0969da;
  }

  .status-badge {
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 0.8rem;
    font-weight: 600;
  }

  .badge-confirmed {
    background-color: #dafbe1;
    color: #0e4a1f;
    border: 1px solid #1a7f37;
  }

  .badge-rescheduled {
    background-color: #ddf4ff;
    color: #0969da;
    border: 1px solid #54a3ff;
  }

  .badge-cancelled {
    background-color: #ffebe9;
    color: #82071e;
    border: 1px solid #cf222e;
  }

  .badge-default {
    background-color: #f6f8fa;
    color: #424a53;
    border: 1px solid #d0d7de;
  }

  .network-sim-box {
    background-color: #fff8c5;
    border: 1px solid #d4a72c;
    border-radius: 6px;
    padding: 10px 12px;
    margin-bottom: 20px;
  }

  .sim-label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 0.85rem;
    font-weight: 600;
    color: #4d3800;
    cursor: pointer;
  }

  .sim-checkbox {
    width: 16px;
    height: 16px;
    accent-color: #0969da;
  }

  .sim-checkbox:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .reschedule-form, .cancel-form {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .form-label {
    font-weight: 600;
    font-size: 0.9rem;
    color: #1f2328;
  }

  .required-asterisk {
    color: #cf222e;
  }

  .form-control {
    padding: 10px 12px;
    font-size: 0.95rem;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    background-color: #ffffff;
    color: #1f2328;
  }

  .form-control:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 1px;
    border-color: #0969da;
  }

  .help-text {
    font-size: 0.8rem;
    color: #424a53;
  }

  .divider {
    border: 0;
    border-top: 1px solid #d0d7de;
    margin: 24px 0;
  }

  .cancel-title {
    color: #cf222e;
  }

  .cancel-warning-text {
    margin: 0;
    font-size: 0.9rem;
    color: #424a53;
  }

  .primary-btn {
    background-color: #0969da;
    color: #ffffff;
    font-size: 1rem;
    font-weight: 600;
    padding: 12px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.15s ease;
  }

  .primary-btn:hover:not(:disabled) {
    background-color: #0353e9;
  }

  .primary-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .primary-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .danger-btn {
    background-color: #cf222e;
    color: #ffffff;
    font-size: 1rem;
    font-weight: 600;
    padding: 12px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.15s ease;
  }

  .danger-btn:hover:not(:disabled) {
    background-color: #a40e26;
  }

  .danger-btn:focus-visible {
    outline: 3px solid #cf222e;
    outline-offset: 2px;
  }

  .danger-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .retry-btn {
    margin-top: 8px;
    background-color: #ffffff;
    color: #cf222e;
    border: 1px solid #cf222e;
    font-size: 0.9rem;
    font-weight: 600;
    padding: 8px 14px;
    border-radius: 6px;
    cursor: pointer;
  }

  .retry-btn:hover {
    background-color: #ffebe9;
  }

  .retry-btn:focus-visible {
    outline: 3px solid #cf222e;
    outline-offset: 2px;
  }

  .status-banner {
    padding: 14px;
    border-radius: 6px;
  }

  .status-banner.error {
    background-color: #ffebe9;
    border: 1px solid #cf222e;
    color: #82071e;
  }

  .status-banner.success {
    background-color: #dafbe1;
    border: 1px solid #1a7f37;
    color: #0e4a1f;
  }

  .status-banner.cancelled-banner {
    background-color: #ffebe9;
    border: 1px solid #cf222e;
    color: #82071e;
    padding: 20px;
  }

  .banner-title {
    margin: 0 0 4px 0;
    font-size: 1rem;
  }

  .banner-text {
    margin: 0;
    font-size: 0.9rem;
  }
</style>
