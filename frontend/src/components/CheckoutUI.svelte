<script>
  export let booking = {
    id: 'BK-9842',
    masterName: 'Giorgi Beridze',
    serviceName: 'Plumbing Repair (Сантехника)',
    amountGEL: 85,
    dateTime: '2026-10-10 14:00'
  };

  let selectedPaymentMethod = 'tbc'; // 'tbc', 'bog', 'cash'
  let transferConfirmed = false;
  let referenceNote = booking.id;

  function handleConfirmPayment(event) {
    event.preventDefault();
    transferConfirmed = true;
  }
</script>

<section class="checkout-card" id="checkout-section" aria-labelledby="checkout-heading">
  <header class="checkout-header">
    <h2 id="checkout-heading">Payment & Checkout</h2>
    <p class="subtitle">Complete your booking with off-platform direct payment</p>
  </header>

  <div class="booking-summary">
    <div class="summary-row">
      <span class="label">Master:</span>
      <span class="value">{booking.masterName}</span>
    </div>
    <div class="summary-row">
      <span class="label">Service:</span>
      <span class="value">{booking.serviceName}</span>
    </div>
    <div class="summary-row">
      <span class="label">Date & Time:</span>
      <span class="value">{booking.dateTime}</span>
    </div>
    <div class="summary-row highlight">
      <span class="label">Total Amount:</span>
      <span class="value price">{booking.amountGEL} ₾ (GEL)</span>
    </div>
  </div>

  <fieldset class="payment-methods">
    <legend class="section-title">Select Payment Method</legend>

    <label class="method-option" for="pay-tbc">
      <input
        id="pay-tbc"
        type="radio"
        name="paymentMethod"
        value="tbc"
        bind:group={selectedPaymentMethod}
        aria-describedby="tbc-desc"
      />
      <div class="option-content">
        <span class="method-title">TBC Bank Transfer</span>
        <span id="tbc-desc" class="method-desc">Direct bank transfer via TBC Pay / Mobile Bank</span>
      </div>
    </label>

    <label class="method-option" for="pay-bog">
      <input
        id="pay-bog"
        type="radio"
        name="paymentMethod"
        value="bog"
        bind:group={selectedPaymentMethod}
        aria-describedby="bog-desc"
      />
      <div class="option-content">
        <span class="method-title">Bank of Georgia (BOG)</span>
        <span id="bog-desc" class="method-desc">Direct transfer via mBank / BOG app</span>
      </div>
    </label>

    <label class="method-option" for="pay-cash">
      <input
        id="pay-cash"
        type="radio"
        name="paymentMethod"
        value="cash"
        bind:group={selectedPaymentMethod}
        aria-describedby="cash-desc"
      />
      <div class="option-content">
        <span class="method-title">Cash on Spot</span>
        <span id="cash-desc" class="method-desc">Pay cash directly to the master after service completion</span>
      </div>
    </label>
  </fieldset>

  <div class="instructions-box" aria-live="polite">
    <h3 class="instructions-title">Off-Platform Payment Instructions</h3>

    {#if selectedPaymentMethod === 'tbc'}
      <div class="bank-details">
        <p><strong>Bank:</strong> TBC Bank Georgia</p>
        <p><strong>Recipient:</strong> Giorgi Beridze</p>
        <p><strong>IBAN:</strong> <code class="iban">GE29TB7000000012345678</code></p>
        <p><strong>Payment Reference / Destination:</strong> <mark>{referenceNote}</mark></p>
        <p class="instruction-note">Please include booking reference <strong>{referenceNote}</strong> in the bank transfer note so the master can verify your payment.</p>
      </div>
    {:else if selectedPaymentMethod === 'bog'}
      <div class="bank-details">
        <p><strong>Bank:</strong> Bank of Georgia (BOG)</p>
        <p><strong>Recipient:</strong> Giorgi Beridze</p>
        <p><strong>IBAN:</strong> <code class="iban">GE02BG0000000987654321</code></p>
        <p><strong>Payment Reference / Destination:</strong> <mark>{referenceNote}</mark></p>
        <p class="instruction-note">Please state <strong>{referenceNote}</strong> in the transfer description.</p>
      </div>
    {:else if selectedPaymentMethod === 'cash'}
      <div class="bank-details">
        <p><strong>Payment Mode:</strong> Cash on Location</p>
        <p class="instruction-note">Hand <strong>{booking.amountGEL} GEL</strong> to {booking.masterName} upon service delivery in Tbilisi/Batumi/Kutaisi.</p>
      </div>
    {/if}
  </div>

  <form on:submit={handleConfirmPayment} class="confirmation-form">
    {#if !transferConfirmed}
      <p class="help-text">Once transferred or agreed, mark your payment request for verification.</p>
      <button type="submit" class="primary-btn">
        I Have Sent / Agreed to Payment
      </button>
    {:else}
      <div class="status-banner success" role="alert">
        <strong>✓ Payment Instructions Acknowledged!</strong>
        <p>Your payment status is set to pending verification. Master {booking.masterName} will update the ledger upon receipt.</p>
      </div>
    {/if}
  </form>
</section>

<style>
  .checkout-card {
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

  .checkout-header {
    border-bottom: 2px solid #0969da;
    padding-bottom: 12px;
    margin-bottom: 20px;
  }

  .checkout-header h2 {
    margin: 0 0 4px 0;
    font-size: 1.5rem;
    color: #0969da;
  }

  .subtitle {
    margin: 0;
    font-size: 0.95rem;
    color: #424a53;
  }

  .booking-summary {
    background: #f6f8fa;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 16px;
    margin-bottom: 20px;
  }

  .summary-row {
    display: flex;
    justify-content: space-between;
    padding: 6px 0;
    font-size: 0.95rem;
  }

  .summary-row.highlight {
    border-top: 1px solid #d0d7de;
    margin-top: 8px;
    padding-top: 12px;
    font-weight: 600;
  }

  .price {
    color: #0969da;
    font-size: 1.1rem;
  }

  .payment-methods {
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 16px;
    margin: 0 0 20px 0;
  }

  .section-title {
    font-weight: 600;
    font-size: 1rem;
    padding: 0 6px;
    color: #1f2328;
  }

  .method-option {
    display: flex;
    align-items: flex-start;
    padding: 10px;
    margin-top: 8px;
    border: 1px solid #e1e4e8;
    border-radius: 6px;
    cursor: pointer;
    background: #ffffff;
    transition: background-color 0.15s ease, border-color 0.15s ease;
  }

  .method-option:hover {
    background-color: #f3f4f6;
  }

  .method-option input[type="radio"] {
    margin-top: 4px;
    margin-right: 12px;
    width: 18px;
    height: 18px;
    accent-color: #0969da;
  }

  .method-option input[type="radio"]:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .option-content {
    display: flex;
    flex-direction: column;
  }

  .method-title {
    font-weight: 600;
    font-size: 0.95rem;
    color: #1f2328;
  }

  .method-desc {
    font-size: 0.85rem;
    color: #424a53;
  }

  .instructions-box {
    background-color: #f0f7ff;
    border: 1px solid #54a3ff;
    border-radius: 6px;
    padding: 16px;
    margin-bottom: 20px;
  }

  .instructions-title {
    margin-top: 0;
    margin-bottom: 12px;
    font-size: 1.05rem;
    color: #0969da;
  }

  .bank-details p {
    margin: 6px 0;
    font-size: 0.95rem;
    color: #1f2328;
  }

  .iban {
    background: #e1e4e8;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: monospace;
    font-size: 1rem;
    color: #1f2328;
  }

  mark {
    background-color: #fff8c5;
    padding: 2px 6px;
    border-radius: 4px;
    font-weight: 600;
  }

  .instruction-note {
    margin-top: 10px !important;
    font-size: 0.9rem !important;
    color: #333333;
  }

  .primary-btn {
    width: 100%;
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

  .primary-btn:hover {
    background-color: #0353e9;
  }

  .primary-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 3px;
  }

  .status-banner {
    padding: 14px;
    border-radius: 6px;
    font-size: 0.95rem;
  }

  .status-banner.success {
    background-color: #dafbe1;
    border: 1px solid #1a7f37;
    color: #0e4a1f;
  }

  .status-banner p {
    margin: 4px 0 0 0;
  }

  .help-text {
    font-size: 0.85rem;
    color: #424a53;
    margin-bottom: 8px;
  }
</style>
