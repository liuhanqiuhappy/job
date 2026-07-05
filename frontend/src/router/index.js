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
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
