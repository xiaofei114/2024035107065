const WebSocket = require('ws');
const QRCode = require('qrcode');

// 配置
const WS_PORT = 8080;  // WebSocket端口
const HTTP_PORT = 3000; // HTTP端口
const DOMAIN = '10.249.105.158'; // 这里可以修改为你的域名

// 游戏状态
const rooms = new Map();
const players = new Map();

// 生成随机房间号
function generateRoomId() {
  return Math.floor(1000 + Math.random() * 9000).toString();
}

// 生成二维码
function generateQRCode(roomId) {
  const url = `http://${DOMAIN}:${HTTP_PORT}/?room=${roomId}`;
  return new Promise((resolve, reject) => {
    QRCode.toDataURL(url, (err, data) => {
      if (err) reject(err);
      else resolve(data);
    });
  });
}

// 创建WebSocket服务器
const wss = new WebSocket.Server({ port: WS_PORT });

console.log(`WebSocket server running on ws://${DOMAIN}:${WS_PORT}`);
console.log(`HTTP server available at http://${DOMAIN}:${HTTP_PORT}`);

// 处理连接
wss.on('connection', (ws, req) => {
  // 获取客户端IP地址
  const clientIP = req.socket.remoteAddress || req.connection.remoteAddress || 'unknown';
  console.log(`Client connected from IP: ${clientIP}`);
  
  let playerId = null;
  let roomId = null;
  
  // 发送初始消息
  ws.send(JSON.stringify({
    type: 'init',
    message: 'Welcome to Guess Number Game!'
  }));
  
  // 处理消息
  ws.on('message', async (message) => {
    try {
      const data = JSON.parse(message);
      
      switch (data.type) {
        case 'createRoom':
          // 创建新房间
          roomId = generateRoomId();
          const qrCode = await generateQRCode(roomId);
          
          // 初始化房间
          rooms.set(roomId, {
            id: roomId,
            players: [],
            spectators: [],
            status: 'waiting', // waiting, ready, playing, ended
            currentPlayer: null,
            secretNumber: null,
            difficulty: 'normal',
            guessCount: 0,
            winner: null
          });
          
          // 使用IP地址作为玩家ID
          playerId = `player_${clientIP.replace(/:/g, '_')}`;
          players.set(playerId, {
            id: playerId,
            ws: ws,
            roomId: roomId,
            role: 'player',
            status: 'waiting', // waiting, ready
            isHost: true
          });
          
          // 添加玩家到房间
          rooms.get(roomId).players.push(playerId);
          
          // 发送房间信息
          ws.send(JSON.stringify({
            type: 'roomCreated',
            roomId: roomId,
            qrCode: qrCode,
            playerId: playerId,
            isHost: true
          }));
          
          console.log(`Room ${roomId} created by ${playerId}`);
          break;
          
        case 'joinRoom':
          // 加入现有房间
          roomId = data.roomId;
          const room = rooms.get(roomId);
          
          if (!room) {
            ws.send(JSON.stringify({
              type: 'error',
              message: 'Room not found'
            }));
            return;
          }
          
          // 使用IP地址作为玩家ID
          playerId = `player_${clientIP.replace(/:/g, '_')}`;
          
          // 检查房间是否已满
          if (room.players.length >= 2) {
            // 作为观众加入
            players.set(playerId, {
              id: playerId,
              ws: ws,
              roomId: roomId,
              role: 'spectator'
            });
            room.spectators.push(playerId);
            
            ws.send(JSON.stringify({
              type: 'joinedAsSpectator',
              roomId: roomId,
              playerId: playerId
            }));
          } else {
            // 作为玩家加入
            players.set(playerId, {
              id: playerId,
              ws: ws,
              roomId: roomId,
              role: 'player',
              status: 'waiting',
              isHost: false
            });
            room.players.push(playerId);
            
            ws.send(JSON.stringify({
              type: 'joinedAsPlayer',
              roomId: roomId,
              playerId: playerId,
              isHost: false
            }));
            
            // 通知房主有新玩家加入
            const hostPlayer = players.get(room.players[0]);
            if (hostPlayer && hostPlayer.ws.readyState === WebSocket.OPEN) {
              hostPlayer.ws.send(JSON.stringify({
                type: 'playerJoined',
                playerId: playerId
              }));
            }
          }
          
          console.log(`Player ${playerId} joined room ${roomId} as ${players.get(playerId).role}`);
          break;
          
        case 'setDifficulty':
          // 设置难度（只有房主可以）
          if (!roomId) return;
          
          const player = players.get(playerId);
          const targetRoom = rooms.get(roomId);
          
          if (player && player.isHost && targetRoom) {
            targetRoom.difficulty = data.difficulty;
            
            // 通知所有玩家
            broadcastToRoom(roomId, JSON.stringify({
              type: 'difficultySet',
              difficulty: data.difficulty
            }));
            
            console.log(`Room ${roomId} difficulty set to ${data.difficulty}`);
          }
          break;
          
        case 'ready':
          // 玩家准备
          if (!roomId || !playerId) return;
          
          const currentPlayer = players.get(playerId);
          const gameRoom = rooms.get(roomId);
          
          if (currentPlayer && currentPlayer.role === 'player' && gameRoom) {
            currentPlayer.status = 'ready';
            
            // 检查是否所有玩家都准备就绪
            const allReady = gameRoom.players.every(pId => {
              const p = players.get(pId);
              return p && p.status === 'ready';
            });
            
            // 通知所有玩家
            broadcastToRoom(roomId, JSON.stringify({
              type: 'playerReady',
              playerId: playerId
            }));
            
            if (allReady && gameRoom.players.length >= 1) {
              // 开始游戏
              startGame(roomId);
            }
          }
          break;
          
        case 'guess':
          // 猜数字
          if (!roomId || !playerId) return;
          
          const guessingPlayer = players.get(playerId);
          const activeRoom = rooms.get(roomId);
          
          if (guessingPlayer && guessingPlayer.role === 'player' && activeRoom && activeRoom.status === 'playing' && activeRoom.currentPlayer === playerId) {
            const guess = parseInt(data.number);
            activeRoom.guessCount++;
            
            let result = '';
            let gameOver = false;
            
            if (guess === activeRoom.secretNumber) {
              result = 'correct';
              gameOver = true;
              activeRoom.status = 'ended';
              activeRoom.winner = playerId;
            } else if (guess > activeRoom.secretNumber) {
              result = 'tooHigh';
            } else {
              result = 'tooLow';
            }
            
            let nextPlayerId = null;
            
            if (!gameOver) {
              // 切换到下一个玩家
              const currentIndex = activeRoom.players.indexOf(playerId);
              const nextIndex = (currentIndex + 1) % activeRoom.players.length;
              activeRoom.currentPlayer = activeRoom.players[nextIndex];
              nextPlayerId = activeRoom.currentPlayer;
              
              // 通知下一个玩家
              const nextPlayer = players.get(activeRoom.currentPlayer);
              if (nextPlayer && nextPlayer.ws.readyState === WebSocket.OPEN) {
                nextPlayer.ws.send(JSON.stringify({
                  type: 'yourTurn'
                }));
              }
            }
            
            // 通知所有玩家
            broadcastToRoom(roomId, JSON.stringify({
              type: 'guessResult',
              playerId: playerId,
              number: guess,
              result: result,
              secretNumber: gameOver ? activeRoom.secretNumber : null,
              winner: gameOver ? playerId : null,
              guessCount: activeRoom.guessCount,
              nextPlayer: nextPlayerId
            }));
          }
          break;
          
        case 'restart':
          // 重新开始游戏
          if (!roomId || !playerId) return;
          
          const restartPlayer = players.get(playerId);
          const restartRoom = rooms.get(roomId);
          
          if (restartPlayer && restartPlayer.isHost && restartRoom) {
            // 重置游戏状态
            restartRoom.players.forEach(pId => {
              const p = players.get(pId);
              if (p) {
                p.status = 'waiting';
              }
            });
            
            restartRoom.status = 'waiting';
            restartRoom.currentPlayer = null;
            restartRoom.secretNumber = null;
            restartRoom.guessCount = 0;
            restartRoom.winner = null;
            
            // 通知所有玩家
            broadcastToRoom(roomId, JSON.stringify({
              type: 'gameRestarted'
            }));
          }
          break;
      }
    } catch (error) {
      console.error('Error processing message:', error);
      ws.send(JSON.stringify({
        type: 'error',
        message: 'Invalid message format'
      }));
    }
  });
  
  // 处理断开连接
  ws.on('close', () => {
    if (playerId && roomId) {
      const player = players.get(playerId);
      const room = rooms.get(roomId);
      
      if (player) {
        if (player.role === 'player' && room) {
          // 从房间中移除玩家
          const playerIndex = room.players.indexOf(playerId);
          if (playerIndex > -1) {
            room.players.splice(playerIndex, 1);
          }
          
          // 如果是房主，转让房主身份
          if (player.isHost && room.players.length > 0) {
            const newHost = players.get(room.players[0]);
            if (newHost) {
              newHost.isHost = true;
              newHost.ws.send(JSON.stringify({
                type: 'becameHost'
              }));
            }
          }
          
          // 如果房间为空，删除房间
          if (room.players.length === 0 && room.spectators.length === 0) {
            rooms.delete(roomId);
            console.log(`Room ${roomId} deleted`);
          } else {
            // 通知其他玩家
            broadcastToRoom(roomId, JSON.stringify({
              type: 'playerLeft',
              playerId: playerId
            }));
          }
        } else if (player.role === 'spectator' && room) {
          // 从观众中移除
          const spectatorIndex = room.spectators.indexOf(playerId);
          if (spectatorIndex > -1) {
            room.spectators.splice(spectatorIndex, 1);
          }
        }
        
        // 移除玩家
        players.delete(playerId);
        console.log(`Player ${playerId} disconnected`);
      }
    }
  });
});

// 开始游戏
function startGame(roomId) {
  const room = rooms.get(roomId);
  if (!room) return;
  
  // 设置游戏状态
  room.status = 'playing';
  
  // 生成随机数
  let maxNumber = 100;
  switch (room.difficulty) {
    case 'easy':
      maxNumber = 50;
      break;
    case 'hard':
      maxNumber = 500;
      break;
  }
  room.secretNumber = Math.floor(Math.random() * maxNumber) + 1;
  
  // 随机选择第一个玩家
  room.currentPlayer = room.players[Math.floor(Math.random() * room.players.length)];
  
  // 通知所有玩家游戏开始
  broadcastToRoom(roomId, JSON.stringify({
    type: 'gameStarted',
    difficulty: room.difficulty,
    firstPlayer: room.currentPlayer
  }));
  
  // 通知第一个玩家轮到他
  const firstPlayer = players.get(room.currentPlayer);
  if (firstPlayer && firstPlayer.ws.readyState === WebSocket.OPEN) {
    firstPlayer.ws.send(JSON.stringify({
      type: 'yourTurn'
    }));
  }
  
  console.log(`Game started in room ${roomId}, secret number: ${room.secretNumber}`);
}

// 向房间内所有玩家广播消息
function broadcastToRoom(roomId, message) {
  const room = rooms.get(roomId);
  if (!room) return;
  
  // 广播给玩家
  room.players.forEach(playerId => {
    const player = players.get(playerId);
    if (player && player.ws.readyState === WebSocket.OPEN) {
      player.ws.send(message);
    }
  });
  
  // 广播给观众
  room.spectators.forEach(spectatorId => {
    const spectator = players.get(spectatorId);
    if (spectator && spectator.ws.readyState === WebSocket.OPEN) {
      spectator.ws.send(message);
    }
  });
}

// 启动HTTP服务器，提供静态文件
const http = require('http');
const fs = require('fs');
const path = require('path');

const server = http.createServer((req, res) => {
  // 解析URL，获取路径部分（不包括查询参数）
  const url = new URL(req.url, `http://${req.headers.host}`);
  const pathname = url.pathname;
  
  // 提供静态文件
  let filePath = path.join(__dirname, pathname === '/' ? 'guess-number-game.html' : pathname);
  
  // 检查文件是否存在
  fs.exists(filePath, (exists) => {
    if (exists) {
      // 读取文件
      fs.readFile(filePath, (err, content) => {
        if (err) {
          res.writeHead(500);
          res.end('Internal Server Error');
        } else {
          // 设置内容类型
          const extname = path.extname(filePath);
          let contentType = 'text/html';
          switch (extname) {
            case '.js':
              contentType = 'text/javascript';
              break;
            case '.css':
              contentType = 'text/css';
              break;
          }
          
          res.writeHead(200, { 'Content-Type': contentType });
          res.end(content, 'utf-8');
        }
      });
    } else {
      res.writeHead(404);
      res.end('File not found');
    }
  });
});

server.listen(HTTP_PORT, () => {
  console.log(`HTTP server running on http://${DOMAIN}:${HTTP_PORT}`);
});