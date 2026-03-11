<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps({
    backgroundColorw: [String, Boolean],
    numberOfWaves: { type: Number, default: 3 },
    location: { type: Number, default: 0.92 },
    color: { type: String, default: '#ffffff' },
})

const canvas = ref(null)
let animationId = null
let waves = [] // 移动waves到组件作用域

onMounted(() => {
    info()
    window.addEventListener('resize', resizeCanvas)
})

onUnmounted(() => {
    cancelAnimationFrame(animationId)
    window.removeEventListener('resize', resizeCanvas)
})

// 同时监听两个prop的变化
watch(props, () => {
    if (canvas.value) info()
})

const getRandom = (min, max) => Math.floor(Math.random() * (max - min + 1) + min)

function resizeCanvas() {
    const dpr = devicePixelRatio
    if (canvas.value) {
        canvas.value.width = window.innerWidth * dpr
        canvas.value.height = window.innerHeight * dpr
    }
    info()
}

function info() {
    if (!canvas.value) return

    cancelAnimationFrame(animationId)

    const dpr = window.devicePixelRatio || 1
    const rect = canvas.value.getBoundingClientRect()
    canvas.value.width = rect.width * dpr
    canvas.value.height = rect.height * dpr
    const ctx = canvas.value.getContext('2d')

    ctx.imageSmoothingEnabled = true
    ctx.scale(dpr, dpr)
    ctx.lineJoin = 'round'
    ctx.lineCap = 'round'

    // 重新创建波浪数组
    waves = [] // 重置波浪数组

    for (let i = 0; i < props.numberOfWaves; i++) {
        waves.push(new Wave(props.location, getRandom(10, 30), getRandom(1, 100) * 0.0001, getRandom(10, 20) * 0.001, props.color, ctx))
    }

    const animate = () => {
        ctx.clearRect(0, 0, canvas.value.width, canvas.value.height)

        waves.forEach(wave => {
            wave.update()
            wave.draw()
        })

        animationId = requestAnimationFrame(animate)
    }

    animate()
}

class Wave {
    constructor(heightRatio, amplitude, speed, frequency, color, ctx) {
        this.ctx = ctx
        this.baseHeight = window.innerHeight * heightRatio
        this.amplitude = amplitude
        this.speed = speed
        this.frequency = frequency
        this.phase = Math.random() * Math.PI * 2
        this.color = color

        // 创建波浪渐变
        this.updateGradient()
    }

    updateGradient() {
        this.gradient = this.ctx.createLinearGradient(0, 0, 0, window.innerHeight)
        this.gradient.addColorStop(0, `${this.color}30`)
        this.gradient.addColorStop(1, `${this.color}09`)
    }

    update() {
        this.phase += this.speed
        this.dynamicAmplitude = this.amplitude + Math.sin(this.phase * 0.8) * 5
    }

    draw() {
        const width = window.innerWidth
        const segments = 10
        this.ctx.beginPath()
        this.ctx.moveTo(-100, this.baseHeight)

        // 更新渐变以确保颜色正确
        this.updateGradient()

        for (let i = 0; i <= segments; i++) {
            const x = i * width / segments
            const y = this.baseHeight + Math.sin(x * this.frequency + this.phase) * this.dynamicAmplitude

            if (i === 0) {
                this.ctx.moveTo(x, y)
            } else {
                const prevX = (i - 1) * width / segments
                const prevY = this.baseHeight + Math.sin(prevX * this.frequency + this.phase) * this.dynamicAmplitude
                const cp1x = prevX + width / segments * 0.4
                const cp1y = prevY
                const cp2x = x - width / segments * 0.4
                const cp2y = y

                this.ctx.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
            }
        }

        this.ctx.lineTo(width + 100, this.baseHeight + 100)
        this.ctx.lineTo(-100, this.baseHeight + 100)
        this.ctx.closePath()
        this.ctx.fillStyle = this.gradient
        this.ctx.fill()
    }
}
</script>

<template>
    <canvas ref="canvas"
        :style="{ backgroundColor: backgroundColorw ? backgroundColorw : backgroundColorw === false ? 'transparent' : '#222' }"></canvas>
</template>

<style scoped>
canvas {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: #222;
}
</style>