import api from './api';

export const accountService = {
  // Récupérer tous les comptes
  getAllAccounts: async () => {
    try {
      const response = await api.get('/accounts');
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Récupérer les comptes actifs
  getActiveAccounts: async () => {
    try {
      const response = await api.get('/accounts/active');
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Récupérer un compte par ID
  getAccountById: async (id) => {
    try {
      const response = await api.get(`/accounts/${id}`);
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Récupérer le solde d'un compte
  getAccountBalance: async (id) => {
    try {
      const response = await api.get(`/accounts/${id}/balance`);
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Créer un compte
  createAccount: async (accountData) => {
    try {
      const response = await api.post('/accounts', {
        account_number: accountData.accountNumber,
        account_name: accountData.accountName,
        account_type: accountData.accountType,
        description: accountData.description,
      });
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Mettre à jour un compte
  updateAccount: async (id, accountData) => {
    try {
      const response = await api.put(`/accounts/${id}`, accountData);
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },

  // Récupérer les comptes par type
  getAccountsByType: async (type) => {
    try {
      const response = await api.get(`/accounts/type/${type}`);
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },
};

export default accountService;
