<script>
  import { onMount, createEventDispatcher } from 'svelte';

  export let storageKey = 'privacy_consent_preferences';

  const dispatch = createEventDispatcher();

  let consentState = {
    essential: true,
    analytics: false,
    marketing: false,
    decided: false,
    updatedAt: null
  };

  let showBanner = true;
  let showModal = false;
  let statusMessage = '';

  onMount(() => {
    try {
      const stored = localStorage.getItem(storageKey);
      if (stored) {
        const parsed = JSON.parse(stored);
        consentState = { ...consentState, ...parsed, essential: true };
        showBanner = !consentState.decided;
      } else {
        showBanner = true;
      }
    } catch (e) {
      showBanner = true;
    }
  });

  function saveConsent(analytics, marketing) {
    consentState = {
      essential: true,
      analytics,
      marketing,
      decided: true,
      updatedAt: new Date().toISOString()
    };
    try {
      localStorage.setItem(storageKey, JSON.stringify(consentState));
    } catch (e) {
      // Storage fallback
    }
    showBanner = false;
    showModal = false;
    statusMessage = 'Your privacy preferences have been saved successfully.';
    dispatch('consentChange', consentState);
  }

  function handleAcceptAll() {
    saveConsent(true, true);
  }

  function handleRejectNonEssential() {
    saveConsent(false, false);
  }

  function handleSaveCustom() {
    saveConsent(consentState.analytics, consentState.marketing);
  }

  function openModal() {
    showModal = true;
  }

  function closeModal() {
    showModal = false;
  }

  function handleKeyDown(event) {
    if (event.key === 'Escape' && showModal) {
      closeModal();
    }
  }
</script>

<svelte:window on:keydown={handleKeyDown} />

<!-- Screen reader live region for status updates -->
<div class="sr-only" aria-live="polite" role="status">
  {statusMessage}
</div>

<!-- Non-essential blocking overlay / banner when not decided -->
{#if showBanner}
  <aside class="privacy-banner-overlay" aria-label="Privacy and Cookie Consent Banner">
    <div class="privacy-banner-card" role="region" aria-labelledby="banner-heading">
      <div class="banner-content">
        <h2 id="banner-heading" class="banner-title">Cookie Consent & Privacy Choices</h2>
        <p class="banner-text">
          We use cookies and essential scripts to provide secure master booking services across Tbilisi, Batumi, and Kutaisi.
          Non-essential cookies (analytics and marketing) are blocked until you give consent.
        </p>
      </div>

      <div class="banner-actions">
        <button
          type="button"
          class="btn btn-primary"
          on:click={handleAcceptAll}
          aria-label="Accept all cookies including analytics and marketing"
        >
          Accept All
        </button>
        <button
          type="button"
          class="btn btn-secondary"
          on:click={handleRejectNonEssential}
          aria-label="Reject non-essential cookies and keep essential only"
        >
          Essential Only
        </button>
        <button
          type="button"
          class="btn btn-outline"
          on:click={openModal}
          aria-label="Customize cookie preferences"
        >
          Customize
        </button>
      </div>
    </div>
  </aside>
{/if}

<!-- Re-open / Status Button -->
<div class="privacy-floating-control">
  <button
    type="button"
    class="btn btn-floating"
    on:click={() => (showBanner ? (showModal = true) : openModal())}
    aria-label="Manage Privacy and Cookie Settings"
  >
    <svg class="icon" viewBox="0 0 24 24" aria-hidden="true" width="18" height="18">
      <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zM11 7h2v6h-2zm0 8h2v2h-2z"/>
    </svg>
    Privacy Settings
  </button>
</div>

<!-- Granular Preferences Modal -->
{#if showModal}
  <div class="modal-backdrop" on:click|self={closeModal} role="presentation">
    <div
      class="modal-card"
      role="dialog"
      aria-modal="true"
      aria-labelledby="privacy-modal-title"
      aria-describedby="privacy-modal-desc"
    >
      <header class="modal-header">
        <h2 id="privacy-modal-title" class="modal-title">Privacy & Cookie Preferences</h2>
        <button
          type="button"
          class="btn-close"
          on:click={closeModal}
          aria-label="Close Privacy Settings dialog"
        >
          ✕
        </button>
      </header>

      <div class="modal-body">
        <p id="privacy-modal-desc" class="modal-desc">
          Control how your data and cookies are handled. Essential cookies are required for system security and account recovery.
        </p>

        <div class="preference-group">
          <div class="preference-item">
            <div class="preference-info">
              <label for="essential-toggle" class="preference-label">
                Essential Cookies & System Security
              </label>
              <p class="preference-help">
                Required for authentication, session handling, and account recovery verification. Cannot be turned off.
              </p>
            </div>
            <div class="toggle-wrapper">
              <input
                id="essential-toggle"
                type="checkbox"
                checked
                disabled
                aria-label="Essential Cookies always enabled"
              />
              <span class="badge badge-locked">Always Active</span>
            </div>
          </div>

          <div class="preference-item">
            <div class="preference-info">
              <label for="analytics-toggle" class="preference-label">
                Analytics & Usage Metrics
              </label>
              <p class="preference-help">
                Allows us to collect anonymous booking platform usage data to improve service response in Georgian cities.
              </p>
            </div>
            <div class="toggle-wrapper">
              <input
                id="analytics-toggle"
                type="checkbox"
                bind:checked={consentState.analytics}
                aria-label="Enable Analytics Cookies"
              />
            </div>
          </div>

          <div class="preference-item">
            <div class="preference-info">
              <label for="marketing-toggle" class="preference-label">
                Marketing & Social Integration
              </label>
              <p class="preference-help">
                Enables Facebook Messenger import features and personalized Georgian master discovery.
              </p>
            </div>
            <div class="toggle-wrapper">
              <input
                id="marketing-toggle"
                type="checkbox"
                bind:checked={consentState.marketing}
                aria-label="Enable Marketing Cookies"
              />
            </div>
          </div>
        </div>
      </div>

      <footer class="modal-footer">
        <button type="button" class="btn btn-secondary" on:click={closeModal}>
          Cancel
        </button>
        <button type="button" class="btn btn-primary" on:click={handleSaveCustom}>
          Save Preferences
        </button>
      </footer>
    </div>
  </div>
{/if}

<style>
  /* Screen Reader Accessible hidden text */
  .sr-only {
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

  /* Fixed bottom banner blocking non-essential interaction */
  .privacy-banner-overlay {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 9990;
    padding: 1rem;
    background-color: rgba(15, 23, 42, 0.75);
    backdrop-filter: blur(4px);
    display: flex;
    justify-content: center;
  }

  .privacy-banner-card {
    background-color: var(--color-bg-card, #ffffff);
    color: var(--color-text-primary, #0f172a);
    max-width: 900px;
    width: 100%;
    border-radius: 8px;
    padding: 1.25rem 1.5rem;
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3);
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  @media (min-width: 768px) {
    .privacy-banner-card {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
    }
  }

  .banner-title {
    font-size: 1.125rem;
    font-weight: 700;
    margin: 0 0 0.375rem 0;
    color: #0f172a;
  }

  .banner-text {
    font-size: 0.9375rem;
    color: #334155;
    margin: 0;
    line-height: 1.45;
  }

  .banner-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.625rem;
    align-items: center;
    min-width: fit-content;
  }

  /* Floating Control Trigger */
  .privacy-floating-control {
    position: fixed;
    bottom: 1.25rem;
    right: 1.25rem;
    z-index: 9000;
  }

  /* Buttons */
  .btn {
    font-family: inherit;
    font-size: 0.875rem;
    font-weight: 600;
    padding: 0.625rem 1.125rem;
    border-radius: 6px;
    border: 1px solid transparent;
    cursor: pointer;
    transition: background-color 0.15s ease, border-color 0.15s ease;
    display: inline-flex;
    align-items: center;
    gap: 0.375rem;
  }

  .btn:focus-visible {
    outline: 3px solid #2563eb !important;
    outline-offset: 2px !important;
  }

  .btn-primary {
    background-color: #1d4ed8;
    color: #ffffff;
  }

  .btn-primary:hover {
    background-color: #1e40af;
  }

  .btn-secondary {
    background-color: #f1f5f9;
    color: #0f172a;
    border-color: #cbd5e1;
  }

  .btn-secondary:hover {
    background-color: #e2e8f0;
  }

  .btn-outline {
    background-color: transparent;
    color: #1d4ed8;
    border-color: #1d4ed8;
  }

  .btn-outline:hover {
    background-color: #eff6ff;
  }

  .btn-floating {
    background-color: #0f172a;
    color: #ffffff;
    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
    border: 1px solid #334155;
  }

  .btn-floating:hover {
    background-color: #1e293b;
  }

  .btn-close {
    background: transparent;
    border: none;
    font-size: 1.25rem;
    line-height: 1;
    color: #475569;
    cursor: pointer;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
  }

  .btn-close:hover {
    color: #0f172a;
    background-color: #f1f5f9;
  }

  .btn-close:focus-visible {
    outline: 3px solid #2563eb;
    outline-offset: 2px;
  }

  /* Modal Dialog */
  .modal-backdrop {
    position: fixed;
    inset: 0;
    z-index: 10000;
    background-color: rgba(15, 23, 42, 0.65);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
  }

  .modal-card {
    background-color: #ffffff;
    color: #0f172a;
    border-radius: 12px;
    max-width: 560px;
    width: 100%;
    max-height: 90vh;
    display: flex;
    flex-direction: column;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.25);
    overflow: hidden;
  }

  .modal-header {
    padding: 1.25rem 1.5rem;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .modal-title {
    font-size: 1.25rem;
    font-weight: 700;
    margin: 0;
  }

  .modal-body {
    padding: 1.5rem;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }

  .modal-desc {
    margin: 0;
    font-size: 0.9375rem;
    color: #334155;
    line-height: 1.5;
  }

  .preference-group {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .preference-item {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 1rem;
    padding: 1rem;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    background-color: #f8fafc;
  }

  .preference-label {
    font-weight: 600;
    font-size: 0.9375rem;
    color: #0f172a;
    display: block;
    margin-bottom: 0.25rem;
  }

  .preference-help {
    font-size: 0.8125rem;
    color: #475569;
    margin: 0;
    line-height: 1.4;
  }

  .toggle-wrapper {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .toggle-wrapper input[type="checkbox"] {
    width: 1.25rem;
    height: 1.25rem;
    cursor: pointer;
    accent-color: #1d4ed8;
  }

  .toggle-wrapper input[type="checkbox"]:focus-visible {
    outline: 3px solid #2563eb;
    outline-offset: 2px;
  }

  .badge-locked {
    font-size: 0.75rem;
    font-weight: 600;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    background-color: #e2e8f0;
    color: #334155;
    white-space: nowrap;
  }

  .modal-footer {
    padding: 1rem 1.5rem;
    border-top: 1px solid #e2e8f0;
    display: flex;
    justify-content: flex-end;
    gap: 0.75rem;
    background-color: #f8fafc;
  }
</style>
