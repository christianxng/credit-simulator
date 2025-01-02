package br.com.creditas.credit_simulator.base

import com.fasterxml.jackson.annotation.JsonProperty

data class EmailMessage(
    @JsonProperty("Id")
    val id: String,
    @JsonProperty("Region")
    val region: String,
    @JsonProperty("Destination")
    val destination: EmailDestination,
    @JsonProperty("Source")
    val source: String,
    @JsonProperty("Subject")
    val subject: String,
    @JsonProperty("Body")
    val body: EmailBody,
    @JsonProperty("Timestamp")
    val timestamp: String
)

data class EmailDestination(
    @JsonProperty("ToAddresses")
    val toAddresses: List<String>
)

data class EmailBody(
    @JsonProperty("text_part")
    val textPart: String?,
    @JsonProperty("html_part")
    val htmlPart: String?
)

data class EmailResponse(
    val messages: List<EmailMessage>
)
