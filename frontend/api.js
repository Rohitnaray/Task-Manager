// ─── CONFIG ───────────────────────────────────────────────────────────────────
// Change this to your Railway backend URL when deployed
const API_BASE = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  ? 'http://localhost:8080/api'
  : 'https://task-manager-production-fcb8.up.railway.app/api';  // ← replace after deployment

// ─── AUTH STATE ───────────────────────────────────────────────────────────────
const Auth = {
  getToken: () => localStorage.getItem('token'),
  getUser:  () => JSON.parse(localStorage.getItem('user') || 'null'),
  setAuth: (token, user) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
  },
  clear: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  isLoggedIn: () => !!localStorage.getItem('token'),
  isAdmin: () => {
    const u = Auth.getUser();
    return u && u.role === 'ADMIN';
  }
};

// ─── HTTP CLIENT ──────────────────────────────────────────────────────────────
async function api(method, path, body = null) {
  const headers = { 'Content-Type': 'application/json' };
  if (Auth.getToken()) headers['Authorization'] = `Bearer ${Auth.getToken()}`;

  const res = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null,
  });

  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }

  if (!res.ok) {
    throw new Error(typeof data === 'string' ? data : (data.message || 'Request failed'));
  }
  return data;
}

const API = {
  // Auth
  signup: (body) => api('POST', '/auth/signup', body),
  login:  (body) => api('POST', '/auth/login', body),

  // Users
  getUsers: () => api('GET', '/users'),
  getMe:    () => api('GET', '/users/me'),

  // Dashboard
  getDashboard: () => api('GET', '/dashboard'),

  // Projects
  getProjects:        ()       => api('GET',    '/projects'),
  getProject:         (id)     => api('GET',    `/projects/${id}`),
  createProject:      (body)   => api('POST',   '/projects', body),
  updateProject:      (id, b)  => api('PUT',    `/projects/${id}`, b),
  deleteProject:      (id)     => api('DELETE', `/projects/${id}`),
  addMember:          (id, uid)=> api('POST',   `/projects/${id}/members`, { userId: uid }),
  removeMember:       (id, uid)=> api('DELETE', `/projects/${id}/members/${uid}`),

  // Tasks
  getProjectTasks: (pid)       => api('GET',    `/projects/${pid}/tasks`),
  createTask:      (pid, body) => api('POST',   `/projects/${pid}/tasks`, body),
  updateTask:      (tid, body) => api('PUT',    `/tasks/${tid}`, body),
  updateStatus:    (tid, s)    => api('PATCH',  `/tasks/${tid}/status`, { status: s }),
  deleteTask:      (tid)       => api('DELETE', `/tasks/${tid}`),
  getMyTasks:      ()          => api('GET',    '/tasks/mine'),
};
