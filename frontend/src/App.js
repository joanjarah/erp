import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import DashboardPage from './pages/DashboardPage';
import AccountsPage from './pages/AccountsPage';
import TransactionsPage from './pages/TransactionsPage';
import ReportsPage from './pages/ReportsPage';
import './App.css';

function DashboardIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 11l9-8 9 8" />
      <path d="M5 10v10h14V10" />
      <path d="M10 20v-6h4v6" />
    </svg>
  );
}

function AccountsIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
    </svg>
  );
}

function TransactionsIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M7 7h13" />
      <path d="M13 3l4 4-4 4" />
      <path d="M17 17H4" />
      <path d="M11 21l-4-4 4-4" />
    </svg>
  );
}

function ReportsIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 3v18h18" />
      <path d="M8 15v-4" />
      <path d="M12 15V9" />
      <path d="M16 15V6" />
      <path d="M20 15v-2" />
    </svg>
  );
}

const NAV_ITEMS = [
  { to: '/', label: 'Tableau de bord', icon: <DashboardIcon />, end: true },
  { to: '/accounts', label: 'Comptes', icon: <AccountsIcon /> },
  { to: '/transactions', label: 'Transactions', icon: <TransactionsIcon /> },
  { to: '/reports', label: 'Reporting', icon: <ReportsIcon /> },
];

function App() {
  const [expanded, setExpanded] = useState(false);

  return (
    <Router>
      <div className="app-shell">
        <aside className={`sidebar ${expanded ? 'expanded' : 'collapsed'}`}>
          <div className="sidebar-top">
            <button
              type="button"
              className="hamburger-btn"
              onClick={() => setExpanded((prev) => !prev)}
              aria-label={expanded ? 'Masquer le menu' : 'Afficher le menu'}
            >
              <span />
              <span />
              <span />
            </button>

            <div className="brand-meta">
              <h1>Compta ERP</h1>
              <p>Module comptable</p>
            </div>
          </div>

          <nav className="side-nav" aria-label="Navigation principale">
            {NAV_ITEMS.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                title={item.label}
                className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              >
                <span className="icon-wrap" aria-hidden="true">{item.icon}</span>
                <span className="label">{item.label}</span>
              </NavLink>
            ))}
          </nav>

          <div className="sidebar-footer">
            <span className="chip">v1.0.0</span>
          </div>
        </aside>

        <section className="content-shell">
          <main className="main-content">
            <Routes>
              <Route path="/" element={<DashboardPage />} />
              <Route path="/accounts" element={<AccountsPage />} />
              <Route path="/transactions" element={<TransactionsPage />} />
              <Route path="/reports" element={<ReportsPage />} />
            </Routes>
          </main>

          <footer className="app-footer">
            <p>ERP Accounting Module - Ariary (Ar)</p>
          </footer>
        </section>
      </div>
    </Router>
  );
}

export default App;
