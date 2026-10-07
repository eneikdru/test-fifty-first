<script>
  import { onMount } from 'svelte';

  const STORAGE_KEY = 'georgian_master_profile_draft_v1';

  // State variables for Master Profile
  let masterName = 'Giorgi Beridze';
  let phone = '+995 599 12 34 56';
  let city = 'Tbilisi';
  let bio = 'Certified Plumbing & HVAC Master with 8+ years experience in Tbilisi, Batumi, and Kutaisi.';
  let address = 'Rustaveli Ave 24, Tbilisi';
  let avatarUrl = 'https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=150&auto=format&fit=crop&q=80';
  let publicSlug = 'giorgi-beridze';
  let isRegistered = true;
  let authMode = 'fb'; // 'fb' | 'phone'

  // Service list items
  let services = [
    { id: 'srv-1', title: 'Plumbing Emergency Repair', priceGEL: 85, durationMin: 60 },
    { id: 'srv-2', title: 'Faucet & Sink Installation', priceGEL: 50, durationMin: 45 },
    { id: 'srv-3', title: 'Central Heating Inspection', priceGEL: 120, durationMin: 90 },
    { id: 'srv-4', title: 'Pipe Leak Diagnostics', priceGEL: 65, durationMin: 30 }
  ];

  let newServiceTitle = '';
  let newServicePrice = 60;
  let newServiceDuration = 45;

  // Calendar availability slots
  let selectedDate = '2026-10-10';
  let slots = [
    { id: 'slot-1', time: '09:00', status: 'available' },
    { id: 'slot-2', time: '11:30', status: 'booked' },
    { id: 'slot-3', time: '14:00', status: 'available' },
    { id: 'slot-4', time: '16:30', status: 'available' }
  ];
  let selectedSlotId = 'slot-3';

  // Network & Persistence State
  let saveStatus = 'idle'; // 'idle' | 'saving' | 'saved' | 'error'
  let statusMessage = 'Profile loaded. Any changes will auto-save locally.';
  let copyLinkMessage = '';
  let isImportingFB = false;

  onMount(() => {
    restoreDraft();
  });

  function saveDraft() {
    try {
      const draft = {
        masterName,
        phone,
        city,
        bio,
        address,
        avatarUrl,
        publicSlug,
        services,
        isRegistered
      };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(draft));
      statusMessage = 'Draft changes saved locally.';
    } catch (err) {
      console.warn('Unable to save draft to localStorage:', err);
    }
  }

  function restoreDraft() {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        if (parsed.masterName !== undefined) masterName = parsed.masterName;
        if (parsed.phone !== undefined) phone = parsed.phone;
        if (parsed.city !== undefined) city = parsed.city;
        if (parsed.bio !== undefined) bio = parsed.bio;
        if (parsed.address !== undefined) address = parsed.address;
        if (parsed.avatarUrl !== undefined) avatarUrl = parsed.avatarUrl;
        if (parsed.publicSlug !== undefined) publicSlug = parsed.publicSlug;
        if (Array.isArray(parsed.services) && parsed.services.length > 0) services = parsed.services;
        if (parsed.isRegistered !== undefined) isRegistered = parsed.isRegistered;
      }
    } catch (err) {
      console.warn('Unable to restore draft from localStorage:', err);
    }
  }

  function handleInputChange() {
    saveDraft();
  }

  function handleFacebookImport() {
    isImportingFB = true;
    statusMessage = 'Importing details from Facebook Business Page...';

    setTimeout(() => {
      masterName = 'Giorgi Beridze (Pro Master)';
      bio = 'Official FB Business Page: Top-rated Plumbing & Handyman Services in Tbilisi, Batumi, Kutaisi. 24/7 Availability!';
      address = 'Chavchavadze Ave 12, Tbilisi';
      city = 'Tbilisi';
      phone = '+995 599 12 34 56';
      isImportingFB = false;
      saveStatus = 'saved';
      statusMessage = '✓ Successfully imported profile, photo, and address from Facebook Business Page!';
      saveDraft();
    }, 600);
  }

  function handleFacebookOneClickRegister() {
    authMode = 'fb';
    isRegistered = true;
    masterName = 'Giorgi Beridze';
    statusMessage = '✓ Registered in 1 click via Facebook!';
    saveDraft();
  }

  function handlePhoneRegister(event) {
    event.preventDefault();
    authMode = 'phone';
    isRegistered = true;
    statusMessage = `✓ Registered with Georgian phone number ${phone}`;
    saveDraft();
  }

  function handleAddService(event) {
    event.preventDefault();
    if (!newServiceTitle.trim()) return;

    const newSrv = {
      id: `srv-${Date.now()}`,
      title: newServiceTitle.trim(),
      priceGEL: Number(newServicePrice) || 50,
      durationMin: Number(newServiceDuration) || 30
    };

    services = [...services, newSrv];
    newServiceTitle = '';
    statusMessage = `✓ Added service: ${newSrv.title} (${newSrv.priceGEL} ₾)`;
    saveDraft();
  }

  function handleRemoveService(srvId) {
    services = services.filter(s => s.id !== srvId);
    statusMessage = 'Service removed from profile list.';
    saveDraft();
  }

  function handleSaveProfile(event) {
    event.preventDefault();
    saveStatus = 'saving';
    statusMessage = 'Saving profile to remote server...';

    // Simulate save request
    setTimeout(() => {
      saveStatus = 'saved';
      statusMessage = '✓ Profile saved successfully on server!';
      saveDraft();
    }, 500);
  }

  function triggerSimulatedNetworkError() {
    saveStatus = 'saving';
    statusMessage = 'Connecting to profile API endpoint...';

    setTimeout(() => {
      saveStatus = 'error';
      statusMessage = 'Network Error: Failed to reach server. Your typed input survives safely in local storage!';
      // Notice: typed input values (masterName, bio, phone, address, etc.) ARE NOT WIPED OR RESET!
    }, 500);
  }

  function copyPublicProfileLink() {
    const publicUrl = `https://masters.ge/profile/${publicSlug}`;
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(publicUrl);
    }
    copyLinkMessage = 'Public link copied to clipboard!';
    setTimeout(() => copyLinkMessage = '', 3000);
  }

  function selectSlot(slotId) {
    if (slots.find(s => s.id === slotId)?.status === 'booked') return;
    selectedSlotId = slotId;
    saveDraft();
  }
</script>

<section class="profile-card" id="master-profile-section" aria-labelledby="profile-heading">
  <header class="profile-header">
    <div class="header-main">
      <h2 id="profile-heading">Mobile-First Master Profile</h2>
      <p class="subtitle">Manage professional profile, services, and calendar availability</p>
    </div>

    <div class="public-link-box">
      <span class="public-url-label">Public Link:</span>
      <code class="public-url">https://masters.ge/profile/{publicSlug}</code>
      <button
        type="button"
        class="secondary-btn btn-sm"
        on:click={copyPublicProfileLink}
        aria-label="Copy public profile link"
      >
        Copy Link 🔗
      </button>
      {#if copyLinkMessage}
        <span class="copy-toast" role="status">{copyLinkMessage}</span>
      {/if}
    </div>
  </header>

  <!-- Live ARIA status region for screen reader accessibility -->
  <div class="sr-only" role="status" aria-live="polite">
    {statusMessage}
  </div>

  <!-- Registration / 1-Click Auth Banner -->
  <div class="auth-box">
    <h3 class="box-title">1-Click Registration & Auth</h3>
    <div class="auth-actions">
      <button
        type="button"
        class="fb-btn"
        on:click={handleFacebookOneClickRegister}
        aria-label="Login or Register with Facebook in 1 click"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
          <path d="M22 12c0-5.52-4.48-10-10-10S2 6.48 2 12c0 4.99 3.66 9.12 8.44 9.88v-6.99H7.9v-2.89h2.54V9.8c0-2.51 1.49-3.89 3.78-3.89 1.09 0 2.23.19 2.23.19v2.47h-1.26c-1.24 0-1.63.77-1.63 1.56v1.88h2.78l-.44 2.89h-2.34v6.99C18.34 21.12 22 16.99 22 12z"/>
        </svg>
        Login with Facebook
      </button>

      <button
        type="button"
        class="secondary-btn fb-import-btn"
        on:click={handleFacebookImport}
        disabled={isImportingFB}
      >
        {#if isImportingFB}
          Importing FB Page...
        {:else}
          Import Photo, Description & Address from FB Page
        {/if}
      </button>
    </div>
  </div>

  <!-- Main Profile Details Form -->
  <form on:submit={handleSaveProfile} class="profile-form">
    <h3 class="box-title">Master Information</h3>

    <div class="form-grid">
      <div class="input-group">
        <label for="master-name-input">Full Name / Business Title</label>
        <input
          id="master-name-input"
          type="text"
          bind:value={masterName}
          on:input={handleInputChange}
          class="form-control"
          placeholder="e.g. Giorgi Beridze"
          required
        />
      </div>

      <div class="input-group">
        <label for="master-phone-input">Georgian Phone (+995)</label>
        <input
          id="master-phone-input"
          type="tel"
          bind:value={phone}
          on:input={handleInputChange}
          class="form-control"
          placeholder="+995 599 12 34 56"
          required
        />
      </div>

      <div class="input-group">
        <label for="master-city-select">City / Region (Georgia)</label>
        <select
          id="master-city-select"
          bind:value={city}
          on:change={handleInputChange}
          class="form-control"
        >
          <option value="Tbilisi">Tbilisi (Тбилиси)</option>
          <option value="Batumi">Batumi (Батуми)</option>
          <option value="Kutaisi">Kutaisi (Кутаиси)</option>
          <option value="Rustavi">Rustavi</option>
        </select>
      </div>

      <div class="input-group">
        <label for="master-address-input">Service Address / Base Area</label>
        <input
          id="master-address-input"
          type="text"
          bind:value={address}
          on:input={handleInputChange}
          class="form-control"
          placeholder="e.g. Rustaveli Ave 24, Tbilisi"
        />
      </div>
    </div>

    <div class="input-group full-width">
      <label for="master-bio-input">Description & Qualifications</label>
      <textarea
        id="master-bio-input"
        bind:value={bio}
        on:input={handleInputChange}
        class="form-control textarea"
        rows="3"
        placeholder="Describe your master services, experience, and pricing policy..."
      ></textarea>
    </div>

    <div class="action-bar">
      <button type="submit" class="primary-btn">
        Save Profile
      </button>
      <button
        type="button"
        class="secondary-btn error-sim-btn"
        on:click={triggerSimulatedNetworkError}
      >
        Simulate Network Error
      </button>
    </div>
  </form>

  <!-- Save / Error Feedback Status Banner -->
  {#if saveStatus === 'error'}
    <div class="status-banner error" role="alert" id="network-error-banner">
      <div class="banner-header">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" aria-hidden="true">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
        </svg>
        <strong>Network Error Occurred</strong>
      </div>
      <p class="status-detail">{statusMessage}</p>
      <p class="survival-note">✓ What you typed is preserved in your browser! Edit or retry saving whenever ready.</p>
    </div>
  {:else if saveStatus === 'saved'}
    <div class="status-banner success" role="alert">
      <strong>{statusMessage}</strong>
    </div>
  {/if}

  <!-- Services & Prices List in GEL (₾) -->
  <fieldset class="services-fieldset">
    <legend class="box-title">Services & Prices in GEL (₾)</legend>

    <ul class="services-list" aria-label="Services List">
      {#each services as srv (srv.id)}
        <li class="service-item">
          <div class="srv-info">
            <span class="srv-title">{srv.title}</span>
            <div class="srv-meta">
              <span class="srv-price">{srv.priceGEL} ₾ (GEL)</span>
              <span class="srv-duration">⏱️ {srv.durationMin} min</span>
            </div>
          </div>
          <button
            type="button"
            class="danger-btn btn-sm"
            on:click={() => handleRemoveService(srv.id)}
            aria-label={`Remove service ${srv.title}`}
          >
            Remove
          </button>
        </li>
      {/each}
    </ul>

    <!-- Add Service Inline Form -->
    <form on:submit={handleAddService} class="add-service-form">
      <div class="add-srv-inputs">
        <div class="input-group">
          <label for="new-srv-title">New Service Title</label>
          <input
            id="new-srv-title"
            type="text"
            bind:value={newServiceTitle}
            class="form-control"
            placeholder="e.g. Washing Machine Repair"
          />
        </div>

        <div class="input-group short">
          <label for="new-srv-price">Price (GEL / ₾)</label>
          <input
            id="new-srv-price"
            type="number"
            min="1"
            bind:value={newServicePrice}
            class="form-control"
          />
        </div>

        <div class="input-group short">
          <label for="new-srv-duration">Duration (min)</label>
          <input
            id="new-srv-duration"
            type="number"
            min="5"
            step="5"
            bind:value={newServiceDuration}
            class="form-control"
          />
        </div>
      </div>

      <button type="submit" class="secondary-btn add-btn">
        + Add Service
      </button>
    </form>
  </fieldset>

  <!-- Availability Calendar & Slot Picker -->
  <div class="calendar-box">
    <h3 class="box-title">Availability Calendar & Free Slots</h3>

    <div class="date-selector">
      <label for="calendar-date-input" class="date-label">Select Date:</label>
      <input
        id="calendar-date-input"
        type="date"
        bind:value={selectedDate}
        class="form-control date-input"
      />
    </div>

    <div class="slots-grid" role="radiogroup" aria-label="Available Time Slots">
      {#each slots as slot (slot.id)}
        <button
          type="button"
          class="slot-btn {slot.status} {selectedSlotId === slot.id ? 'selected' : ''}"
          disabled={slot.status === 'booked'}
          on:click={() => selectSlot(slot.id)}
          aria-checked={selectedSlotId === slot.id}
          role="radio"
        >
          <span class="slot-time">{slot.time}</span>
          <span class="slot-badge">
            {#if slot.status === 'booked'}
              Booked (Занято)
            {:else if selectedSlotId === slot.id}
              Selected
            {:else}
              Free (Свободно)
            {/if}
          </span>
        </button>
      {/each}
    </div>
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

  .profile-card {
    background: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 8px;
    padding: 20px;
    max-width: 640px;
    margin: 0 auto 32px auto;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    color: #1f2328;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    box-sizing: border-box;
  }

  .profile-header {
    border-bottom: 2px solid #0969da;
    padding-bottom: 14px;
    margin-bottom: 20px;
  }

  .profile-header h2 {
    margin: 0 0 4px 0;
    font-size: 1.4rem;
    color: #0969da;
  }

  .subtitle {
    margin: 0 0 12px 0;
    font-size: 0.9rem;
    color: #424a53;
  }

  .public-link-box {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
    background-color: #f6f8fa;
    padding: 8px 12px;
    border-radius: 6px;
    border: 1px solid #d0d7de;
  }

  .public-url-label {
    font-size: 0.85rem;
    font-weight: 600;
    color: #1f2328;
  }

  .public-url {
    font-family: monospace;
    font-size: 0.85rem;
    color: #0969da;
    background: #ffffff;
    padding: 2px 6px;
    border-radius: 4px;
    border: 1px solid #d0d7de;
  }

  .copy-toast {
    font-size: 0.8rem;
    color: #1a7f37;
    font-weight: 600;
  }

  .auth-box {
    background-color: #f0f7ff;
    border: 1px solid #54a3ff;
    border-radius: 6px;
    padding: 16px;
    margin-bottom: 20px;
  }

  .box-title {
    margin: 0 0 12px 0;
    font-size: 1.05rem;
    color: #0969da;
    font-weight: 600;
  }

  .auth-actions {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  @media (min-width: 480px) {
    .auth-actions {
      flex-direction: row;
    }
  }

  .fb-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background-color: #1877f2;
    color: #ffffff;
    font-weight: 600;
    font-size: 0.9rem;
    padding: 10px 16px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.15s ease;
  }

  .fb-btn:hover {
    background-color: #1565c0;
  }

  .fb-btn:focus-visible {
    outline: 3px solid #1877f2;
    outline-offset: 2px;
  }

  .fb-import-btn {
    flex: 1;
  }

  .profile-form {
    background-color: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 16px;
    margin-bottom: 20px;
  }

  .form-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 12px;
    margin-bottom: 12px;
  }

  @media (min-width: 540px) {
    .form-grid {
      grid-template-columns: 1fr 1fr;
    }
  }

  .input-group {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .input-group.full-width {
    margin-bottom: 16px;
  }

  .input-group label {
    font-weight: 600;
    font-size: 0.85rem;
    color: #1f2328;
  }

  .form-control {
    padding: 8px 10px;
    font-size: 0.9rem;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    background-color: #ffffff;
    color: #1f2328;
    box-sizing: border-box;
    width: 100%;
  }

  .form-control:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 1px;
    border-color: #0969da;
  }

  .textarea {
    resize: vertical;
    font-family: inherit;
  }

  .action-bar {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .primary-btn {
    background-color: #0969da;
    color: #ffffff;
    font-size: 0.95rem;
    font-weight: 600;
    padding: 10px 18px;
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
    outline-offset: 2px;
  }

  .secondary-btn {
    background-color: #ffffff;
    color: #1f2328;
    border: 1px solid #d0d7de;
    font-size: 0.9rem;
    font-weight: 600;
    padding: 8px 14px;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.15s ease;
  }

  .secondary-btn:hover {
    background-color: #f6f8fa;
  }

  .secondary-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .secondary-btn.btn-sm {
    padding: 4px 8px;
    font-size: 0.8rem;
  }

  .error-sim-btn {
    color: #cf222e;
    border-color: #ff8182;
  }

  .error-sim-btn:hover {
    background-color: #ffebe9;
  }

  .status-banner {
    padding: 14px;
    border-radius: 6px;
    margin-bottom: 20px;
    font-size: 0.9rem;
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
    gap: 6px;
    font-size: 1rem;
  }

  .status-detail {
    margin: 6px 0 4px 0;
  }

  .survival-note {
    margin: 4px 0 0 0;
    font-weight: 600;
    color: #0e4a1f;
  }

  .services-fieldset {
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 16px;
    margin: 0 0 20px 0;
    background-color: #ffffff;
  }

  .services-list {
    list-style: none;
    padding: 0;
    margin: 0 0 16px 0;
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .service-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    border: 1px solid #e1e4e8;
    border-radius: 6px;
    background-color: #f6f8fa;
  }

  .srv-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .srv-title {
    font-weight: 600;
    font-size: 0.95rem;
    color: #1f2328;
  }

  .srv-meta {
    display: flex;
    gap: 12px;
    font-size: 0.85rem;
  }

  .srv-price {
    color: #0969da;
    font-weight: 600;
  }

  .srv-duration {
    color: #424a53;
  }

  .danger-btn {
    background-color: #ffffff;
    color: #cf222e;
    border: 1px solid #ff8182;
    border-radius: 6px;
    padding: 4px 8px;
    font-size: 0.8rem;
    font-weight: 600;
    cursor: pointer;
  }

  .danger-btn:hover {
    background-color: #ffebe9;
  }

  .danger-btn:focus-visible {
    outline: 3px solid #cf222e;
    outline-offset: 2px;
  }

  .add-service-form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    background-color: #f6f8fa;
    padding: 12px;
    border-radius: 6px;
    border: 1px solid #e1e4e8;
  }

  .add-srv-inputs {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  @media (min-width: 480px) {
    .add-srv-inputs {
      flex-direction: row;
    }
    .input-group.short {
      width: 110px;
    }
  }

  .add-btn {
    align-self: flex-start;
  }

  .calendar-box {
    background-color: #ffffff;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    padding: 16px;
  }

  .date-selector {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 14px;
  }

  .date-label {
    font-weight: 600;
    font-size: 0.9rem;
    color: #1f2328;
  }

  .date-input {
    width: auto;
  }

  .slots-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 10px;
  }

  .slot-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 10px 8px;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    background-color: #ffffff;
    cursor: pointer;
    transition: all 0.15s ease;
  }

  .slot-btn:hover:not(:disabled) {
    border-color: #0969da;
    background-color: #f0f7ff;
  }

  .slot-btn.selected {
    border-color: #0969da;
    background-color: #0969da;
    color: #ffffff;
  }

  .slot-btn:disabled {
    background-color: #f6f8fa;
    border-color: #e1e4e8;
    color: #8c959f;
    cursor: not-allowed;
    opacity: 0.7;
  }

  .slot-btn:focus-visible {
    outline: 3px solid #0969da;
    outline-offset: 2px;
  }

  .slot-time {
    font-weight: 600;
    font-size: 0.95rem;
  }

  .slot-btn.selected .slot-time {
    color: #ffffff;
  }

  .slot-badge {
    font-size: 0.75rem;
    margin-top: 4px;
  }

  .slot-btn.selected .slot-badge {
    color: #ffffff;
  }
</style>
