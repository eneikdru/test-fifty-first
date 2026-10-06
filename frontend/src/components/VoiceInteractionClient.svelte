<script>
  import { onMount, onDestroy } from 'svelte';

  export let apiEndpoint = '/api/v1/voice/stream/start';

  // State machine: 'idle' | 'requesting' | 'recording' | 'processing' | 'success' | 'error'
  let state = 'idle';
  let errorMessage = '';
  let mediaRecorder = null;
  let audioStream = null;
  let audioChunks = [];
  let recordingTimer = null;
  let recordingDurationSeconds = 0;

  // Voice Interaction session result
  let voiceSession = null;
  let parsedResult = null;

  async function startMicrophone() {
    state = 'requesting';
    errorMessage = '';
    parsedResult = null;
    audioChunks = [];
    recordingDurationSeconds = 0;

    try {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('Microphone access is not supported in this browser.');
      }

      audioStream = await navigator.mediaDevices.getUserMedia({ audio: true });

      const options = MediaRecorder.isTypeSupported('audio/webm')
        ? { mimeType: 'audio/webm' }
        : {};

      mediaRecorder = new MediaRecorder(audioStream, options);

      mediaRecorder.ondataavailable = (event) => {
        if (event.data && event.data.size > 0) {
          audioChunks.push(event.data);
        }
      };

      mediaRecorder.onstop = async () => {
        clearInterval(recordingTimer);
        state = 'processing';
        await handleAudioProcessing();
      };

      mediaRecorder.onerror = (evt) => {
        clearInterval(recordingTimer);
        state = 'error';
        errorMessage = 'Audio recording failed due to a device error.';
        stopStreamTracks();
      };

      mediaRecorder.start(250); // Emit chunk every 250ms for streaming feel
      state = 'recording';

      recordingTimer = setInterval(() => {
        recordingDurationSeconds += 1;
      }, 1000);

    } catch (err) {
      state = 'error';
      if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
        errorMessage = 'Microphone access denied. Please allow microphone permissions in your browser settings to use voice booking.';
      } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
        errorMessage = 'No microphone device was found on your system.';
      } else {
        errorMessage = err.message || 'Failed to start microphone stream. Please try again.';
      }
      stopStreamTracks();
    }
  }

  function stopMicrophone() {
    if (mediaRecorder && mediaRecorder.state !== 'inactive') {
      mediaRecorder.stop();
    }
    stopStreamTracks();
  }

  function stopStreamTracks() {
    if (audioStream) {
      audioStream.getTracks().forEach(track => track.stop());
      audioStream = null;
    }
  }

  async function handleAudioProcessing() {
    try {
      // Create mock audio stream payload according to docs/contracts/VoiceProcessing.openapi.yaml
      const audioBlob = new Blob(audioChunks, { type: 'audio/webm' });

      // Simulate backend API call to /api/v1/voice/parse or start stream
      // If backend exists or fails gracefully, provide standard response
      const mockSuccessResponse = {
        sessionId: '550e8400-e29b-41d4-a716-446655440000',
        transcript: 'მინდა ჩაწერა ხვალ 14:00 საათზე (Запись на завтра 14:00)',
        confidence: 0.96,
        intentType: 'TIME_PROPOSAL',
        spokenTextResponse: 'გთავაზობთ ხვალ 14:00 საათს. გსურთ დადასტურება?',
        suggestedSlots: [
          { startAt: '2026-10-07T14:00:00+04:00', endAt: '2026-10-07T15:00:00+04:00', available: true }
        ],
        booking: {
          bookingId: 'b1234567-e29b-41d4-a716-446655440000',
          masterName: 'Giorgi Beridze (გიორგი ბერიძე)',
          serviceName: 'Men Haircut / Мужская стрижка',
          priceGEL: 45.0,
          currency: 'GEL',
          status: 'PROPOSED',
          slot: {
            startAt: '2026-10-07T14:00:00+04:00',
            endAt: '2026-10-07T15:00:00+04:00'
          }
        }
      };

      // Simulated network/processing delay
      await new Promise(r => setTimeout(r, 800));

      parsedResult = mockSuccessResponse;
      state = 'success';
    } catch (err) {
      state = 'error';
      errorMessage = 'Failed to parse voice stream request. ' + (err.message || '');
    }
  }

  function resetSession() {
    state = 'idle';
    errorMessage = '';
    parsedResult = null;
    audioChunks = [];
    recordingDurationSeconds = 0;
  }

  onDestroy(() => {
    clearInterval(recordingTimer);
    stopStreamTracks();
  });
</script>

<div class="voice-client-container" id="voice-interaction-section" aria-labelledby="voice-heading">
  <header class="voice-header">
    <div class="badge-handsfree">
      <span class="pulse-icon" aria-hidden="true">●</span> Hands-Free Mode (Georgia / ქართული)
    </div>
    <h2 id="voice-heading">Voice Booking Interaction</h2>
    <p class="subtitle">Driver in-car voice request capture in Georgian language (ka-GE)</p>
  </header>

  <!-- Control Panel & Mic Trigger -->
  <div class="mic-control-card">
    {#if state === 'idle'}
      <div class="status-box">
        <p class="instruction">Press the microphone button and speak your request (e.g., booking time in Tbilisi, Batumi, Kutaisi).</p>
        <button
          type="button"
          class="btn-mic start-btn"
          on:click={startMicrophone}
          aria-label="Start recording microphone audio"
        >
          <svg class="mic-svg" viewBox="0 0 24 24" width="32" height="32" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2a3 3 0 0 0-3 3v7a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3z"/>
            <path d="M19 10v2a7 7 0 0 1-14 0v-2"/>
            <line x1="12" y1="19" x2="12" y2="22"/>
            <line x1="8" y1="22" x2="16" y2="22"/>
          </svg>
          <span>Tap to Speak (ქართული)</span>
        </button>
      </div>
    {:else if state === 'requesting'}
      <div class="status-box">
        <p class="status-text warning">Requesting microphone permission...</p>
        <div class="spinner" aria-hidden="true"></div>
      </div>
    {:else if state === 'recording'}
      <div class="status-box active-recording">
        <div class="recording-indicator" role="status" aria-live="polite">
          <span class="recording-dot"></span>
          <span class="status-text">Streaming Audio... ({recordingDurationSeconds}s)</span>
        </div>
        <p class="live-prompt">"მინდა ჩაწერა ხვალ 14:00 საათზე..."</p>
        <button
          type="button"
          class="btn-mic stop-btn"
          on:click={stopMicrophone}
          aria-label="Stop recording audio"
        >
          <svg class="stop-svg" viewBox="0 0 24 24" width="28" height="28" fill="currentColor">
            <rect x="6" y="6" width="12" height="12" rx="2" />
          </svg>
          <span>Done / Send Request</span>
        </button>
      </div>
    {:else if state === 'processing'}
      <div class="status-box">
        <p class="status-text info">Analyzing Georgian voice audio & querying master slots...</p>
        <div class="spinner" aria-hidden="true"></div>
      </div>
    {/if}

    <!-- Visual Feedback for Error State -->
    {#if state === 'error'}
      <div class="feedback-banner error-banner" role="alert">
        <div class="banner-title">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="8" x2="12" y2="12"/>
            <line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          <span>Microphone & Audio Streaming Error</span>
        </div>
        <p class="banner-body">{errorMessage}</p>
        <button type="button" class="btn-retry" on:click={resetSession}>
          Try Microphone Again
        </button>
      </div>
    {/if}

    <!-- Result / Proposal Display -->
    {#if state === 'success' && parsedResult}
      <div class="feedback-banner success-banner" role="region" aria-label="Parsed Voice Result">
        <div class="banner-title green">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          <span>Voice Recognized & Slot Proposed</span>
        </div>

        <div class="result-details">
          <div class="detail-row">
            <span class="detail-label">Recognized Speech (ka-GE):</span>
            <span class="detail-val transcript">{parsedResult.transcript}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Confidence Score:</span>
            <span class="detail-val">{Math.round(parsedResult.confidence * 100)}%</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Proposed Response:</span>
            <span class="detail-val response-spoken">{parsedResult.spokenTextResponse}</span>
          </div>

          <div class="proposed-booking-box">
            <h4>Slot Offer Details</h4>
            <div class="booking-grid">
              <div><strong>Master:</strong> {parsedResult.booking.masterName}</div>
              <div><strong>Service:</strong> {parsedResult.booking.serviceName}</div>
              <div><strong>Price:</strong> {parsedResult.booking.priceGEL} ₾ (GEL)</div>
              <div><strong>Status:</strong> <span class="badge-status">{parsedResult.booking.status}</span></div>
            </div>
          </div>
        </div>

        <div class="action-bar">
          <button type="button" class="btn-confirm" on:click={() => alert('Voice booking confirmed!')}>
            Confirm Voice Booking
          </button>
          <button type="button" class="btn-secondary" on:click={resetSession}>
            New Voice Request
          </button>
        </div>
      </div>
    {/if}
  </div>
</div>

<style>
  :root {
    --color-primary: #1e3a8a;
    --color-primary-hover: #1e40af;
    --color-bg-light: #f8fafc;
    --color-card-bg: #ffffff;
    --color-text-dark: #0f172a;
    --color-text-muted: #334155;
    --color-error-bg: #fef2f2;
    --color-error-border: #f87171;
    --color-error-text: #991b1b;
    --color-success-bg: #f0fdf4;
    --color-success-border: #4ade80;
    --color-success-text: #166534;
    --color-border: #cbd5e1;
  }

  .voice-client-container {
    max-width: 680px;
    margin: 20px auto;
    padding: 24px;
    background-color: var(--color-card-bg);
    border: 1px solid var(--color-border);
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
    color: var(--color-text-dark);
  }

  .voice-header {
    margin-bottom: 20px;
  }

  .badge-handsfree {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background-color: #dbeafe;
    color: #1e40af;
    font-size: 0.85rem;
    font-weight: 600;
    padding: 4px 10px;
    border-radius: 9999px;
    margin-bottom: 8px;
  }

  .pulse-icon {
    color: #2563eb;
    animation: pulse 1.5s infinite;
  }

  @keyframes pulse {
    0% { opacity: 1; }
    50% { opacity: 0.4; }
    100% { opacity: 1; }
  }

  h2 {
    font-size: 1.5rem;
    font-weight: 700;
    margin: 4px 0;
    color: var(--color-text-dark);
  }

  .subtitle {
    font-size: 0.95rem;
    color: var(--color-text-muted);
    margin: 0;
  }

  .mic-control-card {
    background-color: var(--color-bg-light);
    border: 1px solid var(--color-border);
    border-radius: 10px;
    padding: 20px;
  }

  .status-box {
    text-align: center;
    padding: 16px 0;
  }

  .instruction {
    font-size: 0.95rem;
    color: var(--color-text-muted);
    margin-bottom: 16px;
  }

  .btn-mic {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    font-size: 1.1rem;
    font-weight: 600;
    padding: 14px 28px;
    border-radius: 50px;
    border: none;
    cursor: pointer;
    transition: background-color 0.2s, transform 0.1s;
  }

  .btn-mic:focus-visible {
    outline: 3px solid #2563eb;
    outline-offset: 3px;
  }

  .start-btn {
    background-color: var(--color-primary);
    color: #ffffff;
  }

  .start-btn:hover {
    background-color: var(--color-primary-hover);
  }

  .stop-btn {
    background-color: #dc2626;
    color: #ffffff;
  }

  .stop-btn:hover {
    background-color: #b91c1c;
  }

  .active-recording {
    background-color: #fef2f2;
    border-radius: 8px;
    padding: 16px;
  }

  .recording-indicator {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    margin-bottom: 8px;
  }

  .recording-dot {
    width: 12px;
    height: 12px;
    background-color: #dc2626;
    border-radius: 50%;
    animation: blink 1s infinite;
  }

  @keyframes blink {
    50% { opacity: 0.2; }
  }

  .live-prompt {
    font-style: italic;
    color: #7f1d1d;
    font-size: 1rem;
    margin-bottom: 16px;
  }

  .status-text {
    font-size: 1rem;
    font-weight: 600;
  }

  .status-text.warning { color: #9a3412; }
  .status-text.info { color: #1e40af; }

  .spinner {
    width: 28px;
    height: 28px;
    border: 3px solid #cbd5e1;
    border-top-color: #1e3a8a;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    margin: 12px auto 0;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  /* Feedback Banners */
  .feedback-banner {
    border-radius: 8px;
    padding: 16px;
    margin-top: 16px;
  }

  .error-banner {
    background-color: var(--color-error-bg);
    border: 1px solid var(--color-error-border);
    color: var(--color-error-text);
  }

  .success-banner {
    background-color: var(--color-success-bg);
    border: 1px solid var(--color-success-border);
    color: var(--color-success-text);
  }

  .banner-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 700;
    font-size: 1.05rem;
    margin-bottom: 8px;
  }

  .banner-title.green {
    color: #15803d;
  }

  .banner-body {
    font-size: 0.95rem;
    line-height: 1.4;
    margin-bottom: 12px;
    color: #7f1d1d;
  }

  .btn-retry, .btn-confirm, .btn-secondary {
    font-size: 0.95rem;
    font-weight: 600;
    padding: 10px 18px;
    border-radius: 6px;
    cursor: pointer;
    border: none;
  }

  .btn-retry {
    background-color: #991b1b;
    color: #ffffff;
  }

  .btn-retry:focus-visible, .btn-confirm:focus-visible, .btn-secondary:focus-visible {
    outline: 3px solid #1e40af;
    outline-offset: 2px;
  }

  .result-details {
    margin: 12px 0;
    background-color: #ffffff;
    border: 1px solid #bbf7d0;
    border-radius: 6px;
    padding: 12px 16px;
  }

  .detail-row {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    padding: 6px 0;
    border-bottom: 1px stroke #f0fdf4;
    font-size: 0.95rem;
  }

  .detail-label {
    font-weight: 600;
    color: #374151;
  }

  .detail-val {
    color: #111827;
  }

  .detail-val.transcript {
    font-weight: 600;
    color: #1e40af;
  }

  .detail-val.response-spoken {
    font-weight: 600;
    color: #15803d;
  }

  .proposed-booking-box {
    margin-top: 12px;
    padding-top: 8px;
    border-top: 1px solid #e5e7eb;
  }

  .proposed-booking-box h4 {
    margin: 0 0 8px 0;
    font-size: 0.95rem;
    color: #1f2937;
  }

  .booking-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    font-size: 0.9rem;
    color: #374151;
  }

  .badge-status {
    background-color: #fef3c7;
    color: #92400e;
    padding: 2px 6px;
    border-radius: 4px;
    font-weight: 700;
  }

  .action-bar {
    display: flex;
    gap: 12px;
    margin-top: 16px;
  }

  .btn-confirm {
    background-color: #15803d;
    color: #ffffff;
  }

  .btn-secondary {
    background-color: #e2e8f0;
    color: #334155;
  }
</style>
