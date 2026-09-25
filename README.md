# Integração ViaCEP

API REST para cadastro de produtos e verificação de disponibilidade por cidade. O projeto consulta a ViaCEP, descobre a cidade do CEP informado e compara com o centro de distribuição do produto.

## Tecnologias

- Java 17
- Spring Boot 3
- Spring Web, Data JPA e Validation
- PostgreSQL
- Flyway
- Maven
- JUnit e Mockito

## Como executar

Pré-requisito para a forma mais simples: Docker Desktop.

1. Suba a aplicação, o PostgreSQL e o pgAdmin:

   ```bash
   docker compose up --build -d
   ```

2. A API ficará disponível em `http://localhost:8080`.

O Flyway cria a tabela, insere os produtos iniciais e adiciona o campo `distribution_center` automaticamente. Nenhum script precisa ser executado manualmente.

Para executar a aplicação pelo VS Code, suba apenas os serviços de apoio:

```bash
docker compose up -d db pgadmin
```

Depois inicie a aplicação com Java 17:

No Windows:

```bash
mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

O banco usa as seguintes configurações:

- banco: `product`
- usuário: `postgres`
- senha: `password`
- porta: `5432`

O pgAdmin fica disponível em `http://localhost:15432`, com usuário `admin@admin.com` e senha `root123`.

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | `/product` | Lista os produtos ativos |
| GET | `/product/{id}` | Busca um produto ativo por id |
| POST | `/product` | Cadastra um produto |
| PUT | `/product/{id}` | Atualiza um produto |
| DELETE | `/product/{id}` | Inativa um produto |
| GET | `/product/category/{category}` | Filtra produtos ativos por categoria |
| GET | `/product/top5-by-price` | Retorna os cinco produtos ativos mais caros |
| GET | `/product/availability/{id}?cep={cep}` | Verifica a disponibilidade pela cidade do CEP |
| POST | `/product/category/{categoryPath}/filter?categoryParam={category}` | Exercício com Path Variable, Request Param, Request Header e Request Body |

### Cadastrar ou atualizar um produto

```json
{
  "name": "Teclado mecânico",
  "price": 35000,
  "category": "electronics",
  "distributionCenter": "Mogi das Cruzes"
}
```

Os centros de distribuição aceitos são `Mogi das Cruzes`, `Recife` e `Porto Alegre`.

### Verificar disponibilidade

```http
GET /product/availability/p1?cep=08773380
```

O retorno é `true` quando a cidade informada pela ViaCEP é igual ao centro de distribuição do produto e `false` quando é diferente.

O serviço também trata os seguintes cenários:

- CEP fora do formato esperado: status `400`;
- produto ou CEP inexistente: status `404`;
- timeout, indisponibilidade ou resposta inválida da ViaCEP: status `503`.

As chamadas externas têm timeout de conexão de 2 segundos e timeout de leitura de 3 segundos. Esses valores e a URL da ViaCEP podem ser alterados no `application.properties`.

## Testes

Execute:

```bash
mvnw.cmd test
```

Os testes cobrem a validação do endpoint, a comparação entre cidade e centro de distribuição, CEP válido, CEP inválido, CEP inexistente e indisponibilidade da ViaCEP.

## Postman

Importe o arquivo `postman/Atividade_M1_ViaCEP.postman_collection.json`. A collection contém o CRUD, os filtros, os exemplos dos componentes HTTP e os principais cenários da integração.
