import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/register.vue')
  },
  {
    path: '/homepage',
    name: 'HomePage',
    component: () => import('../views/HomePage.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue')
  },
  {
    path: '/personal',
    name: 'Personal',
    component: () => import('../views/Personal.vue')
  },
  {
    path: '/enterprise',
    name: 'Enterprise',
    component: () => import('../views/Enterprise.vue')
  },
  {
    path: '/recommend',
    name: 'Recommend',
    component: () => import('../views/Recommend.vue')
  },
  {
    path: '/candidates',
    name: 'Candidates',
    component: () => import('../views/Candidates.vue')
  },
  {
    path: '/intents',
    name: 'IntentManagement',
    component: () => import('../views/IntentManagement.vue')
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
