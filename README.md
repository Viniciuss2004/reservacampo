# Sistema de Agendamento de Campo de Futebol

Este projeto tem como objetivo desenvolver um **sistema para agendamento de campos de futebol**, permitindo que usuários possam reservar horários de forma simples e organizada.

O projeto ainda está em fase inicial de desenvolvimento e **novas funcionalidades serão adicionadas futuramente**, incluindo melhorias no sistema de reservas, gerenciamento de horários e possíveis integrações.

Este repositório será atualizado conforme o progresso do desenvolvimento.

## Configurando variaveis de ambiente no IntelliJ IDEA

1. No topo da IDE, clique no seletor de configuração (ao lado do botão ▶ Run) e selecione Edit Configurations...
2. No painel esquerdo, selecione sua aplicação Spring Boot.
3. Localize o campo Environment variables e clique no ícone 📁 à direita do campo.
4. Na janela que abrir, clique em + e adicione cada variável:
DB_URL_POSTGRES = jdbc:postgresql://localhost:5432/nome_do_banco
DB_USER_POSTGRES = postgres
DB_PASSWORD = sua_senha

⚠️ Substitua nome_do_banco, postgres e sua_senha pelos valores reais do seu ambiente.

5. Clique em OK → Apply → OK.
6. Rode a aplicação normalmente. O Spring Boot irá injetar os valores automaticamente.

## Status do Projeto

🚧 Em desenvolvimento

## Atualizações Futuras

* Sistema de cadastro de usuários
* Agendamento de horários
* Gerenciamento de campos disponíveis
* Melhorias na interface e novas funcionalidades
