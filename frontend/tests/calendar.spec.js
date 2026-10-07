import { test, expect } from '@playwright/test';

test.describe('Professional Calendar Management UI', () => {

  test('Given calendar UI, when accessed, then slots and controls rendered properly', async ({ page }) => {
    await page.goto('/?tab=calendar');

    await expect(page.locator('#calendar-heading')).toContainText('Professional Calendar');
    await expect(page.locator('button:has-text("Unblock Entire Day"), button:has-text("Block Entire Day")')).toBeVisible();
    await expect(page.locator('input#calendar-date-input')).toBeVisible();
  });

  test('Given destructive block day action, when triggered, then requires confirmation modal before updating state', async ({ page }) => {
    await page.goto('/?tab=calendar');

    // Make sure date is set to 2026-10-12
    await page.fill('input#calendar-date-input', '2026-10-12');

    // Click Block Entire Day button
    const blockBtn = page.locator('button:has-text("Block Entire Day")');
    await blockBtn.click();

    // Verify modal is shown before blocking
    const modal = page.locator('.modal-card');
    await expect(modal).toBeVisible();
    await expect(modal).toContainText('Block Entire Day?');

    // Confirm block action
    const confirmBtn = page.locator('#proceed-confirm-btn');
    await confirmBtn.click();

    // Verify feedback message and badge update
    await expect(page.locator('#calendar-feedback-banner')).toContainText('BLOCKED');
    await expect(page.locator('#day-blocked-badge')).toBeVisible();
  });

  test('Given booked slot, when marking no-show, then visual status updates immediately', async ({ page }) => {
    await page.goto('/?tab=calendar');
    await page.fill('input#calendar-date-input', '2026-10-12');

    const markNoShowBtn = page.locator('button:has-text("Mark No-Show")').first();
    await markNoShowBtn.click();

    // Visual feedback banner shown
    await expect(page.locator('#calendar-feedback-banner')).toContainText('marked as NO-SHOW');

    // Restore button is now visible
    await expect(page.locator('button:has-text("Undo No-Show")').first()).toBeVisible();
  });

});
