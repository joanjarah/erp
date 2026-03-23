import React, { useCallback, useEffect, useState } from 'react';
import transactionService from '../services/transactionService';
import { getApiErrorMessage } from '../services/api';
import { format } from 'date-fns';
import '../styles/TransactionList.css';

export default function TransactionList({ accountId }) {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadTransactions = useCallback(async () => {
    try {
      setLoading(true);
      let data;

      if (accountId) {
        data = await transactionService.getTransactionsByAccount(accountId);
      } else {
        data = await transactionService.getAllTransactions();
      }

      setTransactions(data || []);
      setError(null);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Erreur lors du chargement des transactions'));
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [accountId]);

  useEffect(() => {
    loadTransactions();
  }, [loadTransactions]);

  if (loading) return <div className="loading">Chargement...</div>;
  if (error) return <div className="error">Erreur: {error}</div>;

  return (
    <div className="transaction-list-container card">
      <div className="card-header">
        <div>
          <h2>Journal des Transactions</h2>
          <p className="card-subtitle">Historique des operations comptables.</p>
        </div>
        <span className="chip">{transactions.length} operations</span>
      </div>

      <table className="transactions-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Compte</th>
            <th>Type</th>
            <th>Montant</th>
            <th>Reference</th>
            <th>Description</th>
            <th>Statut</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((transaction) => (
            <tr key={transaction.id}>
              <td>{transaction.transaction_date ? format(new Date(transaction.transaction_date), 'dd/MM/yyyy') : '-'}</td>
              <td>{transaction.account_number} - {transaction.account_name}</td>
              <td className={`type type-${(transaction.transaction_type || 'unknown').toLowerCase()}`}>
                {transaction.transaction_type === 'DEBIT' ? 'Debit' : transaction.transaction_type === 'CREDIT' ? 'Credit' : 'N/A'}
              </td>
              <td className="amount">
                {Number(transaction.amount || 0).toFixed(2)} Ar
              </td>
              <td>{transaction.reference_number || '-'}</td>
              <td>{transaction.description || '-'}</td>
              <td>
                <span className={`status status-${(transaction.status || 'unknown').toLowerCase()}`}>
                  {transaction.status || 'UNKNOWN'}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {transactions.length === 0 && (
        <div className="no-data">Aucune transaction trouvee</div>
      )}
    </div>
  );
}
