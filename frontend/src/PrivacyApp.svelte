<script>
  import PrivacyConsentBanner from './components/PrivacyConsentBanner.svelte';
  import AccountRecovery from './components/AccountRecovery.svelte';

  let currentConsent = {
    essential: true,
    analytics: false,
    marketing: false,
    decided: false
  };

  function handleConsentChange(event) {
    currentConsent = event.detail;
  }

  function handleAccountUnlocked(event) {
    console.log('Account unlocked for:', event.detail.identifier);
  }
</script>

<div class="app-layout">
  <header class="app-header" id="header">
    <div class="header-container">
      <div class="brand">
        <svg class="brand-logo" viewBox="0 0 24 24" fill="currentColor" width="28" height="28" aria-hidden="true">
          <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
        </svg>
        <span class="brand-title">Georgian Masters Compliance & Security</span>
      </div>

      <div class="consent-status-pill">
        <span class="status-indicator {currentConsent.analytics || currentConsent.marketing ? 'status-active' : 'status-essential'}"></span>
        <span class="status-label">
          {currentConsent.analytics || currentConsent.marketing ? 'Custom Cookies Active' : 'Essential Cookies Only'}
        </span>
      </div>
    </div>
  </header>

  <main class="app-main" id="main-content">
    <div class="main-container">
      <section class="hero-section" id="hero">
        <h1 class="hero-title">Privacy Tools & Account Access Management</h1>
        <p class="hero-description">
          Compliance portal for Georgian artisan & booking master profiles in Tbilisi, Batumi, and Kutaisi.
        </p>
      </section>

      <section class="recovery-section" id="recovery-flow">
        <AccountRecovery on:accountUnlocked={handleAccountUnlocked} />
      </section>
    </div>
  </main>

  <footer class="app-footer" id="footer">
    <div class="footer-container">
      <p class="footer-copy">
        © Georgian Masters Platform. All compliance controls follow WCAG 2.1 AA accessibility guidelines.
      </p>
    </div>
  </footer>

  <PrivacyConsentBanner on:consentChange={handleConsentChange} />
</div>

<style>
  :global(:root) {
    --color-bg-main: #f8fafc;
    --color-bg-card: #ffffff;
    --color-text-primary: #0f172a;
    --color-text-muted: #334155;
    --color-primary: #1d4ed8;
    --color-focus: #2563eb;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  }

  :global(body) {
    margin: 0;
    padding: 0;
    background-color: var(--color-bg-main);
    color: var(--color-text-primary);
    -webkit-font-smoothing: antialiased;
  }

  .app-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  .app-header {
    background-color: #ffffff;
    border-bottom: 1px solid #e2e8f0;
    padding: 1rem 1.5rem;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  }

  .header-container {
    max-width: 1100px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .brand {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    color: #1d4ed8;
  }

  .brand-title {
    font-size: 1.125rem;
    font-weight: 700;
    color: #0f172a;
  }

  .consent-status-pill {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background-color: #f1f5f9;
    padding: 0.375rem 0.75rem;
    border-radius: 9999px;
    border: 1px solid #cbd5e1;
    font-size: 0.8125rem;
    font-weight: 600;
    color: #334155;
  }

  .status-indicator {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .status-essential {
    background-color: #2563eb;
  }

  .status-active {
    background-color: #16a34a;
  }

  .app-main {
    flex: 1;
    padding: 2.5rem 1rem 4rem 1rem;
  }

  .main-container {
    max-width: 900px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 2rem;
  }

  .hero-section {
    text-align: center;
  }

  .hero-title {
    font-size: 1.75rem;
    font-weight: 800;
    color: #0f172a;
    margin: 0 0 0.5rem 0;
    letter-spacing: -0.025em;
  }

  .hero-description {
    font-size: 1.0625rem;
    color: #334155;
    margin: 0;
    line-height: 1.5;
  }

  .app-footer {
    background-color: #ffffff;
    border-top: 1px solid #e2e8f0;
    padding: 1.5rem 1rem;
    text-align: center;
    margin-top: auto;
  }

  .footer-copy {
    font-size: 0.875rem;
    color: #475569;
    margin: 0;
  }
</style>
