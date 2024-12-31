# Credit Simulator

Sistema referente a simulações de crédito para pessoas físicas, onde é necessário 
entrar com a idade do usuário, quantidade de pagamentos e o valor presente. 
Dado a isso é gerada uma simulação do valor a ser pago.

##  Tecnologias

- Kotlin v1.9.25 compativel com Amazon Corretto JDK v17.0
- Spring Boot v3.2.3
- Gradle v8.5
- Docker
- TestContainers
- Hibernate/JPA
- Hibernate Validator
- LocalStack
- Zalando Problems Details

## Dependencias

- PostgreSQL
- Amazon SES 
- Apache Kafka

## Como Rodar o projeto em sua maquina

1. Clone o repositório em sua máquina e faça a habilite o serviço do docker.

```shell
  git clone url-do-projeto
```

2. Vá até o diretório do projeto e faça o build da imagem do credit simulator, com o comando abaixo: 


```shell
  docker-compose build
```

3. Após o build, execute o comando para subir a infra-estrutura do projeto

```shell
  docker-compose up -d
```

Pronto, agora você tem o projeto executando em seu ambiente local, e pode realizar os testes 🎊🎊🎊

## Como testar o software? 
Com o projeto em execução em sua maquina, basta seguir os passos abaixos para testar os endpoints


### Requisicão de simulação de crédito simples


1. Baixe e configure um cliente HTTP como [Postman](https://www.postman.com/), [Insomnia](https://insomnia.rest/download) ou [Curl](https://curl.se/) para executar 
2. Execute o curl abaixo para realizar uma simulação de crédito simples

```shell
curl -X POST "http://localhost:8080/v1/credit/simulate" -H "Content-Type: application/json"      -d '{
           "presentValue": 100000.00,
           "dateOfBirth": "1990-01-01",
           "numberOfPayments": 50,
           "email": "example@example.com"
         }'
```

O sistema poderá responder com sucesso em duas situações, primeiro caso a solicitação seja respondida em um intervalo de tempo
menor igual a 1 segundo, você receberá uma Resposta HTTP com Status 200 OK, e o seguinte corpo: 

```json
{
   "simulationId": "f66f96b8-c075-4533-9845-f218b69fed50",
   "presentValue": 100000,
   "numberOfPayments": 50,
   "annualInterestRate": 0.03,
   "monthlyPayment": 2130.1,
   "email": "example@example.com",
   "status": "SUCCESS"
}
```

Caso sua solicitação exija um tempo de processamento maior igual 
a 1 segundo, você terá a solicitação processada de forma assíncrona, 
e receberá o id da simulação para consulta futura no tópico kafka 
com nome credit-simulator-topic. E receberá uma resposta HTTP com Status 202 ACCEPT, e o corpo abaixo:

```json
{
  "simulationId":"f66f96b8-c075-4533-9845-f218b69fed50",
  "message":"Request is processing and will finish asynchronously"
}
```


### Requisição de múltiplas análises de credito



```shell
curl --request POST \
  --url http://localhost:8080/v1/credit/simulate-multiple \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/10.3.0' \
  --data '[
	{
		"presentValue": 100000.00,
		"dateOfBirth": "1996-03-23",
		"numberOfPayments": 50,
		"email": "teste@teste.com"
	},
	{
		"presentValue": 100001.00,
		"dateOfBirth": "1996-03-23",
		"numberOfPayments": 50,
		"email": "teste@teste.com"
	}
]'
```
Assim como na simulação simples, a simulação múltipla com 
processamento em até 1 segundo, é retornado a seguinte Resposta HTTP com Status 200 OK:

```json
[
	{
		"simulationId": "86446fa3-630c-4b3b-be55-6a5765ab5573",
		"presentValue": 100000.00,
		"numberOfPayments": 50,
		"annualInterestRate": 0.03,
		"monthlyPayment": 2130.10
	},
	{
		"simulationId": "71a0685e-b63b-4e79-89ba-a268b90d8bed",
		"presentValue": 100001.00,
		"numberOfPayments": 50,
		"annualInterestRate": 0.03,
		"monthlyPayment": 2130.12
	}
]
```

Em caso de processamento assíncrono é retornada a Resposta HTTP com Status 202 ACCEPT

```json
{
	"message": "Request is processing and will finish asynchronously",
	"simulationIds": [
		"66f8f7c5-92c6-4dc2-b2ce-4e732f0658ec",
		"74386999-66fa-4b4f-a251-293c05fb7d4c"
	]
}
```


## Validação

Para validar se sua solicitação foi processada, você poderá consultar as mensagens do topico através do [kafdrop](http://localhost:19000/)
neste [endereco](http://localhost:19000/topic/credit-simulator-topic/allmessages).

E em caso de sucesso, é enviado para o email informado na requisição as informações da simulação.


```shell
From: creditteam@email.com
To: {'ToAddresses': ['teste@teste.com'], 'CcAddresses': [], 'BccAddresses': []}
Subject: Your Credit Simulation Details Are Ready!
Body:
Credit Simulation - ID: 6498bd26-2387-45d0-83fb-b982cbde8176
Dear Customer,
Below are the details of your credit simulation:
Requested amount: 100000.00
Number of installments: 50
Annual interest rate: 0.03%
Monthly installment amount: 2130.10
Best regards,
Credit Team
```

