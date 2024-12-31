package br.com.creditas.credit_simulator.application.service

import br.com.creditas.credit_simulator.application.ports.`in`.InterestRatePort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class InterestRateService : InterestRatePort {

    override fun invoke(yearsOld: Int): BigDecimal {

        return when {
            yearsOld < 26 -> BigDecimal("0.05")
            yearsOld in 26..40 -> BigDecimal("0.03")
            yearsOld in 41..60 -> BigDecimal("0.02")
            else -> BigDecimal("0.04")
        }
    }
}