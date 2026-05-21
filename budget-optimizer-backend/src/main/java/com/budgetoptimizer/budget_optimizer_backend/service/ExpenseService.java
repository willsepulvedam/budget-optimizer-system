package com.budgetoptimizer.budget_optimizer_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.budgetoptimizer.budget_optimizer_backend.dto.expense.ExpenseDTO;
import com.budgetoptimizer.budget_optimizer_backend.dto.expense.ExpenseResponseDTO;
import com.budgetoptimizer.budget_optimizer_backend.enums.PaymentMethod;
import com.budgetoptimizer.budget_optimizer_backend.model.Categoria;
import com.budgetoptimizer.budget_optimizer_backend.model.Empresa;
import com.budgetoptimizer.budget_optimizer_backend.model.Expense;
import com.budgetoptimizer.budget_optimizer_backend.model.Presupuesto;
import com.budgetoptimizer.budget_optimizer_backend.model.Usuario;
import com.budgetoptimizer.budget_optimizer_backend.repository.CategoriaRepository;
import com.budgetoptimizer.budget_optimizer_backend.repository.EmpresaRepository;
import com.budgetoptimizer.budget_optimizer_backend.repository.ExpenseRepository;
import com.budgetoptimizer.budget_optimizer_backend.repository.PresupuestoRepository;
import com.budgetoptimizer.budget_optimizer_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final PresupuestoRepository presupuestoRepo;
    private final CategoriaRepository categoriaRepo;
    private final UsuarioRepository usuarioRepo;
    private final EmpresaRepository empresaRepo;

    // ==========================================
    // CREACIÓN
    // ==========================================

    public ExpenseResponseDTO createExpense(ExpenseDTO dto) {
        log.info("Creando gasto - Usuario: {}, Monto: {}", dto.getUsuarioId(), dto.getMonto());

        // 1. Validar y obtener usuario
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        // 2. Validar y obtener presupuesto
        Presupuesto presupuesto = presupuestoRepo.findById(dto.getPresupuestoId())
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado con ID: " + dto.getPresupuestoId()));

        // 3. Validar que el presupuesto pertenece al usuario
        if (!presupuesto.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("El presupuesto no pertenece al usuario");
        }


        

        // 4. Validar que el presupuesto permite registrar gastos
        if (!presupuesto.getStatus().getPuedeRegistrarGastos()) {
            throw new IllegalStateException(
                    "El presupuesto en estado " + presupuesto.getStatus() + " no permite registrar gastos");
        }

        // 5. Validar y obtener categoría
        Categoria categoria = categoriaRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getCategoriaId()));

        if (!categoria.getActiva()) {
            throw new IllegalArgumentException("La categoría está inactiva");
        }

        if (!categoria.getTipo().puedeUsarseParaGastos()) {
            throw new IllegalArgumentException(
                    "La categoría '" + categoria.getNombre() + "' no puede usarse para gastos");
        }

        // 6. Obtener empresa si viene el ID (opcional)
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepo.findById(dto.getEmpresaId())
                    .orElse(null);
        }

        // 7. ✅ Crear el gasto usando Builder
        Expense expense = Expense.builder()
                .monto(dto.getMonto())
                .descripcion(dto.getDescripcion())
                .fechaGasto(dto.getFechaGasto())
                .usuario(usuario)
                .presupuesto(presupuesto)
                .categoria(categoria)
                .empresa(empresa)
                .metodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : PaymentMethod.CASH)
                .notas(dto.getNotas())
                .build();

        Expense saved = expenseRepo.save(expense);
        log.info("Gasto creado con ID: {}", saved.getId());

        return convertirAResponse(saved);
    }

    // ==========================================
    // CONSULTAS
    // ==========================================

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByPresupuesto(Long presupuestoId) {
        log.info("Obteniendo gastos del presupuesto ID: {}", presupuestoId);

        if (!presupuestoRepo.existsById(presupuestoId)) {
            throw new RuntimeException("Presupuesto no encontrado con ID: " + presupuestoId);
        }

        return expenseRepo.findByPresupuestoId(presupuestoId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByUsuario(Long usuarioId) {
        log.info("Obteniendo gastos del usuario ID: {}", usuarioId);

        if (!usuarioRepo.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
        }

        return expenseRepo.findByUsuarioIdOrderByFechaGastoDesc(usuarioId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByUsuarioAndDateRange(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fin) {

        log.info("Obteniendo gastos del usuario ID: {} entre {} y {}", usuarioId, inicio, fin);

        if (!usuarioRepo.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
        }

        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }

        return expenseRepo.findByUsuarioIdAndFechaGastoBetween(usuarioId, inicio, fin)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenseResponseDTO getExpenseById(Long id) {
        log.info("Obteniendo gasto con ID: {}", id);

        Expense expense = expenseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con ID: " + id));

        return convertirAResponse(expense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByPresupuestoAndCategoria(
            Long presupuestoId,
            Long categoriaId) {

        log.info("Obteniendo gastos del presupuesto {} en categoría {}", presupuestoId, categoriaId);

        if (!presupuestoRepo.existsById(presupuestoId)) {
            throw new RuntimeException("Presupuesto no encontrado con ID: " + presupuestoId);
        }

        if (!categoriaRepo.existsById(categoriaId)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + categoriaId);
        }

        return expenseRepo.findByPresupuestoIdAndCategoriaId(presupuestoId, categoriaId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ACTUALIZACIÓN
    // ==========================================

    public ExpenseResponseDTO updateExpense(Long id, ExpenseDTO dto) {
        log.info("Actualizando gasto ID: {}", id);

        // 1. Obtener gasto existente
        Expense expense = expenseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con ID: " + id));

        // 2. Validar y obtener nuevo presupuesto
        Presupuesto nuevoPresupuesto = presupuestoRepo.findById(dto.getPresupuestoId())
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado con ID: " + dto.getPresupuestoId()));

        // 3. Validar que el presupuesto permite editar
        if (!nuevoPresupuesto.getStatus().getPuedeRegistrarGastos()) {
            throw new IllegalStateException(
                    "El presupuesto en estado " + nuevoPresupuesto.getStatus() + " no permite modificar gastos");
        }

        // 4. Validar y obtener nueva categoría
        Categoria nuevaCategoria = categoriaRepo.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getCategoriaId()));

        if (!nuevaCategoria.getActiva()) {
            throw new IllegalArgumentException("La categoría está inactiva");
        }

        if (!nuevaCategoria.getTipo().puedeUsarseParaGastos()) {
            throw new IllegalArgumentException("La categoría no puede usarse para gastos");
        }

        // 5. Obtener empresa si viene el ID (opcional)
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepo.findById(dto.getEmpresaId())
                    .orElse(null);
        }

        // 6. Actualizar campos
        expense.setMonto(dto.getMonto());
        expense.setDescripcion(dto.getDescripcion());
        expense.setFechaGasto(dto.getFechaGasto());
        expense.setPresupuesto(nuevoPresupuesto);
        expense.setCategoria(nuevaCategoria);
        expense.setEmpresa(empresa);
        expense.setMetodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : PaymentMethod.CASH);
        expense.setNotas(dto.getNotas());

        Expense updated = expenseRepo.save(expense);
        log.info("Gasto actualizado: ID={}", id);

        return convertirAResponse(updated);
    }

    // ==========================================
    // ELIMINACIÓN
    // ==========================================

    public void deleteExpense(Long id) {
        log.info("Eliminando gasto ID: {}", id);

        Expense expense = expenseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con ID: " + id));

        // Validar que el presupuesto permite eliminar gastos
        if (!expense.getPresupuesto().getStatus().getPuedeRegistrarGastos()) {
            throw new IllegalStateException(
                    "No se pueden eliminar gastos de un presupuesto en estado " +
                            expense.getPresupuesto().getStatus());
        }

        expenseRepo.delete(expense);
        log.info("Gasto eliminado: ID={}", id);
    }

    // ==========================================
    // MÉTODO AUXILIAR
    // ==========================================

    private ExpenseResponseDTO convertirAResponse(Expense expense) {
        return ExpenseResponseDTO.builder()
                .id(expense.getId())
                .monto(expense.getMonto())
                .descripcion(expense.getDescripcion())
                .fechaGasto(expense.getFechaGasto())
                .presupuestoId(expense.getPresupuesto().getId())
                .presupuestoNombre(expense.getPresupuesto().getNombre())
                .categoriaId(expense.getCategoria().getId())
                .categoriaNombre(expense.getCategoria().getNombre())
                .categoriaIcono(expense.getCategoria().getIcono())
                .categoriaColor(expense.getCategoria().getColor())
                .usuarioId(expense.getUsuario().getId())
                .usuarioNombre(expense.getUsuario().getNombre())
                .fechaCreacion(expense.getFechaCreacion())
                .build();
    }
}