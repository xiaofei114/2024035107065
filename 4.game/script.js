// 游戏常量
const CANVAS_WIDTH = 800;
const CANVAS_HEIGHT = 400;
const GRAVITY = 0.8;
const JUMP_FORCE = -15;
const PLAYER_SPEED = 5;

// 游戏状态
let gameState = {
    lives: 3,
    level: 1,
    isGameOver: false,
    isPaused: false
};

// 玩家对象
const player = {
    x: 50,
    y: 300,
    width: 30,
    height: 30,
    velocityX: 0,
    velocityY: 0,
    isOnGround: false,
    color: '#ff6b6b'
};

// 键盘状态
const keys = {
    left: false,
    right: false,
    up: false
};

// 障碍物数组
let obstacles = [];

// 平台数组
let platforms = [];

// 陷阱数组
let traps = [];

// 目标点
let goal = {
    x: 700,
    y: 300,
    width: 40,
    height: 40,
    color: '#4ecdc4'
};

// 初始化游戏
function initGame() {
    // 重置玩家位置
    player.x = 50;
    player.y = 300;
    player.velocityX = 0;
    player.velocityY = 0;
    player.isOnGround = false;
    
    // 重置障碍物
    obstacles = [];
    platforms = [];
    traps = [];
    
    // 根据关卡生成障碍物
    generateLevel(gameState.level);
}

// 生成关卡
function generateLevel(level) {
    // 基础平台
    platforms.push(
        { x: 0, y: 350, width: CANVAS_WIDTH, height: 50, color: '#333' }
    );
    
    switch(level) {
        case 1:
            // 简单平台
            platforms.push(
                { x: 200, y: 300, width: 100, height: 20, color: '#666' },
                { x: 400, y: 250, width: 100, height: 20, color: '#666' },
                { x: 600, y: 200, width: 100, height: 20, color: '#666' }
            );
            
            // 陷阱
            traps.push(
                { x: 150, y: 330, width: 40, height: 20, color: '#ff4757' },
                { x: 350, y: 280, width: 40, height: 20, color: '#ff4757' },
                { x: 550, y: 230, width: 40, height: 20, color: '#ff4757' }
            );
            
            // 障碍物
            obstacles.push(
                { x: 250, y: 280, width: 20, height: 20, color: '#2ed573' },
                { x: 450, y: 230, width: 20, height: 20, color: '#2ed573' }
            );
            break;
        case 2:
            // 更复杂的平台
            platforms.push(
                { x: 150, y: 300, width: 80, height: 20, color: '#666' },
                { x: 300, y: 250, width: 80, height: 20, color: '#666' },
                { x: 450, y: 200, width: 80, height: 20, color: '#666' },
                { x: 600, y: 150, width: 80, height: 20, color: '#666' }
            );
            
            // 更多陷阱
            traps.push(
                { x: 120, y: 330, width: 40, height: 20, color: '#ff4757' },
                { x: 270, y: 280, width: 40, height: 20, color: '#ff4757' },
                { x: 420, y: 230, width: 40, height: 20, color: '#ff4757' },
                { x: 570, y: 180, width: 40, height: 20, color: '#ff4757' }
            );
            
            // 移动障碍物
            obstacles.push(
                { x: 200, y: 280, width: 20, height: 20, color: '#2ed573', velocityX: 2 },
                { x: 400, y: 230, width: 20, height: 20, color: '#2ed573', velocityX: -2 },
                { x: 600, y: 180, width: 20, height: 20, color: '#2ed573', velocityX: 1.5 }
            );
            break;
    }
}

// 处理键盘输入
function handleKeyDown(e) {
    switch(e.key) {
        case 'ArrowLeft':
            keys.left = true;
            break;
        case 'ArrowRight':
            keys.right = true;
            break;
        case 'ArrowUp':
            keys.up = true;
            if (player.isOnGround) {
                player.velocityY = JUMP_FORCE;
                player.isOnGround = false;
            }
            break;
    }
}

function handleKeyUp(e) {
    switch(e.key) {
        case 'ArrowLeft':
            keys.left = false;
            break;
        case 'ArrowRight':
            keys.right = false;
            break;
        case 'ArrowUp':
            keys.up = false;
            break;
    }
}

// 碰撞检测
function checkCollision(rect1, rect2) {
    return (
        rect1.x < rect2.x + rect2.width &&
        rect1.x + rect1.width > rect2.x &&
        rect1.y < rect2.y + rect2.height &&
        rect1.y + rect1.height > rect2.y
    );
}

// 更新游戏状态
function updateGame() {
    if (gameState.isGameOver || gameState.isPaused) return;
    
    // 更新玩家位置
    if (keys.left) {
        player.velocityX = -PLAYER_SPEED;
    } else if (keys.right) {
        player.velocityX = PLAYER_SPEED;
    } else {
        player.velocityX = 0;
    }
    
    // 应用重力
    player.velocityY += GRAVITY;
    
    // 更新玩家位置
    player.x += player.velocityX;
    player.y += player.velocityY;
    
    // 边界检查
    if (player.x < 0) {
        player.x = 0;
    } else if (player.x + player.width > CANVAS_WIDTH) {
        player.x = CANVAS_WIDTH - player.width;
    }
    
    if (player.y > CANVAS_HEIGHT) {
        // 玩家掉落，减少生命
        gameState.lives--;
        document.getElementById('lives').textContent = gameState.lives;
        
        if (gameState.lives <= 0) {
            gameState.isGameOver = true;
        } else {
            initGame();
        }
    }
    
    // 平台碰撞检测
    player.isOnGround = false;
    platforms.forEach(platform => {
        if (checkCollision(player, platform)) {
            // 从上方碰撞
            if (player.velocityY > 0 && player.y + player.height <= platform.y + 10) {
                player.y = platform.y - player.height;
                player.velocityY = 0;
                player.isOnGround = true;
            }
        }
    });
    
    // 障碍物碰撞检测
    obstacles.forEach(obstacle => {
        // 更新移动障碍物
        if (obstacle.velocityX) {
            obstacle.x += obstacle.velocityX;
            // 边界检查
            if (obstacle.x < 0 || obstacle.x + obstacle.width > CANVAS_WIDTH) {
                obstacle.velocityX *= -1;
            }
        }
        
        if (checkCollision(player, obstacle)) {
            // 碰到障碍物，减少生命
            gameState.lives--;
            document.getElementById('lives').textContent = gameState.lives;
            
            if (gameState.lives <= 0) {
                gameState.isGameOver = true;
            } else {
                initGame();
            }
        }
    });
    
    // 陷阱碰撞检测
    traps.forEach(trap => {
        if (checkCollision(player, trap)) {
            // 碰到陷阱，减少生命
            gameState.lives--;
            document.getElementById('lives').textContent = gameState.lives;
            
            if (gameState.lives <= 0) {
                gameState.isGameOver = true;
            } else {
                initGame();
            }
        }
    });
    
    // 目标碰撞检测
    if (checkCollision(player, goal)) {
        // 进入下一关
        gameState.level++;
        document.getElementById('level').textContent = gameState.level;
        initGame();
    }
}

// 绘制游戏
function drawGame() {
    const canvas = document.getElementById('gameCanvas');
    const ctx = canvas.getContext('2d');
    
    // 清空画布
    ctx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    
    // 绘制背景
    ctx.fillStyle = '#e0e0e0';
    ctx.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    
    // 绘制平台
    platforms.forEach(platform => {
        ctx.fillStyle = platform.color;
        ctx.fillRect(platform.x, platform.y, platform.width, platform.height);
    });
    
    // 绘制障碍物
    obstacles.forEach(obstacle => {
        ctx.fillStyle = obstacle.color;
        ctx.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
    });
    
    // 绘制陷阱
    traps.forEach(trap => {
        ctx.fillStyle = trap.color;
        ctx.fillRect(trap.x, trap.y, trap.width, trap.height);
    });
    
    // 绘制目标
    ctx.fillStyle = goal.color;
    ctx.fillRect(goal.x, goal.y, goal.width, goal.height);
    
    // 绘制玩家
    ctx.fillStyle = player.color;
    ctx.fillRect(player.x, player.y, player.width, player.height);
    
    // 绘制游戏结束画面
    if (gameState.isGameOver) {
        ctx.fillStyle = 'rgba(0, 0, 0, 0.7)';
        ctx.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        ctx.fillStyle = '#fff';
        ctx.font = '48px Arial';
        ctx.textAlign = 'center';
        ctx.fillText('游戏结束', CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2);
        ctx.font = '24px Arial';
        ctx.fillText('按刷新键重新开始', CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 + 50);
    }
}

// 游戏循环
function gameLoop() {
    updateGame();
    drawGame();
    requestAnimationFrame(gameLoop);
}

// 初始化游戏
window.addEventListener('load', () => {
    initGame();
    gameLoop();
    
    // 添加键盘事件监听
    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
});
