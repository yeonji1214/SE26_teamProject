# Frontend-Backend Integration Runbook

## 1. Purpose

This document explains how to run the Java HTTP API server and the React Web UI together.

## 2. Backend API Server

Run from the project root:

```powershell
.\gradlew.bat run --args="api"
```

The backend API server runs at:

```text
http://localhost:8080
```

Main API endpoints:

```text
GET    /api/users
POST   /api/login
GET    /api/projects
POST   /api/projects
GET    /api/issues
GET    /api/issues/{id}
POST   /api/issues
PATCH  /api/issues/{id}/status
POST   /api/issues/{id}/comments
GET    /api/statistics
GET    /api/issues/{id}/recommendations
```

## 3. Frontend Dev Server

Open another terminal and run:

```powershell
cd frontend
npm install
npm run dev
```

The React Web UI runs at:

```text
http://localhost:5173
```

## 4. API Base URL

The frontend API client uses this default backend URL:

```text
http://localhost:8080
```

If needed, create `frontend/.env.local`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## 5. Recommended Test Flow

1. Start backend API server.
2. Start frontend Vite dev server.
3. Open `http://localhost:5173`.
4. Login using one of the demo users.
5. Check issue list.
6. Open issue detail.
7. Create issue.
8. Check statistics page.
9. Check recommendation result on issue detail page.

## 6. Layered Design

React Web UI does not access SQLite or Java repositories directly.

```text
React Web UI
↓ fetch()
Java HTTP API
↓
Service
↓
Repository Interface
↓
SQLite JDBC Repository
↓
SQLite DB
```

This keeps UI code separated from business logic and storage details.