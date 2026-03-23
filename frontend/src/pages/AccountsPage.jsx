import React, { useState } from 'react';
import AccountList from '../components/AccountList';
import CreateAccountForm from '../components/CreateAccountForm';
import accountService from '../services/accountService';
import '../styles/AccountsPage.css';

export default function AccountsPage() {
  const [showForm, setShowForm] = useState(false);

  const handleAccountCreated = async () => {
    await accountService.getAllAccounts();
    setShowForm(false);
  };

  return (
    <div className="accounts-page">
      <div className="page-header">
        <div>
          <h1>Gestion des Comptes</h1>
          <p className="page-subtitle">
            Structurez votre plan comptable et suivez les soldes en temps réel.
          </p>
        </div>
        <button
          className="btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? 'Annuler' : '+ Nouveau compte'}
        </button>
      </div>

      <div className="page-content">
        {showForm && (
          <CreateAccountForm onAccountCreated={handleAccountCreated} />
        )}

        <AccountList />
      </div>
    </div>
  );
}
