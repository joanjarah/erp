import api from './api';

export const reportService = {
  getMonthlySummary: async (year, month) => {
    try {
      const response = await api.get('/reports/monthly', {
        params: { year, month },
      });
      return response.data.data;
    } catch (error) {
      throw error;
    }
  },
};

export default reportService;
