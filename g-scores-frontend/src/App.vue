<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useTheme } from '@/composables/useTheme'
import MenuIcon from '@/components/MenuIcon.vue'

const { theme, toggle } = useTheme()

// Sidebar: man rong (≥720px) luon mo; man hep moi hien nut burger.
// Dung matchMedia (khong phai innerWidth) de luc "keo cua so ra/nho"
// thi browser tu goi listener: man rong lai thi sidebar tu dong tro ve
// trang thai mo binh thuong, nut burger mat di.
const mq = window.matchMedia('(min-width: 720px)')
const isDesktop = ref(mq.matches)
const drawerOpen = ref(false)

function onMqChange(e: MediaQueryListEvent) {
  isDesktop.value = e.matches
}

// Ben de dung chung cho template/CSS: man rong luon mo, man hep theo nut burger.
const sidebarOpen = computed(() => (isDesktop.value ? true : drawerOpen.value))

function toggleDrawer() {
  drawerOpen.value = !drawerOpen.value
}

function onNavClick() {
  if (!isDesktop.value) drawerOpen.value = false
}

onMounted(() => mq.addEventListener('change', onMqChange))
onBeforeUnmount(() => mq.removeEventListener('change', onMqChange))

const links = [
  { to: '/dashboard', label: 'Tổng quan', icon: 'grid' },
  { to: '/search', label: 'Tra cứu điểm', icon: 'search' },
  { to: '/reports', label: 'Báo cáo', icon: 'file' },
  { to: '/settings', label: 'Cài đặt', icon: 'gear' },
] as const
</script>

<template>
  <div class="layout" :class="{ 'layout--open': sidebarOpen }">
    <button
      v-if="!isDesktop"
      class="burger"
      type="button"
      :aria-expanded="drawerOpen"
      @click="toggleDrawer"
    >
      <span class="sr-only">{{ drawerOpen ? 'Đóng menu' : 'Mở menu' }}</span>
      <span class="burger__line" aria-hidden="true"></span>
      <span class="burger__line" aria-hidden="true"></span>
      <span class="burger__line" aria-hidden="true"></span>
    </button>

    <div class="body">
      <aside class="sidebar" :aria-hidden="!sidebarOpen">
        <div class="sidebar__brand">
          <div class="brand">
            <img class="brand__logo" src="/assets/gscores-logo.png" alt="GScores" />
          </div>
          <div class="sidebar__tag">Báo cáo điểm thi THPT</div>
        </div>

        <nav class="sidebar__nav" aria-label="Điều hướng">
          <RouterLink
            v-for="link in links"
            :key="link.to"
            :to="link.to"
            class="sidebar__link"
            @click="onNavClick"
          >
            <MenuIcon :kind="link.icon" />
            <span>{{ link.label }}</span>
          </RouterLink>
        </nav>

        <div class="sidebar__foot">
          <button class="theme-toggle" type="button" @click="toggle">
            {{ theme === 'dark' ? 'Chế độ sáng' : 'Chế độ tối' }}
          </button>
        </div>
      </aside>

      <main class="main">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
}

.burger {
  position: fixed;
  top: 12px;
  left: 12px;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 1px solid var(--glass-border);
  background: var(--glass);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  cursor: pointer;
  flex-shrink: 0;
  z-index: 30;
  box-shadow: var(--shadow-card);
}

.burger__line {
  display: block;
  width: 17px;
  height: 2px;
  border-radius: 1px;
  background: var(--text);
  transition:
    transform 0.18s ease,
    opacity 0.18s ease;
}

.layout--open .burger__line:nth-child(1) {
  transform: translateY(5px) rotate(45deg);
}

.layout--open .burger__line:nth-child(2) {
  opacity: 0;
}

.layout--open .burger__line:nth-child(3) {
  transform: translateY(-5px) rotate(-45deg);
}

.sidebar {
  position: fixed;
  left: 16px;
  top: 16px;
  bottom: 16px;
  width: 280px;
  background: var(--glass);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-panel);
  padding: var(--space-4) var(--space-3) var(--space-4);
  overflow-y: auto;
  z-index: 15;

  display: flex;
  flex-direction: column;
  gap: var(--space-4);

  /* Trang thai tron: luon dong + mat; .layout--open (luon co o man rong) mo no. */
  opacity: 0;
  visibility: hidden;
  transform: translateX(-14px);
  transition:
    transform 0.24s ease,
    opacity 0.24s ease,
    visibility 0s linear 0.24s;
}

.layout--open .sidebar {
  opacity: 1;
  visibility: visible;
  transform: translateX(0);
  transition:
    transform 0.24s ease,
    opacity 0.24s ease,
    visibility 0s linear 0s;
}

.sidebar__brand {
  padding: 4px 12px 0;
}

.brand {
  display: flex;
  align-items: center;
}

.brand__logo {
  display: block;
  width: 144px;
  height: auto;
  border-radius: var(--radius-sm);
}

.sidebar__tag {
  margin-top: 2px;
  font-size: var(--text-xs);
  color: var(--sidebar-text);
}

.sidebar__nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: var(--space-2);
}

.sidebar__link {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  color: var(--sidebar-text);
  font-size: var(--text-lg);
  font-weight: 600;
  transition:
    background 0.15s ease,
    color 0.15s ease,
    transform 0.15s ease;
}

.sidebar__link:hover {
  background: color-mix(in srgb, var(--text) 6%, transparent);
  color: var(--text);
  text-decoration: none;
  transform: translateX(2px);
}

.sidebar__link.router-link-active {
  background: var(--accent-soft);
  color: var(--sidebar-text);
  font-weight: 600;
}

.sidebar__link.router-link-active:hover {
  background: var(--accent-soft);
  color: var(--sidebar-text);
  transform: none;
}

.sidebar__foot {
  margin-top: auto;
  padding: 12px;
  border-top: 1px solid var(--glass-border);
}

.theme-toggle {
  width: 100%;
  border: 1px solid var(--border);
  background: transparent;
  border-radius: var(--radius-sm);
  padding: var(--space-2) var(--space-3);
  font-size: var(--text-xs);
  color: var(--sidebar-text);
  cursor: pointer;
  transition:
    background 0.15s ease,
    border-color 0.15s ease,
    color 0.15s ease;
}

.theme-toggle:hover {
  background: var(--panel-2);
  border-color: var(--border-strong);
  color: var(--sidebar-text);
}

.body {
  transition: padding-left 0.24s ease;
  background: var(--bg);
  min-height: 100vh;
  position: relative;
  isolation: isolate;
}

.layout--open .body {
  padding-left: 312px;
}

.main {
  max-width: 1120px;
  margin: 0 auto;
  padding: 32px;
}

@media (max-width: 720px) {
  .layout--open .body {
    padding-left: 0;
  }

  .sidebar {
    left: 12px;
    right: 12px;
    top: 12px;
    bottom: 12px;
    width: auto;
    padding-top: 64px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .layout,
  .sidebar,
  .sidebar__link,
  .burger,
  .burger__line,
  .main {
    transition-duration: 0.01ms !important;
    animation-duration: 0.01ms !important;
  }
}
</style>
