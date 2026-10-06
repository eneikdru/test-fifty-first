<script>
  import { createEventDispatcher } from 'svelte';

  const dispatch = createEventDispatcher();

  export let initialLocked = true;

  let currentStep = initialLocked ? 1 : 4; // 1: Identify, 2: Verification, 3: New Password, 4: Complete
  let identifier = '+995 555 123 456';
  let verificationCode = '';
  let newPassword = '';
  let confirmPassword = '';

  let isSubmitting = false;
  let errorMessage = '';
  let statusMessage = 'Account recovery step 1: Identify your locked account.';

  function handleIdentify(e) {
    e.preventDefault();
    if (!identifier.trim()) {
      errorMessage = 'Please enter your Georgian phone number (+995) or registered Facebook email.';
      return;
    }
    errorMessage = '';
    isSubmitting = true;

    setTimeout(() => {
      isSubmitting = false;
      currentStep = 2;
      statusMessage = `Recovery code sent to ${identifier}. Step 2: Enter verification code.`;
    }, 400);
  }

  function handleVerifyCode(e) {
    e.preventDefault();
    if (!verificationCode || verificationCode.trim().length < 6) {
      errorMessage = 'Please enter a valid 6-digit verification code.';
      return;
    }
    errorMessage = '';
    isSubmitting = true;

    setTimeout(() => {
      isSubmitting = false;
      currentStep = 3;
      statusMessage = 'Code verified successfully. Step 3: Set your new password.';
    }, 400);
  }

  function handleResetPassword(e) {
    e.preventDefault();
    if (!newPassword || newPassword.length < 8) {
      errorMessage = 'Password must be at least 8 characters long.';
      return;
    }
    if (newPassword !== confirmPassword) {
      errorMessage = 'Passwords do not match. Please check and try again.';
      return;
    }
    errorMessage = '';
    isSubmitting = true;

    setTimeout(() => {
      isSubmitting = false;
      currentStep = 4;
      statusMessage = 'Account unlocked successfully! Self-service recovery is complete.';
      dispatch('accountUnlocked', { identifier });
    }, 400);
  }

  function handleRestart() {
    currentStep = 1;
    identifier = '+995 555 123 456';
    verificationCode = '';
    newPassword = '';
    confirmPassword = '';
    errorMessage = '';
    statusMessage = 'Account recovery step 1: Identify your locked account.';
  }
</script>

<div class="recovery-card" aria-labelledby="recovery-heading">
  <header class="recovery-header">
    <div class="header-badge-wrapper">
      <span class="badge {currentStep === 4 ? 'badge-success' : 'badge-warning'}">
        {currentStep === 4 ? 'Account Unlocked' : 'Account Locked'}
      </span>
      <span class="step-indicator">Step {currentStep} of 4</span>
    </div>
    <h2 id="recovery-heading" class="recovery-title">Self-Service Account Recovery</h2>
    <p class="recovery-subtitle">
      Securely restore access to your master profile and booking schedule in Georgia.
    </p>
  </header>

  <!-- Progress Bar -->
  <div class="progress-bar-container" aria-label="Recovery Progress">
    <div
      class="progress-bar-fill"
      style="width: {(currentStep / 4) * 100}%"
      role="progressbar"
      aria-valuenow={(currentStep / 4) * 100}
      aria-valuemin="0"
      aria-valuemax="100"
    ></div>
  </div>

  <!-- Screen reader live region -->
  <div class="sr-only" aria-live="polite" role="status">
    {statusMessage}
  </div>

  <!-- Error alert -->
  {#if errorMessage}
    <div class="alert alert-error" role="alert" tabIndex="-1">
      <svg class="alert-icon" viewBox="0 0 20 20" fill="currentColor" width="20" height="20" aria-hidden="true">
        <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
      </svg>
      <span>{errorMessage}</span>
    </div>
  {/if}

  <!-- Step 1: Identify Account -->
  {#if currentStep === 1}
    <form on:submit={handleIdentify} class="recovery-form" novalidate>
      <div class="form-group">
        <label for="account-identifier" class="form-label">
          Georgian Phone Number or Facebook Email <span class="required">*</span>
        </label>
        <input
          id="account-identifier"
          type="text"
          class="form-input"
          bind:value={identifier}
          placeholder="+995 5xx xxx xxx or name@example.ge"
          required
          aria-describedby="identifier-help"
        />
        <p id="identifier-help" class="form-help">
          Enter the Georgian mobile phone (+995) or Facebook email linked to your account.
        </p>
      </div>

      <div class="form-actions">
        <button
          type="submit"
          class="btn btn-primary"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Sending Code...' : 'Send Recovery Code'}
        </button>
      </div>
    </form>
  {/if}

  <!-- Step 2: Verification Code -->
  {#if currentStep === 2}
    <form on:submit={handleVerifyCode} class="recovery-form" novalidate>
      <div class="form-group">
        <p class="step-desc">
          A 6-digit security code has been sent to <strong>{identifier}</strong> via SMS / Facebook Messenger.
        </p>
        <label for="verification-code" class="form-label">
          6-Digit Verification Code <span class="required">*</span>
        </label>
        <input
          id="verification-code"
          type="text"
          inputmode="numeric"
          pattern="[0-9]*"
          maxlength="6"
          class="form-input code-input"
          bind:value={verificationCode}
          placeholder="123456"
          required
          autocomplete="one-time-code"
          aria-describedby="code-help"
        />
        <p id="code-help" class="form-help">
          Check your mobile SMS or Messenger notification.
        </p>
      </div>

      <div class="form-actions">
        <button
          type="button"
          class="btn btn-secondary"
          on:click={() => (currentStep = 1)}
          disabled={isSubmitting}
        >
          Back
        </button>
        <button
          type="submit"
          class="btn btn-primary"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Verifying...' : 'Verify Code'}
        </button>
      </div>
    </form>
  {/if}

  <!-- Step 3: New Password -->
  {#if currentStep === 3}
    <form on:submit={handleResetPassword} class="recovery-form" novalidate>
      <div class="form-group">
        <label for="new-password" class="form-label">
          New Password <span class="required">*</span>
        </label>
        <input
          id="new-password"
          type="password"
          class="form-input"
          bind:value={newPassword}
          required
          aria-describedby="password-help"
        />
        <p id="password-help" class="form-help">
          Must be at least 8 characters long.
        </p>
      </div>

      <div class="form-group">
        <label for="confirm-password" class="form-label">
          Confirm New Password <span class="required">*</span>
        </label>
        <input
          id="confirm-password"
          type="password"
          class="form-input"
          bind:value={confirmPassword}
          required
        />
      </div>

      <div class="form-actions">
        <button
          type="button"
          class="btn btn-secondary"
          on:click={() => (currentStep = 2)}
          disabled={isSubmitting}
        >
          Back
        </button>
        <button
          type="submit"
          class="btn btn-primary"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Updating...' : 'Unlock Account & Set Password'}
        </button>
      </div>
    </form>
  {/if}

  <!-- Step 4: Complete -->
  {#if currentStep === 4}
    <div class="success-state">
      <div class="success-icon-wrapper">
        <svg class="success-icon" viewBox="0 0 24 24" fill="currentColor" width="48" height="48" aria-hidden="true">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
        </svg>
      </div>
      <h3 class="success-title">Account Successfully Unlocked</h3>
      <p class="success-text">
        Your account access for <strong>{identifier}</strong> has been restored. You can now access your master profile, schedule appointments, and control privacy preferences.
      </p>
      <div class="success-actions">
        <button type="button" class="btn btn-primary" on:click={handleRestart}>
          Return to Profile Overview
        </button>
      </div>
    </div>
  {/if}
</div>

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

  .recovery-card {
    background-color: #ffffff;
    color: #0f172a;
    border-radius: 12px;
    border: 1px solid #cbd5e1;
    padding: 1.75rem;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    max-width: 580px;
    margin: 0 auto;
  }

  .recovery-header {
    margin-bottom: 1.25rem;
  }

  .header-badge-wrapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0.75rem;
  }

  .badge {
    font-size: 0.75rem;
    font-weight: 700;
    padding: 0.25rem 0.625rem;
    border-radius: 9999px;
    text-transform: uppercase;
    letter-spacing: 0.025em;
  }

  .badge-warning {
    background-color: #fef3c7;
    color: #92400e;
  }

  .badge-success {
    background-color: #dcfce7;
    color: #14532d;
  }

  .step-indicator {
    font-size: 0.8125rem;
    font-weight: 600;
    color: #475569;
  }

  .recovery-title {
    font-size: 1.375rem;
    font-weight: 700;
    margin: 0 0 0.375rem 0;
    color: #0f172a;
  }

  .recovery-subtitle {
    font-size: 0.9375rem;
    color: #334155;
    margin: 0;
    line-height: 1.45;
  }

  .progress-bar-container {
    height: 6px;
    background-color: #e2e8f0;
    border-radius: 3px;
    overflow: hidden;
    margin-bottom: 1.5rem;
  }

  .progress-bar-fill {
    height: 100%;
    background-color: #1d4ed8;
    transition: width 0.3s ease;
  }

  .alert {
    padding: 0.875rem 1rem;
    border-radius: 6px;
    font-size: 0.875rem;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 0.625rem;
    margin-bottom: 1.25rem;
  }

  .alert-error {
    background-color: #fef2f2;
    color: #b91c1c;
    border: 1px solid #fca5a5;
  }

  .alert-icon {
    flex-shrink: 0;
  }

  .recovery-form {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.375rem;
  }

  .form-label {
    font-size: 0.875rem;
    font-weight: 600;
    color: #0f172a;
  }

  .required {
    color: #b91c1c;
  }

  .form-input {
    font-family: inherit;
    font-size: 0.9375rem;
    padding: 0.625rem 0.875rem;
    border: 1px solid #cbd5e1;
    border-radius: 6px;
    color: #0f172a;
    background-color: #ffffff;
    transition: border-color 0.15s ease;
  }

  .form-input:focus-visible {
    outline: 3px solid #2563eb !important;
    outline-offset: 2px !important;
    border-color: #2563eb;
  }

  .code-input {
    letter-spacing: 0.25em;
    font-weight: 700;
    font-size: 1.125rem;
    text-align: center;
  }

  .form-help {
    font-size: 0.8125rem;
    color: #475569;
    margin: 0;
  }

  .step-desc {
    font-size: 0.9375rem;
    color: #334155;
    margin: 0 0 0.5rem 0;
    line-height: 1.45;
  }

  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 0.75rem;
    margin-top: 0.5rem;
  }

  .btn {
    font-family: inherit;
    font-size: 0.875rem;
    font-weight: 600;
    padding: 0.625rem 1.25rem;
    border-radius: 6px;
    border: 1px solid transparent;
    cursor: pointer;
    transition: background-color 0.15s ease;
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

  .success-state {
    text-align: center;
    padding: 1.5rem 0 0.5rem 0;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .success-icon-wrapper {
    color: #15803d;
    margin-bottom: 0.75rem;
  }

  .success-title {
    font-size: 1.25rem;
    font-weight: 700;
    color: #0f172a;
    margin: 0 0 0.5rem 0;
  }

  .success-text {
    font-size: 0.9375rem;
    color: #334155;
    margin: 0 0 1.5rem 0;
    line-height: 1.5;
    max-width: 460px;
  }

  .success-actions {
    display: flex;
    justify-content: center;
  }
</style>
