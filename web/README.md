# Deansgate Square General Store - Standalone Web Apps

A complete, responsive, real-time web application suite for Deansgate Square doorstep delivery in Manchester, ready for **Netlify** deployment.

## Portals Included (Completely Separated)

1. **Resident Storefront (`index.html` or `/storefront`)**
   - Tower Selection (South, West, East, North)
   - Category filtering & live dynamic product, price, and image rendering
   - Interactive Delivery Bag & Automatic Floor detection
   - WhatsApp Order Dispatch
   - *Note: Completely separated with zero Admin controls or links.*

2. **Runner Dispatch Hub (`runner.html` or `/runner`)**
   - Passcode security screen (default passcode: `@Elijah16`, changeable from Admin Hub)
   - Lift Direction Floor Sorting (Floor 65➔1 or Floor 1➔65)
   - Step-by-step lift run workflow (Placed ➔ Accepted ➔ In Lift ➔ Delivered) with elevator chime sound effects
   - Real-time order sync with Storefront and Admin Hub

3. **Admin Command Hub (`admin.html` or `/admin`)**
   - Manage Products, Prices, Descriptions, and Image URLs with real-time storefront broadcast
   - Manage Delivery Fee, Store Open/Closed Status, Runner WhatsApp Number, and Announcement Banner
   - Security Passcode Management for **Admin Password** and **Runner Password**
   - Order history tracking & live operational metrics

## Real-Time Instant Synchronization

The portals synchronize changes instantly across tabs and devices using a 3-tier sync layer:
1. **`BroadcastChannel("deansgate_ops_network")`**: Instant inter-tab message passing.
2. **`window.addEventListener("storage", ...)`**: Automatic DOM storage event triggers across windows on the same domain.
3. **1-Second Heartbeat Polling**: Ensures zero desync even if a tab is backgrounded.

## Deployment to Netlify

This project includes `netlify.toml` and `_redirects` preconfigured for Netlify.

### Option A: Netlify Drag & Drop (Easiest)
1. Log in to [Netlify](https://app.netlify.com/).
2. Go to **Sites** and drag & drop the `web/` folder directly into the upload area.
3. Your site will be live instantly at `https://<your-site-name>.netlify.app`.

### Option B: Netlify CLI
```bash
npm install -g netlify-cli
cd web
netlify deploy --prod
```

### Option C: Git Repository Deployment
1. Connect your repository to Netlify.
2. Set Build Command: *(Leave blank)*
3. Set Publish Directory: `web`
