import React, { useState } from 'react';
import accountService from '../services/accountService';
import { getApiErrorMessage } from '../services/api';
import '../styles/CreateAccountForm.css';

const ACCOUNT_TYPES = [
  { value: 'ACTIF', label: 'Actif' },
  { value: 'PASSIF', label: 'Passif' },
  { value: 'CAPITAUX_PROPRES', label: 'Capitaux propres' },
  { value: 'PRODUITS', label: 'Produits' },
  { value: 'CHARGES', label: 'Charges' },
  { value: 'TRESORERIE', label: 'Tresorerie' },
];

export default function CreateAccountForm({ onAccountCreated }) {
  const [formData, setFormData] = useState({
    accountNumber: '',
    accountName: '',
    accountType: 'ACTIF',
    description: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const newAccount = await accountService.createAccount(formData);
      setSuccess('Compte cree avec succes');
      setFormData({
        accountNumber: '',
        accountName: '',
        accountType: 'ACTIF',
        description: '',
      });

      if (onAccountCreated) {
        onAccountCreated(newAccount);
      }

      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Erreur lors de la creation du compte'));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-account-form">
      <h2>Creer un nouveau compte</h2>

      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="accountNumber">Numero de compte *</label>
          <input
            type="text"
            id="accountNumber"
            name="accountNumber"
            value={formData.accountNumber}
            onChange={handleInputChange}
            placeholder="Ex: 1010"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="accountName">Nom du compte *</label>
          <input
            type="text"
            id="accountName"
            name="accountName"
            value={formData.accountName}
            onChange={handleInputChange}
            placeholder="Ex: Caisse"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="accountType">Type de compte *</label>
          <select
            id="accountType"
            name="accountType"
            value={formData.accountType}
            onChange={handleInputChange}
            required
          >
            {ACCOUNT_TYPES.map((type) => (
              <option key={type.value} value={type.value}>
                {type.label}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleInputChange}
            placeholder="Decrivez ce compte..."
            rows="3"
          />
        </div>

        <button type="submit" disabled={loading} className="btn-submit">
          {loading ? 'Creation en cours...' : 'Creer le compte'}
        </button>
      </form>
    </div>
  );
}
