import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/components/Layout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '仪表盘', icon: 'Odometer' }
        },
        {
          path: 'apps',
          name: 'Apps',
          component: () => import('@/views/apps/index.vue'),
          meta: { title: '应用管理', icon: 'Connection' }
        },
        {
          path: 'files',
          name: 'Files',
          component: () => import('@/views/files/index.vue'),
          meta: { title: '文档空间', icon: 'FolderOpened' }
        },
        {
          path: 'sessions',
          name: 'Sessions',
          component: () => import('@/views/sessions/index.vue'),
          meta: { title: '会话监控', icon: 'Monitor' }
        },
        {
          path: 'fonts',
          name: 'Fonts',
          component: () => import('@/views/fonts/index.vue'),
          meta: { title: '字体管理', icon: 'EditPen' }
        },
        {
          path: 'settings',
          name: 'Settings',
          component: () => import('@/views/settings/index.vue'),
          meta: { title: '系统设置', icon: 'Setting' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth === false) {
    next()
  } else if (!userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
