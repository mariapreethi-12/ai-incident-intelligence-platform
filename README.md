# AI Incident Intelligence Platform

Repository: `mariapreethi-12/ai-incident-intelligence-platform`

A full-stack observability and incident response project that behaves like a focused mini Datadog/PagerDuty: simulated microservices generate logs, errors, latency spikes, and timeout signals; Spring Boot streams those events through Kafka into PostgreSQL; incident detection opens actionable alerts; and an AI incident analyst produces root-cause summaries, timelines, affected services, and debugging steps.

[![Deploy backend on Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/mariapreethi-12/ai-incident-intelligence-platform)
[![Deploy frontend on Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https://github.com/mariapreethi-12/ai-incident-intelligence-platform&root-directory=frontend&env=VITE_API_BASE_URL)

## Why this project is recruiter-impressive

- Uses a production-style backend stack: Java 17, Spring Boot 3, Spring Kafka, PostgreSQL, Docker, and environment-driven deployment config.
- Demonstrates event-driven architecture with a real local Kafka pipeline and a free-hosting demo mode that does not require paid Kafka.
- Includes AI integration with `OPENAI_API_KEY`, plus a realistic mock fallback so reviewers can test the workflow instantly.
- Ships a React + Vite + TypeScript dashboard with live event streams, incident cards, severity badges, and manual incident analysis controls.

## Architecture

```mermaid
flowchart LR
    A[Simulated services] --> B[Spring Boot producer]
    B --> C[(Kafka topic: service-events)]
    C --> D[Spring Kafka consumer]
    D --> E[(PostgreSQL)]
    D --> F[Incident detection service]
    F --> E
    G[React dashboard] --> H[Spring REST APIs]
    H --> E
    H --> I[OpenAI API]
    I --> H
    J[DEMO_MODE scheduler] --> F
```

## Monorepo structure

```text
backend/
  src/main/java/com/mariapreethi/incidentintelligence/
    config/
    controller/
    dto/
    kafka/
    model/
    repository/
    scheduler/
    service/
frontend/
docker-compose.yml
README.md
```

## Local setup with real Kafka

Prerequisites: Docker, Docker Compose, Java 17, Maven, Node 20.

1. Clone the repo.

   ```bash
   git clone https://github.com/mariapreethi-12/ai-incident-intelligence-platform.git
   cd ai-incident-intelligence-platform
   ```

2. Start PostgreSQL, Kafka, backend, and optionally frontend.

   ```bash
   docker compose --profile frontend up --build
   ```

   Backend: `http://localhost:8080`  
   Frontend: `http://localhost:5173`

3. Or run frontend locally outside Docker.

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

4. Generate telemetry from the dashboard or API.

   ```bash
   curl -X POST http://localhost:8080/api/events/generate \
     -H "Content-Type: application/json" \
     -d '{"scenario":"payment-failure"}'
   ```

## Free deployment mode

Free hosting usually does not include a managed Kafka cluster. This app supports a deployable demo mode:

```text
DEMO_MODE=true
KAFKA_ENABLED=false
```

When both are set, Spring scheduled jobs generate realistic service events and send them directly through the same ingestion and incident detection services used by the Kafka consumer. The live demo still shows events, incidents, AI analysis, and dashboard summaries without requiring Kafka.

## Backend environment variables

| Variable | Purpose | Example |
| --- | --- | --- |
| `PORT` | HTTP port for Render or local runtime | `8080` |
| `DATABASE_URL` | PostgreSQL connection string | `postgres://user:pass@host:5432/db` |
| `FRONTEND_URL` | Allowed CORS frontend origin | `https://your-app.vercel.app` |
| `OPENAI_API_KEY` | Optional OpenAI key for real AI analysis | `sk-...` |
| `DEMO_MODE` | Enables scheduled synthetic telemetry | `true` |
| `KAFKA_ENABLED` | Enables Kafka producer and consumer | `true` locally, `false` on free demo |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka bootstrap servers | `localhost:9092` |

## Frontend environment variables

| Variable | Purpose | Example |
| --- | --- | --- |
| `VITE_API_BASE_URL` | Backend API base URL | `https://your-render-service.onrender.com` |

## API list

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/health` | Health check for deployment |
| `GET` | `/api/events/latest` | Latest generated service events |
| `POST` | `/api/events/generate` | Generate a batch of synthetic events |
| `GET` | `/api/incidents` | List recent incidents |
| `GET` | `/api/incidents/{id}` | Get one incident |
| `POST` | `/api/incidents/{id}/analyze` | Generate AI root-cause analysis |
| `GET` | `/api/dashboard/summary` | Dashboard metrics summary |

Supported generation scenarios: `mixed`, `payment-failure`, `database-timeout`, `latency-spike`, `5xx-burst`.

## Render deployment

1. Create a free PostgreSQL database on Render or another provider such as Neon or Supabase.
2. Create a new Render Web Service from `mariapreethi-12/ai-incident-intelligence-platform`.
3. Render can use the included `render.yaml` Blueprint, or you can configure the service manually.
4. Set root directory to `backend`.
5. Use Docker runtime. Render will use `backend/Dockerfile`.
6. Add environment variables:

   ```text
   PORT=8080
   DATABASE_URL=<your postgres URL>
   FRONTEND_URL=https://<your-vercel-app>.vercel.app
   DEMO_MODE=true
   KAFKA_ENABLED=false
   OPENAI_API_KEY=<optional>
   ```

7. Confirm `GET /api/health` returns `status: UP`.

## Vercel deployment

1. Import the same GitHub repo into Vercel.
2. Set root directory to `frontend`.
3. Vercel can use the included `frontend/vercel.json` project configuration.
4. Set build command to `npm run build`.
5. Set output directory to `dist`.
6. Add:

   ```text
   VITE_API_BASE_URL=https://<your-render-service>.onrender.com
   ```

## Resume bullets

- Built an AI-powered incident intelligence platform with Java 17, Spring Boot 3, Spring Kafka, PostgreSQL, Docker, React, and TypeScript to simulate production observability workflows.
- Implemented event-driven ingestion using Kafka producers/consumers, automated incident detection for 5xx bursts, latency spikes, payment failures, and database timeouts, plus PostgreSQL persistence.
- Integrated OpenAI-powered root-cause analysis with graceful mock fallback, generating incident summaries, timelines, affected services, and debugging recommendations.
- Designed a free-deploy demo mode for Render/Vercel that preserves the full product workflow without requiring managed Kafka infrastructure.

## Quick demo script

1. Open the dashboard.
2. Click `payment-failure` or `database-timeout` to generate high-severity events.
3. Select the new incident card.
4. Click `Analyze`.
5. Walk through the AI root cause, timeline, and debugging steps.
