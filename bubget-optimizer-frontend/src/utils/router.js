// Función simple de enrutamiento para JavaScript vanilla
export function initRouter() {
    window.addEventListener('popstate', handleRoute);
    document.addEventListener('click', handleLinkClick);
    handleRoute();
}

function handleLinkClick(e) {
    if (e.target.matches('a') && e.target.href.startsWith(window.location.origin)) {
        e.preventDefault();
        const url = new URL(e.target.href);
        history.pushState(null, '', url.pathname);
        handleRoute();
    }
}

function handleRoute() {
    const path = window.location.pathname;
    const mainContent = document.getElementById('main-content');
    
    // Limpiar el contenido actual
    mainContent.innerHTML = '';
    
    // Enrutar a la página correspondiente
    switch(path) {
        case '/':
            loadDashboard(mainContent);
            break;
        case '/budgets':
            loadBudgets(mainContent);
            break;
        case '/transactions':
            loadTransactions(mainContent);
            break;
        case '/reports':
            loadReports(mainContent);
            break;
        default:
            mainContent.innerHTML = '<h2>404 - Página no encontrada</h2>';
    }
}

function loadDashboard(container) {
    container.innerHTML = '<h2>Dashboard</h2>';
    // Aquí irá la lógica para cargar el dashboard
}

function loadBudgets(container) {
    container.innerHTML = '<h2>Presupuestos</h2>';
    // Aquí irá la lógica para cargar los presupuestos
}

function loadTransactions(container) {
    container.innerHTML = '<h2>Transacciones</h2>';
    // Aquí irá la lógica para cargar las transacciones
}

function loadReports(container) {
    container.innerHTML = '<h2>Reportes</h2>';
    // Aquí irá la lógica para cargar los reportes
}