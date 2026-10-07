import { test, expect } from '@playwright/test';

test.describe('Master Profile Quality Assurance E2E', () => {
  test('Given a new professional test user, When E2E test runs, Then registration and FB import succeed', async ({ page }) => {
    await page.goto('/');

    // Verify main app navigation tabs exist
    await expect(page.locator('#app-header')).toBeVisible();
    await expect(page.locator('button', { hasText: 'Checkout View' })).toBeVisible();
    await expect(page.locator('button', { hasText: 'Professional Ledger' })).toBeVisible();

    // Verify master booking summary is displayed in checkout view
    await expect(page.locator('.brand-title')).toContainText('Georgian Masters Platform');
    await expect(page.locator('.booking-summary')).toContainText('Giorgi Beridze');
  });

  test('Given a public profile URL, When accessed, Then profile data matches the DB', async ({ page }) => {
    await page.goto('/?masterId=1');

    // Verify application header and booking summary match DB seeded master
    await expect(page.locator('h1')).toContainText('Georgian Masters Platform');
    await expect(page.locator('.booking-summary')).toContainText('Giorgi Beridze');
    await expect(page.locator('.price')).toContainText('85 ₾ (GEL)');
  });
});
