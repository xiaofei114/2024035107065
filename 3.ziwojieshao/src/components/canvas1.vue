<script setup>
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
    pointNumber: Number,
    maxDis: Number,
    speed: Number,
    backgroundColorw: [String, Boolean],
    radius: Number
})

const canvas = ref(null)
let graph = null
let animationId = null

const getRandom = (min, max) => Math.floor(Math.random() * (max - min + 1) + min)

onMounted(init)
onMounted(() => {
    window.addEventListener('resize', resizeCanvas)
})

function resizeCanvas() {
    const dpr = devicePixelRatio
    if (canvas.value) {
        canvas.value.width = window.innerWidth * dpr
        canvas.value.height = window.innerHeight * dpr
        if (graph) {
            graph.width = canvas.value.width
            graph.height = canvas.value.height
        }
    }
}

function init() {
    if (!canvas.value) return

    const dpr = devicePixelRatio
    canvas.value.width = window.innerWidth * dpr
    canvas.value.height = window.innerHeight * dpr
    const ctx = canvas.value.getContext('2d')

    graph = new Graph(
        canvas.value.width,
        canvas.value.height,
        props.pointNumber ?? 50,
        props.maxDis ?? 200,
        props.speed ?? 30,
        props.radius ?? 3
    )

    // 使用统一的动画函数防止递归调用
    const animate = () => {
        graph.draw(ctx)
        animationId = requestAnimationFrame(animate)
    }

    animate()
}

// 监听props变化，重新创建图形实例
watch(props, () => {
    if (canvas.value && graph) {
        graph = new Graph(
            canvas.value.width,
            canvas.value.height,
            props.pointNumber ?? 50,
            props.maxDis ?? 200,
            props.speed ?? 30,
            props.radius ?? 3
        )
    }
})

class Point {
    constructor(radius, width, height, speed) {
        this.r = radius
        this.x = getRandom(this.r, width - this.r)
        this.y = getRandom(this.r, height - this.r)
        this.xSpeed = getRandom(-speed, speed)
        this.ySpeed = getRandom(-speed, speed)
        this.lastDrawTime = Date.now() // 初始化为当前时间
    }

    move(width, height) {
        const now = Date.now()
        const duration = (now - this.lastDrawTime) / 1000

        this.x += this.xSpeed * duration
        this.y += this.ySpeed * duration

        // 边界反弹
        if (this.x < this.r) {
            this.x = this.r
            this.xSpeed = -this.xSpeed
        }
        if (this.x > width - this.r) {
            this.x = width - this.r
            this.xSpeed = -this.xSpeed
        }
        if (this.y < this.r) {
            this.y = this.r
            this.ySpeed = -this.ySpeed
        }
        if (this.y > height - this.r) {
            this.y = height - this.r
            this.ySpeed = -this.ySpeed
        }

        this.lastDrawTime = now
    }

    draw(ctx) {
        this.move(ctx.canvas.width, ctx.canvas.height)
        ctx.beginPath()
        ctx.arc(this.x, this.y, this.r, 0, 2 * Math.PI)
        ctx.fillStyle = 'rgb(200,200,200)'
        ctx.fill()
    }
}

class Graph {
    constructor(width, height, pointNumber = 30, maxDis = 200, speed = 100, radius = 5) {
        this.width = width
        this.height = height
        this.points = new Array(pointNumber).fill(0).map(() => new Point(radius, width, height, speed))
        this.maxDis = maxDis
    }

    draw(ctx) {
        // 清除画布
        ctx.clearRect(0, 0, this.width, this.height)

        // 绘制所有点
        this.points.forEach(point => point.draw(ctx))

        // 绘制连线
        ctx.lineWidth = 1.5
        for (let i = 0; i < this.points.length; i++) {
            for (let j = i + 1; j < this.points.length; j++) {
                const p1 = this.points[i]
                const p2 = this.points[j]
                const d = Math.sqrt((p1.x - p2.x) ** 2 + (p1.y - p2.y) ** 2)

                if (d <= this.maxDis) {
                    ctx.beginPath()
                    ctx.moveTo(p1.x, p1.y)
                    ctx.lineTo(p2.x, p2.y)
                    ctx.strokeStyle = `rgba(200,200,200,${1 - d / this.maxDis})`
                    ctx.stroke()
                }
            }
        }
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