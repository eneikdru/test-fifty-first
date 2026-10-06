<script>
  export let bookings = [
    {
      id: 'BK-9842',
      clientName: 'Nino Kapanadze',
      phone: '+995 599 123 456',
      serviceName: 'Plumbing Repair (Сантехника)',
      dateTime: '2026-10-10 14:00',
      amountGEL: 85,
      method: 'TBC Bank Transfer',
      status: 'PENDING_VERIFICATION' // 'UNPAID', 'PENDING_VERIFICATION', 'PAID'
    },
    {
      id: 'BK-9843',
      clientName: 'Luka Maisuradze',
      phone: '+995 598 987 654',
      serviceName: 'Electrical Fixing (Электрика)',
      dateTime: '2026-10-10 16:30',
      amountGEL: 120,
      method: 'Bank of Georgia',
      status: 'UNPAID'
    },
    {
      id: 'BK-9840',
      clientName: 'Tamar Tsiklauri',
      phone: '+995 555 112 233',
      serviceName: 'AC Cleaning (Чистка кондиционера)',
      dateTime: '2026-10-09 11:00',
      amountGEL: 70,
      method: 'Cash',
      status: 'PAID'
    }
  ];

  let filterStatus = 'ALL';

  function toggleStatus(bookingId, newStatus) {
    bookings = bookings.map(b => {
      if (b.id === bookingId) {
        return { ...b, status: newStatus };
      }
      return b;
    });
  }

  $: filteredBookings = filterStatus === 'ALL'
    ? bookings
    : bookings.filter(b => b.status === filterStatus);

  function getStatusLabel(status) {
    switch (status) {
      case 'PAID': return 'Paid (Оплачено)';
      case 'PENDING_VERIFICATION': return 'Pending Verification (Проверка)';
      case 'UNPAID': return 'Unpaid (Не оплачено)';
      default: return status;
    }
  }

  function getStatusBadgeClass(status) {
    switch (status) {
      case 'PAID': return 'badge-paid';
      case 'PENDING_VERIFICATION': return 'badge-pending';
      case 'UNPAID': return 'badge-unpaid';
      default: return '';
    }
  }
</script>

<section class="ledger-card" id="ledger-section" aria-labelledby="ledger-heading">
  <header class="ledger-header">
    <h2 id="ledger-heading">Professional Ledger & Booking Status</h2>
    <p class="subtitle">Manage customer payments, off-platform transfers, and order settlement</p>
  </header>

  <div class="filter-bar">
    <label for="status-filter-select" class="filter-label">Filter by Status:</label>
    <select
      id="status-filter-select"
      bind:value={filterStatus}
      class="filter-select"
      aria-label="Filter bookings by payment status"
    >
      <option value="ALL">All Bookings ({bookings.length})</option>
      <option value="UNPAID">Unpaid</option>
      <option value="PENDING_VERIFICATION">Pending Verification</option>
      <option value="PAID">Paid</option>
    </select>
  </div>

  <div class="table-container">
    <table class="ledger-table" aria-label="Bookings payment ledger">
      <thead>
        <tr>
          <th scope="col">Booking ID</th>
          <th scope="col">Client & Service</th>
          <th scope="col">Date / Time</th>
          <th scope="col">Amount</th>
          <th scope="col">Current Status</th>
          <th scope="col">Action / Toggle Status</th>
        </tr>
      </thead>
      <tbody>
        {#each filteredBookings as item (item.id)}
          <tr>
            <td class="cell-id"><strong>{item.id}</strong></td>
            <td>
              <div class="client-info">
                <span class="client-name">{item.clientName}</span>
                <span class="client-phone">{item.phone}</span>
                <span class="service-tag">{item.serviceName}</span>
              </div>
            </td>
            <td class="cell-datetime">{item.dateTime}</td>
            <td class="cell-amount"><strong>{item.amountGEL} ₾</strong> <span class="method-tag">({item.method})</span></td>
            <td>
              <span class={`status-badge ${getStatusBadgeClass(item.status)}`}>
                {getStatusLabel(item.status)}
              </span>
            </td>
            <td class="cell-action">
              <label for={`select-status-${item.id}`} class="visually-hidden">
                Update payment status for {item.id}
              </label>
              <select
                id={`select-status-${item.id}`}
                value={item.status}
                on:change={(e) => toggleStatus(item.id, e.target.value)}
                class="status-toggle-select"
                aria-label={`Update payment status for booking ${item.id}`}
              >
                <option value="UNPAID">Set Unpaid</option>
                <option value="PENDING_VERIFICATION">Set Pending</option>
                <option value="PAID">Set Paid</option>
              </select>
            </td>
          </tr>
        {/each}
        {#if filteredBookings.length === 0}
          <tr>
            <td colspan="6" class="empty-state">No bookings match the selected status.</td>
          </tr>
        {/if}
      </tbody>
    </table>
  </div>
</section>

<style>
  .ledger-card {
    background: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 8px;
    padding: 24px;
    max-width: 900px;
    margin: 0 auto;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    color: #1f2328;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .ledger-header {
    border-bottom: 2px solid #0969da;
    padding-bottom: 12px;
    margin-bottom: 20px;
  }

  .ledger-header h2 {
    margin: 0 0 4px 0;
    font-size: 1.5rem;
    color: #0969da;
  }

  .subtitle {
    margin: 0;
    font-size: 0.95rem;
    color: #424a53;
  }

  .filter-bar {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    background: #f6f8fa;
    padding: 12px;
    border-radius: 6px;
    border: 1px solid #d0d7de;
  }

  .filter-label {
    font-weight: 600;
    font-size: 0.9rem;
    color: #1f2328;
  }

  .filter-select {
    padding: 6px 12px;
    border-radius: 6px;
    border: 1px solid #d0d7de;
    background-color: #ffffff;
    font-size: 0.9rem;
    color: #1f2328;
  }

  .filter-select:focus-visible, .status-toggle-select:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .table-container {
    overflow-x: auto;
  }

  .ledger-table {
    width: 100%;
    border-collapse: collapse;
    text-align: left;
    font-size: 0.9rem;
  }

  .ledger-table th {
    background-color: #f6f8fa;
    color: #1f2328;
    font-weight: 600;
    padding: 12px 10px;
    border-bottom: 2px solid #d0d7de;
  }

  .ledger-table td {
    padding: 12px 10px;
    border-bottom: 1px solid #e1e4e8;
    vertical-align: middle;
  }

  .client-info {
    display: flex;
    flex-direction: column;
  }

  .client-name {
    font-weight: 600;
    color: #1f2328;
  }

  .client-phone {
    font-size: 0.8rem;
    color: #424a53;
  }

  .service-tag {
    font-size: 0.8rem;
    color: #0969da;
  }

  .method-tag {
    font-size: 0.8rem;
    font-weight: normal;
    color: #424a53;
  }

  .status-badge {
    display: inline-block;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 0.8rem;
    font-weight: 600;
    text-align: center;
  }

  .badge-paid {
    background-color: #dafbe1;
    color: #0e4a1f;
    border: 1px solid #1a7f37;
  }

  .badge-pending {
    background-color: #fff8c5;
    color: #4d3800;
    border: 1px solid #9a6700;
  }

  .badge-unpaid {
    background-color: #ffebe9;
    color: #82071e;
    border: 1px solid #cf222e;
  }

  .status-toggle-select {
    padding: 6px 10px;
    border-radius: 6px;
    border: 1px solid #d0d7de;
    background-color: #ffffff;
    font-size: 0.85rem;
    color: #1f2328;
    cursor: pointer;
  }

  .status-toggle-select:hover {
    background-color: #f3f4f6;
  }

  .empty-state {
    text-align: center;
    color: #424a53;
    padding: 24px;
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
