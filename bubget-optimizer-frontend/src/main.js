// simulacion de datos 
const mockProducts = [
  {
    id: 1,
    name: 'iPhone 15',
    image: '',
    category: 'Electrónica',
    prices: [
      { store: 'Amazon', price: 999 },
      { store: 'Best Buy', price: 1019 },
      { store: 'Apple', price: 999 }
    ]
  },
  {
    id: 2,
    name: 'Zapatillas Nike',
    image: '',
    category: 'Ropa',
    prices: [
      { store: 'Amazon', price: 120 },
      { store: 'Nike Store', price: 140 },
      { store: 'Walmart', price: 115 }
    ]
  },
  {
    id: 3,
    name: 'Laptop HP',
    image: '',
    category: 'Electrónica',
    prices: [
      { store: 'Amazon', price: 599 },
      { store: 'Best Buy', price: 579 },
      { store: 'Newegg', price: 589 }
    ]
  },
  {
    id: 4,
    name: 'Sofá gris',
    image: '',
    category: 'Muebles',
    prices: [
      { store: 'IKEA', price: 299 },
      { store: 'Wayfair', price: 319 },
      { store: 'Amazon', price: 289 }
    ]
  },
  {
    id: 5,
    name: 'Café Premium',
    image: '',
    category: 'Alimentos',
    prices: [
      { store: 'Amazon', price: 12.99 },
      { store: 'Whole Foods', price: 14.99 },
      { store: 'Local Store', price: 11.99 }
    ]
  },
  {
    id: 6,
    name: 'Auriculares Sony',
    image: '',
    category: 'Electrónica',
    prices: [
      { store: 'Amazon', price: 349 },
      { store: 'Best Buy', price: 349 },
      { store: 'B&H', price: 339 }
    ]
  }
];

let currentProducts = [...mockProducts];
let selectedProduct = null;
let currentUser = null;

// inicializacion 
document.addEventListener('DOMContentLoaded', () => {
  initApp();
  setupEventListeners();
});

function initApp() {
  currentUser = JSON.parse(localStorage.getItem('currentUser'));
  if (currentUser) {
    goToDashboard();
  }
}

// navegacion
function goToComparador() {
  if (!currentUser) {
    openAuthOverlay();
    return;
  }
  showAppLayout();
  navigateTo('comparador');
}

function goToDashboard() {
  showAppLayout();
  navigateTo('dashboard');
}

function navigateTo(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.getElementById(page + '-page').classList.add('active');
  
  document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));
  document.querySelector(`[data-page="${page}"]`).classList.add('active');
  
  if (page === 'comparador') {
    renderProducts();
    loadChart();
  } else if (page === 'dashboard') {
    loadDashboardData();
  } else if (page === 'reports') {
    loadReports();
  }
}

function showAppLayout() {
  document.getElementById('landing-page').classList.remove('active');
  document.getElementById('app-layout').classList.remove('hidden');
  document.getElementById('user-name-display').textContent = currentUser.name;
}

// event listener
function setupEventListeners() {
  document.getElementById('form-login').addEventListener('submit', handleLogin);
  document.getElementById('form-register').addEventListener('submit', handleRegister);
  
  document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', () => {
      navigateTo(item.dataset.page);
    });
  });

  document.getElementById('search-input').addEventListener('keypress', (e) => {
    if (e.key === 'Enter') handleSearch();
  });
}

// autenticacion 
function openAuthOverlay() {
  document.getElementById('auth-overlay').classList.add('active');
}

function closeAuthOverlay() {
  document.getElementById('auth-overlay').classList.remove('active');
}

function switchToRegister() {
  document.getElementById('login-form').classList.add('hidden');
  document.getElementById('register-form').classList.remove('hidden');
}

function switchToLogin() {
  document.getElementById('register-form').classList.add('hidden');
  document.getElementById('login-form').classList.remove('hidden');
}

function handleLogin(e) {
  e.preventDefault();
  
  const email = document.getElementById('login-email').value.trim();
  const password = document.getElementById('login-password').value;
  
  const users = JSON.parse(localStorage.getItem('users')) || [];
  const user = users.find(u => u.email === email && u.password === password);
  
  if (user) {
    currentUser = user;
    localStorage.setItem('currentUser', JSON.stringify(user));
    document.getElementById('form-login').reset();
    closeAuthOverlay();
    goToDashboard();
    alert('Sesión iniciada correctamente ;D');
  } else {
    showError('login-password', 'Email o contraseña incorrectos');
  }
}

function handleRegister(e) {
  e.preventDefault();
  
  const name = document.getElementById('register-name').value.trim();
  const email = document.getElementById('register-email').value.trim();
  const password = document.getElementById('register-password').value;
  
  if (!name || !email || !password) {
    showError('register-name', 'Todos los campos son requeridos');
    return;
  }
  
  const users = JSON.parse(localStorage.getItem('users')) || [];
  
  if (users.some(u => u.email === email)) {
    showError('register-email', 'Este email ya está registrado');
    return;
  }
  
  if (password.length < 6) {
    showError('register-password', 'La contraseña debe tener al menos 6 caracteres');
    return;
  }
  
  const newUser = {
    id: Date.now(),
    name,
    email,
    password,
    income: 0,
    expenses: []
  };
  
  users.push(newUser);
  localStorage.setItem('users', JSON.stringify(users));
  
  alert('Registro exitoso. Por favor inicia sesión');
  switchToLogin();
  document.getElementById('form-register').reset();
}

function handleLogout() {
  if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
    localStorage.removeItem('currentUser');
    currentUser = null;
    document.getElementById('landing-page').classList.add('active');
    document.getElementById('app-layout').classList.add('hidden');
  }
}

// comparador 
function renderProducts() {
  const container = document.getElementById('products-list');
  
  if (currentProducts.length === 0) {
    container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: #999;">No se encontraron productos</p>';
    return;
  }

  container.innerHTML = currentProducts.map(product => `
    <div class="product-card" onclick="selectProduct(${product.id})">
      <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">${product.image}</div>
      <h4>${product.name}</h4>
      <p>$${Math.min(...product.prices.map(p => p.price)).toFixed(2)}</p>
    </div>
  `).join('');
}

function selectProduct(productId) {
  selectedProduct = currentProducts.find(p => p.id === productId);
  if (!selectedProduct) return;

  document.querySelectorAll('.product-card').forEach(card => card.classList.remove('selected'));
  event.target.closest('.product-card').classList.add('selected');

  const detailHtml = `
    <div style="text-align: center;">
      <div style="font-size: 4rem; margin-bottom: 1rem;">${selectedProduct.image}</div>
      <h3 style="color: #4f46e5; font-size: 1.5rem; margin-bottom: 0.5rem;">${selectedProduct.name}</h3>
      <p style="color: #999; margin-bottom: 0.5rem;">${selectedProduct.category}</p>
      <p style="color: #999; margin-bottom: 1.5rem;">Compara precios en todas las tiendas</p>
    </div>
  `;
  document.getElementById('product-detail').innerHTML = detailHtml;

  showComparison(selectedProduct);
  updateStats(selectedProduct);
}

function showComparison(product) {
  const container = document.getElementById('comparison-list');
  const sorted = [...product.prices].sort((a, b) => a.price - b.price);
  const bestPrice = sorted[0].price;
  
  container.innerHTML = sorted.map((item, index) => `
    <div class="store-item">
      <span class="store-name">${index === 0 ? '🏆 ' : ''}${item.store}</span>
      <span class="store-price">$${item.price.toFixed(2)}</span>
    </div>
  `).join('');
}

function updateStats(product) {
  const prices = product.prices.map(p => p.price);
  const avg = Math.round(prices.reduce((a, b) => a + b) / prices.length);
  const best = Math.min(...prices);
  const worst = Math.max(...prices);
  const savings = worst - best;

  document.getElementById('avg-price').textContent = `$${avg.toFixed(2)}`;
  document.getElementById('best-price').textContent = `$${best.toFixed(2)}`;
  document.getElementById('max-savings').textContent = `$${savings.toFixed(2)}`;

  updateDistributionChart(product);
}

function handleSearch() {
  const searchTerm = document.getElementById('search-input').value.trim().toLowerCase();
  
  if (!searchTerm) {
    currentProducts = [...mockProducts];
  } else {
    currentProducts = mockProducts.filter(p => 
      p.name.toLowerCase().includes(searchTerm) ||
      p.category.toLowerCase().includes(searchTerm)
    );
  }
  
  renderProducts();
  selectedProduct = null;
  document.getElementById('product-detail').innerHTML = '<p style="color: #999; text-align: center; padding: 2rem;"><i class="fas fa-inbox"></i> Selecciona un producto</p>';
  document.getElementById('comparison-list').innerHTML = '<p style="color: #999; text-align: center; padding: 1.5rem;"><i class="fas fa-inbox"></i> No hay comparación</p>';
}

//  dashboard
function loadDashboardData() {
  const expenses = currentUser.expenses || [];
  const totalExpenses = expenses.reduce((sum, e) => sum + e.amount, 0);
  const totalIncome = currentUser.income || 0;
  const savings = totalIncome - totalExpenses;

  document.getElementById('total-income').textContent = `$${totalIncome.toFixed(2)}`;
  document.getElementById('total-expenses').textContent = `$${totalExpenses.toFixed(2)}`;
  document.getElementById('total-savings').textContent = `$${savings.toFixed(2)}`;

  loadExpensesChart();
}

let expensesChart = null;

function loadExpensesChart() {
  const expenses = currentUser.expenses || [];
  const categoriesData = {
    'alimentación': 0,
    'transporte': 0,
    'vivienda': 0,
    'entretenimiento': 0,
    'salud': 0,
    'otro': 0
  };

  expenses.forEach(exp => {
    if (categoriesData.hasOwnProperty(exp.category)) {
      categoriesData[exp.category] += exp.amount;
    }
  });

  const ctx = document.getElementById('expenses-chart').getContext('2d');
  
  if (expensesChart) expensesChart.destroy();
  
  expensesChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: Object.keys(categoriesData).map(c => c.charAt(0).toUpperCase() + c.slice(1)),
      datasets: [{
        data: Object.values(categoriesData),
        backgroundColor: ['#4f46e5', '#6366f1', '#a5b4fc', '#c7d2fe', '#ddd6fe', '#ede9fe'],
        borderColor: '#fff',
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'bottom'
        }
      }
    }
  });
}

//  grafica
let trendChart = null;
let distributionChart = null;

function loadChart() {
  const ctx = document.getElementById('trend-chart').getContext('2d');
  
  if (trendChart) trendChart.destroy();
  
  trendChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sab', 'Dom'],
      datasets: [{
        label: 'Precio promedio',
        data: [850, 820, 900, 870, 880, 900, 920],
        borderColor: '#4f46e5',
        backgroundColor: 'rgba(79, 70, 229, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#4f46e5',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 5
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, grid: { drawBorder: false } }
      }
    }
  });
}

function updateDistributionChart(product) {
  const ctx = document.getElementById('distribution-chart').getContext('2d');
  
  if (distributionChart) distributionChart.destroy();
  
  const stores = product.prices.map(p => p.store);
  const prices = product.prices.map(p => p.price);
  
  distributionChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: stores,
      datasets: [{
        data: prices,
        backgroundColor: ['#4f46e5', '#6366f1', '#a5b4fc'],
        borderColor: '#fff',
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { position: 'bottom' } }
    }
  });
}

// presupuestos
function openAddBudget() {
  alert(' Funcionalidad para agregar presupuestos');
}

// trnasaacciones
function openAddTransaction() {
  alert(' Funcionalidad para agregar transacciones');
}

// reportes
function loadReports() {
  // Charts para reportes
}

// utilidades
function showError(inputId, message) {
  const input = document.getElementById(inputId);
  const errorDiv = input.nextElementSibling;
  errorDiv.textContent = message;
  errorDiv.style.display = 'block';
  input.style.borderColor = '#ef4444';
  
  setTimeout(() => {
    errorDiv.style.display = 'none';
    input.style.borderColor = '#e5e7eb';
  }, 3000);
}