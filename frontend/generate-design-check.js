import { chromium } from 'playwright';
import http from 'http';
import fs from 'fs';
import path from 'path';

const recordDir = path.resolve(process.cwd(), '../.eneik/records/design-check-1e2ff278-4cf1-4e39-8b07-7e849434313f');
const distDir = path.resolve(process.cwd(), 'dist');

// Simple static file server using native node http
function startStaticServer(port) {
  const mimeTypes = {
    '.html': 'text/html',
    '.js': 'text/javascript',
    '.css': 'text/css',
    '.json': 'application/json',
    '.png': 'image/png',
    '.svg': 'image/svg+xml'
  };

  const server = http.createServer((req, res) => {
    let filePath = path.join(distDir, req.url === '/' ? 'index.html' : req.url);
    if (!fs.existsSync(filePath)) {
      filePath = path.join(distDir, 'index.html');
    }
    const ext = path.extname(filePath).toLowerCase();
    const contentType = mimeTypes[ext] || 'application/octet-stream';

    fs.readFile(filePath, (err, content) => {
      if (err) {
        res.writeHead(500);
        res.end(`Server Error: ${err.code}`);
      } else {
        res.writeHead(200, { 'Content-Type': contentType });
        res.end(content, 'utf-8');
      }
    });
  });

  return new Promise((resolve, reject) => {
    server.on('error', reject);
    server.listen(port, '127.0.0.1', () => {
      console.log(`Static server running at http://127.0.0.1:${port}`);
      resolve(server);
    });
  });
}

async function main() {
  if (!fs.existsSync(recordDir)) {
    fs.mkdirSync(recordDir, { recursive: true });
  }

  const serverPort = 5199;
  const server = await startStaticServer(serverPort);

  let browser;
  try {
    const launchOptions = {
      headless: true,
      args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-dev-shm-usage']
    };

    if (fs.existsSync('/usr/bin/google-chrome')) {
      launchOptions.executablePath = '/usr/bin/google-chrome';
    } else if (fs.existsSync('/usr/bin/chromium-browser')) {
      launchOptions.executablePath = '/usr/bin/chromium-browser';
    } else if (fs.existsSync('/usr/bin/chromium')) {
      launchOptions.executablePath = '/usr/bin/chromium';
    }

    browser = await chromium.launch(launchOptions);
    const page = await browser.newPage();

    // 1. Desktop Viewport (1440px)
    console.log('Capturing 1440px desktop screenshot...');
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto(`http://127.0.0.1:${serverPort}`, { waitUntil: 'networkidle' });
    await page.screenshot({ path: path.join(recordDir, 'desktop-1440.png'), fullPage: true });

    // Extract layout geometry bounding boxes
    const layout = await page.evaluate(() => {
      const elements = [
        { id: 'header', selector: '#header' },
        { id: 'main-content', selector: '#main-content' },
        { id: 'hero', selector: '#hero' },
        { id: 'recovery-flow', selector: '#recovery-flow' },
        { id: 'footer', selector: '#footer' },
        { id: 'privacy-banner', selector: '.privacy-banner-card' }
      ];

      return elements.map(({ id, selector }) => {
        const el = document.querySelector(selector);
        if (!el) {
          return { id, left: 0, top: 0, width: 0, height: 0 };
        }
        const rect = el.getBoundingClientRect();
        return {
          id,
          left: Math.round(rect.left),
          top: Math.round(rect.top),
          width: Math.round(rect.width),
          height: Math.round(rect.height)
        };
      });
    });

    fs.writeFileSync(path.join(recordDir, 'layout-check.json'), JSON.stringify(layout, null, 2));
    console.log('Saved layout-check.json:', layout);

    // 2. Mobile Viewport (375px)
    console.log('Capturing 375px mobile screenshot...');
    await page.setViewportSize({ width: 375, height: 812 });
    await page.goto(`http://127.0.0.1:${serverPort}`, { waitUntil: 'networkidle' });
    await page.screenshot({ path: path.join(recordDir, 'mobile-375.png'), fullPage: true });

    console.log('Design verification files generated successfully in:', recordDir);
  } finally {
    if (browser) {
      await browser.close();
    }
    server.close();
  }
}

main().catch((err) => {
  console.error('Error generating design verification check:', err);
  process.exit(1);
});
