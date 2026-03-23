import React, { useCallback, useEffect, useState } from 'react';
import accountService from '../services/accountService';
import { getApiErrorMessage } from '../services/api';
import '../styles/AccountList.css';

export default function AccountList() {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all');

  const loadAccounts = useCallback(async () => {
    try {
      setLoading(true);
      let data;

      if (filter === 'active') {
        data = await accountService.getActiveAccounts();
      } else {
        data = await accountService.getAllAccounts();
      }

      setAccounts(data || []);
      setError(null);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Erreur lors du chargement des comptes'));
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    loadAccounts();
  }, [loadAccounts]);

  if (loading) return <div className="loading">Chargement...</div>;
  if (error) return <div className="error">Erreur: {error}</div>;

  return (
    <div className="account-list-container card">
      <div className="card-header">
        <div>
          <h2>Liste des Comptes</h2>
          <p className="card-subtitle">Vue d'ensemble de votre plan comptable.</p>
        </div>
        <div className="filter-section">
          <button
            className={filter === 'all' ? 'chip active' : 'chip'}
            onClick={() => setFilter('all')}
          >
            Tous les comptes
          </button>
          <button
            className={filter === 'active' ? 'chip active' : 'chip'}
            onClick={() => setFilter('active')}
          >
            Comptes actifs
          </button>
        </div>
      </div>

      <table className="accounts-table">
        <thead>
          <tr>
            <th>Numero</th>
            <th>Nom</th>
            <th>Type</th>
            <th>Solde</th>
            <th>Statut</th>
          </tr>
        </thead>
        <tbody>
          {accounts.map((account) => (
            <tr key={account.id}>
              <td className="account-number">{account.account_number}</td>
              <td>{account.account_name}</td>
              <td>{account.account_type}</td>
              <td className="balance">
                {Number(account.balance || 0).toFixed(2)} Ar
              </td>
              <td>
                <span className={`status status-${(account.status || 'unknown').toLowerCase()}`}>
                  {account.status || 'UNKNOWN'}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {accounts.length === 0 && (
        <div className="no-data">Aucun compte trouve</div>
      )}
    </div>
  );
}
