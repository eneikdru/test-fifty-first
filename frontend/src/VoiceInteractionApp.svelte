<script>
  import VoiceInteractionClient from './components/VoiceInteractionClient.svelte';
</script>

<header class="app-header" id="app-header">
  <div class="header-inner">
    <h1>Georgian Master Booking Platform</h1>
    <span class="subtext">Hands-Free Driver Voice Interaction</span>
  </div>
</header>

<main class="app-main">
  <VoiceInteractionClient />
</main>

<style>
  :global(body) {
    margin: 0;
    padding: 0;
    background-color: #f1f5f9;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  }

  .app-header {
    background-color: #1e3a8a;
    color: #ffffff;
    padding: 16px 24px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .header-inner {
    max-width: 800px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
  }

  h1 {
    font-size: 1.25rem;
    margin: 0;
    font-weight: 700;
  }

  .subtext {
    font-size: 0.85rem;
    opacity: 0.9;
  }

  .app-main {
    max-width: 800px;
    margin: 0 auto;
    padding: 16px;
  }
</style>
