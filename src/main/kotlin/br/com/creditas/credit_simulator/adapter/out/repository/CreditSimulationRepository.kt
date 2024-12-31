package br.com.creditas.credit_simulator.adapter.out.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CreditSimulationRepository: JpaRepository<CreditSimulationEntity, UUID>