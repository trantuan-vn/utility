#!/bin/bash

# Script to create TradingSTO monorepo structure with Turborepo (using pnpm), TypeScript frontend (Next.js in apps/web), and Rust backend (in apps/backend)
# Based on critique 6, updated for pnpm and new folder structure

# Exit on error
set -e

# --- Config ---
PROJECT_ROOT=${1:-~/trading-sto}

# --- Step 1: Scaffold turbo repo ---
echo ">>> Creating turbo repo at $PROJECT_ROOT ..."
rm -rf $PROJECT_ROOT
pnpm dlx create-turbo@latest $PROJECT_ROOT --package-manager pnpm

cd $PROJECT_ROOT/apps

# --- Step 2: Add Rust backend ---
echo ">>> Creating Rust backend ..."
cargo new backend --bin

# --- Step 3: Dummy package.json cho turbo nhận diện ---
cat > backend/package.json <<'EOF'
{
  "name": "backend",
  "version": "0.0.0",
  "private": true,
  "scripts": {
    "build": "cargo build",
    "dev": "cargo run"
  }
}
EOF

# --- Step 4: Update turbo.json ---
cd ..
cat > turbo.json <<'EOF'
{
  "$schema": "https://turbo.build/schema.json",
  "tasks": {
    "build": {
      "dependsOn": ["^build"],
      "inputs": ["$TURBO_DEFAULT$", ".env*"],
      "outputs": [".next/**", "!.next/cache/**", "dist/**", "target/**"]      
    },
    "dev": {
      "cache": false,
      "persistent": true
    },
    "backend#build": {
      "outputs": ["target/**"]
    },
    "backend#dev": {
      "cache": false
    }
  }
}
EOF

# --- Step 5: Update root package.json ---
echo ">>> Updating root package.json ..."
TMP=$(mktemp)
jq '.scripts["backend:build"]="cd apps/backend && cargo build"' package.json > "$TMP" && mv "$TMP" package.json
TMP=$(mktemp)
jq '.scripts["backend:dev"]="cd apps/backend && cargo run"' package.json > "$TMP" && mv "$TMP" package.json

# --- Step 6: Install dependencies ---  
cd apps/backend
cargo add tokio --features full
cargo add axum --features "json,http1,http2,ws,multipart,macros,tracing"

cd apps/web
pnpm add next-intl

# --- Step 7: Create project structure ---
cd $PROJECT_ROOT
# Create apps/web (frontend) directory structure
mkdir -p apps/web/app/{api/{wallets,tokens,analytics},components,hooks,lib/{context,web3,ai,realtime},locales,\[symbol\],dashboard,styles}
mkdir -p apps/web/public/images
mkdir -p apps/web/tests/{__mocks__,e2e}

# Create other root directories
mkdir -p {shared,infra/{kubernetes,monitoring},docs,smart-contracts,security/pentest-scripts}
mkdir -p .github/workflows

# Create frontend files in apps/web
# apps/web/app/api/wallets/route.ts
cat << EOF > apps/web/app/api/wallets/route.ts
import { NextResponse } from 'next/server';

export async function GET() {
  // Proxy to Rust backend for wallet auth
  const response = await fetch('http://localhost:3001/wallets', { method: 'GET' });
  const data = await response.json();
  return NextResponse.json(data);
}
EOF

# apps/web/app/api/tokens/route.ts
cat << EOF > apps/web/app/api/tokens/route.ts
import { NextResponse } from 'next/server';

export async function GET() {
  // Proxy to Rust backend for token data
  const response = await fetch('http://localhost:3001/tokens', { method: 'GET' });
  const data = await response.json();
  return NextResponse.json(data);
}
EOF

# apps/web/app/api/analytics/route.ts
cat << EOF > apps/web/app/api/analytics/route.ts
import { NextResponse } from 'next/server';

export async function POST(request: Request) {
  // Send analytics to Rust backend
  const body = await request.json();
  const response = await fetch('http://localhost:3001/analytics', {
    method: 'POST',
    body: JSON.stringify(body),
  });
  const data = await response.json();
  return NextResponse.json(data);
}
EOF

# apps/web/app/components/Header.tsx
cat << EOF > apps/web/app/components/Header.tsx
"use client";
import { useTranslations } from 'next-intl';
import { useLocale } from '../lib/context/LocaleContext';
import styles from '../styles/TradingSTO.module.css';

const Header: React.FC = () => {
  const t = useTranslations('Header');
  const { locale, setLocale } = useLocale();

  return (
    <header className={styles.header}>
      <h1>{t('title')}</h1>
      <input placeholder={t('searchPlaceholder')} className={styles.searchBox} />
      <select value={locale} onChange={(e) => setLocale(e.target.value)} className={styles.btn}>
        <option value="en">🇺🇸 {t('languages.en')}</option>
        <option value="vi">🇻🇳 {t('languages.vi')}</option>
        <option value="ar">🇸🇦 {t('languages.ar')}</option>
      </select>
      <button className={styles.btn}>🌙 Dark Mode</button>
    </header>
  );
};

export default Header;
EOF

# apps/web/app/components/Footer.tsx
cat << EOF > apps/web/app/components/Footer.tsx
import styles from '../styles/TradingSTO.module.css';

const Footer: React.FC = () => (
  <footer className={styles.footer}>
    &copy; 2025 TradingSTO – Built with TradingView Widgets
  </footer>
);

export default Footer;
EOF

# apps/web/app/components/TradingViewWidget.tsx
cat << EOF > apps/web/app/components/TradingViewWidget.tsx
"use client";
import { useEffect } from 'react';
import { WidgetConfig } from '../lib/widgetConfig';

interface TradingViewWidgetProps {
  id: string;
  config: WidgetConfig;
}

const TradingViewWidget: React.FC<TradingViewWidgetProps> = ({ id, config }) => {
  useEffect(() => {
    const script = document.createElement('script');
    script.src = config.src;
    script.async = true;
    script.innerHTML = JSON.stringify(config.config);
    const container = document.getElementById(id);
    if (container) {
      container.appendChild(script);
      return () => { container.innerHTML = ''; };
    }
  }, [id, config]);

  return (
    <div id={id} className="tradingview-widget-container">
      <div className="tradingview-widget-container__widget"></div>
    </div>
  );
};

export default TradingViewWidget;
EOF

# apps/web/app/components/WidgetContainer.tsx
cat << EOF > apps/web/app/components/WidgetContainer.tsx
import styles from '../styles/TradingSTO.module.css';

interface WidgetContainerProps {
  title?: string;
  children: React.ReactNode;
  isRow?: boolean;
  isHalf?: boolean;
}

const WidgetContainer: React.FC<WidgetContainerProps> = ({ title, children, isRow, isHalf }) => (
  <div className={\`\${styles.section} \${isRow ? styles.row : ''} \${isHalf ? styles.half : ''}\`}>
    {title && <h2>{title}</h2>}
    {children}
  </div>
);

export default WidgetContainer;
EOF

# apps/web/app/components/WalletConnect.tsx
cat << EOF > apps/web/app/components/WalletConnect.tsx
"use client";
import { useWallet } from '../hooks/useWallet';
import styles from '../styles/TradingSTO.module.css';

const WalletConnect: React.FC = () => {
  const { address, connect, disconnect } = useWallet();

  return (
    <div className={styles.walletConnect}>
      {address ? (
        <>
          <span>Connected: {address.slice(0, 6)}...{address.slice(-4)}</span>
          <button onClick={disconnect} className={styles.btn}>Disconnect</button>
        </>
      ) : (
        <button onClick={connect} className={styles.btn}>Connect Wallet</button>
      )}
    </div>
  );
};

export default WalletConnect;
EOF

# apps/web/app/components/PortfolioViewer.tsx
cat << EOF > apps/web/app/components/PortfolioViewer.tsx
"use client";
import { useWallet } from '../hooks/useWallet';
import styles from '../styles/TradingSTO.module.css';

const PortfolioViewer: React.FC = () => {
  const { address, balance } = useWallet();

  return (
    <div className={styles.section}>
      <h2>Portfolio</h2>
      <p>Address: {address || 'Not connected'}</p>
      <p>Balance: {balance ? \`\${balance} ETH\` : 'N/A'}</p>
    </div>
  );
};

export default PortfolioViewer;
EOF

# apps/web/app/components/Leaderboard.tsx
cat << EOF > apps/web/app/components/Leaderboard.tsx
import styles from '../styles/TradingSTO.module.css';

interface LeaderboardEntry {
  user: string;
  volume: number;
}

const Leaderboard: React.FC = () => {
  const entries: LeaderboardEntry[] = [
    { user: '0x123...', volume: 1000 },
    { user: '0x456...', volume: 800 },
  ];

  return (
    <div className={styles.section}>
      <h2>Leaderboard</h2>
      <ul>
        {entries.map((entry, index) => (
          <li key={index}>{entry.user}: {entry.volume} USD</li>
        ))}
      </ul>
    </div>
  );
};

export default Leaderboard;
EOF

# apps/web/app/hooks/useWallet.ts
cat << EOF > apps/web/app/hooks/useWallet.ts
import { useState, useEffect } from 'react';
import { ethers } from 'ethers';

export function useWallet() {
  const [address, setAddress] = useState<string | null>(null);
  const [balance, setBalance] = useState<string | null>(null);

  const connect = async () => {
    if (typeof window.ethereum !== 'undefined') {
      const provider = new ethers.BrowserProvider(window.ethereum);
      const accounts = await provider.send('eth_requestAccounts', []);
      setAddress(accounts[0]);
      const balance = await provider.getBalance(accounts[0]);
      setBalance(ethers.formatEther(balance));
    }
  };

  const disconnect = () => {
    setAddress(null);
    setBalance(null);
  };

  return { address, balance, connect, disconnect };
}
EOF

# apps/web/app/hooks/useChainData.ts
cat << EOF > apps/web/app/hooks/useChainData.ts
import { useState, useEffect } from 'react';

interface TokenData {
  symbol: string;
  price: number;
}

export function useChainData(symbol: string) {
  const [data, setData] = useState<TokenData | null>(null);

  useEffect(() => {
    // Fetch from Rust backend
    fetch(\`http://localhost:3001/tokens?symbol=\${symbol}\`)
      .then(res => res.json())
      .then(setData);
  }, [symbol]);

  return data;
}
EOF

# apps/web/app/hooks/useTheme.ts
cat << EOF > apps/web/app/hooks/useTheme.ts
import { useState, useEffect } from 'react';

export function useTheme() {
  const [theme, setTheme] = useState<string>('light');

  useEffect(() => {
    const savedTheme = localStorage.getItem('theme') || 'light';
    setTheme(savedTheme);
    document.body.classList.toggle('dark-mode', savedTheme === 'dark');
  }, []);

  const toggleTheme = () => {
    const newTheme = theme === 'light' ? 'dark' : 'light';
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);
    document.body.classList.toggle('dark-mode', newTheme === 'dark');
  };

  return { theme, toggleTheme };
}
EOF

# apps/web/app/hooks/useRealtime.ts
cat << EOF > apps/web/app/hooks/useRealtime.ts
import { useState, useEffect } from 'react';
import { createClient } from 'graphql-ws';

export function useRealtime(symbol: string) {
  const [price, setPrice] = useState<number | null>(null);

  useEffect(() => {
    const client = createClient({
      url: 'ws://localhost:3001/graphql',
    });

    const unsubscribe = client.subscribe(
      {
        query: \`subscription { price(symbol: "\${symbol}") }\`,
      },
      {
        next: ({ data }) => setPrice(data.price),
        error: console.error,
        complete: () => {},
      }
    );

    return () => unsubscribe();
  }, [symbol]);

  return price;
}
EOF

# apps/web/app/lib/context/ThemeContext.tsx 
cat << EOF > apps/web/app/lib/context/ThemeContext.tsx 
"use client";
import { createContext, useContext } from 'react';
import { useTheme } from '../../hooks/useTheme';

interface ThemeContextType {
  theme: string;
  toggleTheme: () => void;
}

export const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const { theme, toggleTheme } = useTheme();
  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

export function useThemeContext() {
  const context = useContext(ThemeContext);
  if (!context) throw new Error('useThemeContext must be used within ThemeProvider');
  return context;
}
EOF

# apps/web/app/lib/context/LocaleContext.tsx 
cat << EOF > apps/web/app/lib/context/LocaleContext.tsx 
"use client";
import { createContext, useContext, useState } from 'react';

interface LocaleContextType {
  locale: string;
  setLocale: (locale: string) => void;
}

export const LocaleContext = createContext<LocaleContextType | undefined>(undefined);

export function LocaleProvider({ children }: { children: React.ReactNode }) {
  const [locale, setLocale] = useState<string>('en');

  return (
    <LocaleContext.Provider value={{ locale, setLocale }}>
      {children}
    </LocaleContext.Provider>
  );
}

export function useLocale() {
  const context = useContext(LocaleContext);
  if (!context) throw new Error('useLocale must be used within LocaleProvider');
  return context;
}
EOF

# apps/web/app/lib/context/WalletContext.tsx 
cat << EOF > apps/web/app/lib/context/WalletContext.tsx 
"use client";
import { createContext, useContext } from 'react';
import { useWallet } from '../../hooks/useWallet';

interface WalletContextType {
  address: string | null;
  balance: string | null;
  connect: () => void;
  disconnect: () => void;
}

export const WalletContext = createContext<WalletContextType | undefined>(undefined);

export function WalletProvider({ children }: { children: React.ReactNode }) {
  const wallet = useWallet();
  return (
    <WalletContext.Provider value={wallet}>
      {children}
    </WalletContext.Provider>
  );
}

export function useWalletContext() {
  const context = useContext(WalletContext);
  if (!context) throw new Error('useWalletContext must be used within WalletProvider');
  return context;
}
EOF

# apps/web/app/lib/web3/contracts.ts
cat << EOF > apps/web/app/lib/web3/contracts.ts
import { ethers } from 'ethers';

export const TRADING_TOKEN_ABI = [
  // Simplified ERC-20 ABI
  'function balanceOf(address) view returns (uint256)',
];

export function getContract(address: string, provider: ethers.Provider) {
  return new ethers.Contract(address, TRADING_TOKEN_ABI, provider);
}
EOF

# apps/web/app/lib/web3/providers.ts
cat << EOF > apps/web/app/lib/web3/providers.ts
import { ethers } from 'ethers';

export function getProvider() {
  if (typeof window.ethereum !== 'undefined') {
    return new ethers.BrowserProvider(window.ethereum);
  }
  return new ethers.JsonRpcProvider('https://mainnet.infura.io/v3/YOUR_API_KEY');
}
EOF

# apps/web/app/lib/web3/relayers.ts
cat << EOF > apps/web/app/lib/web3/relayers.ts
import { GelatoRelay } from '@gelatonetwork/relay-sdk';

export async function sendGaslessTx(txData: any) {
  const relay = new GelatoRelay();
  return relay.sponsoredCall(txData, 'YOUR_GELATO_API_KEY');
}
EOF

# apps/web/app/lib/web3/sessions.ts
cat << EOF > apps/web/app/lib/web3/sessions.ts
import { Bundler } from '@account-abstraction/sdk';

export async function createSmartSession(userAddress: string) {
  const bundler = new Bundler({
    bundlerUrl: 'https://bundler.example.com',
  });
  const userOp = await bundler.buildUserOp({ /* params */ });
  return fetch('http://localhost:3001/sessions', {
    method: 'POST',
    body: JSON.stringify({ userOp, address: userAddress }),
  }).then(res => res.json());
}
EOF

# apps/web/app/lib/ai/pricePrediction.ts
cat << EOF > apps/web/app/lib/ai/pricePrediction.ts
export async function predictPrice(symbol: string): Promise<{ predictedPrice: number }> {
  const response = await fetch('https://api.x.ai/grok/predict', {
    method: 'POST',
    body: JSON.stringify({ symbol, timeframe: '1h' }),
  });
  return response.json();
}
EOF

# apps/web/app/lib/realtime/priceFeed.ts
cat << EOF > apps/web/app/lib/realtime/priceFeed.ts
import { createClient } from 'graphql-ws';

export function getPriceFeed(symbol: string) {
  const client = createClient({
    url: 'ws://localhost:3001/graphql',
  });

  return client.subscribe({
    query: \`subscription { price(symbol: "\${symbol}") }\`,
  });
}
EOF

# apps/web/app/lib/widgetConfig.ts
cat << EOF > apps/web/app/lib/widgetConfig.ts
export interface WidgetConfig {
  src: string;
  config: any;
}

export const tickerTapeConfig: WidgetConfig = {
  src: 'https://s3.tradingview.com/external-embedding/embed-widget-ticker-tape.js',
  config: {
    symbols: [
      { proName: 'BITSTAMP:BTCUSD', title: 'Bitcoin' },
      { proName: 'BITSTAMP:ETHUSD', title: 'Ethereum' },
    ],
    colorTheme: 'light',
    locale: 'en',
  },
};
EOF

# apps/web/app/lib/constants.ts
cat << EOF > apps/web/app/lib/constants.ts
export const SUPPORTED_CHAINS = [
  { id: 1, name: 'Ethereum' },
  { id: 56, name: 'Binance Smart Chain' },
];

export const SUPPORTED_LOCALES = ['en', 'vi', 'ar'];
EOF

# apps/web/app/locales/en.json
cat << EOF > apps/web/app/locales/en.json
{
  "Header": {
    "title": "TradingSTO",
    "searchPlaceholder": "Search crypto/token/NFT symbol...",
    "languages": {
      "en": "English",
      "vi": "Vietnamese",
      "ar": "Arabic"
    }
  }
}
EOF

# apps/web/app/locales/vi.json
cat << EOF > apps/web/app/locales/vi.json
{
  "Header": {
    "title": "TradingSTO",
    "searchPlaceholder": "Tìm kiếm mã crypto/token/NFT...",
    "languages": {
      "en": "Tiếng Anh",
      "vi": "Tiếng Việt",
      "ar": "Tiếng Ả Rập"
    }
  }
}
EOF

# apps/web/app/locales/ar.json
cat << EOF > apps/web/app/locales/ar.json
{
  "Header": {
    "title": "TradingSTO",
    "searchPlaceholder": "ابحث عن رمز العملة/الرمز/NFT...",
    "languages": {
      "en": "الإنجليزية",
      "vi": "الفيتنامية",
      "ar": "العربية"
    }
  }
}
EOF

# apps/web/app/[symbol]/page.tsx
cat << EOF > apps/web/app/[symbol]/page.tsx
import { useTranslations } from 'next-intl';
import { TradingViewWidget } from '../components/TradingViewWidget';
import { widgetConfig } from '../lib/widgetConfig';

export default function SymbolPage({ params }: { params: { symbol: string } }) {
  const t = useTranslations('Header');
  return (
    <div>
      <h1>{t('title')} - {params.symbol}</h1>
      <TradingViewWidget id="symbol-info" config={{
        ...widgetConfig,
        config: { ...widgetConfig.config, symbol: \`BITSTAMP:\${params.symbol}USD\` }
      }} />
    </div>
  );
}
EOF

# apps/web/app/dashboard/page.tsx
cat << EOF > apps/web/app/dashboard/page.tsx
import { PortfolioViewer } from '../components/PortfolioViewer';
import { WidgetContainer } from '../components/WidgetContainer';

export default function DashboardPage() {
  return (
    <WidgetContainer title="Dashboard">
      <PortfolioViewer />
    </WidgetContainer>
  );
}
EOF

# apps/web/app/layout.tsx
cat << EOF > apps/web/app/layout.tsx
import { ThemeProvider } from './lib/context/ThemeContext';
import { LocaleProvider } from './lib/context/LocaleContext';
import { WalletProvider } from './lib/context/WalletContext';
import { Header } from './components/Header';
import { Footer } from './components/Footer';
import './styles/globals.css';

export const metadata = {
  title: 'TradingSTO',
  description: 'Web3 Trading Platform',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>
        <ThemeProvider>
          <LocaleProvider>
            <WalletProvider>
              <Header />
              {children}
              <Footer />
            </WalletProvider>
          </LocaleProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
EOF

# apps/web/app/page.tsx
cat << EOF > apps/web/app/page.tsx
import { TradingViewWidget } from './components/TradingViewWidget';
import { WidgetContainer } from './components/WidgetContainer';
import { tickerTapeConfig } from './lib/widgetConfig';
import styles from './styles/TradingSTO.module.css';

export default function HomePage() {
  return (
    <div className={styles.container}>
      <TradingViewWidget id="ticker-tape" config={tickerTapeConfig} />
      <WidgetContainer title="Symbol Info">
        <TradingViewWidget id="symbol-info" config={tickerTapeConfig} />
      </WidgetContainer>
    </div>
  );
}
EOF

# apps/web/public/images/icon-192.png (placeholder)
touch apps/web/public/images/icon-192.png

# apps/web/public/images/eth-logo.svg (placeholder)
touch apps/web/public/images/eth-logo.svg

# apps/web/public/manifest.json
cat << EOF > apps/web/public/manifest.json
{
  "name": "TradingSTO",
  "short_name": "TSTO",
  "start_url": "/",
  "display": "standalone",
  "background_color": "#f0f2f5",
  "theme_color": "#0073e6",
  "icons": [
    {
      "src": "/images/icon-192.png",
      "sizes": "192x192",
      "type": "image/png"
    }
  ]
}
EOF

# apps/web/public/tradingview-logo.svg (placeholder)
touch apps/web/public/tradingview-logo.svg

# apps/web/app/styles/globals.css
cat << EOF > apps/web/app/styles/globals.css
* {
  box-sizing: border-box;
  margin: 0;
}

body {
  font-family: Arial, sans-serif;
  background-color: #f0f2f5;
  color: #333;
  transition: background-color 0.3s, color 0.3s;
}

.dark-mode {
  background-color: #121212;
  color: #eee;
}
EOF

# apps/web/app/styles/TradingSTO.module.css
cat << EOF > apps/web/app/styles/TradingSTO.module.css
.header {
  background-color: white;
  padding: 15px 30px;
  font-size: 24px;
  font-weight: bold;
  color: #0073e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
}

.section {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 20px;
  padding: 20px;
}

.container {
  max-width: 1100px;
  margin: auto;
  padding: 20px;
}

.btn {
  border: 1px solid #ccc;
  background: #fff;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.searchBox {
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
  }
}
EOF

# apps/web/tests/__mocks__/wagmi.ts
cat << EOF > apps/web/tests/__mocks__/wagmi.ts
export const useAccount = () => ({
  address: '0x123...',
  isConnected: true,
});
EOF

# apps/web/tests/__mocks__/tradingview.ts
cat << EOF > apps/web/tests/__mocks__/tradingview.ts
export const mockWidget = jest.fn();
EOF

# apps/web/tests/Header.test.tsx
cat << EOF > apps/web/tests/Header.test.tsx
import { render, screen } from '@testing-library/react';
import Header from '../app/components/Header';
import { ThemeProvider } from '../app/lib/context/ThemeContext';
import { LocaleProvider } from '../app/lib/context/LocaleContext';

test('renders TradingSTO title', () => {
  render(
    <ThemeProvider>
      <LocaleProvider>
        <Header />
      </LocaleProvider>
    </ThemeProvider>
  );
  expect(screen.getByText('TradingSTO')).toBeInTheDocument();
});
EOF

# apps/web/tests/TradingViewWidget.test.tsx
cat << EOF > apps/web/tests/TradingViewWidget.test.tsx
import { render } from '@testing-library/react';
import TradingViewWidget from '../app/components/TradingViewWidget';

test('renders widget container', () => {
  const { container } = render(
    <TradingViewWidget id="test-widget" config={{ src: '', config: {} }} />
  );
  expect(container.querySelector('.tradingview-widget-container')).toBeInTheDocument();
});
EOF

# apps/web/tests/e2e/walletConnect.cy.ts
cat << EOF > apps/web/tests/e2e/walletConnect.cy.ts
describe('WalletConnect', () => {
  it('connects wallet', () => {
    cy.visit('/dashboard');
    cy.get('button').contains('Connect Wallet').click();
    cy.get('span').contains('Connected:').should('exist');
  });
});
EOF

# apps/web/tsconfig.json
cat << EOF > apps/web/tsconfig.json
{
  "compilerOptions": {
    "target": "es5",
    "lib": ["dom", "dom.iterable", "esnext"],
    "allowJs": true,
    "skipLibCheck": true,
    "strict": true,
    "noImplicitAny": true,
    "forceConsistentCasingInFileNames": true,
    "noEmit": true,
    "esModuleInterop": true,
    "module": "esnext",
    "moduleResolution": "node",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "jsx": "preserve",
    "incremental": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["./app/*"]
    }
  },
  "include": ["next-env.d.ts", "**/*.ts", "**/*.tsx"],
  "exclude": ["node_modules"]
}
EOF

# apps/web/next.config.ts
cat << EOF > apps/web/next.config.ts
import { NextConfig } from 'next';

const nextConfig: NextConfig = {
  reactStrictMode: true,
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://localhost:3001/:path*',
      },
    ];
  },
};

export default nextConfig;
EOF

# apps/web/package.json
cat << EOF > apps/web/package.json
{
  "name": "trading-sto-web",
  "version": "1.0.0",
  "scripts": {
    "build": "next build",
    "dev": "next dev",
    "start": "next start",
    "test": "jest"
  },
  "dependencies": {
    "next": "^14.2.0",
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "ethers": "^6.7.0",
    "next-intl": "^3.0.0",
    "graphql-ws": "^5.14.0",
    "@gelatonetwork/relay-sdk": "^3.0.0",
    "@account-abstraction/sdk": "^0.5.0"
  },
  "devDependencies": {
    "typescript": "^5.1.6",
    "@types/react": "^18.2.0",
    "@types/node": "^20.5.0",
    "jest": "^29.6.2",
    "@testing-library/react": "^14.0.0",
    "ts-jest": "^29.1.1",
    "eslint": "^8.45.0",
    "eslint-plugin-react": "^7.33.0",
    "eslint-plugin-jsx-a11y": "^6.7.1"
  }
}
EOF

# apps/backend/src/main.rs
cat << EOF > apps/backend/src/main.rs
mod routes;
use axum::{routing::get, Router};
use routes::routes;
use serde::Serialize;

#[derive(Serialize)]
struct Response {
    message: String,
}

async fn hello() -> axum::Json<Response> {
    axum::Json(Response { message: "Hello from Rust!".to_string() })
}

#[tokio::main]
async fn main() {
    let app = Router::new()
        .route("/", get(hello))
        .merge(routes());
    axum::Server::bind(&"0.0.0.0:3001".parse().unwrap())
        .serve(app.into_make_service())
        .await
        .unwrap();
}
EOF

# apps/backend/src/routes.rs
cat << EOF > apps/backend/src/routes.rs
use axum::{routing::{get, post}, Router};
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
struct WalletResponse {
    address: String,
}

#[derive(Serialize, Deserialize)]
struct TokenResponse {
    symbol: String,
    price: f64,
}

pub fn routes() -> Router {
    Router::new()
        .route("/wallets", get(get_wallets))
        .route("/tokens", get(get_tokens))
        .route("/analytics", post(post_analytics))
}

async fn get_wallets() -> axum::Json<WalletResponse> {
    axum::Json(WalletResponse {
        address: "0x123...".to_string(),
    })
}

async fn get_tokens() -> axum::Json<Vec<TokenResponse>> {
    axum::Json(vec![
        TokenResponse {
            symbol: "BTC".to_string(),
            price: 60000.0,
        },
    ])
}

async fn post_analytics() -> axum::Json<()> {
    axum::Json(())
}
EOF

# apps/backend/src/models.rs
cat << EOF > apps/backend/src/models.rs
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct UserOp {
    pub sender: String,
    pub nonce: u64,
}
EOF

# apps/backend/src/services.rs
cat << EOF > apps/backend/src/services.rs
use crate::models::UserOp;

pub async fn process_user_op(_op: UserOp) -> Result<(), String> {
    Ok(())
}
EOF

# apps/backend/src/utils.rs
cat << EOF > apps/backend/src/utils.rs
pub fn log(message: &str) {
    println!("Log: {}", message);
}
EOF

# apps/backend/Cargo.toml
cat << EOF > apps/backend/Cargo.toml
[package]
name = "trading-sto-backend"
version = "0.1.0"
edition = "2021"

[dependencies]
axum = "0.6"
serde = { version = "1.0", features = ["derive"] }
tokio = { version = "1.0", features = ["full"] }
EOF

# shared/types.proto
cat << EOF > shared/types.proto
syntax = "proto3";

message Token {
  string symbol = 1;
  double price = 2;
}
EOF

# docs/architecture.md
cat << EOF > docs/architecture.md
# TradingSTO Architecture

Monorepo managed by Turborepo (pnpm) with TypeScript frontend (Next.js in apps/web) and Rust backend (Axum in apps/backend).
- Frontend: App Router, Web3 (Wagmi), i18n (next-intl).
- Backend: Rust APIs, smart sessions (ERC-4337).
- Deployment: Docker, K8s with auto-scaling.
EOF

# docs/api.md
cat << EOF > docs/api.md
# API Documentation

- GET /wallets: Fetch wallet info
- GET /tokens: Fetch token prices
- POST /analytics: Submit user analytics
EOF

# docs/compliance.md
cat << EOF > docs/compliance.md
# Compliance

- GDPR: User data consent
- CCPA: Data deletion requests
- Web3: SIWE for secure auth
EOF

# smart-contracts/TradingToken.sol
cat << EOF > smart-contracts/TradingToken.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract TradingToken {
    string public name = "TradingSTO Token";
    string public symbol = "TVT";
    uint8 public decimals = 18;
    uint256 public totalSupply;

    mapping(address => uint256) public balanceOf;

    constructor(uint256 initialSupply) {
        totalSupply = initialSupply * 10 ** uint256(decimals);
        balanceOf[msg.sender] = totalSupply;
    }
}
EOF

# security/pentest-scripts/hardhat.config.js
cat << EOF > security/pentest-scripts/hardhat.config.js
module.exports = {
  solidity: "0.8.0",
  networks: {
    localhost: {
      url: "http://127.0.0.1:8545"
    }
  }
};
EOF

# infra/terraform.yml
cat << EOF > infra/terraform.yml
# Placeholder Terraform config
provider "aws" {
  region = "us-east-1"
}
EOF

# infra/kubernetes/deployment.yaml
cat << EOF > infra/kubernetes/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: trading-sto
spec:
  replicas: 3
  selector:
    matchLabels:
      app: trading-sto
  template:
    metadata:
      labels:
        app: trading-sto
    spec:
      containers:
      - name: web
        image: trading-sto-web:latest
        ports:
        - containerPort: 3000
      - name: backend
        image: trading-sto-backend:latest
        ports:
        - containerPort: 3001
EOF

# infra/kubernetes/service.yaml
cat << EOF > infra/kubernetes/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: trading-sto
spec:
  selector:
    app: trading-sto
  ports:
  - name: web
    port: 80
    targetPort: 3000
  - name: backend
    port: 3001
    targetPort: 3001
  type: LoadBalancer
EOF

# infra/kubernetes/ingress.yaml
cat << EOF > infra/kubernetes/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: trading-sto
spec:
  rules:
  - http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: trading-sto
            port:
              number: 80
EOF

# infra/monitoring/grafana-dashboard.yaml
cat << EOF > infra/monitoring/grafana-dashboard.yaml
# Placeholder Grafana dashboard config
EOF

# Dockerfile.web
cat << EOF > Dockerfile.web
FROM node:20-alpine AS base
WORKDIR /app
COPY apps/web/package*.json ./
RUN npm ci

FROM base AS builder
COPY apps/web . .
RUN npm run build

FROM base AS runner
ENV NODE_ENV=production
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
CMD ["npm", "start"]
EOF

# Dockerfile.backend
cat << EOF > Dockerfile.backend
FROM rust:1.70 AS builder
WORKDIR /app
COPY apps/backend . .
RUN cargo build --release

FROM gcr.io/distroless/cc-debian11
COPY --from=builder /app/target/release/trading-sto-backend /app/trading-sto-backend
CMD ["/app/trading-sto-backend"]
EOF

# docker-compose.yml
cat << EOF > docker-compose.yml
version: '3.8'
services:
  web:
    build:
      context: .
      dockerfile: Dockerfile.web
    ports:
      - "3000:3000"
    volumes:
      - ./apps/web:/app
    command: pnpm run dev
  backend:
    build:
      context: .
      dockerfile: Dockerfile.backend
    ports:
      - "3001:3001"
    volumes:
      - ./apps/backend:/app
EOF

# .env.example
cat << EOF > .env.example
INFURA_API_KEY=
GELATO_API_KEY=
NEXT_PUBLIC_API_URL=http://localhost:3001
EOF

# .eslintrc.json
cat << EOF > .eslintrc.json
{
  "env": {
    "browser": true,
    "es2021": true,
    "node": true
  },
  "extends": [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:@typescript-eslint/recommended"
  ],
  "parser": "@typescript-eslint/parser",
  "plugins": ["react", "@typescript-eslint", "jsx-a11y"],
  "rules": {
    "react/prop-types": "off"
  }
}
EOF

# .github/workflows/deploy.yml
cat << EOF > .github/workflows/deploy.yml
name: Deploy to K8s
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Install pnpm
      uses: pnpm/action-setup@v3
      with:
        version: 8
    - name: Build Docker images
      run: |
        docker build -f Dockerfile.web -t trading-sto-web .
        docker build -f Dockerfile.backend -t trading-sto-backend .
    - name: Deploy to K8s
      run: kubectl apply -f infra/kubernetes
EOF

# README.md
cat << EOF > README.md
# TradingSTO

Web3 trading platform with TypeScript frontend (Next.js in apps/web) and Rust backend (Axum in apps/backend), managed by Turborepo with pnpm.

## Setup
1. Install dependencies:
   - Root: \`pnpm install\`
   - Backend: \`cd apps/backend && cargo build\`

## Run locally
- Run all apps (web + backend):  
  \`pnpm run dev\`

- Run only web (Next.js):  
  \`pnpm turbo run dev --filter=web\`

- Run only backend (Rust):  
  \`pnpm turbo run dev --filter=backend\`

## Access
- Web: http://localhost:3000
- Backend (Axum): http://localhost:3001 (if you set it to run on 3001)
EOF


# .gitignore
cat << EOF > .gitignore
node_modules
.next
target
.env
*.log
EOF

echo "Project structure with Turborepo (pnpm) created successfully in $PROJECT_ROOT!"
echo ">>> Script finished successfully"
exit 0
