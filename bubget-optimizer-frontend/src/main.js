// Importar estilos
import './style.css'

// Importar módulos
import { initRouter } from './utils/router.js'
import { setupNavigation } from './components/navigation.js'

// Inicializar la aplicación
document.addEventListener('DOMContentLoaded', () => {
  // Inicializar el router
  initRouter()
  
  // Configurar la navegación
  setupNavigation()
  
  // Cargar la página inicial
  const app = document.getElementById('app')
  app.innerHTML = `
    <div class="container">
      <header class="py-3">
        <h1>Budget Optimizer</h1>
        <nav class="nav">
          <a href="/" class="nav-link">Dashboard</a>
          <a href="/budgets" class="nav-link">Budgets</a>
          <a href="/transactions" class="nav-link">Transactions</a>
          <a href="/reports" class="nav-link">Reports</a>
        </nav>
      </header>
      <main id="main-content" class="py-4">
        <!-- El contenido dinámico se cargará aquí -->
      </main>
    </div>
  `
})
