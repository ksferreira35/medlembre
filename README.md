# 💊 MedLembre

![CI](https://github.com/ksferreira35/medlembre/actions/workflows/ci.yml/badge.svg)
![Version](https://img.shields.io/badge/version-1.1.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/license-MIT-green)
![Docker](https://img.shields.io/badge/docker-ksferreira35%2Fmemldembre-blue?logo=docker)

## Descrição do Problema

Idosos frequentemente precisam tomar múltiplos medicamentos em horários diferentes ao longo do dia. Esquecimentos são comuns e podem trazer sérias consequências à saúde. Cuidadores e familiares também têm dificuldade em acompanhar quais remédios foram tomados.

## Proposta da Solução

O **MedLembre** é uma aplicação CLI simples que permite cadastrar medicamentos com nome, dose e horário, listar os remédios do dia, marcar quais já foram tomados e remover medicamentos quando necessário. Os dados são salvos localmente em arquivo JSON, garantindo persistência entre sessões. A aplicação também consulta a BrasilAPI para avisar quando o dia atual for feriado nacional, ajudando no planejamento de estoque de medicamentos.

## Público-alvo

- Idosos que gerenciam seus próprios medicamentos
- Cuidadores e familiares responsáveis pela rotina de saúde de idosos

## Funcionalidades

- Cadastrar medicamento (nome, dose, horário)
- Listar todos os medicamentos cadastrados
- Marcar medicamento como tomado no dia (por ID ou nome)
- Remover medicamento da lista (por ID ou nome)
- Persistência dos dados em arquivo JSON
- Aviso automático ao iniciar quando o dia for feriado nacional
- Listagem de todos os feriados nacionais do ano

## Tecnologias Utilizadas

- **Java 21**
- **Maven** — gerenciamento de dependências e build
- **Gson 2.10.1** — serialização/desserialização JSON
- **JUnit 5** — testes automatizados
- **Checkstyle** — análise estática de código
- **Javadoc** — documentação do código gerada automaticamente
- **BrasilAPI** — API pública de feriados nacionais
- **Docker** — containerização e deploy
- **GitHub Actions** — integração contínua (CI)

## Instalação

### Pré-requisitos

- Java 21
- Maven 3.8+

### Opção 1 — Clonar o repositório

```bash
git clone https://github.com/ksferreira35/medlembre.git
cd medlembre
```

### Opção 2 — Download do arquivo ZIP

**Via navegador:**

Acesse diretamente o link abaixo para baixar a versão `v1.1.0`:

```
https://github.com/ksferreira35/medlembre/archive/refs/tags/v1.1.0.zip
```

**Via terminal (Linux/macOS com wget):**

```bash
wget https://github.com/ksferreira35/medlembre/archive/refs/tags/v1.1.0.zip
unzip v1.1.0.zip
cd medlembre-1.1.0
```

**Via terminal (Linux/macOS com curl):**

```bash
curl -L -o v1.1.0.zip https://github.com/ksferreira35/medlembre/archive/refs/tags/v1.1.0.zip
unzip v1.1.0.zip
cd medlembre-1.1.0
```

### Instalar dependências

```bash
mvn dependency:resolve
```

## Execução

```bash
mvn package -DskipTests
java -jar target/medlembre-1.1.0-jar-with-dependencies.jar
```

Ou diretamente via Maven:

```bash
mvn compile exec:java -Dexec.mainClass="br.com.kaiky.medlembre.ui.MenuCLI"
```

## Execução via Docker

A imagem está disponível publicamente no Docker Hub:
[https://hub.docker.com/r/ksferreira35/medlembre](https://hub.docker.com/r/ksferreira35/medlembre)

```bash
docker run -it ksferreira35/medlembre:latest
```

### Exemplo de uso

```
╔══════════════════════════════════╗
║     💊 MedLembre v1.1.0          ║
║  Controle de Medicamentos        ║
╚══════════════════════════════════╝

⚠️  Atenção: hoje é feriado (Tiradentes).
    Verifique se seus medicamentos estão em estoque!

--- MENU ---
1 - Cadastrar medicamento
2 - Listar medicamentos
3 - Marcar como tomado hoje
4 - Remover medicamento
5 - Ver feriados nacionais do ano
0 - Sair

Escolha: 5

--- FERIADOS NACIONAIS 2026 ---
01/01 — Ano Novo
20/04 — Tiradentes
21/04 — Tiradentes
01/05 — Dia do Trabalho
07/09 — Independência do Brasil
12/10 — Nossa Senhora Aparecida
02/11 — Finados
15/11 — Proclamação da República
25/12 — Natal
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

## Gerando a Documentação (Javadoc)

```bash
mvn javadoc:javadoc
```

A documentação será gerada dentro de `target/`. Para visualizá-la, abra o arquivo `index.html` no navegador:

```bash
find target/ -name "index.html"
```

## 📁 Estrutura do Projeto

```
medlembre/
├── .github/
│   └── workflows/
│       └── ci.yml
├── src/
│   ├── main/java/br/com/kaiky/medlembre/
│   │   ├── model/
│   │   │   └── Medicamento.java
│   │   ├── service/
│   │   │   ├── FeriadoService.java
│   │   │   └── MedicamentoService.java
│   │   └── ui/
│   │       └── MenuCLI.java
│   └── test/java/br/com/kaiky/medlembre/
│       └── service/
│           ├── FeriadoServiceIntegrationTest.java
│           └── MedicamentoServiceTest.java
├── checkstyle.xml
├── CHANGELOG.md
├── Dockerfile
├── LICENSE
├── pom.xml
└── README.md
```

## Versão

**v1.1.0** — Integração com BrasilAPI para verificação de feriados nacionais.

## Autor

**Kaiky Ferreira**
Bootcamp II

## Repositório

[https://github.com/ksferreira35/medlembre](https://github.com/ksferreira35/medlembre)

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
