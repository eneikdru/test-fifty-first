<script>
  import { onMount } from 'svelte';
  import CheckoutUI from './components/CheckoutUI.svelte';
  import LedgerUI from './components/LedgerUI.svelte';
  import VoiceInteractionUI from './components/VoiceInteractionUI.svelte';
  import RescheduleCancelUI from './components/RescheduleCancelUI.svelte';
  import CalendarManagementUI from './components/CalendarManagementUI.svelte';

  let activeTab = 'checkout'; // 'checkout' | 'ledger' | 'voice' | 'reschedule' | 'calendar'

  onMount(() => {
    if (typeof window !== 'undefined') {
      const urlParams = new URLSearchParams(window.location.search);
      if (urlParams.has('token') || urlParams.get('page') === 'reschedule' || urlParams.get('tab') === 'reschedule') {
        activeTab = 'reschedule';
      } else if (urlParams.get('page') === 'calendar' || urlParams.get('tab') === 'calendar') {
        activeTab = 'calendar';
      }
    }
  });
</script>

<div class="app-container">
  <header class="app-header" id="app-header">
    <div class="brand-title">
      <h1>Georgian Masters Platform · Professional Dashboard</h1>
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
        class="tab-btn {activeTab === 'calendar' ? 'active' : ''}"
        aria-pressed={activeTab === 'calendar'}
        on:click={() => activeTab = 'calendar'}
      >
        Calendar Management (📅)
      </button>
      <button
        type="button"
        class="tab-btn {activeTab === 'voice' ? 'active' : ''}"
        aria-pressed={activeTab === 'voice'}
        on:click={() => activeTab = 'voice'}
      >
        Voice Booking (🎙️)
      </button>
      <button
        type="button"
        class="tab-btn {activeTab === 'reschedule' ? 'active' : ''}"
        aria-pressed={activeTab === 'reschedule'}
        on:click={() => activeTab = 'reschedule'}
      >
        Reschedule & Cancel (🗓️)
      </button>
    </nav>
  </header>

  <main class="app-main" id="main-content">
    {#if activeTab === 'checkout'}
      <CheckoutUI />
    {:else if activeTab === 'ledger'}
      <LedgerUI />
    {:else if activeTab === 'calendar'}
      <CalendarManagementUI />
    {:else if activeTab === 'voice'}
      <VoiceInteractionUI />
    {:else if activeTab === 'reschedule'}
      <RescheduleCancelUI />
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
    overflow-x: hidden;
  }

  .app-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 100vw;
    box-sizing: border-box;
  }

  .app-header {
    background-color: #0969da;
    color: #ffffff;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    box-sizing: border-box;
    width: 100%;
  }

  @media (min-width: 768px) {
    .app-header {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
      padding: 16px 24px;
    }
  }

  .brand-title h1 {
    margin: 0;
    font-size: 1.15rem;
    font-weight: 600;
  }

  .nav-tabs {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    width: 100%;
  }

  @media (min-width: 768px) {
    .nav-tabs {
      width: auto;
    }
  }

  .tab-btn {
    background: transparent;
    color: #ffffff;
    border: 1px solid rgba(255, 255, 255, 0.4);
    border-radius: 6px;
    padding: 6px 12px;
    font-size: 0.85rem;
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
    padding: 16px;
    flex: 1;
    box-sizing: border-box;
    width: 100%;
  }

  @media (min-width: 768px) {
    .app-main {
      padding: 24px 16px;
    }
  }
</style>
