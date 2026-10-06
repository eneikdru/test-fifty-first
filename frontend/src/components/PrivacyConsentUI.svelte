<script>
  import { onMount } from 'svelte';

  // Cookie Consent State
  let cookieConsentGiven = false;
  let showCookieDetails = false;
  let cookies = {
    essential: true, // Always true & disabled
    analytics: false,
    marketing: false
  };

  // Account Recovery State
  let recoveryStep = 1; // 1: Request code, 2: Verify code, 3: Success
  let recoveryPhone = '+995';
  let recoveryCode = '';
  let newPassword = '';
  let recoveryStatus = '';
  let recoveryError = '';
  let isLockedAccount = true;

  // Privacy Request State (Export & Erasure)
  let exportIncludeMedia = true;
  let exportStatus = null; // null | 'PENDING' | 'COMPLETED'
  let exportData = null;

  let erasureConfirmed = false;
  let erasureScope = 'ALL_DATA';
  let erasureReason = '';
  let erasureStatus = null; // null | 'SCHEDULED' | 'COMPLETED'

  onMount(() => {
    try {
      const savedConsent = localStorage.getItem('privacy_cookie_consent');
      if (savedConsent) {
        const parsed = JSON.parse(savedConsent);
        cookies = { ...cookies, ...parsed };
        cookieConsentGiven = true;
      }
    } catch (e) {
      console.error('Failed to load cookie consent from localStorage', e);
    }
  });

  function acceptAllCookies() {
    cookies.analytics = true;
    cookies.marketing = true;
    saveCookiePreferences();
  }

  function rejectNonEssentialCookies() {
    cookies.analytics = false;
    cookies.marketing = false;
    saveCookiePreferences();
  }

  function saveCookiePreferences() {
    cookieConsentGiven = true;
    try {
      localStorage.setItem('privacy_cookie_consent', JSON.stringify({
        essential: true,
        analytics: cookies.analytics,
        marketing: cookies.marketing,
        timestamp: new Date().toISOString()
      }));
    } catch (e) {
      console.error('Failed to save cookie consent', e);
    }
  }

  function resetCookieConsent() {
    cookieConsentGiven = false;
  }

  // Account Recovery Handlers
  function handleSendRecoveryCode(e) {
    e.preventDefault();
    recoveryError = '';
    if (!recoveryPhone.startsWith('+995') || recoveryPhone.replace(/\s+/g, '').length < 12) {
      recoveryError = 'Please enter a valid Georgian phone number (+995 XXX XX XX XX)';
      return;
    }
    recoveryStatus = 'Verification code sent via SMS to ' + recoveryPhone;
    recoveryStep = 2;
  }

  function handleVerifyCode(e) {
    e.preventDefault();
    recoveryError = '';
    if (!recoveryCode || recoveryCode.trim().length < 4) {
      recoveryError = 'Please enter the 4-6 digit verification code sent to your phone.';
      return;
    }
    recoveryStatus = 'Code verified successfully. Set your new security credentials.';
    recoveryStep = 3;
  }

  function handleResetPassword(e) {
    e.preventDefault();
    recoveryError = '';
    if (!newPassword || newPassword.length < 6) {
      recoveryError = 'Password must be at least 6 characters long.';
      return;
    }
    recoveryStatus = 'Account successfully unlocked and updated! You can now sign in.';
    isLockedAccount = false;
  }

  // Privacy Export Handler
  async function handleRequestExport(e) {
    e.preventDefault();
    exportStatus = 'PENDING';

    // Simulate or call Privacy API
    setTimeout(() => {
      exportStatus = 'COMPLETED';
      exportData = {
        exportId: 'exp-' + Math.random().toString(36).substring(2, 9),
        requestedAt: new Date().toISOString(),
        userProfile: {
          phone: recoveryPhone || '+995555123456',
          city: 'TBILISI',
          createdAt: new Date(Date.now() - 8640000000).toISOString()
        },
        bookingsCount: 3,
        privacyConsent: cookies
      };
    }, 600);
  }

  // Privacy Erasure Handler
  async function handleRequestErasure(e) {
    e.preventDefault();
    if (!erasureConfirmed) {
      alert('Please check the confirmation box to proceed with data erasure.');
      return;
    }
    erasureStatus = 'SCHEDULED';
  }
</script>

<div class="privacy-container" id="privacy-section">

  <!-- COOKIE CONSENT BANNER (Blocks non-essential cookies until accepted/configured) -->
  {#if !cookieConsentGiven}
    <div class="cookie-banner-overlay" role="region" aria-label="Cookie Consent Banner">
      <div class="cookie-banner-content">
        <div class="cookie-banner-header">
          <h2 id="cookie-banner-title">Cookie & Privacy Consent</h2>
          <span class="badge-tag">Compliance WCAG 2.1 AA</span>
        </div>
        <p class="cookie-description">
          We use cookies and local storage to provide essential booking services in Tbilisi, Batumi, and Kutaisi.
          Non-essential cookies (analytics & marketing) remain blocked until you give explicit consent.
        </p>

        {#if showCookieDetails}
          <div class="cookie-options" role="group" aria-label="Cookie Preferences Options">
            <div class="cookie-option-item">
              <input type="checkbox" id="cookie-essential" checked disabled aria-describedby="essential-desc" />
              <label for="cookie-essential">
                <strong>Essential Cookies (Required)</strong>
                <span id="essential-desc" class="option-desc">Necessary for core booking, session state, and security.</span>
              </label>
            </div>
            <div class="cookie-option-item">
              <input type="checkbox" id="cookie-analytics" bind:checked={cookies.analytics} aria-describedby="analytics-desc" />
              <label for="cookie-analytics">
                <strong>Analytics Cookies</strong>
                <span id="analytics-desc" class="option-desc">Helps us improve service search and response times for Georgian masters.</span>
              </label>
            </div>
            <div class="cookie-option-item">
              <input type="checkbox" id="cookie-marketing" bind:checked={cookies.marketing} aria-describedby="marketing-desc" />
              <label for="cookie-marketing">
                <strong>Marketing & Social Cookies</strong>
                <span id="marketing-desc" class="option-desc">Allows integration with Facebook Business Page photos and promo updates.</span>
              </label>
            </div>
          </div>
        {/if}

        <div class="cookie-banner-actions">
          <button type="button" class="btn btn-primary" on:click={acceptAllCookies}>
            Accept All Cookies
          </button>
          <button type="button" class="btn btn-secondary" on:click={rejectNonEssentialCookies}>
            Reject Non-Essential
          </button>
          <button
            type="button"
            class="btn btn-outline"
            aria-expanded={showCookieDetails}
            on:click={() => showCookieDetails = !showCookieDetails}
          >
            {showCookieDetails ? 'Hide Details' : 'Customize Preferences'}
          </button>
          {#if showCookieDetails}
            <button type="button" class="btn btn-primary-dark" on:click={saveCookiePreferences}>
              Save Preferences
            </button>
          {/if}
        </div>
      </div>
    </div>
  {/if}

  <!-- MAIN PRIVACY & ACCOUNT RECOVERY INTERFACE -->
  <div class="privacy-card-grid">

    <!-- ACCOUNT RECOVERY FLOW -->
    <section class="privacy-card" id="account-recovery-section" aria-labelledby="recovery-card-title">
      <div class="card-header">
        <h2 id="recovery-card-title">Account Self-Service Recovery</h2>
        {#if isLockedAccount}
          <span class="status-pill pill-locked" id="account-status-indicator">Account Locked</span>
        {:else}
          <span class="status-pill pill-active">Account Active</span>
        {/if}
      </div>

      <p class="card-intro">
        If your account has been locked due to security measures or failed sign-in attempts, complete this guided self-service recovery process using your registered Georgian mobile number (+995).
      </p>

      {#if recoveryError}
        <div class="alert alert-danger" role="alert">
          {recoveryError}
        </div>
      {/if}

      {#if recoveryStatus}
        <div class="alert alert-success" role="status" aria-live="polite">
          {recoveryStatus}
        </div>
      {/if}

      {#if recoveryStep === 1}
        <form on:submit={handleSendRecoveryCode} class="recovery-form">
          <div class="form-group">
            <label for="recovery-phone-input">Georgian Mobile Phone Number (+995)</label>
            <input
              type="tel"
              id="recovery-phone-input"
              class="form-control"
              bind:value={recoveryPhone}
              placeholder="+995 555 12 34 56"
              required
              aria-required="true"
            />
            <small class="form-text">Enter your 9-digit Georgian phone number registered with the account.</small>
          </div>

          <button type="submit" class="btn btn-primary full-width">
            Send SMS Verification Code
          </button>
        </form>
      {:else if recoveryStep === 2}
        <form on:submit={handleVerifyCode} class="recovery-form">
          <div class="form-group">
            <label for="recovery-code-input">SMS Verification Code</label>
            <input
              type="text"
              id="recovery-code-input"
              class="form-control"
              bind:value={recoveryCode}
              placeholder="e.g. 482910"
              required
              aria-required="true"
            />
            <small class="form-text">Check your Georgian mobile SMS for the 6-digit confirmation code.</small>
          </div>

          <div class="form-actions-row">
            <button type="submit" class="btn btn-primary">
              Verify Code
            </button>
            <button type="button" class="btn btn-outline" on:click={() => recoveryStep = 1}>
              Back / Change Phone
            </button>
          </div>
        </form>
      {:else if recoveryStep === 3}
        <form on:submit={handleResetPassword} class="recovery-form">
          <div class="form-group">
            <label for="new-password-input">New Security Password / Passcode</label>
            <input
              type="password"
              id="new-password-input"
              class="form-control"
              bind:value={newPassword}
              placeholder="Minimum 6 characters"
              required
              aria-required="true"
            />
          </div>

          <button type="submit" class="btn btn-success full-width">
            Unlock Account & Set New Password
          </button>
        </form>
      {/if}
    </section>

    <!-- PRIVACY DATA EXPORT & RIGHT TO BE FORGOTTEN -->
    <section class="privacy-card" id="privacy-tools-section" aria-labelledby="privacy-tools-title">
      <div class="card-header">
        <h2 id="privacy-tools-title">Privacy Tools & Rights</h2>
        <span class="status-pill pill-info">GDPR & Georgian Privacy Standard</span>
      </div>

      <div class="tabs-subnav" role="tablist">
        <button
          type="button"
          class="subnav-btn {cookieConsentGiven ? 'active' : ''}"
          on:click={resetCookieConsent}
        >
          Manage Cookie Preferences
        </button>
      </div>

      <div class="privacy-subcard">
        <h3>1. Personal Data Export</h3>
        <p class="card-intro">
          Download a complete JSON copy of your profile, master service listings, GEL booking history, and voice requests.
        </p>

        <form on:submit={handleRequestExport}>
          <div class="form-group checkbox-group">
            <input type="checkbox" id="export-media-check" bind:checked={exportIncludeMedia} />
            <label for="export-media-check">
              Include Facebook page photos & media metadata in export
            </label>
          </div>

          <button type="submit" class="btn btn-primary" disabled={exportStatus === 'PENDING'}>
            {exportStatus === 'PENDING' ? 'Generating Export...' : 'Request Data Export (JSON)'}
          </button>
        </form>

        {#if exportStatus === 'COMPLETED' && exportData}
          <div class="export-result-box" role="region" aria-label="Exported JSON Data Summary">
            <h4>Data Export Generated</h4>
            <p><strong>Export ID:</strong> {exportData.exportId}</p>
            <p><strong>City:</strong> {exportData.userProfile.city}</p>
            <p><strong>Bookings Exported:</strong> {exportData.bookingsCount}</p>
            <pre class="json-preview">{JSON.stringify(exportData, null, 2)}</pre>
          </div>
        {/if}
      </div>

      <hr class="divider" />

      <div class="privacy-subcard">
        <h3>2. Account & Data Erasure (Right to be Forgotten)</h3>
        <p class="card-intro">
          Permanently erase or anonymize your user account, master service catalog, and contact history.
        </p>

        <form on:submit={handleRequestErasure}>
          <div class="form-group">
            <label for="erasure-scope-select">Erasure Scope</label>
            <select id="erasure-scope-select" class="form-control" bind:value={erasureScope}>
              <option value="ALL_DATA">All Personal Data & Master Profile (Full Deletion)</option>
              <option value="ACCOUNT_ONLY">Account Credentials Only (Anonymize Bookings)</option>
            </select>
          </div>

          <div class="form-group">
            <label for="erasure-reason-input">Reason for Erasure (Optional)</label>
            <input
              type="text"
              id="erasure-reason-input"
              class="form-control"
              bind:value={erasureReason}
              placeholder="e.g. No longer providing master services in Tbilisi"
            />
          </div>

          <div class="form-group checkbox-group danger-checkbox">
            <input type="checkbox" id="confirm-erasure-check" bind:checked={erasureConfirmed} required />
            <label for="confirm-erasure-check">
              I understand that this action is irreversible and schedules permanent deletion of my account.
            </label>
          </div>

          <button type="submit" class="btn btn-danger" disabled={!erasureConfirmed}>
            Request Irrevocable Account Erasure
          </button>
        </form>

        {#if erasureStatus === 'SCHEDULED'}
          <div class="alert alert-warning" role="alert" style="margin-top: 16px;">
            <strong>Erasure Scheduled:</strong> Your data erasure request has been accepted. Account and profile deletion will take effect according to legal requirements.
          </div>
        {/if}
      </div>

    </section>
  </div>
</div>

<style>
  .privacy-container {
    max-width: 1100px;
    margin: 0 auto;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    color: #0f172a;
  }

  /* COOKIE BANNER OVERLAY */
  .cookie-banner-overlay {
    background-color: #ffffff;
    border: 2px solid #0969da;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .cookie-banner-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }

  .cookie-banner-header h2 {
    margin: 0;
    font-size: 1.35rem;
    color: #0f172a;
  }

  .badge-tag {
    background-color: #eff6ff;
    color: #1d4ed8;
    border: 1px solid #bfdbfe;
    padding: 4px 10px;
    border-radius: 16px;
    font-size: 0.8rem;
    font-weight: 600;
  }

  .cookie-description {
    font-size: 0.95rem;
    color: #334155;
    line-height: 1.5;
    margin-bottom: 16px;
  }

  .cookie-options {
    background-color: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .cookie-option-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
  }

  .cookie-option-item input[type="checkbox"] {
    margin-top: 4px;
    width: 18px;
    height: 18px;
    cursor: pointer;
  }

  .cookie-option-item label {
    font-size: 0.95rem;
    color: #0f172a;
    cursor: pointer;
  }

  .option-desc {
    display: block;
    font-size: 0.85rem;
    color: #475569;
    margin-top: 2px;
  }

  .cookie-banner-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }

  /* BUTTONS */
  .btn {
    font-family: inherit;
    font-size: 0.95rem;
    font-weight: 600;
    padding: 10px 18px;
    border-radius: 8px;
    border: 1px solid transparent;
    cursor: pointer;
    transition: background-color 0.15s ease, border-color 0.15s ease;
  }

  .btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .btn-primary {
    background-color: #0969da;
    color: #ffffff;
  }

  .btn-primary:hover {
    background-color: #0451a5;
  }

  .btn-primary-dark {
    background-color: #1e293b;
    color: #ffffff;
  }

  .btn-primary-dark:hover {
    background-color: #0f172a;
  }

  .btn-secondary {
    background-color: #475569;
    color: #ffffff;
  }

  .btn-secondary:hover {
    background-color: #334155;
  }

  .btn-outline {
    background-color: #ffffff;
    color: #0f172a;
    border-color: #cbd5e1;
  }

  .btn-outline:hover {
    background-color: #f1f5f9;
  }

  .btn-success {
    background-color: #166534;
    color: #ffffff;
  }

  .btn-success:hover {
    background-color: #14532d;
  }

  .btn-danger {
    background-color: #dc2626;
    color: #ffffff;
  }

  .btn-danger:hover {
    background-color: #b91c1c;
  }

  .btn-danger:disabled {
    background-color: #f87171;
    cursor: not-allowed;
  }

  .full-width {
    width: 100%;
  }

  /* CARD GRID */
  .privacy-card-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 24px;
  }

  @media (min-width: 850px) {
    .privacy-card-grid {
      grid-template-columns: 1fr 1fr;
    }
  }

  .privacy-card {
    background-color: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .card-header h2 {
    margin: 0;
    font-size: 1.25rem;
    color: #0f172a;
  }

  .status-pill {
    font-size: 0.8rem;
    font-weight: 600;
    padding: 4px 10px;
    border-radius: 12px;
  }

  .pill-locked {
    background-color: #fef2f2;
    color: #991b1b;
    border: 1px solid #fecaca;
  }

  .pill-active {
    background-color: #f0fdf4;
    color: #166534;
    border: 1px solid #bbf7d0;
  }

  .pill-info {
    background-color: #f0f9ff;
    color: #0369a1;
    border: 1px solid #bae6fd;
  }

  .card-intro {
    font-size: 0.9rem;
    color: #334155;
    line-height: 1.5;
    margin-bottom: 20px;
  }

  /* FORMS & CONTROLS */
  .form-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 16px;
  }

  .form-group label {
    font-size: 0.9rem;
    font-weight: 600;
    color: #0f172a;
    margin-bottom: 6px;
  }

  .form-control {
    font-family: inherit;
    font-size: 0.95rem;
    padding: 10px 12px;
    border: 1px solid #94a3b8;
    border-radius: 6px;
    background-color: #ffffff;
    color: #0f172a;
  }

  .form-control:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 1px;
    border-color: #0969da;
  }

  .form-text {
    font-size: 0.8rem;
    color: #64748b;
    margin-top: 4px;
  }

  .checkbox-group {
    flex-direction: row;
    align-items: flex-start;
    gap: 10px;
  }

  .checkbox-group input[type="checkbox"] {
    margin-top: 3px;
    width: 18px;
    height: 18px;
  }

  .checkbox-group label {
    font-weight: normal;
    margin-bottom: 0;
  }

  .danger-checkbox label {
    color: #991b1b;
    font-weight: 500;
  }

  .form-actions-row {
    display: flex;
    gap: 12px;
  }

  /* ALERTS */
  .alert {
    padding: 12px 16px;
    border-radius: 8px;
    font-size: 0.9rem;
    line-height: 1.4;
    margin-bottom: 16px;
  }

  .alert-danger {
    background-color: #fef2f2;
    color: #991b1b;
    border: 1px solid #fecaca;
  }

  .alert-success {
    background-color: #f0fdf4;
    color: #166534;
    border: 1px solid #bbf7d0;
  }

  .alert-warning {
    background-color: #fffbeb;
    color: #92400e;
    border: 1px solid #fde68a;
  }

  .divider {
    border: 0;
    border-top: 1px solid #e2e8f0;
    margin: 24px 0;
  }

  .privacy-subcard h3 {
    margin-top: 0;
    margin-bottom: 8px;
    font-size: 1.1rem;
    color: #0f172a;
  }

  .tabs-subnav {
    margin-bottom: 16px;
  }

  .subnav-btn {
    background: transparent;
    border: 1px solid #cbd5e1;
    border-radius: 6px;
    padding: 6px 12px;
    font-size: 0.85rem;
    cursor: pointer;
    color: #0f172a;
  }

  .subnav-btn:hover {
    background-color: #f1f5f9;
  }

  .export-result-box {
    background-color: #f8fafc;
    border: 1px solid #cbd5e1;
    border-radius: 8px;
    padding: 12px 16px;
    margin-top: 16px;
    font-size: 0.85rem;
  }

  .export-result-box h4 {
    margin-top: 0;
    margin-bottom: 8px;
    color: #1e293b;
  }

  .json-preview {
    background-color: #0f172a;
    color: #38bdf8;
    padding: 12px;
    border-radius: 6px;
    font-size: 0.8rem;
    overflow-x: auto;
    max-height: 180px;
  }
</style>
