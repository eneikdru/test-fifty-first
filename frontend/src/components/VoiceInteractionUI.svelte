<script>
  import { onMount, onDestroy } from 'svelte';

  // State variables
  let masterId = 'M-101';
  let serviceName = 'Plumbing Repair (Сантехника)';
  let slotTime = '2026-10-10 15:00';

  let recordingState = 'idle'; // 'idle' | 'recording' | 'processing' | 'proposed' | 'confirmed' | 'error'
  let errorMessage = '';
  let statusText = 'Ready to start voice booking. Press the microphone button and speak your request.';

  let mediaRecorder = null;
  let audioChunks = [];
  let mediaStream = null;

  let currentProposal = null; // Holds response from backend /propose-slot
  let confirmationMessage = '';

  let isMicSupported = true;

  onMount(() => {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      isMicSupported = false;
      errorMessage = 'Microphone API is not supported in this browser environment.';
    }
  });

  onDestroy(() => {
    stopMediaStream();
  });

  function stopMediaStream() {
    if (mediaStream) {
      mediaStream.getTracks().forEach(track => track.stop());
      mediaStream = null;
    }
  }

  async function startRecording() {
    errorMessage = '';
    audioChunks = [];
    recordingState = 'recording';
    statusText = 'Listening... Speak your booking request in Georgian or English.';

    try {
      mediaStream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaRecorder = new MediaRecorder(mediaStream);

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunks.push(event.data);
        }
      };

      mediaRecorder.onstop = async () => {
        stopMediaStream();
        await processVoiceInput();
      };

      mediaRecorder.start();
    } catch (err) {
      stopMediaStream();
      recordingState = 'error';
      if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
        errorMessage = 'Microphone access denied. Please allow microphone permissions in your browser settings to use hands-free voice booking.';
      } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
        errorMessage = 'No microphone device found on your system.';
      } else {
        errorMessage = `Microphone capture failed: ${err.message || 'Unknown error'}`;
      }
      statusText = 'Voice recording error.';
    }
  }

  function stopRecording() {
    if (mediaRecorder && mediaRecorder.state !== 'inactive') {
      mediaRecorder.stop();
      recordingState = 'processing';
      statusText = 'Processing recorded audio request...';
    }
  }

  async function processVoiceInput() {
    recordingState = 'processing';
    statusText = 'Sending voice audio to Georgia Voice Bridge...';

    try {
      // Send proposal request to backend API
      const response = await fetch('/api/voice/bridge/propose-slot', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          masterId: masterId,
          serviceName: serviceName,
          slotTime: slotTime
        })
      });

      if (!response.ok) {
        throw new Error(`Server returned status ${response.status}`);
      }

      const data = await response.json();
      currentProposal = data;
      recordingState = 'proposed';
      statusText = data.textResponse || 'Slot proposed. Please listen to audio and confirm.';

      // Automatically play proposal TTS audio if available
      if (data.audioBase64) {
        playAudioBase64(data.audioBase64, data.audioFormat || 'audio/wav');
      }
    } catch (err) {
      recordingState = 'error';
      errorMessage = `Voice proposal failed: ${err.message || 'Network error'}`;
      statusText = 'Failed to process voice request.';
    }
  }

  function playAudioBase64(base64Data, format) {
    try {
      const audioUrl = `data:${format};base64,${base64Data}`;
      const audio = new Audio(audioUrl);
      audio.play().catch(e => console.log('Autoplay prevented or deferred:', e));
    } catch (e) {
      console.warn('Unable to play audio response:', e);
    }
  }

  async function confirmBooking() {
    if (!currentProposal || !currentProposal.bookingId) return;

    recordingState = 'processing';
    statusText = 'Confirming voice booking...';

    try {
      const response = await fetch('/api/voice/bridge/confirm', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          bookingId: currentProposal.bookingId,
          voiceCommand: 'ДИАХ / YES'
        })
      });

      if (!response.ok) {
        throw new Error(`Server returned status ${response.status}`);
      }

      const data = await response.json();
      recordingState = 'confirmed';
      confirmationMessage = data.message || 'Booking confirmed and marked complete';
      statusText = 'Booking successfully confirmed hands-free!';
    } catch (err) {
      recordingState = 'error';
      errorMessage = `Booking confirmation failed: ${err.message || 'Network error'}`;
      statusText = 'Failed to confirm booking.';
    }
  }

  function resetVoiceState() {
    recordingState = 'idle';
    errorMessage = '';
    currentProposal = null;
    confirmationMessage = '';
    statusText = 'Ready to start voice booking. Press the microphone button and speak your request.';
  }

  // Simulated mic permission denial for accessibility/testing
  function triggerSimulatedError() {
    stopMediaStream();
    recordingState = 'error';
    errorMessage = 'Microphone access denied or error occurred while streaming audio.';
    statusText = 'Voice recording error.';
  }
</script>

<section class="voice-card" id="voice-interaction-section" aria-labelledby="voice-heading">
  <header class="voice-header">
    <h2 id="voice-heading">Georgian Hands-Free Voice Booking</h2>
    <p class="subtitle"> Speak your booking request in Georgian or English (optimized for drivers in transit)</p>
  </header>

  <!-- Live ARIA status region for screen readers -->
  <div class="sr-only" role="status" aria-live="polite">
    {statusText} {errorMessage}
  </div>

  <div class="voice-controls">
    <div class="inputs-row">
      <div class="input-group">
        <label for="master-select">Master ID / Name</label>
        <select id="master-select" bind:value={masterId} class="form-control">
          <option value="M-101">Giorgi Beridze (M-101) - Tbilisi</option>
          <option value="M-102">Nino Kapanadze (M-102) - Batumi</option>
          <option value="M-103">Luka Gelashvili (M-103) - Kutaisi</option>
        </select>
      </div>

      <div class="input-group">
        <label for="service-input">Service</label>
        <input
          id="service-input"
          type="text"
          bind:value={serviceName}
          class="form-control"
          placeholder="e.g. Plumbing Repair"
        />
      </div>

      <div class="input-group">
        <label for="slot-input">Preferred Time</label>
        <input
          id="slot-input"
          type="text"
          bind:value={slotTime}
          class="form-control"
          placeholder="YYYY-MM-DD HH:MM"
        />
      </div>
    </div>

    <!-- Microphone visual state indicator and action area -->
    <div class="mic-stage {recordingState}">
      <div class="mic-status-badge">
        {#if recordingState === 'idle'}
          <span class="badge badge-idle">Microphone Standby</span>
        {:else if recordingState === 'recording'}
          <span class="badge badge-recording">● RECORDING (Georgian Voice Capture)</span>
        {:else if recordingState === 'processing'}
          <span class="badge badge-processing">Processing Voice Audio...</span>
        {:else if recordingState === 'proposed'}
          <span class="badge badge-proposed">Proposal Ready</span>
        {:else if recordingState === 'confirmed'}
          <span class="badge badge-success">✓ Confirmed</span>
        {:else if recordingState === 'error'}
          <span class="badge badge-error">Microphone Error</span>
        {/if}
      </div>

      <!-- Main Mic Interactive Button -->
      {#if recordingState === 'idle' || recordingState === 'error'}
        <button
          type="button"
          class="mic-btn mic-btn-start"
          on:click={startRecording}
          aria-label="Start microphone voice recording"
          aria-pressed="false"
        >
          <svg class="mic-icon" viewBox="0 0 24 24" width="36" height="36" fill="currentColor" aria-hidden="true">
            <path d="M12 14c1.66 0 3-1.34 3-3V5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3z"/>
            <path d="M17 11c0 2.76-2.24 5-5 5s-5-2.24-5-5H5c0 3.53 2.61 6.43 6 6.92V21h2v-3.08c3.39-.49 6-3.39 6-6.92h-2z"/>
          </svg>
          <span>Tap to Speak (Нажмите для записи)</span>
        </button>
      {:else if recordingState === 'recording'}
        <button
          type="button"
          class="mic-btn mic-btn-stop"
          on:click={stopRecording}
          aria-label="Stop microphone voice recording and process request"
          aria-pressed="true"
        >
          <div class="pulse-ring"></div>
          <svg class="mic-icon" viewBox="0 0 24 24" width="36" height="36" fill="currentColor" aria-hidden="true">
            <rect x="6" y="6" width="12" height="12" rx="2" />
          </svg>
          <span>Stop & Send Request</span>
        </button>
      {/if}

      <!-- Quick test simulation button for browser environments with no mic attached -->
      <div class="sim-actions">
        {#if recordingState === 'idle'}
          <button type="button" class="btn-link" on:click={processVoiceInput}>
            Simulate Voice Request (Georgia TTS Bridge)
          </button>
          <button type="button" class="btn-link error-sim" on:click={triggerSimulatedError}>
            Simulate Mic Permission Error
          </button>
        {/if}
      </div>
    </div>

    <!-- Error Display State -->
    {#if recordingState === 'error'}
      <div class="status-banner error" role="alert" id="mic-error-banner">
        <div class="banner-header">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" aria-hidden="true">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
          <strong>Microphone Error / Access Issue</strong>
        </div>
        <p class="error-detail">{errorMessage}</p>
        <button type="button" class="secondary-btn" on:click={resetVoiceState}>
          Try Again
        </button>
      </div>
    {/if}

    <!-- Proposed Slot Output View -->
    {#if recordingState === 'proposed' && currentProposal}
      <div class="proposal-card" role="region" aria-label="Voice Booking Proposal Details">
        <h3 class="proposal-title">Voice Booking Proposal Received</h3>
        <p class="proposal-text">{currentProposal.textResponse}</p>

        <div class="proposal-details">
          <div class="detail-item">
            <span class="detail-label">Booking ID:</span>
            <span class="detail-value">{currentProposal.bookingId}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Master ID:</span>
            <span class="detail-value">{currentProposal.masterId}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Service:</span>
            <span class="detail-value">{currentProposal.serviceName}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Proposed Time:</span>
            <span class="detail-value">{currentProposal.slotTime}</span>
          </div>
        </div>

        {#if currentProposal.audioBase64}
          <div class="audio-player-box">
            <label for="proposal-audio-player" class="audio-label">Georgian Voice Audio Proposal (🔊):</label>
            <!-- Audio element using data URI from base64 response -->
            <audio
              id="proposal-audio-player"
              controls
              src="data:{currentProposal.audioFormat || 'audio/wav'};base64,{currentProposal.audioBase64}"
              class="audio-element"
            >
              Your browser does not support the audio element.
            </audio>
          </div>
        {/if}

        <div class="action-buttons">
          <button type="button" class="primary-btn confirm-btn" on:click={confirmBooking}>
            Confirm Voice Booking (დადასტურება)
          </button>
          <button type="button" class="secondary-btn" on:click={resetVoiceState}>
            Cancel / Start Over
          </button>
        </div>
      </div>
    {/if}

    <!-- Confirmed State -->
    {#if recordingState === 'confirmed'}
      <div class="status-banner success" role="alert">
        <div class="banner-header">
          <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor" aria-hidden="true">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
          </svg>
          <strong>Booking Confirmed!</strong>
        </div>
        <p>{confirmationMessage}</p>
        <button type="button" class="secondary-btn" on:click={resetVoiceState}>
          Make Another Booking
        </button>
      </div>
    {/if}
  </div>
</section>

<style>
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

  .voice-card {
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

  .voice-header {
    border-bottom: 2px solid #0969da;
    padding-bottom: 12px;
    margin-bottom: 20px;
  }

  .voice-header h2 {
    margin: 0 0 4px 0;
    font-size: 1.5rem;
    color: #0969da;
  }

  .subtitle {
    margin: 0;
    font-size: 0.95rem;
    color: #424a53;
  }

  .inputs-row {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 20px;
  }

  .input-group {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .input-group label {
    font-weight: 600;
    font-size: 0.9rem;
    color: #1f2328;
  }

  .form-control {
    padding: 8px 12px;
    font-size: 0.95rem;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    background-color: #f6f8fa;
    color: #1f2328;
  }

  .form-control:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 1px;
    background-color: #ffffff;
  }

  .mic-stage {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 24px 16px;
    background-color: #f6f8fa;
    border: 2px dashed #d0d7de;
    border-radius: 8px;
    margin-bottom: 20px;
    text-align: center;
  }

  .mic-stage.recording {
    background-color: #fff8f8;
    border-color: #cf222e;
  }

  .mic-stage.proposed {
    background-color: #f0f7ff;
    border-color: #54a3ff;
  }

  .mic-status-badge {
    margin-bottom: 16px;
  }

  .badge {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 0.85rem;
    font-weight: 600;
  }

  .badge-idle {
    background-color: #eaeef2;
    color: #333333;
  }

  .badge-recording {
    background-color: #ffebe9;
    color: #cf222e;
    animation: pulse 1.5s infinite;
  }

  .badge-processing {
    background-color: #fff8c5;
    color: #7d4e00;
  }

  .badge-proposed {
    background-color: #ddf4ff;
    color: #0969da;
  }

  .badge-success {
    background-color: #dafbe1;
    color: #1a7f37;
  }

  .badge-error {
    background-color: #ffebe9;
    color: #cf222e;
  }

  @keyframes pulse {
    0% { opacity: 1; }
    50% { opacity: 0.6; }
    100% { opacity: 1; }
  }

  .mic-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 16px 24px;
    border-radius: 50px;
    border: none;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: transform 0.15s ease, background-color 0.15s ease;
  }

  .mic-btn-start {
    background-color: #0969da;
    color: #ffffff;
  }

  .mic-btn-start:hover {
    background-color: #0353e9;
    transform: scale(1.02);
  }

  .mic-btn-stop {
    background-color: #cf222e;
    color: #ffffff;
    position: relative;
  }

  .mic-btn-stop:hover {
    background-color: #a40e26;
  }

  .mic-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 4px;
  }

  .sim-actions {
    margin-top: 12px;
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .btn-link {
    background: none;
    border: none;
    color: #0969da;
    font-size: 0.85rem;
    text-decoration: underline;
    cursor: pointer;
    padding: 4px 8px;
  }

  .btn-link:hover {
    color: #0353e9;
  }

  .btn-link.error-sim {
    color: #cf222e;
  }

  .btn-link:focus-visible {
    outline: 2px solid #0969da;
    outline-offset: 2px;
    border-radius: 4px;
  }

  .status-banner {
    padding: 16px;
    border-radius: 6px;
    margin-bottom: 20px;
  }

  .status-banner.error {
    background-color: #ffebe9;
    border: 1px solid #cf222e;
    color: #7d0e1b;
  }

  .status-banner.success {
    background-color: #dafbe1;
    border: 1px solid #1a7f37;
    color: #0e4a1f;
  }

  .banner-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 1.05rem;
  }

  .error-detail {
    margin: 8px 0 12px 0;
    font-size: 0.95rem;
  }

  .proposal-card {
    background-color: #f0f7ff;
    border: 1px solid #54a3ff;
    border-radius: 6px;
    padding: 16px;
    margin-bottom: 20px;
  }

  .proposal-title {
    margin-top: 0;
    margin-bottom: 8px;
    font-size: 1.1rem;
    color: #0969da;
  }

  .proposal-text {
    font-size: 1rem;
    font-weight: 600;
    margin: 0 0 12px 0;
    color: #1f2328;
  }

  .proposal-details {
    background: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 12px;
    margin-bottom: 16px;
  }

  .detail-item {
    display: flex;
    justify-content: space-between;
    padding: 4px 0;
    font-size: 0.9rem;
  }

  .detail-label {
    color: #424a53;
  }

  .detail-value {
    font-weight: 600;
    color: #1f2328;
  }

  .audio-player-box {
    display: flex;
    flex-direction: column;
    gap: 6px;
    margin-bottom: 16px;
  }

  .audio-label {
    font-weight: 600;
    font-size: 0.9rem;
    color: #1f2328;
  }

  .audio-element {
    width: 100%;
    border-radius: 6px;
  }

  .action-buttons {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .primary-btn {
    flex: 1;
    background-color: #0969da;
    color: #ffffff;
    font-size: 0.95rem;
    font-weight: 600;
    padding: 10px 16px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
  }

  .primary-btn:hover {
    background-color: #0353e9;
  }

  .primary-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .secondary-btn {
    background-color: #ffffff;
    color: #1f2328;
    border: 1px solid #d0d7de;
    font-size: 0.95rem;
    font-weight: 600;
    padding: 10px 16px;
    border-radius: 6px;
    cursor: pointer;
  }

  .secondary-btn:hover {
    background-color: #f6f8fa;
  }

  .secondary-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }
</style>
