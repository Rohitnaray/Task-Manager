# 🚀 TaskFlow — Team Task Manager

A full-stack team task management application with role-based access control.

**Tech Stack:** Spring Boot 3 · PostgreSQL · JWT Auth · Vanilla JS · HTML/CSS

---

## ✨ Features

- **Auth** — JWT-based Signup/Login, role-based access (Admin / Member)
- **Projects** — Create, edit, delete projects; manage team members
- **Tasks** — Kanban board (Todo / In Progress / Done), priorities, due dates, assignment
- **Dashboard** — Live stats: total tasks, progress, overdue count
- **RBAC** — Admins see everything; Members see only their projects/tasks

---

## 🗂️ Project Structure

```
Team-Task-Manager/
├── backend/                    # Spring Boot app
│   ├── src/main/java/com/taskmanager/
│   │   ├── config/             # Security config, CORS
│   │   ├── controller/         # REST controllers
│   │   ├── dto/                # Request/Response DTOs
│   │   ├── entity/             # JPA entities (User, Project, Task)
│   │   ├── repository/         # Spring Data JPA repos
│   │   ├── security/           # JWT utils, filters, UserDetailsService
│   │   └── service/            # Business logic
│   ├── src/main/resources/
│   │   ├── application.properties       # Common config
│   │   ├── application-dev.properties   # H2 in-memory (local)
│   │   └── application-prod.properties  # PostgreSQL (Railway)
│   ├── pom.xml
│   └── railway.toml
└── frontend/
    ├── index.html              # Login / Signup page
    ├── dashboard.html          # Main app (projects, tasks, dashboard)
    └── api.js                  # API client helper
```

---

## 🔌 REST API Reference

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/login` | Login → returns JWT token |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List all users |
| GET | `/api/users/me` | Get current user |

### Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | List projects (filtered by role) |
| POST | `/api/projects` | Create project |
| GET | `/api/projects/{id}` | Get project by ID |
| PUT | `/api/projects/{id}` | Update project |
| DELETE | `/api/projects/{id}` | Delete project |
| POST | `/api/projects/{id}/members` | Add member |
| DELETE | `/api/projects/{id}/members/{uid}` | Remove member |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects/{pid}/tasks` | Tasks by project |
| POST | `/api/projects/{pid}/tasks` | Create task |
| PUT | `/api/tasks/{id}` | Update task |
| PATCH | `/api/tasks/{id}/status` | Update status only |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/tasks/mine` | My assigned tasks |
| GET | `/api/dashboard` | Dashboard stats |

---

## 🖥️ Local Development Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- Any modern browser (for frontend)

### 1. Run the Backend

```bash
cd backend
mvn spring-boot:run
```

The backend starts on **http://localhost:8080** using H2 in-memory database.
H2 Console available at: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:taskmanagerdb`)

### 2. Run the Frontend

Simply open `frontend/index.html` in your browser, or use Live Server (VS Code extension):

```bash
# If you have Python
cd frontend
python -m http.server 5500
# Then open http://localhost:5500
```

The `api.js` file auto-detects localhost and points to `http://localhost:8080/api`.

---

## ☁️ Railway Deployment (Step-by-Step)

### Step 1: Push to GitHub

```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/task-manager.git
git push -u origin main
```

### Step 2: Deploy Backend on Railway

1. Go to [railway.app](https://railway.app) → **New Project**
2. Click **Deploy from GitHub repo** → Select your repo
3. Set **Root Directory** to `backend`
4. Railway detects Spring Boot and builds automatically

### Step 3: Add PostgreSQL

1. In your Railway project → **+ New Service** → **Database** → **PostgreSQL**
2. Railway auto-injects `DATABASE_URL`, `PGUSER`, `PGPASSWORD`

### Step 4: Set Environment Variables

In Railway → your Spring Boot service → **Variables**, add:

```
SPRING_PROFILES_ACTIVE = prod
JWT_SECRET = your-very-long-random-secret-key-at-least-32-chars
CORS_ORIGINS = https://your-frontend-domain.com,http://localhost:5500
```

### Step 5: Deploy Frontend

Option A — **GitHub Pages** (simplest):
1. Put `frontend/` contents in a `docs/` folder in your repo
2. Enable GitHub Pages on `main` branch → `/docs`
3. Update `api.js` with your Railway backend URL

Option B — **Netlify** (recommended):
1. Drag & drop the `frontend/` folder at [netlify.com/drop](https://netlify.com/drop)
2. Get your Netlify URL
3. Update `api.js`: replace `YOUR_RAILWAY_APP` with your actual Railway URL
4. Add the Netlify URL to `CORS_ORIGINS` on Railway

### Step 6: Update api.js

In `frontend/api.js`, line 4:
```js
: 'https://YOUR_ACTUAL_RAILWAY_URL.up.railway.app/api';
```

---

## 🔐 Role-Based Access

| Action | Admin | Project Owner | Project Member |
|--------|-------|--------------|----------------|
| Create project | ✅ | ✅ | ✅ |
| Edit/Delete any project | ✅ | ❌ | ❌ |
| Edit/Delete own project | ✅ | ✅ | ❌ |
| Add/Remove members | ✅ | ✅ | ❌ |
| Create tasks in project | ✅ | ✅ | ✅ |
| Update task status | ✅ | ✅ | If assigned |
| Delete tasks | ✅ | ✅ | ❌ |
| View all projects | ✅ | ❌ | ❌ |

---

## 📹 Demo Video Checklist

Record a 2–5 min video showing:
1. Signup as Admin
2. Create a project
3. Signup as Member (new tab/incognito)
4. Admin adds Member to the project
5. Member creates and updates tasks
6. Show dashboard stats
7. Show overdue task handling

---

## 🧪 Quick Test with curl

```bash
# Signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@test.com","password":"secret1","role":"ADMIN"}'

# Login → copy the token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@test.com","password":"secret1"}'

# Create project (use token from above)
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"name":"My Project","description":"Test project"}'
```
