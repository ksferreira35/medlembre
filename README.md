# 💊 MedLembre

![CI](https://github.com/ksferreira35/medlembre/actions/workflows/ci.yml/badge.svg)
![Version](https://img.shields.io/badge/version-1.2.1-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/license-MIT-green)
![Docker](https://img.shields.io/badge/docker-ksferreira35%2Fmedlembre-blue?logo=docker)

## Descrição do Problema

Idosos frequentemente precisam tomar múltiplos medicamentos em horários diferentes ao longo do dia. Esquecimentos são comuns e podem trazer sérias consequências à saúde. Cuidadores e familiares também têm dificuldade em acompanhar quais remédios foram tomados.

## Proposta da Solução

O **MedLembre** é uma aplicação Java com duas formas de uso: interface gráfica (GUI) e menu de terminal (CLI). Em ambas, é possível cadastrar medicamentos com nome, dose e horário, listar os remédios do dia, marcar quais já foram tomados e remover medicamentos quando necessário. Na entrega final, os dados são persistidos no **Supabase**, usando um banco PostgreSQL em nuvem. A aplicação também consulta a BrasilAPI para avisar quando o dia atual for feriado nacional, ajudando no planejamento de estoque de medicamentos.

Para desenvolvimento offline, o projeto ainda possui um fallback local em arquivo JSON.

## Público-alvo

- Idosos que gerenciam seus próprios medicamentos
- Cuidadores e familiares responsáveis pela rotina de saúde de idosos

## Funcionalidades

- Cadastrar medicamento (nome, dose, horário)
- Listar todos os medicamentos cadastrados
- Marcar medicamento como tomado no dia (por ID ou nome)
- Remover medicamento da lista (por ID ou nome)
- Execução por interface gráfica (GUI)
- Execução por menu de terminal (CLI)
- Persistência dos dados em banco PostgreSQL no Supabase
- Fallback local em arquivo JSON para desenvolvimento offline
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
- **Supabase** — persistência remota via API REST
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

Acesse diretamente o link abaixo para baixar a versão `v1.2.1`:

```
https://github.com/ksferreira35/medlembre/archive/refs/tags/v1.2.1.zip
```

**Via terminal (Linux/macOS com wget):**

```bash
wget https://github.com/ksferreira35/medlembre/archive/refs/tags/v1.2.1.zip
unzip v1.2.1.zip
cd medlembre-1.2.1
```

**Via terminal (Linux/macOS com curl):**

```bash
curl -L -o v1.2.1.zip https://github.com/ksferreira35/medlembre/archive/refs/tags/v1.2.1.zip
unzip v1.2.1.zip
cd medlembre-1.2.1
```

### Instalar dependências

```bash
mvn dependency:resolve
```

## Execução

Antes de executar a versão final, configure o Supabase conforme a seção abaixo.

Você pode escolher entre a interface gráfica ou o menu de terminal.

### Interface gráfica (GUI)

Para abrir a aplicação com janelas:

```bash
mvn compile exec:java -Dexec.mainClass="br.com.kaiky.medlembre.ui.MainGUI"
```

Depois de gerar os JARs:

```bash
mvn package -DskipTests
java -jar target/medlembre-gui-1.2.1-jar-with-dependencies.jar
```

### Menu de terminal (CLI)

Para usar a aplicação pelo terminal:

```bash
mvn compile exec:java -Dexec.mainClass="br.com.kaiky.medlembre.ui.MenuCLI"
```

Depois de gerar os JARs:

```bash
mvn package -DskipTests
java -jar target/medlembre-cli-1.2.1-jar-with-dependencies.jar
```

### JARs gerados

O comando `mvn package -DskipTests` gera dois arquivos executáveis:

- `target/medlembre-gui-1.2.1-jar-with-dependencies.jar`
- `target/medlembre-cli-1.2.1-jar-with-dependencies.jar`

## Configurando Supabase

Para atender à entrega final, rode a aplicação com `MEDLEMBRE_STORAGE=supabase`.

1. Crie um projeto no Supabase.
2. No SQL Editor do Supabase, execute o script [`supabase/schema.sql`](supabase/schema.sql).
   Se você já criou a tabela com uma versão anterior do script, recrie a tabela antes de rodar a versão final para garantir que o ID seja gerado automaticamente pelo banco.
3. Copie o arquivo de exemplo:

```bash
cp .env.example .env
```

4. Preencha no `.env`:

```env
MEDLEMBRE_STORAGE=supabase
SUPABASE_URL=https://seu-projeto.supabase.co
SUPABASE_ANON_KEY=sua-chave-anon-public
```

5. Execute normalmente:

```bash
mvn compile exec:java -Dexec.mainClass="br.com.kaiky.medlembre.ui.MainGUI"
```

Ou, se preferir o terminal:

```bash
mvn compile exec:java -Dexec.mainClass="br.com.kaiky.medlembre.ui.MenuCLI"
```

Para voltar ao modo local, remova `MEDLEMBRE_STORAGE=supabase` ou altere para `local`.

## Execução via Docker

A imagem está disponível publicamente no Docker Hub:
[https://hub.docker.com/r/ksferreira35/medlembre](https://hub.docker.com/r/ksferreira35/medlembre)

Tags publicadas:

- `ksferreira35/medlembre:1.2.1` — CLI
- `ksferreira35/medlembre:latest` — CLI
- `ksferreira35/medlembre:1.2.1-cli` — CLI
- `ksferreira35/medlembre:1.2.1-gui` — GUI

### Docker com CLI

A imagem padrão executa o menu de terminal e funciona em qualquer ambiente com Docker:

```bash
docker run -it ksferreira35/medlembre:1.2.1
```

Também é possível usar:

```bash
docker run -it ksferreira35/medlembre:latest
```

Para buildar localmente:

```bash
docker build --target cli -t medlembre:cli .
docker run -it medlembre:cli
```

### Docker com GUI (Linux)

A imagem GUI precisa acessar o servidor gráfico do computador. Por isso, no Linux, execute com `DISPLAY` e o socket do X11:

```bash
xhost +local:docker
docker run -it \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  ksferreira35/medlembre:1.2.1-gui
```

Para buildar localmente:

```bash
docker build --target gui -t medlembre:gui .
xhost +local:docker
docker run -it \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  medlembre:gui
```

No Windows e macOS, a GUI via Docker depende de configuração extra de servidor gráfico, como VcXsrv ou XQuartz. Para esses ambientes, recomenda-se rodar a interface gráfica diretamente com Maven:

```bash
mvn compile exec:java -Dexec.mainClass="br.com.kaiky.medlembre.ui.MainGUI"
```

### Docker com Supabase

CLI com Supabase:

```bash
docker run -it \
  -e MEDLEMBRE_STORAGE=supabase \
  -e SUPABASE_URL=https://seu-projeto.supabase.co \
  -e SUPABASE_ANON_KEY=sua-chave-anon-public \
  ksferreira35/medlembre:1.2.1
```

GUI com Supabase no Linux:

```bash
xhost +local:docker
docker run -it \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -e MEDLEMBRE_STORAGE=supabase \
  -e SUPABASE_URL=https://seu-projeto.supabase.co \
  -e SUPABASE_ANON_KEY=sua-chave-anon-public \
  ksferreira35/medlembre:1.2.1-gui
```

### Exemplo de uso

```
╔══════════════════════════════════╗
║     💊 MedLembre v1.2.1          ║
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

O teste de integração real com Supabase só executa quando `MEDLEMBRE_STORAGE=supabase`, `SUPABASE_URL` e `SUPABASE_ANON_KEY` estão configurados. Sem essas variáveis, ele é ignorado para manter o CI funcionando sem expor credenciais.

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
│   │   ├── config/
│   │   │   └── AppConfig.java
│   │   ├── model/
│   │   │   └── Medicamento.java
│   │   ├── repository/
│   │   │   ├── JsonMedicamentoRepository.java
│   │   │   ├── MedicamentoRepository.java
│   │   │   └── SupabaseMedicamentoRepository.java
│   │   ├── service/
│   │   │   ├── FeriadoService.java
│   │   │   └── MedicamentoService.java
│   │   └── ui/
│   │       ├── MainGUI.java
│   │       └── MenuCLI.java
│   └── test/java/br/com/kaiky/medlembre/
│       ├── repository/
│       │   └── SupabaseMedicamentoRepositoryIntegrationTest.java
│       └── service/
│           ├── FeriadoServiceIntegrationTest.java
│           └── MedicamentoServiceTest.java
├── supabase/
│   └── schema.sql
├── checkstyle.xml
├── CHANGELOG.md
├── Dockerfile
├── LICENSE
├── pom.xml
└── README.md
```

## Versão

**v1.2.1** — Execução documentada para interface gráfica (GUI) e menu de terminal (CLI).

## Autor

**Kaiky Ferreira**
Bootcamp II

## Integrantes

- Kaiky Ferreira
- Adicionar nome completo e matrícula dos demais integrantes

## Entrega Final

- Repositório: [https://github.com/ksferreira35/medlembre](https://github.com/ksferreira35/medlembre)
- Deploy Docker Hub: [https://hub.docker.com/r/ksferreira35/medlembre](https://hub.docker.com/r/ksferreira35/medlembre)
- Imagem CLI: `docker run -it ksferreira35/medlembre:1.2.1`
- Imagem GUI Linux: `ksferreira35/medlembre:1.2.1-gui`
- Banco de dados: Supabase PostgreSQL
- CI: GitHub Actions em `.github/workflows/ci.yml`

## Repositório

[https://github.com/ksferreira35/medlembre](https://github.com/ksferreira35/medlembre)

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
