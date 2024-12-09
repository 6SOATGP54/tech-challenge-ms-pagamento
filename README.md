# Sistema para Lanchonete - Microsserviço de Pagamento

Este repositório contém a implementação de um dos microsserviços do sistema controle e autoatendimento de pedidos em lanchonete, desenvolvido como parte de um trabalho de Pós-graduação em Software Architecture. O projeto foi refatorado em microsserviços seguindo as boas práticas de arquitetura de software e os requisitos estabelecidos.

O microsseviço de pagamento está, também, integrado à API do Mercado Pago.

## Estrutura do Sistema

O sistema foi dividido em três microsserviços principais, cada um com responsabilidade específica:

### **1. Pedido**
- Responsável pela gestão e acompanhamento do processo de produção de pedidos.
- Funcionalidades:
    - Registro de novos pedidos.
    - Retorno das informações para montagem de pedidos.
    - Listagem de pedidos registrados.
    - Gestão da fila de pedidos (visão da cozinha).
    - Atualização de status de cada etapa de produção.

### **2. Pagamento**
- Responsável pela gestão dos pagamentos dos pedidos.
- Funcionalidades:
    - Registro de solicitações de pagamento.
    - Atualização do status dos pedidos com base no retorno do processo de pagamento.

### **3. Produto**
- Responsável pela gestão do catálogo de produtos da lanchonete.
- Funcionalidades:
    - Registro e atualização de produtos no catálogo.
    - Consulta detalhada de informações sobre produtos.
    - Disponibilização de dados para outros serviços, como preços e descrições.

Cada microsserviço possui um banco de dados independente, garantindo isolamento e modularidade. Foi utilizado:
- **Banco de dados SQL** para o microsserviço **Produto**.
- **Banco de dados NoSQL** para os microsserviços **Pedido** e **Pagamento**.

## Tecnologias Utilizadas

- **Linguagens e Frameworks**: Java <img src="./docs/logos/java-logo.png" width="16"></img> com Springboot <img src="./docs/logos/spring-boot-logo.png" width="16"></img>
- **Bancos de Dados**: PostgreSQL <img src="./docs/logos/postgresql-logo.png" width="16"></img> e MongoDB <img src="./docs/logos/mongodb-logo.png" width="16"></img>
- **Ferramentas de CI/CD**: GitHub Actions <img src="./docs/logos/github-actions-logo.png" width="16"></img>
- **Ferramentas de Testes**:
    - Cobertura de testes: 80% (mínimo) para todos os serviços com JUnit <img src="./docs/logos/junit.png" width="16"></img>.
    - BDD implementado em um dos caminhos de teste <img src="./docs/logos/cypress-logo.png" width="16"></img>.

## Regras de Desenvolvimento

1. **Estrutura de Branches**:
    - As branches `main` estão protegidas, não permitindo commits diretamente.
    - As branches `develop` contém as atualizações mais recentes de código.
    - Atualizações nas branches `main` são feitas por meio de Pull Requests.

2. **Validação Automática**:
    - O build da aplicação e a qualidade do código (mínimo de 70% de cobertura) são validados via SonarQube.

3. **Deploy Automatizado**:
    - O merge para a branch principal dispara o deploy automatizado de todos os microsserviços.

## Evidências de Cobertura de Testes

<p align="center">
    <img src="./docs/coverage/ms-payment-coverage.png"><br>Evidência de cobertura de testes para o microsserviço Pagamento</img>
</p>

<p align="center">
    <img src="./docs/coverage/ms-payment-coverage-sonar.png"><br>Evidência de cobertura de testes para o microsserviço Pagamento</img>
</p>

Os links para as ferramentas utilizadas estão disponíveis na seção **Links dos Repositórios**.

## Arquitetura

<p align="center">
    <img src="./docs/diagrams/nova-arquitetura.png"><br>Nova Arquitetura na AWS</img>
</p>

**Principais atualizações**:

O sistema agora é baseado em três microsserviços independentes, projetados para atender diferentes funcionalidades. Essa divisão facilita a manutenção e a escalabilidade, além de otimizar o desempenho. Os serviços incluem: Pedido, que gerencia o ciclo de vida dos pedidos; Pagamento, responsável pelo processamento de pagamentos; e Produto, para administração do catálogo de produtos.

Cada microsserviço possui seu próprio banco de dados, promovendo o isolamento de dados. O PostgreSQL é usado no microsserviço Pagamento para transações confiáveis, enquanto o MongoDB foi adotado pelos microsserviços Pedido e Produto devido à flexibilidade de um banco NoSQL.

Um API Gateway foi integrado para centralizar a comunicação entre os clientes e os microsserviços, oferecendo um ponto único de acesso e gerenciando questões como autenticação e roteamento.

Essa arquitetura melhora a modularidade e permite que cada serviço seja escalado ou atualizado independentemente, alinhando-se às boas práticas de desenvolvimento moderno.

## Links dos Repositórios

- [Pedido](https://github.com/6SOATGP54/tech-challenge-ms-pedido)
- [Pagamento](https://github.com/6SOATGP54/tech-challenge-ms-pagamento)
- [Produto](https://github.com/6SOATGP54/tech-challenge-ms-produto)

Cada repositório contém:
- Código-fonte do microsserviço correspondente.
- Evidências de cobertura de testes no README.

## Acesso ao Código

Este projeto está configurado como privado. Para avaliação, o usuário **soat-architecture** foi adicionado com permissão de acesso.

## Contato

Grupo nº 54 da turma 6SOAT/2024 do curso lato sensu "Especialização em Arquitetura de Software" composto por:
- Bruno Matias
- Clederson Cruz
- Lucas Santiago

91% (200/218)      
