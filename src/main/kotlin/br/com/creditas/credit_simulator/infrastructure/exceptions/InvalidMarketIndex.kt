package br.com.creditas.credit_simulator.infrastructure.exceptions

class InvalidMarketIndex(marketIndex: String)
    : RuntimeException("Market index $marketIndex not found in the configured list.")
