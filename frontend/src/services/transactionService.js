import api from './api';

export const transactionService = {
  // Récupérer toutes les transactions
  getAllTransactions: async () => {
    try {
      const response = await api.get('/transactions');
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Récupérer une transaction par ID
  getTransactionById: async (id) => {
    try {
      const response = await api.get(`/transactions/${id}`);
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Récupérer les transactions d'un compte
  getTransactionsByAccount: async (accountId) => {
    try {
      const response = await api.get(`/transactions/account/${accountId}`);
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Récupérer les transactions par plage de dates
  getTransactionsByDateRange: async (accountId, startDate, endDate) => {
    try {
      const response = await api.get(`/transactions/account/${accountId}/range`, {
        params: {
          startDate: startDate.toISOString().split('T')[0],
          endDate: endDate.toISOString().split('T')[0],
        },
      });
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Créer une transaction
  createTransaction: async (transactionData) => {
    try {
      const response = await api.post('/transactions', {
        account_id: transactionData.accountId,
        transaction_type: transactionData.transactionType,
        amount: transactionData.amount,
        transaction_date: transactionData.transactionDate,
        reference_number: transactionData.referenceNumber,
        description: transactionData.description,
      });
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Réconcilier une transaction
  reconcileTransaction: async (id, reconciliationDate) => {
    try {
      const response = await api.patch(`/transactions/${id}/reconcile`, null, {
        params: {
          reconciliationDate: reconciliationDate.toISOString().split('T')[0],
        },
      });
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },
};

export default transactionService;
