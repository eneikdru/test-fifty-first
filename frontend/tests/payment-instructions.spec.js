import { test, expect } from '@playwright/test';

test.describe('Payment Instructions and Ledger QA Verification', () => {

  test('Given a completed E2E booking, When checking the confirmation page, Then TBC/BOG details are present', async ({ page }) => {
    await page.goto('/');

    // Ensure checkout view is visible by default
    await expect(page.locator('#checkout-section')).toBeVisible();

    // Verify TBC default selection details
    const tbcDetails = page.locator('.bank-details');
    await expect(tbcDetails).toContainText('TBC Bank Georgia');
    await expect(tbcDetails).toContainText('GE29TB7000000012345678');
    await expect(tbcDetails).toContainText('BK-9842');

    // Switch to BOG (Bank of Georgia) radio option
    await page.locator('#pay-bog').check();

    // Verify BOG bank transfer details
    await expect(tbcDetails).toContainText('Bank of Georgia (BOG)');
    await expect(tbcDetails).toContainText('GE02BG0000000987654321');
    await expect(tbcDetails).toContainText('BK-9842');

    // Switch to Cash option
    await page.locator('#pay-cash').check();
    await expect(tbcDetails).toContainText('Cash on Location');
  });

  test('Given a professional marks a booking as paid, When queried, Then the new state persists', async ({ page }) => {
    await page.goto('/');

    // Switch to Professional Ledger tab
    await page.getByRole('button', { name: 'Professional Ledger' }).click();

    // Ensure ledger view is visible
    await expect(page.locator('#ledger-section')).toBeVisible();

    // Locate booking BK-9843 which starts as UNPAID
    const selectStatusBK9843 = page.locator('#select-status-BK-9843');
    await expect(selectStatusBK9843).toHaveValue('UNPAID');

    // Mark as PAID
    await selectStatusBK9843.selectOption('PAID');

    // Verify the dropdown state updated to PAID
    await expect(selectStatusBK9843).toHaveValue('PAID');

    // Verify the status badge text updated to Paid
    const rowBK9843 = page.locator('tr', { hasText: 'BK-9843' });
    await expect(rowBK9843.locator('.status-badge')).toContainText('Paid');

    // Filter ledger by PAID status
    await page.locator('#status-filter-select').selectOption('PAID');

    // Confirm BK-9843 persists in the filtered PAID list
    await expect(rowBK9843).toBeVisible();

    // Switch filter to UNPAID and ensure BK-9843 is no longer shown
    await page.locator('#status-filter-select').selectOption('UNPAID');
    await expect(rowBK9843).not.toBeVisible();
  });

});
