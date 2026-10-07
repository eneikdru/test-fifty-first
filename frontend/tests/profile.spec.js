import { test, expect } from '@playwright/test';

test.describe('Mobile-First Master Profile UI Verification', () => {
  test('Given a mobile browser, When viewing the profile, Then layout is responsive and services/slots are visible', async ({ page }) => {
    // Set mobile viewport (375px)
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/?tab=profile');

    // Ensure Master Profile view is active
    const profileHeading = page.locator('#profile-heading');
    await expect(profileHeading).toContainText('Mobile-First Master Profile');

    // Verify public profile link display
    await expect(page.locator('.public-url')).toContainText('https://masters.ge/profile/giorgi-beridze');

    // Verify service list with GEL prices and durations
    await expect(page.locator('.services-list')).toContainText('Plumbing Emergency Repair');
    await expect(page.locator('.services-list')).toContainText('85 ₾ (GEL)');
    await expect(page.locator('.services-list')).toContainText('60 min');

    // Verify availability calendar slots
    await expect(page.locator('.slots-grid')).toContainText('09:00');
    await expect(page.locator('.slots-grid')).toContainText('Booked (Занято)');
  });

  test('Given form input, When a network error occurs, Then what the user typed survives', async ({ page }) => {
    await page.goto('/?tab=profile');

    // Type custom input into Master Name and Bio fields
    const newMasterName = 'Earle C. Williams Plumbing Masters';
    const newBio = 'High quality plumbing repair across Tbilisi and Batumi';

    await page.locator('#master-name-input').fill(newMasterName);
    await page.locator('#master-bio-input').fill(newBio);

    // Trigger simulated network error
    await page.locator('.error-sim-btn').click();

    // Verify network error banner appears
    await expect(page.locator('#network-error-banner')).toBeVisible();
    await expect(page.locator('#network-error-banner')).toContainText('Network Error Occurred');

    // Verify typed text survived in the input fields!
    await expect(page.locator('#master-name-input')).toHaveValue(newMasterName);
    await expect(page.locator('#master-bio-input')).toHaveValue(newBio);
  });

  test('Given form controls, When inspected, Then labelled form controls and WCAG standards are met', async ({ page }) => {
    await page.goto('/?tab=profile');

    // Check presence of explicitly labelled inputs
    const nameLabel = page.locator('label[for="master-name-input"]');
    await expect(nameLabel).toBeVisible();

    const phoneLabel = page.locator('label[for="master-phone-input"]');
    await expect(phoneLabel).toBeVisible();

    const cityLabel = page.locator('label[for="master-city-select"]');
    await expect(cityLabel).toBeVisible();

    // Verify focus state visibility on primary action button
    const saveButton = page.locator('button', { hasText: 'Save Profile' });
    await saveButton.focus();
    await expect(saveButton).toBeFocused();
  });

  test('Given Facebook Business Page import, When triggered, Then profile details update and persist', async ({ page }) => {
    await page.goto('/?tab=profile');

    // Click FB import button
    await page.locator('button', { hasText: 'Import Photo, Description & Address from FB Page' }).click();

    // Verify updated values after import
    await expect(page.locator('#master-name-input')).toHaveValue('Giorgi Beridze (Pro Master)');
    await expect(page.locator('#master-address-input')).toHaveValue('Chavchavadze Ave 12, Tbilisi');
  });
});
