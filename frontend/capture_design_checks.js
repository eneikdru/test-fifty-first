import { chromium } from 'playwright';
import { spawn } from 'child_process';
import fs from 'fs';
import path from 'path';

async function run() {
  const outputDir = path.resolve('../.eneik/records/design-check-aac7ba3f-3637-4221-bfef-2934cc4cd363');
  fs.mkdirSync(outputDir, { recursive: true });

  console.log('Starting preview server...');
  const npmPath = 'npm';
  const server = spawn(npmPath, ['run', 'preview', '--', '--port', '4173'], {
    cwd: path.resolve('.'),
    stdio: 'inherit',
    shell: '/usr/bin/bash'
  });

  // Give server time to start
  await new Promise(r => setTimeout(r, 2500));

  console.log('Launching browser...');
  const browser = await chromium.launch();

  try {
    // 1. Desktop Screenshot (1440px)
    const pageDesktop = await browser.newPage({ viewport: { width: 1440, height: 900 } });
    await pageDesktop.goto('http://localhost:4173/?tab=profile');
    const profileTabBtn = pageDesktop.locator('button:has-text("Master Profile")');
    await profileTabBtn.click();
    await pageDesktop.waitForSelector('#master-profile-section');
    await pageDesktop.screenshot({ path: path.join(outputDir, 'desktop-1440.png'), fullPage: true });
    console.log('Desktop screenshot saved.');

    // 2. Mobile Screenshot (375px)
    const pageMobile = await browser.newPage({ viewport: { width: 375, height: 667 } });
    await pageMobile.goto('http://localhost:4173/?tab=profile');
    const profileTabBtnMobile = pageMobile.locator('button:has-text("Master Profile")');
    await profileTabBtnMobile.click();
    await pageMobile.waitForSelector('#master-profile-section');
    await pageMobile.screenshot({ path: path.join(outputDir, 'mobile-375.png'), fullPage: true });
    console.log('Mobile screenshot saved.');

    // 3. Layout Check JSON (bounding boxes on 375px mobile)
    const layoutElements = ['#app-header', '#profile-heading', '#master-profile-section'];
    const layoutCheck = [];

    for (const selector of layoutElements) {
      const element = await pageMobile.$(selector);
      if (element) {
        const box = await element.boundingBox();
        if (box) {
          layoutCheck.push({
            id: selector.replace('#', ''),
            left: Math.round(box.x),
            top: Math.round(box.y),
            width: Math.round(box.width),
            height: Math.round(box.height)
          });
        }
      }
    }

    fs.writeFileSync(
      path.join(outputDir, 'layout-check.json'),
      JSON.stringify(layoutCheck, null, 2)
    );
    console.log('Layout check JSON saved:', layoutCheck);

  } finally {
    await browser.close();
    server.kill();
    process.exit(0);
  }
}

run().catch(err => {
  console.error(err);
  process.exit(1);
});
