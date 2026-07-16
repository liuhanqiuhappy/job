<template>
  <div class="home-page">
    <header class="navbar">
      <div class="navbar-inner">
        <div class="logo-section">
          <div class="logo-icon">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
              <rect width="28" height="28" rx="6" fill="#1677ff"/>
              <path d="M8 20V12L14 8L20 12V20H16V16H12V20H8Z" fill="white"/>
            </svg>
          </div>
          <span class="logo-text">�ǻ��˲�ƥ��ϵͳ</span>
        </div>
        <nav class="nav-links">
          <a href="/login" class="nav-btn login-btn">��¼</a>
          <a href="/register" class="nav-btn register-btn">ע��</a>
        </nav>
      </div>
    </header>
    <section class="carousel-section">
      <div class="carousel-container">
        <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
          <div v-for="(slide, idx) in slides" :key="idx" class="carousel-slide" :style="{ background: slide.bg }">
            <div class="slide-overlay"></div>
            <div class="slide-content">
              <h2 class="slide-title">{{ slide.title }}</h2>
              <p class="slide-desc">{{ slide.desc }}</p>
              <a :href="slide.ctaLink" class="slide-cta">{{ slide.ctaText }}</a>
            </div>
          </div>
        </div>
        <button class="carousel-arrow left" @click="prevSlide">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
        </button>
        <button class="carousel-arrow right" @click="nextSlide">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
        </button>
        <div class="carousel-dots">
          <span v-for="(_, idx) in slides" :key="idx" :class="['dot', { active: currentSlide === idx }]" @click="currentSlide = idx"></span>
        </div>
      </div>
    </section>
    <section class="features-section">
      <h2 class="section-title">ƽ̨��������</h2>
      <p class="section-subtitle">AI ������ʡ���˲��ǻ�ƥ��ƽ̨</p>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon" style="background: rgba(22,119,255,0.10)">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#1677ff" stroke-width="1.5"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
          </div>
          <h3>���ܼ�������</h3>
          <p>�Զ���ȡ�����еĹؼ���Ϣ�������ṹ���˲ŵ���</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon" style="background: rgba(82,196,26,0.10)">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#52c41a" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
          </div>
          <h3>��׼ƥ���Ƽ�</h3>
          <p>���ڶ�ά����������ʵ���˸�����ƥ��</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon" style="background: rgba(250,173,20,0.10)">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#faad14" stroke-width="1.5"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
          </div>
          <h3>�����Ϲ���</h3>
          <p>��ҵ���˲�˫��ѡ�񣬸�Ч��ɺ�������</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon" style="background: rgba(114,46,209,0.10)">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#722ed1" stroke-width="1.5"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>
          </div>
          <h3>�˲����ݷ���</h3>
          <p>���ӻ������״�ͼ�뼼�ܹ���ͼ�ף������˲ż�ֵ</p>
        </div>
      </div>
    </section>
    <footer class="footer">
      <div class="footer-inner">
        <p>&copy; 2026 �ǻ��˲�ƥ��ϵͳ ��Ȩ����</p>
        <div class="footer-links">
          <a href="/login">��¼</a>
          <a href="/register">ע��</a>
        </div>
      </div>
    </footer>
  </div>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
const slides = [
  { title: 'ʡ���˲��ǻ�ƥ��ƽ̨', desc: '���� AI ������Ϊ��ҵ���˲��ṩ��׼����Ч������ƥ�����', ctaText: '��������', ctaLink: '/register', bg: '#1a1a2e' },
  { title: '���ܼ�������', desc: '�ϴ����������Զ���ȡ�����������������顢�����س��Ⱥ�����Ϣ', ctaText: '�˽����', ctaLink: '/register', bg: '#2c3e50' },
  { title: '��׼�˸�ƥ��', desc: '��ά�����������������ÿһ�ݼ��������ҵ����ʺϵĸ�λ', ctaText: '��ʼƥ��', ctaLink: '/login', bg: '#434a4e' }
]
const currentSlide = ref(0)
let timer = null
const AUTOPLAY_MS = 4000
const nextSlide = () => { currentSlide.value = (currentSlide.value + 1) % slides.length }
const prevSlide = () => { currentSlide.value = (currentSlide.value - 1 + slides.length) % slides.length }
const startAutoplay = () => { stopAutoplay(); timer = setInterval(nextSlide, AUTOPLAY_MS) }
const stopAutoplay = () => { if (timer) { clearInterval(timer); timer = null } }
onMounted(() => startAutoplay())
onUnmounted(() => stopAutoplay())
</script>
<style scoped>
.home-page {
  --primary: #1677ff;
  --primary-hover: #4096ff;
  --primary-active: #0958d9;
  --primary-bg: rgba(22,119,255,0.06);
  --bg: #f8f9fa;
  --card-bg: #ffffff;
  --text: #1d1d1f;
  --text-secondary: #6b7280;
  --text-muted: #9ca3af;
  --border: #e5e7eb;
  --radius: 12px;
  --shadow: 0 2px 12px rgba(0,0,0,0.06);
  --shadow-hover: 0 8px 30px rgba(0,0,0,0.10);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #fff;
  color: var(--text);
  min-height: 100vh;
}

/* Navbar */
.navbar { position: fixed; top: 0; left: 0; right: 0; z-index: 100; background: rgba(255,255,255,0.90); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); border-bottom: 1px solid var(--border); }
.navbar-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; height: 60px; display: flex; align-items: center; justify-content: space-between; }
.logo-section { display: flex; align-items: center; gap: 10px; }
.logo-text { font-size: 18px; font-weight: 700; color: var(--text); }
.nav-links { display: flex; gap: 10px; }
.nav-btn { padding: 8px 22px; border-radius: 8px; font-size: 14px; font-weight: 500; text-decoration: none; transition: all 0.2s; }
.login-btn { color: var(--primary); border: 1.5px solid var(--border); background: transparent; }
.login-btn:hover { border-color: var(--primary); background: var(--primary-bg); }
.register-btn { color: #fff; background: var(--primary); border: 1.5px solid var(--primary); }
.register-btn:hover { background: var(--primary-hover); }

/* Hero Carousel */
.carousel-section { margin-top: 60px; }
.carousel-container { position: relative; width: 100%; height: 480px; overflow: hidden; }
.carousel-track { display: flex; height: 100%; transition: transform 0.5s ease; }
.carousel-slide { position: relative; min-width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
.slide-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.50); }
.slide-content { position: relative; z-index: 2; text-align: center; max-width: 680px; padding: 0 24px; }
.slide-title { font-size: 42px; font-weight: 800; color: #fff; margin: 0 0 14px; line-height: 1.2; letter-spacing: -0.02em; }
.slide-desc { font-size: 17px; color: rgba(255,255,255,0.80); margin: 0 0 30px; line-height: 1.6; }
.slide-cta { display: inline-block; padding: 13px 36px; background: var(--primary); color: #fff; border-radius: 8px; font-size: 15px; font-weight: 600; text-decoration: none; transition: background 0.2s, transform 0.1s; }
.slide-cta:hover { background: var(--primary-hover); transform: translateY(-1px); }

.carousel-arrow { position: absolute; top: 50%; transform: translateY(-50%); z-index: 5; width: 42px; height: 42px; border-radius: 50%; border: none; background: rgba(255,255,255,0.18); color: #fff; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: background 0.2s; }
.carousel-arrow:hover { background: rgba(255,255,255,0.30); }
.carousel-arrow.left { left: 24px; }
.carousel-arrow.right { right: 24px; }
.carousel-dots { position: absolute; bottom: 28px; left: 50%; transform: translateX(-50%); z-index: 5; display: flex; gap: 10px; }
.dot { width: 10px; height: 10px; border-radius: 50%; background: rgba(255,255,255,0.35); cursor: pointer; transition: all 0.3s; }
.dot.active { background: #fff; width: 28px; border-radius: 5px; }

/* Features */
.features-section { background: var(--bg); padding: 80px 0; }
.features-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; text-align: center; }
.section-title { font-size: 30px; font-weight: 700; margin: 0 0 8px; color: var(--text); }
.section-subtitle { font-size: 15px; color: var(--text-muted); margin: 0 0 48px; }
.features-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.feature-card { background: var(--card-bg); border-radius: var(--radius); padding: 36px 28px 32px; border: 1px solid var(--border); box-shadow: var(--shadow); transition: box-shadow 0.3s, transform 0.3s; text-align: center; }
.feature-card:hover { box-shadow: var(--shadow-hover); transform: translateY(-4px); }
.feature-icon { width: 56px; height: 56px; border-radius: 14px; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px; }
.feature-card h3 { font-size: 17px; font-weight: 600; margin: 0 0 10px; color: var(--text); }
.feature-card p { font-size: 14px; color: var(--text-secondary); line-height: 1.7; margin: 0; }

/* Footer */
.footer { border-top: 1px solid var(--border); background: #fafafa; }
.footer-inner { max-width: 1200px; margin: 0 auto; padding: 24px; display: flex; justify-content: space-between; align-items: center; font-size: 14px; color: var(--text-muted); }
.footer-inner p { margin: 0; }
.footer-links { display: flex; gap: 24px; }
.footer-links a { color: var(--text-secondary); text-decoration: none; transition: color 0.2s; }
.footer-links a:hover { color: var(--primary); }

@media (max-width: 1024px) { .features-grid { grid-template-columns: repeat(2, 1fr); } .slide-title { font-size: 34px; } }
@media (max-width: 768px) { .carousel-container { height: 400px; } .slide-title { font-size: 28px; } }
@media (max-width: 560px) {
  .features-grid { grid-template-columns: 1fr; }
  .carousel-container { height: 360px; }
  .slide-title { font-size: 24px; }
  .navbar-inner { padding: 0 16px; }
  .features-inner { padding: 48px 16px; }
  .footer-inner { flex-direction: column; gap: 12px; text-align: center; }
}
</style>