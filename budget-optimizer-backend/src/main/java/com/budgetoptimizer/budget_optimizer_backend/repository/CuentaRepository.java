package com.budgetoptimizer.budget_optimizer_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.budgetoptimizer.budget_optimizer_backend.model.Cuenta;
import com.budgetoptimizer.budget_optimizer_backend.enums.AccountType;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByUsuarioId(Long usuarioId);
    List<Cuenta> findByTipoCuenta(AccountType tipoCuenta);
    List<Cuenta> findBySaldoGreaterThan(Double saldo);
}
