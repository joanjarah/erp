import React, { useState } from 'react';
import transactionService from '../services/transactionService';
import '../styles/CreateTransactionForm.css';

export default function CreateTransactionForm({ accounts, onTransactionCreated }) {
  const [formData, setFormData] = useState({
    accountId: '',
    transactionType: 'DEBIT',
    amount: '',
    transactionDate: new Date().toISOString().split('T')[0],
    referenceNumber: '',
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
      if (!formData.accountId) {
        throw new Error('Veuillez sélectionner un compte');
      }

      const newTransaction = await transactionService.createTransaction({
        ...formData,
        accountId: parseInt(formData.accountId),
        amount: parseFloat(formData.amount),
      });

      setSuccess('Transaction créée avec succès!');
      setFormData({
        accountId: '',
        transactionType: 'DEBIT',
        amount: '',
        transactionDate: new Date().toISOString().split('T')[0],
        referenceNumber: '',
        description: '',
      });

      if (onTransactionCreated) {
        onTransactionCreated(newTransaction);
      }

      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.message || err.message || 'Erreur lors de la création';
      setError(errorMsg);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-transaction-form">
      <h2>Ajouter une transaction</h2>

      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="accountId">Compte *</label>
          <select
            id="accountId"
            name="accountId"
            value={formData.accountId}
            onChange={handleInputChange}
            required
          >
            <option value="">-- Sélectionner un compte --</option>
            {accounts.map((account) => (
              <option key={account.id} value={account.id}>
                {account.account_number} - {account.account_name}
              </option>
            ))}
          </select>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="transactionType">Type *</label>
            <select
              id="transactionType"
              name="transactionType"
              value={formData.transactionType}
              onChange={handleInputChange}
              required
            >
              <option value="DEBIT">Débit</option>
              <option value="CREDIT">Crédit</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="amount">Montant *</label>
            <input
              type="number"
              id="amount"
              name="amount"
              value={formData.amount}
              onChange={handleInputChange}
              placeholder="0.00"
              step="0.01"
              min="0"
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="transactionDate">Date *</label>
          <input
            type="date"
            id="transactionDate"
            name="transactionDate"
            value={formData.transactionDate}
            onChange={handleInputChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="referenceNumber">Numéro de référence</label>
          <input
            type="text"
            id="referenceNumber"
            name="referenceNumber"
            value={formData.referenceNumber}
            onChange={handleInputChange}
            placeholder="Ex: FAC-001"
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleInputChange}
            placeholder="Détails de la transaction..."
            rows="3"
          />
        </div>

        <button type="submit" disabled={loading} className="btn-submit">
          {loading ? 'Création en cours...' : 'Créer la transaction'}
        </button>
      </form>
    </div>
  );
}
