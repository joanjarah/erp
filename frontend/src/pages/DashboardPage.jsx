import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import accountService from '../services/accountService';
import '../styles/DashboardPage.css';

const ACCOUNT_TYPE = {
  ACTIF: 'ACTIF',
  PASSIF: 'PASSIF',
  CAPITAUX_PROPRES: 'CAPITAUX_PROPRES',
  TRESORERIE: 'TRESORERIE',
};

export default function DashboardPage() {
  const [stats, setStats] = useState({
    totalAccounts: 0,
    totalAssets: 0,
    totalLiabilities: 0,
    totalEquity: 0,
  });
  const [loading, setLoading] = useState(true);
  const [accounts, setAccounts] = useState([]);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const data = await accountService.getAllAccounts();
      setAccounts(data || []);

      const totals = (data || []).reduce(
        (acc, account) => {
          const balance = Number(account.balance || 0);
          const type = account.account_type;
          return {
            totalAccounts: acc.totalAccounts + 1,
            totalAssets:
              type === ACCOUNT_TYPE.ACTIF || type === ACCOUNT_TYPE.TRESORERIE
                ? acc.totalAssets + balance
                : acc.totalAssets,
            totalLiabilities:
              type === ACCOUNT_TYPE.PASSIF
                ? acc.totalLiabilities + balance
                : acc.totalLiabilities,
            totalEquity:
              type === ACCOUNT_TYPE.CAPITAUX_PROPRES
                ? acc.totalEquity + balance
                : acc.totalEquity,
          };
        },
        { totalAccounts: 0, totalAssets: 0, totalLiabilities: 0, totalEquity: 0 }
      );

      setStats(totals);
    } catch (err) {
      console.error('Erreur dashboard:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="dashboard-page">
      <div className="hero">
        <div>
          <h1>Tableau de bord comptable</h1>
          <p>
            Une vue synthetique et fiable de la sante financiere de votre entreprise.
          </p>
        </div>
        <div className="hero-actions">
          <Link to="/accounts" className="hero-action hero-action-secondary">
            <span className="hero-action-title">Gerer les comptes</span>
            <span className="hero-action-subtitle">Plan comptable et soldes</span>
          </Link>
          <Link to="/transactions" className="hero-action hero-action-primary">
            <span className="hero-action-title">Ajouter une transaction</span>
            <span className="hero-action-subtitle">Debit ou credit en 1 clic</span>
          </Link>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <h3>Nombre de comptes</h3>
          <p className="stat-value">{stats.totalAccounts}</p>
        </div>

        <div className="stat-card asset">
          <h3>Total actifs</h3>
          <p className="stat-value">{stats.totalAssets.toFixed(2)} Ar</p>
        </div>

        <div className="stat-card liability">
          <h3>Total passifs</h3>
          <p className="stat-value">{stats.totalLiabilities.toFixed(2)} Ar</p>
        </div>

        <div className="stat-card equity">
          <h3>Total capitaux propres</h3>
          <p className="stat-value">{stats.totalEquity.toFixed(2)} Ar</p>
        </div>
      </div>

      <div className="quick-links">
        <Link to="/accounts" className="link-btn">
          Gerer les comptes
        </Link>
        <Link to="/transactions" className="link-btn">
          Journal des transactions
        </Link>
        <Link to="/reports" className="link-btn">
          Reporting mensuel
        </Link>
      </div>

      <div className="recent-accounts card">
        <div className="card-header">
          <h2>Comptes recents</h2>
          <span className="chip">{accounts.slice(0, 5).length} affiches</span>
        </div>
        <table className="mini-table">
          <thead>
            <tr>
              <th>Compte</th>
              <th>Type</th>
              <th>Solde</th>
            </tr>
          </thead>
          <tbody>
            {accounts.slice(0, 5).map((account) => (
              <tr key={account.id}>
                <td>
                  {account.account_number} - {account.account_name}
                </td>
                <td>{account.account_type}</td>
                <td className="amount">
                  {Number(account.balance || 0).toFixed(2)} Ar
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
