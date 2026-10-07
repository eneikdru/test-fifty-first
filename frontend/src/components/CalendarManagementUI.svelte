<script>
  let selectedDate = '2026-10-12';
  let calendarView = 'day'; // 'day' | 'week'
  let feedbackMessage = '';
  let feedbackType = 'info'; // 'info' | 'success' | 'warning' | 'danger'

  // Confirmation modal state for destructive action
  let showConfirmModal = false;
  let modalAction = null; // { type: 'BLOCK_DAY' | 'BLOCK_SLOT', target: any, message: string }

  // Initial schedule slots and day blocks state
  let blockedDays = new Set(['2026-10-15']); // ISO dates blocked completely

  let slots = [
    { id: 'slot-1', date: '2026-10-12', time: '09:00 - 10:00', status: 'AVAILABLE', clientName: '', serviceName: '' },
    { id: 'slot-2', date: '2026-10-12', time: '10:30 - 11:30', status: 'BOOKED', clientName: 'Giorgi Maisuradze', serviceName: 'Plumbing Repair (Сантехника)' },
    { id: 'slot-3', date: '2026-10-12', time: '12:00 - 13:00', status: 'AVAILABLE', clientName: '', serviceName: '' },
    { id: 'slot-4', date: '2026-10-12', time: '14:00 - 15:00', status: 'BOOKED', clientName: 'Nino Tsereteli', serviceName: 'Electrical Fixing (Электрика)' },
    { id: 'slot-5', date: '2026-10-12', time: '15:30 - 16:30', status: 'NO_SHOW', clientName: 'Levan Dolidze', serviceName: 'AC Maintenance (Чистка)' },
    { id: 'slot-6', date: '2026-10-12', time: '17:00 - 18:00', status: 'BLOCKED', clientName: '', serviceName: '' },

    { id: 'slot-7', date: '2026-10-13', time: '10:00 - 11:00', status: 'BOOKED', clientName: 'Ana Kobakhidze', serviceName: 'Furniture Assembly' },
    { id: 'slot-8', date: '2026-10-13', time: '12:00 - 13:00', status: 'AVAILABLE', clientName: '', serviceName: '' },

    { id: 'slot-9', date: '2026-10-14', time: '09:00 - 10:00', status: 'AVAILABLE', clientName: '', serviceName: '' },
    { id: 'slot-10', date: '2026-10-14', time: '11:00 - 12:00', status: 'BOOKED', clientName: 'Irakli Kapanadze', serviceName: 'Pipe Fitting' }
  ];

  $: currentSlots = slots.filter(s => s.date === selectedDate);
  $: isDayBlocked = blockedDays.has(selectedDate);

  function setFeedback(msg, type = 'info') {
    feedbackMessage = msg;
    feedbackType = type;
  }

  function promptBlockDay() {
    if (isDayBlocked) {
      // Unblocking day is non-destructive
      blockedDays.delete(selectedDate);
      blockedDays = new Set(blockedDays);
      setFeedback(`Day ${selectedDate} has been unblocked.`, 'success');
    } else {
      // Destructive action: require explicit confirmation
      modalAction = {
        type: 'BLOCK_DAY',
        targetDate: selectedDate,
        title: 'Block Entire Day?',
        message: `Are you sure you want to block the entire day (${selectedDate})? Clients will not be able to book any slots on this day.`
      };
      showConfirmModal = true;
    }
  }

  function promptBlockSlot(slot) {
    if (slot.status === 'BLOCKED') {
      // Unblock slot
      slots = slots.map(s => s.id === slot.id ? { ...s, status: 'AVAILABLE' } : s);
      setFeedback(`Slot ${slot.time} on ${slot.date} is now Available.`, 'success');
    } else {
      // Destructive action: require explicit confirmation
      modalAction = {
        type: 'BLOCK_SLOT',
        targetSlot: slot,
        title: 'Block Time Slot?',
        message: `Are you sure you want to block slot ${slot.time} on ${slot.date}?`
      };
      showConfirmModal = true;
    }
  }

  function confirmAction() {
    if (!modalAction) return;

    if (modalAction.type === 'BLOCK_DAY') {
      blockedDays.add(modalAction.targetDate);
      blockedDays = new Set(blockedDays);
      setFeedback(`Day ${modalAction.targetDate} is now BLOCKED for bookings.`, 'warning');
    } else if (modalAction.type === 'BLOCK_SLOT') {
      const slot = modalAction.targetSlot;
      slots = slots.map(s => s.id === slot.id ? { ...s, status: 'BLOCKED' } : s);
      setFeedback(`Slot ${slot.time} on ${slot.date} is now BLOCKED.`, 'warning');
    }

    closeModal();
  }

  function closeModal() {
    showConfirmModal = false;
    modalAction = null;
  }

  function markNoShow(slotId) {
    slots = slots.map(s => {
      if (s.id === slotId) {
        return { ...s, status: 'NO_SHOW' };
      }
      return s;
    });
    setFeedback(`Booking marked as NO-SHOW for slot.`, 'danger');
  }

  function clearNoShow(slotId) {
    slots = slots.map(s => {
      if (s.id === slotId) {
        return { ...s, status: 'BOOKED' };
      }
      return s;
    });
    setFeedback(`Booking status restored to BOOKED.`, 'info');
  }

  function getStatusLabel(status) {
    switch (status) {
      case 'AVAILABLE': return 'Available (Свободно)';
      case 'BOOKED': return 'Booked (Забронировано)';
      case 'BLOCKED': return 'Blocked (Заблокировано)';
      case 'NO_SHOW': return 'No-Show (Неявка)';
      default: return status;
    }
  }

  function getStatusBadgeClass(status) {
    switch (status) {
      case 'AVAILABLE': return 'badge-available';
      case 'BOOKED': return 'badge-booked';
      case 'BLOCKED': return 'badge-blocked';
      case 'NO_SHOW': return 'badge-noshow';
      default: return '';
    }
  }
</script>

<section class="calendar-card" id="calendar-section" aria-labelledby="calendar-heading">
  <header class="calendar-header">
    <div class="header-titles">
      <h2 id="calendar-heading">Professional Calendar & Schedule Management</h2>
      <p class="subtitle">Manage working hours, block off dates/slots, and mark client no-shows</p>
    </div>
    <div class="view-controls" role="group" aria-label="View switchers">
      <button
        type="button"
        class="view-btn {calendarView === 'day' ? 'active' : ''}"
        aria-pressed={calendarView === 'day'}
        on:click={() => { calendarView = 'day'; setFeedback('Switched to Day View', 'info'); }}
      >
        Day View
      </button>

      <button
        type="button"
        class="view-btn {calendarView === 'week' ? 'active' : ''}"
        aria-pressed={calendarView === 'week'}
        on:click={() => { calendarView = 'week'; setFeedback('Switched to Week View', 'info'); }}
      >
        Week Overview
      </button>
    </div>
  </header>

  <!-- Live visual feedback state region -->
  {#if feedbackMessage}
    <div
      class="feedback-banner {feedbackType}"
      role="status"
      aria-live="polite"
      id="calendar-feedback-banner"
    >
      <span class="feedback-text">{feedbackMessage}</span>
      <button
        type="button"
        class="close-feedback-btn"
        aria-label="Dismiss message"
        on:click={() => feedbackMessage = ''}
      >
        ×
      </button>
    </div>
  {/if}

  <!-- Calendar Controls Bar -->
  <div class="controls-bar">
    <div class="date-picker-group">
      <label for="calendar-date-input" class="control-label">Select Date:</label>
      <input
        id="calendar-date-input"
        type="date"
        class="date-input"
        bind:value={selectedDate}
        on:change={() => setFeedback(`Selected date changed to ${selectedDate}`, 'info')}
      />
    </div>

    <div class="day-action-group">
      <button
        type="button"
        class="action-btn {isDayBlocked ? 'btn-unblock' : 'btn-block-danger'}"
        on:click={promptBlockDay}
        aria-describedby="day-block-status"
      >
        {isDayBlocked ? 'Unblock Entire Day' : '🚫 Block Entire Day'}
      </button>
      <span id="day-block-status" class="status-indicator-text">
        Status: <strong>{isDayBlocked ? 'BLOCKED' : 'ACTIVE / OPEN'}</strong>
      </span>
    </div>
  </div>

  <!-- Day View Content -->
  {#if calendarView === 'day'}
    <div class="schedule-container">
      <h3 class="schedule-date-title">
        Schedule for {selectedDate}
        {#if isDayBlocked}
          <span class="day-blocked-badge" id="day-blocked-badge">DAY BLOCKED</span>
        {/if}
      </h3>

      <div class="slots-list" role="list" aria-label={`Slots for ${selectedDate}`}>
        {#each currentSlots as slot (slot.id)}
          <div class="slot-item {slot.status.toLowerCase()}" role="listitem">
            <div class="slot-time-col">
              <span class="slot-time">{slot.time}</span>
              <span class={`status-badge ${getStatusBadgeClass(slot.status)}`}>
                {getStatusLabel(slot.status)}
              </span>
            </div>

            <div class="slot-details-col">
              {#if slot.status === 'BOOKED' || slot.status === 'NO_SHOW'}
                <div class="client-info">
                  <span class="client-name">👤 {slot.clientName}</span>
                  <span class="service-name">🛠️ {slot.serviceName}</span>
                </div>
              {:else if slot.status === 'BLOCKED'}
                <span class="slot-disabled-note">This slot is blocked from public booking.</span>
              {:else}
                <span class="slot-available-note">Open for client bookings.</span>
              {/if}
            </div>

            <div class="slot-actions-col">
              {#if slot.status === 'BOOKED'}
                <button
                  type="button"
                  class="btn-noshow"
                  on:click={() => markNoShow(slot.id)}
                  aria-label={`Mark no-show for ${slot.clientName}`}
                >
                  Mark No-Show
                </button>
              {:else if slot.status === 'NO_SHOW'}
                <button
                  type="button"
                  class="btn-restore"
                  on:click={() => clearNoShow(slot.id)}
                  aria-label={`Restore booking for ${slot.clientName}`}
                >
                  Undo No-Show
                </button>
              {/if}

              <button
                type="button"
                class="btn-toggle-block"
                on:click={() => promptBlockSlot(slot)}
                aria-label={`${slot.status === 'BLOCKED' ? 'Unblock' : 'Block'} time slot ${slot.time}`}
              >
                {slot.status === 'BLOCKED' ? 'Unblock Slot' : 'Block Slot'}
              </button>
            </div>
          </div>
        {/each}

        {#if currentSlots.length === 0}
          <div class="empty-schedule">
            <p>No predefined time slots for this date. Default working hours apply.</p>
          </div>
        {/if}
      </div>
    </div>
  {/if}

  <!-- Week View Content -->
  {#if calendarView === 'week'}
    <div class="week-grid-container">
      <h3 class="schedule-date-title">Week Overview</h3>
      <div class="week-grid">
        {#each ['2026-10-12', '2026-10-13', '2026-10-14', '2026-10-15', '2026-10-16', '2026-10-17', '2026-10-18'] as weekDate}
          <div
            class="week-day-card {blockedDays.has(weekDate) ? 'day-card-blocked' : ''} {selectedDate === weekDate ? 'day-card-selected' : ''}"
          >
            <div class="week-day-header">
              <span class="week-day-date">{weekDate}</span>
              {#if blockedDays.has(weekDate)}
                <span class="badge-blocked-sm">BLOCKED</span>
              {/if}
            </div>
            <div class="week-day-body">
              <span class="slot-count">
                {slots.filter(s => s.date === weekDate && s.status === 'BOOKED').length} Bookings
              </span>
              <button
                type="button"
                class="btn-select-day"
                on:click={() => { selectedDate = weekDate; calendarView = 'day'; setFeedback(`Navigated to ${weekDate}`, 'info'); }}
              >
                Manage Day
              </button>
            </div>
          </div>
        {/each}
      </div>
    </div>
  {/if}

  <!-- Confirmation Modal Dialog for Destructive Actions -->
  {#if showConfirmModal && modalAction}
    <div class="modal-backdrop" role="dialog" aria-modal="true" aria-labelledby="modal-title">
      <div class="modal-card">
        <h3 id="modal-title" class="modal-title">{modalAction.title}</h3>
        <p class="modal-message">{modalAction.message}</p>

        <div class="modal-actions">
          <button
            type="button"
            class="modal-btn btn-cancel"
            on:click={closeModal}
            id="cancel-confirm-btn"
          >
            Cancel
          </button>
          <button
            type="button"
            class="modal-btn btn-confirm-destructive"
            on:click={confirmAction}
            id="proceed-confirm-btn"
          >
            Confirm Block Action
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
    display: flex;
    flex-direction: column;
    gap: 12px;
    border-bottom: 2px solid #0969da;
    padding-bottom: 12px;
    margin-bottom: 20px;
  }

  @media (min-width: 600px) {
    .calendar-header {
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }
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

  .view-controls {
    display: flex;
    gap: 8px;
  }

  .view-btn {
    padding: 6px 14px;
    border: 1px solid #0969da;
    background: #ffffff;
    color: #0969da;
    border-radius: 6px;
    font-weight: 600;
    font-size: 0.85rem;
    cursor: pointer;
    transition: background-color 0.15s ease, color 0.15s ease;
  }

  .view-btn.active {
    background: #0969da;
    color: #ffffff;
  }

  .view-btn:focus-visible,
  .date-input:focus-visible,
  .action-btn:focus-visible,
  .btn-noshow:focus-visible,
  .btn-restore:focus-visible,
  .btn-toggle-block:focus-visible,
  .btn-select-day:focus-visible,
  .modal-btn:focus-visible,
  .close-feedback-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .feedback-banner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 16px;
    border-radius: 6px;
    margin-bottom: 16px;
    font-size: 0.9rem;
    font-weight: 600;
  }

  .feedback-banner.info {
    background-color: #ddf4ff;
    border: 1px solid #54a3ff;
    color: #0969da;
  }

  .feedback-banner.success {
    background-color: #dafbe1;
    border: 1px solid #1a7f37;
    color: #0e4a1f;
  }

  .feedback-banner.warning {
    background-color: #fff8c5;
    border: 1px solid #9a6700;
    color: #4d3800;
  }

  .feedback-banner.danger {
    background-color: #ffebe9;
    border: 1px solid #cf222e;
    color: #82071e;
  }

  .close-feedback-btn {
    background: transparent;
    border: none;
    font-size: 1.2rem;
    line-height: 1;
    cursor: pointer;
    color: inherit;
    padding: 0 4px;
  }

  .controls-bar {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    align-items: center;
    justify-content: space-between;
    background: #f6f8fa;
    border: 1px solid #d0d7de;
    padding: 12px 16px;
    border-radius: 6px;
    margin-bottom: 20px;
  }

  .date-picker-group, .day-action-group {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .control-label {
    font-weight: 600;
    font-size: 0.9rem;
  }

  .date-input {
    padding: 6px 10px;
    border-radius: 6px;
    border: 1px solid #d0d7de;
    font-size: 0.9rem;
  }

  .action-btn {
    padding: 8px 14px;
    border-radius: 6px;
    font-size: 0.85rem;
    font-weight: 600;
    cursor: pointer;
    border: 1px solid transparent;
  }

  .btn-block-danger {
    background-color: #cf222e;
    color: #ffffff;
  }

  .btn-block-danger:hover {
    background-color: #a40e26;
  }

  .btn-unblock {
    background-color: #1f883d;
    color: #ffffff;
  }

  .btn-unblock:hover {
    background-color: #1a7f37;
  }

  .status-indicator-text {
    font-size: 0.85rem;
    color: #424a53;
  }

  .schedule-date-title {
    font-size: 1.15rem;
    margin: 0 0 12px 0;
    color: #1f2328;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .day-blocked-badge {
    background-color: #ffebe9;
    color: #cf222e;
    border: 1px solid #cf222e;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 0.75rem;
    font-weight: 700;
  }

  .slots-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .slot-item {
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 12px 16px;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    background: #ffffff;
    transition: background-color 0.15s ease;
  }

  @media (min-width: 640px) {
    .slot-item {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
    }
  }

  .slot-item.blocked {
    background-color: #f6f8fa;
    border-color: #d0d7de;
  }

  .slot-item.no_show {
    background-color: #fff8c5;
    border-color: #d4a72c;
  }

  .slot-time-col {
    display: flex;
    flex-direction: column;
    gap: 4px;
    min-width: 140px;
  }

  .slot-time {
    font-weight: 700;
    font-size: 0.95rem;
  }

  .status-badge {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 0.75rem;
    font-weight: 600;
    width: fit-content;
  }

  .badge-available {
    background-color: #dafbe1;
    color: #0e4a1f;
    border: 1px solid #1a7f37;
  }

  .badge-booked {
    background-color: #ddf4ff;
    color: #0969da;
    border: 1px solid #54a3ff;
  }

  .badge-blocked {
    background-color: #f3f4f6;
    color: #57606a;
    border: 1px solid #8c959f;
  }

  .badge-noshow {
    background-color: #ffebe9;
    color: #82071e;
    border: 1px solid #cf222e;
  }

  .slot-details-col {
    flex: 1;
  }

  .client-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .client-name {
    font-weight: 600;
    font-size: 0.9rem;
  }

  .service-name {
    font-size: 0.85rem;
    color: #424a53;
  }

  .slot-disabled-note, .slot-available-note {
    font-size: 0.85rem;
    color: #57606a;
    font-style: italic;
  }

  .slot-actions-col {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }

  .btn-noshow, .btn-restore, .btn-toggle-block {
    padding: 6px 12px;
    border-radius: 6px;
    font-size: 0.8rem;
    font-weight: 600;
    cursor: pointer;
    border: 1px solid #d0d7de;
    background: #ffffff;
  }

  .btn-noshow {
    color: #cf222e;
    border-color: #cf222e;
  }

  .btn-noshow:hover {
    background-color: #ffebe9;
  }

  .btn-restore {
    color: #0969da;
    border-color: #0969da;
  }

  .btn-restore:hover {
    background-color: #ddf4ff;
  }

  .btn-toggle-block:hover {
    background-color: #f3f4f6;
  }

  .week-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
    gap: 10px;
  }

  .week-day-card {
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 10px;
    background: #ffffff;
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .week-day-card.day-card-blocked {
    background: #f6f8fa;
    border-color: #cf222e;
  }

  .week-day-card.day-card-selected {
    border-color: #0969da;
    box-shadow: 0 0 0 2px rgba(9, 105, 218, 0.2);
  }

  .week-day-header {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .week-day-date {
    font-size: 0.8rem;
    font-weight: 700;
  }

  .badge-blocked-sm {
    font-size: 0.65rem;
    background: #ffebe9;
    color: #cf222e;
    padding: 1px 4px;
    border-radius: 4px;
    width: fit-content;
  }

  .week-day-body {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .slot-count {
    font-size: 0.75rem;
    color: #424a53;
  }

  .btn-select-day {
    padding: 4px 8px;
    font-size: 0.75rem;
    border: 1px solid #0969da;
    color: #0969da;
    background: #ffffff;
    border-radius: 4px;
    cursor: pointer;
  }

  .btn-select-day:hover {
    background: #ddf4ff;
  }

  .modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }

  .modal-card {
    background: #ffffff;
    border-radius: 8px;
    padding: 24px;
    max-width: 440px;
    width: 90%;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
    border: 1px solid #d0d7de;
  }

  .modal-title {
    margin-top: 0;
    margin-bottom: 8px;
    color: #cf222e;
    font-size: 1.2rem;
  }

  .modal-message {
    font-size: 0.95rem;
    color: #1f2328;
    margin-bottom: 20px;
    line-height: 1.4;
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }

  .modal-btn {
    padding: 8px 16px;
    border-radius: 6px;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
  }

  .btn-cancel {
    background: #ffffff;
    border: 1px solid #d0d7de;
    color: #1f2328;
  }

  .btn-cancel:hover {
    background: #f3f4f6;
  }

  .btn-confirm-destructive {
    background: #cf222e;
    border: 1px solid #cf222e;
    color: #ffffff;
  }

  .btn-confirm-destructive:hover {
    background: #a40e26;
  }

  .empty-schedule {
    text-align: center;
    padding: 24px;
    color: #57606a;
    font-size: 0.9rem;
    background: #f6f8fa;
    border-radius: 6px;
    border: 1px solid #d0d7de;
  }
</style>
