const BOARD_SIZE = 4;
const TARGET = 2028;

let board = [];
let score = 0;
let undoStack = [];
let redoStack = [];

const boardElem = document.getElementById('game-board');
const scoreElem = document.getElementById('score');
const messageElem = document.getElementById('game-message');
const undoBtn = document.getElementById('undo-btn');
const redoBtn = document.getElementById('redo-btn');
const restartBtn = document.getElementById('restart-btn');

document.addEventListener('DOMContentLoaded', () => {
  startGame();
  document.addEventListener('keydown', handleKey);
  undoBtn.addEventListener('click', undoMove);
  redoBtn.addEventListener('click', redoMove);
  restartBtn.addEventListener('click', startGame);
});

function startGame() {
  board = Array.from({ length: BOARD_SIZE }, () => Array(BOARD_SIZE).fill(0));
  score = 0;
  undoStack = [];
  redoStack = [];
  addRandomTile();
  addRandomTile();
  updateUI();
  messageElem.textContent = '';
}

function saveState() {
  undoStack.push({
    board: board.map(row => row.slice()),
    score: score
  });
  if (undoStack.length > 100) undoStack.shift(); // Limit history
}

function handleKey(e) {
  if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
    e.preventDefault();
    move(e.key.replace('Arrow', '').toLowerCase());
  }
}

function move(direction) {
  if (isGameOver() || isGameWon()) return;
  saveState();
  redoStack = [];
  let { newBoard, moved, gained } = moveBoard(board, direction);
  if (moved) {
    board = newBoard;
    score += gained;
    addRandomTile();
    updateUI();
    if (isGameWon()) {
      messageElem.textContent = 'You win!';
    } else if (isGameOver()) {
      messageElem.textContent = 'Game over!';
    }
  } else {
    // If no move, don't save state
    undoStack.pop();
  }
}

function undoMove() {
  if (undoStack.length === 0) return;
  redoStack.push({
    board: board.map(row => row.slice()),
    score: score
  });
  const prev = undoStack.pop();
  board = prev.board.map(row => row.slice());
  score = prev.score;
  updateUI();
  messageElem.textContent = '';
}

function redoMove() {
  if (redoStack.length === 0) return;
  undoStack.push({
    board: board.map(row => row.slice()),
    score: score
  });
  const next = redoStack.pop();
  board = next.board.map(row => row.slice());
  score = next.score;
  updateUI();
  messageElem.textContent = '';
}

function addRandomTile() {
  let empty = [];
  for (let r = 0; r < BOARD_SIZE; r++) {
    for (let c = 0; c < BOARD_SIZE; c++) {
      if (board[r][c] === 0) empty.push([r, c]);
    }
  }
  if (empty.length === 0) return;
  let [r, c] = empty[Math.floor(Math.random() * empty.length)];
  board[r][c] = Math.random() < 0.9 ? 2 : 4;
}

function updateUI() {
  boardElem.innerHTML = '';
  for (let r = 0; r < BOARD_SIZE; r++) {
    for (let c = 0; c < BOARD_SIZE; c++) {
      const val = board[r][c];
      const tile = document.createElement('div');
      tile.className = 'tile' + (val ? ' tile-' + val : '');
      tile.textContent = val ? val : '';
      boardElem.appendChild(tile);
    }
  }
  scoreElem.textContent = score;
}

function moveBoard(inputBoard, direction) {
  let board = inputBoard.map(row => row.slice());
  let moved = false;
  let gained = 0;
  function slide(row) {
    let arr = row.filter(x => x);
    for (let i = 0; i < arr.length - 1; i++) {
      if (arr[i] === arr[i + 1]) {
        arr[i] *= 2;
        gained += arr[i];
        arr[i + 1] = 0;
        i++;
      }
    }
    arr = arr.filter(x => x);
    while (arr.length < BOARD_SIZE) arr.push(0);
    return arr;
  }
  for (let i = 0; i < BOARD_SIZE; i++) {
    let row = [];
    if (direction === 'left') {
      row = slide(board[i]);
      if (!moved && row.some((v, idx) => v !== board[i][idx])) moved = true;
      board[i] = row;
    } else if (direction === 'right') {
      row = slide(board[i].slice().reverse()).reverse();
      if (!moved && row.some((v, idx) => v !== board[i][idx])) moved = true;
      board[i] = row;
    } else if (direction === 'up') {
      let col = slide(board.map(row => row[i]));
      if (!moved && col.some((v, idx) => v !== board[idx][i])) moved = true;
      for (let j = 0; j < BOARD_SIZE; j++) board[j][i] = col[j];
    } else if (direction === 'down') {
      let col = slide(board.map(row => row[i]).reverse()).reverse();
      if (!moved && col.some((v, idx) => v !== board[idx][i])) moved = true;
      for (let j = 0; j < BOARD_SIZE; j++) board[j][i] = col[j];
    }
  }
  return { newBoard: board, moved, gained };
}

function isGameWon() {
  return board.flat().includes(TARGET);
}

function isGameOver() {
  if (board.flat().includes(0)) return false;
  for (let r = 0; r < BOARD_SIZE; r++) {
    for (let c = 0; c < BOARD_SIZE; c++) {
      let val = board[r][c];
      if (r < BOARD_SIZE - 1 && board[r + 1][c] === val) return false;
      if (c < BOARD_SIZE - 1 && board[r][c + 1] === val) return false;
    }
  }
  return true;
}