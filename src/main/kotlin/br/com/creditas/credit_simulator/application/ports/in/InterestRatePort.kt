package br.com.creditas.credit_simulator.application.ports.`in`

import br.com.creditas.credit_simulator.application.domain.InterestRate

interface InterestRatePort {
    operator fun invoke(yearsOld: Int): InterestRate
}