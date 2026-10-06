import { test, expect } from '@playwright/test';

test.describe('Payment Instructions QA Verification', () => {
  test('Given a completed E2E booking, When checking the confirmation page, Then TBC/BOG details are present', async ({ page }) => {
    await page.goto('/');

    // Ensure we are on the Checkout View
    const checkoutTab = page.locator('button', { hasText: 'Checkout View' });
    if (await checkoutTab.getAttribute('aria-pressed') !== 'true') {
      await checkoutTab.click();
    }

    // Verify TBC details are present when TBC is selected
    await page.locator('label[for="pay-tbc"]').click();
    await expect(page.locator('.bank-details')).toContainText('TBC Bank Georgia');
    await expect(page.locator('.bank-details')).toContainText('GE29TB7000000012345678');
    await expect(page.locator('.bank-details')).toContainText('BK-9842'); // Reference Note

    // Verify BOG details are present when BOG is selected
    await page.locator('label[for="pay-bog"]').click();
    await expect(page.locator('.bank-details')).toContainText('Bank of Georgia (BOG)');
    await expect(page.locator('.bank-details')).toContainText('GE02BG0000000987654321');
    await expect(page.locator('.bank-details')).toContainText('BK-9842'); // Reference Note

    // Select Cash
    await page.locator('label[for="pay-cash"]').click();
    await expect(page.locator('.bank-details')).toContainText('Cash on Location');
    await expect(page.locator('.bank-details')).toContainText('85 GEL');
  });

  test('Given a professional marks a booking as paid, When queried, Then the new state persists', async ({ page }) => {
    await page.goto('/');

    // Switch to Ledger UI
    await page.locator('button', { hasText: 'Professional Ledger' }).click();

    // Verify initial state for BK-9842
    const rowBK9842 = page.locator('tr').filter({ hasText: 'BK-9842' });
    await expect(rowBK9842.locator('.status-badge')).toContainText('Pending Verification (Проверка)');

    // Change state to PAID
    await rowBK9842.locator('select.status-toggle-select').selectOption('PAID');

    // Verify the UI state updated immediately
    await expect(rowBK9842.locator('.status-badge')).toContainText('Paid (Оплачено)');

    // Filter to PAID and verify the booking is in the list
    await page.locator('#status-filter-select').selectOption('PAID');
    await expect(page.locator('.ledger-table')).toContainText('BK-9842');

    // Filter to UNPAID and verify the booking is not in the list
    await page.locator('#status-filter-select').selectOption('UNPAID');
    await expect(page.locator('.ledger-table')).not.toContainText('BK-9842');
  });
});
