# Changelog

Todas as mudanças notáveis neste projeto serão documentadas aqui.

## [v1.0-SNAPSHOT] - 2026-03-27

### Adicionado
- Cadastro de medicamentos com nome, dose e horário
- Listagem de medicamentos cadastrados
- Marcação de medicamento como tomado no dia
- Remoção de medicamentos
- Persistência em arquivo JSON
- Testes automatizados com JUnit 5
- Análise estática com Checkstyle
- Pipeline de CI com GitHub Actions

## [v1.0.0] - 2026-03-29

### Adicionado
- ID único para cada medicamento
- Confirmação antes de remover medicamento
- Validação de formato de horário

### Alterado
- Versão promovida de SNAPSHOT para release estável

### Melhorado
- Comentários e Javadoc no código

## [v1.1.0] - 2026-05-14

### Adicionado
- Integração com BrasilAPI para verificação de feriados nacionais
- Aviso automático ao iniciar quando o dia for feriado
- Opção 5 no menu para listar todos os feriados do ano
- Testes de integração para FeriadoService
