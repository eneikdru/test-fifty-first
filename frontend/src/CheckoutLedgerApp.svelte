<script>
  import CheckoutUI from './components/CheckoutUI.svelte';
  import LedgerUI from './components/LedgerUI.svelte';
  import VoiceInteractionUI from './components/VoiceInteractionUI.svelte';

  let activeTab = 'checkout'; // 'checkout' | 'ledger' | 'voice'
</script>

<div class="app-container">
  <header class="app-header" id="app-header">
    <div class="brand-title">
      <h1>Georgian Masters Platform · Voice & Checkout</h1>
    </div>
    <nav class="nav-tabs" aria-label="Main Navigation">
      <button
        type="button"
        class="tab-btn {activeTab === 'checkout' ? 'active' : ''}"
        aria-pressed={activeTab === 'checkout'}
        on:click={() => activeTab = 'checkout'}
      >
        Checkout View
      </button>
      <button
        type="button"
        class="tab-btn {activeTab === 'ledger' ? 'active' : ''}"
        aria-pressed={activeTab === 'ledger'}
        on:click={() => activeTab = 'ledger'}
      >
        Professional Ledger
      </button>
      <button
        type="button"
        class="tab-btn {activeTab === 'voice' ? 'active' : ''}"
        aria-pressed={activeTab === 'voice'}
        on:click={() => activeTab = 'voice'}
      >
        Voice Booking (🎙️)
      </button>
    </nav>
  </header>

  <main class="app-main" id="main-content">
    {#if activeTab === 'checkout'}
      <CheckoutUI />
    {:else if activeTab === 'ledger'}
      <LedgerUI />
    {:else if activeTab === 'voice'}
      <VoiceInteractionUI />
    {/if}
  </main>
</div>

<style>
  :global(body) {
    margin: 0;
    padding: 0;
    background-color: #f6f8fa;
    color: #1f2328;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  }

  .app-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  .app-header {
    background-color: #0969da;
    color: #ffffff;
    padding: 16px 24px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .brand-title h1 {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
  }

  .nav-tabs {
    display: flex;
    gap: 12px;
    margin-top: 8px;
  }

  @media (min-width: 600px) {
    .nav-tabs {
      margin-top: 0;
    }
  }

  .tab-btn {
    background: transparent;
    color: #ffffff;
    border: 1px solid rgba(255, 255, 255, 0.4);
    border-radius: 6px;
    padding: 8px 16px;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.15s ease, border-color 0.15s ease;
  }

  .tab-btn:hover {
    background-color: rgba(255, 255, 255, 0.15);
  }

  .tab-btn.active {
    background-color: #ffffff;
    color: #0969da;
    border-color: #ffffff;
  }

  .tab-btn:focus-visible {
    outline: 3px solid #ffffff;
    outline-offset: 2px;
  }

  .app-main {
    padding: 24px 16px;
    flex: 1;
  }
</style>
