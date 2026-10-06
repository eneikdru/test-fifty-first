import { describe, it, expect, beforeEach } from 'vitest';
import { render, fireEvent, screen } from '@testing-library/svelte';
import PrivacyConsentBanner from '../components/PrivacyConsentBanner.svelte';
import AccountRecovery from '../components/AccountRecovery.svelte';

describe('PrivacyConsentBanner', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('renders consent banner when preferences are undecided', () => {
    const { getByText, getByRole } = render(PrivacyConsentBanner);
    expect(getByText(/Cookie Consent & Privacy Choices/i)).toBeTruthy();
    expect(getByRole('button', { name: /Accept All/i })).toBeTruthy();
    expect(getByRole('button', { name: /Essential Only/i })).toBeTruthy();
  });

  it('stores accept all preference in localStorage upon accepting', async () => {
    const { getByRole } = render(PrivacyConsentBanner, { storageKey: 'test_consent' });
    const acceptBtn = getByRole('button', { name: /Accept All/i });
    await fireEvent.click(acceptBtn);

    const saved = JSON.parse(localStorage.getItem('test_consent'));
    expect(saved).toBeTruthy();
    expect(saved.essential).toBe(true);
    expect(saved.analytics).toBe(true);
    expect(saved.marketing).toBe(true);
    expect(saved.decided).toBe(true);
  });

  it('stores essential only preference when rejecting non-essential', async () => {
    const { getByRole } = render(PrivacyConsentBanner, { storageKey: 'test_consent' });
    const essentialBtn = getByRole('button', { name: /Essential Only/i });
    await fireEvent.click(essentialBtn);

    const saved = JSON.parse(localStorage.getItem('test_consent'));
    expect(saved).toBeTruthy();
    expect(saved.essential).toBe(true);
    expect(saved.analytics).toBe(false);
    expect(saved.marketing).toBe(false);
    expect(saved.decided).toBe(true);
  });
});

describe('AccountRecovery Self-Service Flow', () => {
  it('navigates through recovery steps for locked accounts', async () => {
    const { getByText, getByLabelText, getByRole } = render(AccountRecovery);

    // Step 1: Identify
    expect(getByText(/Self-Service Account Recovery/i)).toBeTruthy();
    expect(getByText(/Step 1 of 4/i)).toBeTruthy();

    const input = getByLabelText(/Georgian Phone Number or Facebook Email/i);
    await fireEvent.input(input, { target: { value: '+995 599 112 233' } });

    const submitBtn = getByRole('button', { name: /Send Recovery Code/i });
    await fireEvent.click(submitBtn);

    // Wait for step transition
    await new Promise((r) => setTimeout(r, 500));

    // Step 2: Verification
    expect(getByText(/Step 2 of 4/i)).toBeTruthy();
    const codeInput = getByLabelText(/6-Digit Verification Code/i);
    await fireEvent.input(codeInput, { target: { value: '654321' } });

    const verifyBtn = getByRole('button', { name: /Verify Code/i });
    await fireEvent.click(verifyBtn);

    // Wait for step transition
    await new Promise((r) => setTimeout(r, 500));

    // Step 3: Password reset
    expect(getByText(/Step 3 of 4/i)).toBeTruthy();
    const passInput = getByLabelText(/^New Password/i);
    const confirmInput = getByLabelText(/^Confirm New Password/i);

    await fireEvent.input(passInput, { target: { value: 'SecurePass123!' } });
    await fireEvent.input(confirmInput, { target: { value: 'SecurePass123!' } });

    const unlockBtn = getByRole('button', { name: /Unlock Account & Set Password/i });
    await fireEvent.click(unlockBtn);

    // Wait for step transition
    await new Promise((r) => setTimeout(r, 500));

    // Step 4: Complete
    expect(getByText(/Account Successfully Unlocked/i)).toBeTruthy();
  });
});
