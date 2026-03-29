package hrms.payroll.repository;

import hrms.payroll.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByCodeIgnoreCase(String code);

    Optional<Currency> findByBaseCurrencyTrue();

    boolean existsByBaseCurrencyTrueAndIdNot(Long id);

    boolean existsByBaseCurrencyTrue();
}
