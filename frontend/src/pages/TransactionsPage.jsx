import React, { useState, useEffect } from 'react';
import CreateTransactionForm from '../components/CreateTransactionForm';
import TransactionList from '../components/TransactionList';
import accountService from '../services/accountService';
import { getApiErrorMessage } from '../services/api';
import '../styles/TransactionsPage.css';

export default function TransactionsPage() {
  const [accounts, setAccounts] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [selectedAccountId, setSelectedAccountId] = useState('');
  const [refreshKey, setRefreshKey] = useState(0);
  const [loadError, setLoadError] = useState(null);

  useEffect(() => {
    loadAccounts();
  }, []);

  const loadAccounts = async () => {
    try {
      const data = await accountService.getActiveAccounts();
      setAccounts(data || []);
      setLoadError(null);
    } catch (err) {
      const message = getApiErrorMessage(err, 'Erreur lors du chargement des comptes');
      setLoadError(message);
      console.error('Erreur lors du chargement des comptes:', err);
    }
  };

  const handleTransactionCreated = () => {
    setShowForm(false);
    setRefreshKey((prev) => prev + 1);
  };

  return (
    <div className="transactions-page">
      <div className="page-header">
        <div>
          <h1>Journal des Transactions</h1>
          <p className="page-subtitle">
            Suivez l'ensemble des mouvements comptables et filtrez par compte.
          </p>
        </div>
        <button
          className="btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? 'Annuler' : '+ Nouvelle transaction'}
        </button>
      </div>

      <div className="toolbar">
        <div className="filter">
          <label>Filtrer par compte</label>
          <select
            value={selectedAccountId}
            onChange={(e) => setSelectedAccountId(e.target.value)}
          >
            <option value="">Tous les comptes</option>
            {accounts.map((account) => (
              <option key={account.id} value={account.id}>
                {account.account_number} - {account.account_name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {loadError && <div className="error">Erreur: {loadError}</div>}

      <div className="page-content">
        {showForm && (
          <CreateTransactionForm
            accounts={accounts}
            onTransactionCreated={handleTransactionCreated}
          />
        )}

        <TransactionList
          key={refreshKey}
          accountId={selectedAccountId || null}
        />
      </div>
    </div>
  );
}
