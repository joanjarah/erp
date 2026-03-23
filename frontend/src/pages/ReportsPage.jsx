import React, { useCallback, useEffect, useMemo, useState } from 'react';
import reportService from '../services/reportService';
import { getApiErrorMessage } from '../services/api';
import '../styles/ReportsPage.css';

const MONTHS = [
  { value: 1, label: 'Janvier' },
  { value: 2, label: 'Fevrier' },
  { value: 3, label: 'Mars' },
  { value: 4, label: 'Avril' },
  { value: 5, label: 'Mai' },
  { value: 6, label: 'Juin' },
  { value: 7, label: 'Juillet' },
  { value: 8, label: 'Aout' },
  { value: 9, label: 'Septembre' },
  { value: 10, label: 'Octobre' },
  { value: 11, label: 'Novembre' },
  { value: 12, label: 'Decembre' },
];

export default function ReportsPage() {
  const currentYear = new Date().getFullYear();
  const currentMonth = new Date().getMonth() + 1;
  const [year, setYear] = useState(currentYear);
  const [month, setMonth] = useState(currentMonth);
  const [summary, setSummary] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const years = useMemo(
    () => Array.from({ length: 5 }, (_, idx) => currentYear - 2 + idx),
    [currentYear]
  );

  const loadSummary = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await reportService.getMonthlySummary(year, month);
      setSummary(data || []);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Impossible de charger le reporting mensuel.'));
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [year, month]);

  useEffect(() => {
    loadSummary();
  }, [loadSummary]);

  const totals = useMemo(() => {
    return summary.reduce(
      (acc, item) => {
        acc.debits += Number(item.month_debits || 0);
        acc.credits += Number(item.month_credits || 0);
        acc.net += Number(item.month_net_change || 0);
        return acc;
      },
      { debits: 0, credits: 0, net: 0 }
    );
  }, [summary]);

  return (
    <div className="reports-page">
      <div className="page-header">
        <div>
          <h1>Reporting Mensuel</h1>
          <p className="page-subtitle">
            Visualisez la performance comptable par compte et suivez l'evolution mensuelle.
          </p>
        </div>
        <div className="filters">
          <div className="filter">
            <label>Mois</label>
            <select value={month} onChange={(e) => setMonth(Number(e.target.value))}>
              {MONTHS.map((m) => (
                <option key={m.value} value={m.value}>
                  {m.label}
                </option>
              ))}
            </select>
          </div>
          <div className="filter">
            <label>Annee</label>
            <select value={year} onChange={(e) => setYear(Number(e.target.value))}>
              {years.map((y) => (
                <option key={y} value={y}>
                  {y}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      <div className="summary-grid">
        <div className="summary-card">
          <span>Total debits</span>
          <strong>{totals.debits.toFixed(2)} Ar</strong>
        </div>
        <div className="summary-card accent">
          <span>Total credits</span>
          <strong>{totals.credits.toFixed(2)} Ar</strong>
        </div>
        <div className="summary-card dark">
          <span>Variation nette</span>
          <strong>{totals.net.toFixed(2)} Ar</strong>
        </div>
      </div>

      <div className="report-table card">
        <div className="card-header">
          <h2>Detail par compte</h2>
          <span className="chip">{summary.length} comptes analyses</span>
        </div>

        {loading && <div className="loading">Chargement...</div>}
        {error && <div className="error">{error}</div>}

        {!loading && !error && (
          <table className="table">
            <thead>
              <tr>
                <th>Compte</th>
                <th>Type</th>
                <th>Solde actuel</th>
                <th>Debits</th>
                <th>Credits</th>
                <th>Variation</th>
              </tr>
            </thead>
            <tbody>
              {summary.map((item) => (
                <tr key={item.id}>
                  <td>
                    <div className="account-main">
                      <strong>{item.account_number}</strong>
                      <span>{item.account_name}</span>
                    </div>
                  </td>
                  <td>{item.account_type}</td>
                  <td className="balance">{Number(item.current_balance || 0).toFixed(2)} Ar</td>
                  <td className="debit">{Number(item.month_debits || 0).toFixed(2)} Ar</td>
                  <td className="credit">{Number(item.month_credits || 0).toFixed(2)} Ar</td>
                  <td className="net">{Number(item.month_net_change || 0).toFixed(2)} Ar</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {!loading && !error && summary.length === 0 && (
          <div className="no-data">Aucune donnee disponible pour cette periode.</div>
        )}
      </div>
    </div>
  );
}
