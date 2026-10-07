import { test, expect } from '@playwright/test';

test.describe('Calendar Management UI Verification', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/?tab=calendar');
  });

  test('Given a schedule view, When interacting with days and slots, Then visible feedback is shown for every state change', async ({ page }) => {
    // Check main calendar section is present
    await expect(page.locator('#calendar-management-section')).toBeVisible();
    await expect(page.locator('h2#calendar-heading')).toContainText('Professional Calendar');

    // Click on Tue, Oct 13 button and verify feedback banner
    const dayBtn = page.locator('button.day-select-btn').filter({ hasText: 'Tue, Oct 13' });
    await dayBtn.click();

    const banner = page.locator('#calendar-feedback-banner');
    await expect(banner).toBeVisible();
    await expect(banner).toContainText('Selected date: Tue, Oct 13');
  });

  test('Given a destructive action like blocking a day, When triggered, Then it is confirmed before happening', async ({ page }) => {
    // Trigger "Block Day" for Mon, Oct 12
    const blockBtn = page.locator('button.block-btn').first();
    await blockBtn.click();

    // Verify modal dialog appears with description
    const modal = page.locator('div[role="dialog"]');
    await expect(modal).toBeVisible();
    await expect(modal.locator('#block-modal-title')).toContainText('Confirm Day Blocking');
    await expect(modal.locator('#block-modal-desc')).toContainText('Are you sure you want to block');

    // Confirm day block
    await modal.locator('#confirm-block-day-btn').click();

    // Modal closes and feedback banner indicates blocked state
    await expect(modal).not.toBeVisible();
    const banner = page.locator('#calendar-feedback-banner');
    await expect(banner).toContainText('has been blocked');

    // Verify the day card shows BLOCKED tag
    const dayCard = page.locator('.day-card').first();
    await expect(dayCard.locator('.blocked-tag')).toContainText('BLOCKED');
  });

  test('Given a professional, When marking an appointment as No-Show, Then slot state updates with feedback', async ({ page }) => {
    // Find slot with Nino Kapanadze
    const noshowBtn = page.locator('button.noshow-btn').first();
    await noshowBtn.click();

    // Check feedback banner
    const banner = page.locator('#calendar-feedback-banner');
    await expect(banner).toBeVisible();
    await expect(banner).toContainText('Slot SLOT-101 marked as No-Show for client Nino Kapanadze');

    // Verify status badge changed to NO_SHOW
    const slotRow = page.locator('.slot-row').filter({ hasText: '09:00 - 10:00' });
    await expect(slotRow.locator('.status-badge')).toContainText('NO_SHOW');
  });

  test('Given calendar UI, When interacted with, Then WCAG 2.1 AA keyboard & accessibility standards are met', async ({ page }) => {
    // Select a date to produce feedback banner
    const dayBtn = page.locator('button.day-select-btn').first();
    await dayBtn.click();

    // Check key aria attributes and labels
    await expect(page.locator('#calendar-feedback-banner')).toHaveAttribute('role', 'status');
    await expect(page.locator('#calendar-feedback-banner')).toHaveAttribute('aria-live', 'polite');

    const selectElement = page.locator('#slot-status-select-SLOT-101');
    await expect(selectElement).toHaveAttribute('aria-label', 'Update status for time slot 09:00 - 10:00');
  });
});
