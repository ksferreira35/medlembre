# 💊 MedLembre

![CI](https://github.com/ksferreira35/medlembre/actions/workflows/ci.yml/badge.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/license-MIT-green)

## Descrição do Problema

Idosos frequentemente precisam tomar múltiplos medicamentos em horários diferentes ao longo do dia. Esquecimentos são comuns e podem trazer sérias consequências à saúde. Cuidadores e familiares também têm dificuldade em acompanhar quais remédios foram tomados.

## Proposta da Solução

O **MedLembre** é uma aplicação CLI simples que permite cadastrar medicamentos com nome, dose e horário, listar os remédios do dia, marcar quais já foram tomados e remover medicamentos quando necessário. Os dados são salvos localmente em arquivo JSON, garantindo persistência entre sessões.

## Público-alvo

- Idosos que gerenciam seus próprios medicamentos
- Cuidadores e familiares responsáveis pela rotina de saúde de idosos

## Suas Funcionalidades

- Cadastrar medicamento (nome, dose, horário)
- Listar todos os medicamentos cadastrados
- Marcar medicamento como tomado no dia
- Remover medicamento da lista
- Persistência dos dados em arquivo JSON
  
## Tecnologias Utilizadas

- **Java 21**
- **Maven** — gerenciamento de dependências e build
- **Gson 2.10.1** — serialização/desserialização JSON
- **JUnit 5** — testes automatizados
- **Checkstyle** — análise estática de código
- **Javadoc** — documentação gerada automaticamente
- **GitHub Actions** — integração contínua (CI)

## Instalação

### Pré-requisitos

- Java 21
- Maven 3.8+

### Clonar o repositório

```bash
git clone https://github.com/ksferreira35/medlembre.git
cd medlembre
```

### Instalar dependências

```bash
mvn dependency:resolve
```

## Execução

```bash
mvn package -DskipTests
java -jar target/medlembre-1.0.0-jar-with-dependencies.jar
```

Ou diretamente via Maven:

```bash
mvn compile exec:java -Dexec.mainClass="br.com.kaiky.medlembre.ui.MenuCLI"
```

### Exemplo de uso

```
╔══════════════════════════════════╗
║       💊 MedLembre v1.0.0        ║
║      Controle de Medicamentos    ║
╚══════════════════════════════════╝

--- MENU ---
1 - Cadastrar medicamento
2 - Listar medicamentos
3 - Marcar como tomado hoje
4 - Remover medicamento
0 - Sair
Escolha: 1

Nome do medicamento: Losartana
Dose (ex: 1 comprimido): 1 comprimido
Horário (ex: 08:00): 08:00
Medicamento cadastrado com sucesso!
```

## Rodando os Testes

```bash
mvn test
```

Os relatórios são gerados em `target/surefire-reports/`.

## Rodando o Lint (Checkstyle)

```bash
mvn checkstyle:check
```

## 📁 Estrutura do Projeto

```
medlembre/
├── .github/
│   └── workflows/
│       └── ci.yml              # Pipeline GitHub Actions
├── src/
│   ├── main/java/br/com/kaiky/medlembre/
│   │   ├── model/
│   │   │   └── Medicamento.java
│   │   ├── service/
│   │   │   └── MedicamentoService.java
│   │   └── ui/
│   │       └── MenuCLI.java
│   └── test/java/com/medlembre/
│       └── service/
│           └── MedicamentoServiceTest.java
├── checkstyle.xml
├── pom.xml
└── README.md
```

## Versão

**v1.0.0** — Versão inicial estável com funcionalidades básicas de controle de medicamentos.

## Autor

**Kaiky Ferreira**  
Bootcamp II

## Repositório

[https://github.com/ksferreira35/medlembre](https://github.com/ksferreira35/medlembre)

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
