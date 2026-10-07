<script>
  // Days of the week mock schedule for a professional
  export let selectedDate = '2026-10-12';
  export let masterName = 'Giorgi Beridze';

  // Schedule days list
  let days = [
    { date: '2026-10-12', label: 'Mon, Oct 12', isBlocked: false },
    { date: '2026-10-13', label: 'Tue, Oct 13', isBlocked: false },
    { date: '2026-10-14', label: 'Wed, Oct 14', isBlocked: false },
    { date: '2026-10-15', label: 'Thu, Oct 15', isBlocked: false },
    { date: '2026-10-16', label: 'Fri, Oct 16', isBlocked: false }
  ];

  // Time slots for selected date
  let slots = [
    { id: 'SLOT-101', time: '09:00 - 10:00', status: 'BOOKED', clientName: 'Nino Kapanadze', service: 'Plumbing Repair' },
    { id: 'SLOT-102', time: '10:30 - 11:30', status: 'AVAILABLE', clientName: '', service: '' },
    { id: 'SLOT-103', time: '12:00 - 13:00', status: 'BOOKED', clientName: 'Luka Maisuradze', service: 'Pipe Replacement' },
    { id: 'SLOT-104', time: '14:00 - 15:00', status: 'AVAILABLE', clientName: '', service: '' },
    { id: 'SLOT-105', time: '15:30 - 16:30', status: 'BOOKED', clientName: 'Tamar Tsiklauri', service: 'Drain Cleaning' }
  ];

  // Feedback banner state
  let feedbackMessage = '';
  let feedbackType = 'info'; // 'info' | 'success' | 'warning'

  // Modal for destructive action confirmation (blocking a day)
  let showBlockConfirmModal = false;
  let dayToBlock = null;

  function setFeedback(msg, type = 'success') {
    feedbackMessage = msg;
    feedbackType = type;
  }

  function handleSelectDate(dateStr) {
    selectedDate = dateStr;
    const dayObj = days.find(d => d.date === dateStr);
    setFeedback(`Selected date: ${dayObj ? dayObj.label : dateStr}`, 'info');
  }

  // Destructive action initiation
  function promptBlockDay(day) {
    dayToBlock = day;
    showBlockConfirmModal = true;
  }

  // Confirming destructive action
  function confirmBlockDay() {
    if (!dayToBlock) return;
    days = days.map(d => {
      if (d.date === dayToBlock.date) {
        return { ...d, isBlocked: true };
      }
      return d;
    });
    setFeedback(`Day ${dayToBlock.label} has been blocked. All slots on this day are now unavailable.`, 'warning');
    showBlockConfirmModal = false;
    dayToBlock = null;
  }

  function cancelBlockDay() {
    showBlockConfirmModal = false;
    dayToBlock = null;
    setFeedback('Day block operation cancelled.', 'info');
  }

  function unblockDay(day) {
    days = days.map(d => {
      if (d.date === day.date) {
        return { ...d, isBlocked: false };
      }
      return d;
    });
    setFeedback(`Day ${day.label} is now unblocked and available for bookings.`, 'success');
  }

  // Slot management (marking no-show or toggling availability)
  function markNoShow(slotId) {
    let client = '';
    slots = slots.map(s => {
      if (s.id === slotId) {
        client = s.clientName;
        return { ...s, status: 'NO_SHOW' };
      }
      return s;
    });
    setFeedback(`Slot ${slotId} marked as No-Show for client ${client}.`, 'warning');
  }

  function updateSlotStatus(slotId, newStatus) {
    slots = slots.map(s => {
      if (s.id === slotId) {
        return { ...s, status: newStatus };
      }
      return s;
    });
    setFeedback(`Updated slot ${slotId} status to ${newStatus}.`, 'success');
  }

  function getStatusBadgeClass(status) {
    switch (status) {
      case 'BOOKED': return 'badge-booked';
      case 'AVAILABLE': return 'badge-available';
      case 'NO_SHOW': return 'badge-noshow';
      case 'BLOCKED': return 'badge-blocked';
      default: return 'badge-default';
    }
  }

  $: currentDayObj = days.find(d => d.date === selectedDate) || days[0];
</script>

<section class="calendar-card" id="calendar-management-section" aria-labelledby="calendar-heading">
  <header class="calendar-header">
    <h2 id="calendar-heading">Professional Calendar & Time Management</h2>
    <p class="subtitle">Manage availability, block schedule days, and mark customer no-shows for {masterName}</p>
  </header>

  <!-- Live feedback banner -->
  {#if feedbackMessage}
    <div
      class={`feedback-banner ${feedbackType}`}
      role="status"
      aria-live="polite"
      id="calendar-feedback-banner"
    >
      <span class="feedback-text">{feedbackMessage}</span>
      <button
        type="button"
        class="close-banner-btn"
        aria-label="Dismiss message"
        on:click={() => feedbackMessage = ''}
      >
        ✕
      </button>
    </div>
  {/if}

  <!-- Day selector and day blocking -->
  <div class="day-navigation-container">
    <h3 class="section-subtitle">Select Working Day</h3>
    <div class="day-buttons-grid" role="group" aria-label="Working Days">
      {#each days as day (day.date)}
        <div class="day-card {day.date === selectedDate ? 'selected' : ''} {day.isBlocked ? 'blocked' : ''}">
          <button
            type="button"
            class="day-select-btn"
            aria-pressed={day.date === selectedDate}
            on:click={() => handleSelectDate(day.date)}
          >
            <span class="day-label">{day.label}</span>
            {#if day.isBlocked}
              <span class="blocked-tag">BLOCKED</span>
            {/if}
          </button>
          <div class="day-action-container">
            {#if day.isBlocked}
              <button
                type="button"
                class="unblock-btn"
                aria-label={`Unblock ${day.label}`}
                on:click={() => unblockDay(day)}
              >
                Unblock
              </button>
            {:else}
              <button
                type="button"
                class="block-btn"
                aria-label={`Block day ${day.label}`}
                on:click={() => promptBlockDay(day)}
              >
                Block Day
              </button>
            {/if}
          </div>
        </div>
      {/each}
    </div>
  </div>

  <!-- Time Slots Schedule -->
  <div class="slots-container">
    <div class="slots-header">
      <h3 class="section-subtitle">
        Time Slots for {currentDayObj ? currentDayObj.label : selectedDate}
      </h3>
      {#if currentDayObj && currentDayObj.isBlocked}
        <span class="day-status-notice blocked" role="alert">
          ⛔ This day is currently BLOCKED from accepting bookings.
        </span>
      {/if}
    </div>

    <div class="slots-list" role="region" aria-label="Time slots list">
      {#each slots as slot (slot.id)}
        <div class="slot-row {slot.status.toLowerCase()}">
          <div class="slot-info">
            <span class="slot-time"><strong>{slot.time}</strong></span>
            {#if slot.clientName}
              <span class="slot-client">Client: <strong>{slot.clientName}</strong> ({slot.service})</span>
            {:else}
              <span class="slot-client open-slot">Open for booking</span>
            {/if}
          </div>

          <div class="slot-status-box">
            <span class={`status-badge ${getStatusBadgeClass(slot.status)}`}>
              {slot.status}
            </span>
          </div>

          <div class="slot-actions">
            {#if slot.status === 'BOOKED'}
              <button
                type="button"
                class="noshow-btn"
                aria-label={`Mark ${slot.clientName} as No-Show for ${slot.time}`}
                on:click={() => markNoShow(slot.id)}
              >
                Mark No-Show
              </button>
            {/if}

            <label for={`slot-status-select-${slot.id}`} class="visually-hidden">
              Change status for slot {slot.time}
            </label>
            <select
              id={`slot-status-select-${slot.id}`}
              value={slot.status}
              on:change={(e) => updateSlotStatus(slot.id, e.target.value)}
              class="slot-status-select"
              aria-label={`Update status for time slot ${slot.time}`}
            >
              <option value="AVAILABLE">Available</option>
              <option value="BOOKED">Booked</option>
              <option value="NO_SHOW">No-Show</option>
              <option value="BLOCKED">Blocked</option>
            </select>
          </div>
        </div>
      {/each}
    </div>
  </div>

  <!-- Confirmation Modal for Destructive Day Blocking -->
  {#if showBlockConfirmModal && dayToBlock}
    <div
      class="modal-backdrop"
      role="dialog"
      aria-modal="true"
      aria-labelledby="block-modal-title"
      aria-describedby="block-modal-desc"
    >
      <div class="modal-content">
        <h3 id="block-modal-title" class="modal-title">Confirm Day Blocking</h3>
        <p id="block-modal-desc" class="modal-desc">
          Are you sure you want to block <strong>{dayToBlock.label}</strong>?
          Blocking a day will make all time slots unavailable and prevent new client bookings on this date.
        </p>

        <div class="modal-actions">
          <button
            type="button"
            class="modal-cancel-btn"
            on:click={cancelBlockDay}
            id="cancel-block-day-btn"
          >
            Cancel
          </button>
          <button
            type="button"
            class="modal-confirm-btn"
            on:click={confirmBlockDay}
            id="confirm-block-day-btn"
          >
            Yes, Block Day
          </button>
        </div>
      </div>
    </div>
  {/if}
</section>

<style>
  .calendar-card {
    background: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 8px;
    padding: 24px;
    max-width: 900px;
    margin: 0 auto 32px auto;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    color: #1f2328;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .calendar-header {
    border-bottom: 2px solid #0969da;
    padding-bottom: 12px;
    margin-bottom: 20px;
  }

  .calendar-header h2 {
    margin: 0 0 4px 0;
    font-size: 1.5rem;
    color: #0969da;
  }

  .subtitle {
    margin: 0;
    font-size: 0.95rem;
    color: #424a53;
  }

  .feedback-banner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-radius: 6px;
    margin-bottom: 20px;
    font-size: 0.95rem;
    font-weight: 500;
  }

  .feedback-banner.success {
    background-color: #dafbe1;
    border: 1px solid #1a7f37;
    color: #0e4a1f;
  }

  .feedback-banner.info {
    background-color: #ddf4ff;
    border: 1px solid #54a3ff;
    color: #0969da;
  }

  .feedback-banner.warning {
    background-color: #fff8c5;
    border: 1px solid #9a6700;
    color: #4d3800;
  }

  .close-banner-btn {
    background: transparent;
    border: none;
    font-size: 1.1rem;
    font-weight: bold;
    cursor: pointer;
    color: inherit;
    padding: 0 4px;
  }

  .close-banner-btn:focus-visible {
    outline: 2px solid currentColor;
    border-radius: 4px;
  }

  .section-subtitle {
    margin-top: 0;
    margin-bottom: 12px;
    font-size: 1.1rem;
    color: #0969da;
  }

  .day-navigation-container {
    margin-bottom: 24px;
  }

  .day-buttons-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 12px;
  }

  .day-card {
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 10px;
    background-color: #f6f8fa;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
    transition: border-color 0.15s ease, background-color 0.15s ease;
  }

  .day-card.selected {
    border-color: #0969da;
    border-width: 2px;
    background-color: #ffffff;
  }

  .day-card.blocked {
    background-color: #ffebe9;
    border-color: #cf222e;
  }

  .day-select-btn {
    background: transparent;
    border: none;
    cursor: pointer;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    padding: 4px;
  }

  .day-select-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
    border-radius: 4px;
  }

  .day-label {
    font-weight: 600;
    font-size: 0.95rem;
    color: #1f2328;
  }

  .blocked-tag {
    font-size: 0.75rem;
    font-weight: bold;
    color: #cf222e;
  }

  .block-btn {
    background-color: #ffffff;
    border: 1px solid #cf222e;
    color: #cf222e;
    font-size: 0.8rem;
    font-weight: 600;
    padding: 4px 8px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.15s ease;
  }

  .block-btn:hover {
    background-color: #ffebe9;
  }

  .block-btn:focus-visible {
    outline: 3px solid #cf222e;
    outline-offset: 2px;
  }

  .unblock-btn {
    background-color: #ffffff;
    border: 1px solid #1a7f37;
    color: #1a7f37;
    font-size: 0.8rem;
    font-weight: 600;
    padding: 4px 8px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.15s ease;
  }

  .unblock-btn:hover {
    background-color: #dafbe1;
  }

  .unblock-btn:focus-visible {
    outline: 3px solid #1a7f37;
    outline-offset: 2px;
  }

  .slots-container {
    background-color: #f6f8fa;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 16px;
  }

  .slots-header {
    display: flex;
    flex-direction: column;
    gap: 6px;
    margin-bottom: 12px;
  }

  @media (min-width: 600px) {
    .slots-header {
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }
  }

  .day-status-notice.blocked {
    font-size: 0.85rem;
    font-weight: 600;
    color: #82071e;
    background: #ffebe9;
    padding: 4px 8px;
    border-radius: 4px;
    border: 1px solid #cf222e;
  }

  .slots-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .slot-row {
    background: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 12px 16px;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  @media (min-width: 640px) {
    .slot-row {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
    }
  }

  .slot-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .slot-time {
    font-size: 1rem;
    color: #1f2328;
  }

  .slot-client {
    font-size: 0.85rem;
    color: #424a53;
  }

  .open-slot {
    color: #1a7f37;
  }

  .status-badge {
    display: inline-block;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 0.8rem;
    font-weight: 600;
  }

  .badge-booked {
    background-color: #ddf4ff;
    color: #0969da;
    border: 1px solid #54a3ff;
  }

  .badge-available {
    background-color: #dafbe1;
    color: #0e4a1f;
    border: 1px solid #1a7f37;
  }

  .badge-noshow {
    background-color: #fff8c5;
    color: #4d3800;
    border: 1px solid #9a6700;
  }

  .badge-blocked {
    background-color: #ffebe9;
    color: #82071e;
    border: 1px solid #cf222e;
  }

  .badge-default {
    background-color: #f6f8fa;
    color: #424a53;
    border: 1px solid #d0d7de;
  }

  .slot-actions {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
  }

  .noshow-btn {
    background-color: #fff8c5;
    border: 1px solid #9a6700;
    color: #4d3800;
    font-size: 0.85rem;
    font-weight: 600;
    padding: 6px 10px;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.15s ease;
  }

  .noshow-btn:hover {
    background-color: #fbeb92;
  }

  .noshow-btn:focus-visible {
    outline: 3px solid #9a6700;
    outline-offset: 2px;
  }

  .slot-status-select {
    padding: 6px 10px;
    border-radius: 6px;
    border: 1px solid #d0d7de;
    background-color: #ffffff;
    font-size: 0.85rem;
    color: #1f2328;
    cursor: pointer;
  }

  .slot-status-select:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  /* Modal Dialog styles */
  .modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(15, 23, 42, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 999;
    padding: 16px;
  }

  .modal-content {
    background-color: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 8px;
    padding: 24px;
    max-width: 480px;
    width: 100%;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  }

  .modal-title {
    margin-top: 0;
    margin-bottom: 12px;
    font-size: 1.25rem;
    color: #cf222e;
  }

  .modal-desc {
    font-size: 0.95rem;
    color: #1f2328;
    line-height: 1.5;
    margin-bottom: 20px;
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }

  .modal-cancel-btn {
    background-color: #f6f8fa;
    border: 1px solid #d0d7de;
    color: #1f2328;
    font-size: 0.9rem;
    font-weight: 600;
    padding: 8px 16px;
    border-radius: 6px;
    cursor: pointer;
  }

  .modal-cancel-btn:hover {
    background-color: #e1e4e8;
  }

  .modal-cancel-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .modal-confirm-btn {
    background-color: #cf222e;
    border: none;
    color: #ffffff;
    font-size: 0.9rem;
    font-weight: 600;
    padding: 8px 16px;
    border-radius: 6px;
    cursor: pointer;
  }

  .modal-confirm-btn:hover {
    background-color: #a40e26;
  }

  .modal-confirm-btn:focus-visible {
    outline: 3px solid #cf222e;
    outline-offset: 2px;
  }

  .visually-hidden {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
  }
</style>
