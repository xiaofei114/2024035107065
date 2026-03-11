<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const name = "晓飞"
const tagline = "—— 欢迎来到我的个人主页！这里记录我的学习与成长"
const aboutMe = "我是一名前端开发学习者，热爱技术，喜欢探索新知识。空闲时间喜欢写代码、听音乐和旅行。希望能通过这个网站认识更多志同道合的朋友。"

const hobbies = [
    {
        emoji: "💻",
        title: "编程",
        description: "热爱编写代码，享受解决问题的过程，致力于创造出优雅高效的解决方案。"
    },
    {
        emoji: "🎵",
        title: "音乐",
        description: "喜欢听各种类型的音乐，偶尔弹弹吉他，在旋律中寻找灵感和平静。"
    },
    {
        emoji: "✈️",
        title: "旅行",
        description: "热爱探索新地方，体验不同文化，用脚步丈量世界的广阔与美好。"
    }
]

const contactInfo = {
    email: "example@email.com",
    github: "github.com/linxiaofei"
}

const displayText = ref("")
const showContent = ref(false)
const particles = ref([])

// 生成粒子背景
function createParticles() {
    const particleCount = 50
    const newParticles = []

    for (let i = 0; i < particleCount; i++) {
        newParticles.push({
            x: Math.random() * window.innerWidth,
            y: Math.random() * window.innerHeight,
            size: Math.random() * 10 + 2,
            speed: Math.random() * 20 + 10
        })
    }

    particles.value = newParticles
}

// 响应窗口大小变化
function handleResize() {
    createParticles()
}

onMounted(() => {
    createParticles()
    window.addEventListener('resize', handleResize)

    setTimeout(() => {
        showContent.value = true
    }, 300)

    let index = 0
    const typeWriter = () => {
        if (index < tagline.length) {
            displayText.value += tagline.charAt(index)
            index++
            setTimeout(typeWriter, 100)
        }
    }
    typeWriter()
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
})
</script>

<template>
    <div class="personal-homepage">

        <div class="page-container">
            <!-- 英雄区域 -->
            <div class="hero-section" :class="{ visible: showContent }">
                <div class="hero-content">
                    <h1 class="name">{{ name }}</h1>
                    <p class="tagline">{{ displayText }}<span class="cursor">|</span></p>
                    <div class="hero-buttons">
                        <a href="#about" class="btn btn-primary">了解更多</a>
                        <a href="#contact" class="btn btn-secondary">联系我</a>
                    </div>
                </div>
            </div>

            <!-- 关于我 -->
            <section id="about" class="about-section" :class="{ visible: showContent }">
                <h2 class="section-title">关于我</h2>
                <div class="about-content">
                    <div class="about-text-container">
                        <p class="about-text">{{ aboutMe }}</p>
                    </div>
                </div>
            </section>

            <!-- 我的爱好 -->
            <section id="hobbies" class="hobbies-section" :class="{ visible: showContent }">
                <h2 class="section-title">我的爱好</h2>
                <div class="hobbies-grid">
                    <div v-for="(hobby, index) in hobbies" :key="index" class="hobby-card"
                        :style="{ animationDelay: `${index * 0.2 + 0.5}s` }">
                        <div class="hobby-icon">
                            <span class="hobby-emoji">{{ hobby.emoji }}</span>
                        </div>
                        <h3 class="hobby-title">{{ hobby.title }}</h3>
                        <p class="hobby-description">{{ hobby.description }}</p>
                    </div>
                </div>
            </section>

            <!-- 底部 -->
            <footer id="contact" class="footer" :class="{ visible: showContent }">
                <div class="footer-content">
                    <div class="contact-info">
                        <a href="mailto:{{ contactInfo.email }}" class="contact-link">
                            <span class="contact-icon">📧</span>
                            <span>{{ contactInfo.email }}</span>
                        </a>
                        <a href="https://{{ contactInfo.github }}" target="_blank" class="contact-link">
                            <span class="contact-icon">🐱</span>
                            <span>{{ contactInfo.github }}</span>
                        </a>
                    </div>
                    <div class="footer-bottom">
                        <p>&copy; 2026 {{ name }}. All rights reserved.</p>
                    </div>
                </div>
            </footer>
        </div>
    </div>
</template>

<style scoped>
.personal-homepage {
    position: absolute;
    top: 0;
    height: 100vh;
    width: 100%;
    box-sizing: border-box;
    font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
    overflow-x: hidden;
    background-attachment: fixed;
}

/* 粒子背景效果 */
.particle {
    position: absolute;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    pointer-events: none;
    z-index: 0;
}

/* 页面容器 */

.personal-homepage::-webkit-scrollbar {
    width: 6px;
}

.personal-homepage::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
}

.personal-homepage::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 10px;
}

/* 平滑滚动 */
html {
    scroll-behavior: smooth;
}

.personal-homepage::-webkit-scrollbar {
    width: 8px;
}

.personal-homepage::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
}

.personal-homepage::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 10px;
    transition: all 0.3s ease;
}

.personal-homepage::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.5);
}

/* 粒子浮动动画 */
@keyframes float {
    0% {
        transform: translateY(0) translateX(0);
    }

    25% {
        transform: translateY(-20px) translateX(10px);
    }

    50% {
        transform: translateY(0) translateX(20px);
    }

    75% {
        transform: translateY(20px) translateX(10px);
    }

    100% {
        transform: translateY(0) translateX(0);
    }
}

/* 导航栏 */
.navbar {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    background: rgba(102, 126, 234, 0.9);
    backdrop-filter: blur(10px);
    padding: 20px 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    z-index: 1000;
    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
    opacity: 0;
    transform: translateY(-100%);
    transition: all 0.5s ease-out 0.2s;
}

.navbar.visible {
    opacity: 1;
    transform: translateY(0);
}

.nav-brand {
    font-size: 24px;
    font-weight: bold;
    color: #fff;
    text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
}

.nav-links {
    display: flex;
    gap: 30px;
}

.nav-link {
    color: rgba(255, 255, 255, 0.9);
    text-decoration: none;
    font-size: 16px;
    font-weight: 500;
    transition: all 0.3s ease;
    position: relative;
    padding: 8px 0;
}

.nav-link::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 0;
    height: 2px;
    background: #fff;
    transition: width 0.3s ease;
}

.nav-link:hover {
    color: #fff;
    transform: translateY(-2px);
}

.nav-link:hover::after {
    width: 100%;
}

/* 英雄区域 */
.hero-section {
    text-align: center;
    padding: 120px 20px 80px;
    opacity: 0;
    transform: translateY(30px);
    transition: all 0.8s ease-out 0.4s;
}

.hero-content {
    max-width: 800px;
    margin: 0 auto;
}

.hero-buttons {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 40px;
    flex-wrap: wrap;
}

.hero-section.visible {
    opacity: 1;
    transform: translateY(0);
}

/* 按钮样式 */
.btn {
    display: inline-block;
    padding: 12px 24px;
    border-radius: 50px;
    font-size: 16px;
    font-weight: 600;
    text-decoration: none;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    z-index: 1;
}

.btn::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 0;
    height: 100%;
    transition: width 0.3s ease;
    z-index: -1;
}

.btn:hover::before {
    width: 100%;
}

.btn-primary {
    background: #fff;
    color: #667eea;
    box-shadow: 0 4px 15px rgba(255, 255, 255, 0.3);
}

.btn-primary::before {
    background: rgba(102, 126, 234, 0.1);
}

.btn-primary:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(255, 255, 255, 0.4);
}

.btn-secondary {
    background: transparent;
    color: #fff;
    border: 2px solid #fff;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.btn-secondary::before {
    background: rgba(255, 255, 255, 0.1);
}

.btn-secondary:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(255, 255, 255, 0.2);
    background: rgba(255, 255, 255, 0.1);
}

.name {
    font-size: clamp(48px, 8vw, 72px);
    font-weight: bold;
    color: #fff;
    margin: 0;
    text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
    letter-spacing: 4px;
    background: linear-gradient(45deg, #fff, rgba(255, 255, 255, 0.8));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.tagline {
    font-size: clamp(18px, 3vw, 24px);
    color: rgba(255, 255, 255, 0.9);
    margin: 20px 0 0;
    text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
    min-height: 36px;
}

.cursor {
    animation: blink 1s infinite;
    font-weight: bold;
}

@keyframes blink {

    0%,
    50% {
        opacity: 1;
    }

    51%,
    100% {
        opacity: 0;
    }
}

/* 章节标题 */
.section-title {
    font-size: clamp(28px, 5vw, 36px);
    color: #fff;
    text-align: center;
    margin-bottom: 40px;
    text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
    position: relative;
    padding-bottom: 20px;
}

.section-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 3px;
    background: linear-gradient(90deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.2));
    border-radius: 3px;
}

/* 关于我 */
.about-section {
    margin: 0 auto 80px;
    padding: 0 20px;
    opacity: 0;
    transform: translateY(30px);
    transition: all 0.8s ease-out 0.6s;
}

.about-section.visible {
    opacity: 1;
    transform: translateY(0);
}

.about-content {
    max-width: 800px;
    margin: 0 auto;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 20px;
    backdrop-filter: blur(10px);
    padding: 50px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
}

.about-content:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.about-text {
    font-size: 18px;
    line-height: 1.8;
    color: rgba(255, 255, 255, 0.95);
    text-align: justify;
    margin: 0;
}

/* 我的爱好 */
.hobbies-section {
    margin: 0 auto 80px;
    padding: 0 20px;
    opacity: 0;
    transform: translateY(30px);
    transition: all 0.8s ease-out 0.8s;
}

.hobbies-section.visible {
    opacity: 1;
    transform: translateY(0);
}

.hobbies-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 40px;
    max-width: 1100px;
    margin: 0 auto;
}

.hobby-card {
    background: rgba(255, 255, 255, 0.15);
    border-radius: 20px;
    padding: 50px 30px;
    text-align: center;
    backdrop-filter: blur(10px);
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    opacity: 0;
    transform: translateY(30px);
    animation: cardFadeIn 0.8s ease-out forwards;
    border: 1px solid rgba(255, 255, 255, 0.2);
    position: relative;
    overflow: hidden;
}

.hobby-card::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(45deg,
            transparent,
            rgba(255, 255, 255, 0.1),
            transparent);
    transform: rotate(45deg);
    animation: shine 6s infinite;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.hobby-card:hover::before {
    opacity: 1;
}

@keyframes shine {
    0% {
        transform: translateX(-100%) rotate(45deg);
    }

    100% {
        transform: translateX(100%) rotate(45deg);
    }
}

@keyframes cardFadeIn {
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.hobby-card:hover {
    transform: translateY(-15px) scale(1.03);
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.hobby-icon {
    width: 100px;
    height: 100px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 30px;
    transition: all 0.3s ease;
    position: relative;
    z-index: 1;
}

.hobby-card:hover .hobby-icon {
    transform: scale(1.1);
    background: rgba(255, 255, 255, 0.3);
    box-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
}

.hobby-emoji {
    font-size: 48px;
    display: block;
}

.hobby-title {
    font-size: 24px;
    color: #fff;
    margin: 0 0 20px;
    text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
    position: relative;
    z-index: 1;
}

.hobby-description {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.85);
    line-height: 1.6;
    margin: 0;
    position: relative;
    z-index: 1;
}

/* 底部 */
.footer {
    margin: 0 auto;
    padding: 0;
    opacity: 0;
    transform: translateY(30px);
    transition: all 0.8s ease-out 1s;
}

.footer.visible {
    opacity: 1;
    transform: translateY(0);
}

.footer-content {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    padding: 20px 40px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    text-align: center;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.contact-info {
    display: flex;
    justify-content: center;
    gap: 40px;
    margin-bottom: 30px;
    flex-wrap: wrap;
}

.contact-link {
    display: flex;
    align-items: center;
    gap: 10px;
    color: rgba(255, 255, 255, 0.8);
    text-decoration: none;
    font-size: 16px;
    transition: all 0.3s ease;
    padding: 10px 20px;
    border-radius: 50px;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.contact-link:hover {
    transform: translateY(-3px);
    background: rgba(255, 255, 255, 0.2);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
    color: #fff;
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.contact-icon {
    font-size: 20px;
}

.footer-bottom {
    padding-top: 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-bottom p {
    color: rgba(255, 255, 255, 0.6);
    font-size: 14px;
    margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .navbar {
        padding: 15px 20px;
    }

    .nav-links {
        gap: 20px;
    }

    .hero-section {
        padding: 100px 20px 60px;
    }

    .btn {
        padding: 10px 20px;
        font-size: 14px;
    }

    .about-content {
        padding: 30px 20px;
    }

    .hobby-card {
        padding: 40px 20px;
    }

    .hobby-icon {
        width: 80px;
        height: 80px;
    }

    .hobby-emoji {
        font-size: 36px;
    }

    .footer-content {
        padding: 40px 20px;
    }

    .contact-info {
        gap: 20px;
    }

    .contact-link {
        padding: 8px 16px;
        font-size: 14px;
    }
}
</style>
