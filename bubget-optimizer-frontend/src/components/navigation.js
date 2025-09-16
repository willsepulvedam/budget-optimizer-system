// Configuración de la navegación
export function setupNavigation() {
    // Marcar el enlace activo
    const links = document.querySelectorAll('.nav-link');
    
    links.forEach(link => {
        if (link.getAttribute('href') === window.location.pathname) {
            link.classList.add('active');
        }
        
        link.addEventListener('click', () => {
            // Remover la clase active de todos los enlaces
            links.forEach(l => l.classList.remove('active'));
            // Agregar la clase active al enlace clickeado
            link.classList.add('active');
        });
    });
}