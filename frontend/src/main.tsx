import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { Activity, AlertTriangle, BrainCircuit, Database, Play, RefreshCw, Server, Zap } from 'lucide-react';
import './styles.css';

type Severity = 'INFO' | 'WARN' | 'ERROR' | 'CRITICAL';

type ServiceEvent = {
  id?: number;
  serviceName: string;
  eventType: string;
  severity: Severity;
  message: string;
  latencyMs: number;
  statusCode: number;
  traceId: string;
  timestamp: string;
};

type Incident = {
  id: number;
  title: string;
  summary: string;
  severity: Severity;
  status: string;
  incidentType: string;
  primaryService: string;
  affectedServices: string;
  aiAnalysisJson?: string;
  createdAt: string;
};

type AiAnalysis = {
  title: string;
  summary: string;
  rootCause: string;
  affectedServices: string[];
  timeline: string[];
  recommendedDebuggingSteps: string[];
};

type DashboardSummary = {
  totalEvents: number;
  openIncidents: number;
  criticalIncidents: number;
  averageLatencyMs: number;
  eventsByService: Record<string, number>;
  incidentsBySeverity: Record<string, number>;
};

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

function severityClass(severity: Severity) {
  return `badge ${severity.toLowerCase()}`;
}

function parseAnalysis(incident?: Incident): AiAnalysis | undefined {
  if (!incident?.aiAnalysisJson) return undefined;
  try {
    return JSON.parse(incident.aiAnalysisJson);
  } catch {
    return undefined;
  }
}

function App() {
  const [events, setEvents] = useState<ServiceEvent[]>([]);
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [summary, setSummary] = useState<DashboardSummary>();
  const [selectedId, setSelectedId] = useState<number>();
  const [analysis, setAnalysis] = useState<AiAnalysis>();
  const [loading, setLoading] = useState(false);

  const selectedIncident = useMemo(
    () => incidents.find((incident) => incident.id === selectedId) || incidents[0],
    [incidents, selectedId]
  );

  async function refresh() {
    const [eventsRes, incidentsRes, summaryRes] = await Promise.all([
      fetch(`${API_BASE}/api/events/latest`),
      fetch(`${API_BASE}/api/incidents`),
      fetch(`${API_BASE}/api/dashboard/summary`)
    ]);
    setEvents(await eventsRes.json());
    const nextIncidents = await incidentsRes.json();
    setIncidents(nextIncidents);
    setSummary(await summaryRes.json());
    if (!selectedId && nextIncidents.length) setSelectedId(nextIncidents[0].id);
  }

  async function generate(scenario = 'mixed') {
    setLoading(true);
    await fetch(`${API_BASE}/api/events/generate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ scenario })
    });
    await refresh();
    setLoading(false);
  }

  async function analyze(id?: number) {
    if (!id) return;
    setLoading(true);
    const res = await fetch(`${API_BASE}/api/incidents/${id}/analyze`, { method: 'POST' });
    setAnalysis(await res.json());
    await refresh();
    setLoading(false);
  }

  useEffect(() => {
    refresh();
    const timer = window.setInterval(refresh, 5000);
    return () => window.clearInterval(timer);
  }, []);

  useEffect(() => {
    setAnalysis(parseAnalysis(selectedIncident));
  }, [selectedIncident?.id]);

  const activeAnalysis = analysis || parseAnalysis(selectedIncident);

  return (
    <main>
      <header className="topbar">
        <div>
          <p className="eyebrow">mariapreethi-12 / ai-incident-intelligence-platform</p>
          <h1>AI Incident Intelligence Platform</h1>
        </div>
        <div className="actions">
          <button onClick={() => refresh()} title="Refresh data"><RefreshCw size={18} />Refresh</button>
          <button onClick={() => generate()} disabled={loading} title="Generate synthetic service events"><Play size={18} />Generate</button>
        </div>
      </header>

      <section className="metrics">
        <Metric icon={<Activity />} label="Total events" value={summary?.totalEvents ?? 0} />
        <Metric icon={<AlertTriangle />} label="Open incidents" value={summary?.openIncidents ?? 0} />
        <Metric icon={<Zap />} label="Critical incidents" value={summary?.criticalIncidents ?? 0} />
        <Metric icon={<Server />} label="Average latency" value={`${summary?.averageLatencyMs ?? 0} ms`} />
      </section>

      <section className="layout">
        <div className="panel">
          <div className="panelHeader">
            <h2>Live Service Events</h2>
            <div className="quickActions">
              {['payment-failure', 'database-timeout', 'latency-spike', '5xx-burst'].map((scenario) => (
                <button key={scenario} onClick={() => generate(scenario)}>{scenario}</button>
              ))}
            </div>
          </div>
          <div className="eventList">
            {events.map((event) => (
              <article className="eventRow" key={event.id ?? event.traceId}>
                <span className={severityClass(event.severity)}>{event.severity}</span>
                <div>
                  <strong>{event.serviceName}</strong>
                  <p>{event.message}</p>
                  <small>{event.statusCode} · {event.latencyMs}ms · {event.traceId.slice(0, 8)}</small>
                </div>
              </article>
            ))}
          </div>
        </div>

        <div className="panel">
          <div className="panelHeader">
            <h2>Incidents</h2>
            <button onClick={() => analyze(selectedIncident?.id)} disabled={!selectedIncident || loading} title="Run AI root-cause analysis">
              <BrainCircuit size={18} />Analyze
            </button>
          </div>
          <div className="incidentList">
            {incidents.map((incident) => (
              <button className={`incidentCard ${incident.id === selectedIncident?.id ? 'active' : ''}`} key={incident.id} onClick={() => setSelectedId(incident.id)}>
                <span className={severityClass(incident.severity)}>{incident.severity}</span>
                <strong>{incident.title}</strong>
                <small>{incident.primaryService} · {incident.status}</small>
                <p>{incident.summary}</p>
              </button>
            ))}
          </div>
        </div>
      </section>

      <section className="aiPanel">
        <div className="aiSummary">
          <div className="sectionTitle"><BrainCircuit size={20} /><h2>AI Root Cause Summary</h2></div>
          {activeAnalysis ? (
            <>
              <h3>{activeAnalysis.title}</h3>
              <p>{activeAnalysis.summary}</p>
              <p><strong>Likely root cause:</strong> {activeAnalysis.rootCause}</p>
              <div className="chips">{activeAnalysis.affectedServices.map((service) => <span key={service}>{service}</span>)}</div>
            </>
          ) : (
            <p>Select or generate an incident, then run analysis to create an AI incident brief.</p>
          )}
        </div>
        <div>
          <h3>Timeline</h3>
          <ol>{activeAnalysis?.timeline?.map((item) => <li key={item}>{item}</li>)}</ol>
        </div>
        <div>
          <h3>Debugging Steps</h3>
          <ol>{activeAnalysis?.recommendedDebuggingSteps?.map((item) => <li key={item}>{item}</li>)}</ol>
        </div>
      </section>

      <section className="architecture">
        <div className="sectionTitle"><Database size={20} /><h2>Architecture</h2></div>
        <div className="flow">
          <span>Simulated services</span>
          <span>Kafka topic: service-events</span>
          <span>Spring consumer</span>
          <span>PostgreSQL</span>
          <span>AI RCA API</span>
          <span>React dashboard</span>
        </div>
      </section>
    </main>
  );
}

function Metric({ icon, label, value }: { icon: React.ReactNode; label: string; value: number | string }) {
  return (
    <article className="metric">
      <div>{icon}</div>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

createRoot(document.getElementById('root')!).render(<App />);
