import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: false,
});

export const getApiErrorMessage = (error, fallbackMessage = 'Erreur de communication avec le serveur') => {
  if (error?.response?.data?.message) {
    return error.response.data.message;
  }
  if (error?.message) {
    return error.message;
  }
  return fallbackMessage;
};

// Interceptor pour les erreurs
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      console.error('Erreur API:', error.response.status, error.response.data);
    } else {
      console.error('Erreur API (réseau):', error.message);
    }
    return Promise.reject(error);
  }
);

export default api;
