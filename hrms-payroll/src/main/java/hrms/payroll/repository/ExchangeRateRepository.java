package hrms.payroll.repository;

import hrms.payroll.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    List<ExchangeRate> findByCurrencyCodeIgnoreCaseOrderByEffectiveDateDescIdDesc(String currencyCode);

    Optional<ExchangeRate> findTopByCurrencyCodeIgnoreCaseAndEffectiveDateLessThanEqualOrderByEffectiveDateDescIdDesc(
            String currencyCode,
            Date effectiveDate
    );
}
