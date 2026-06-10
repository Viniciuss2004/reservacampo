# language: pt
Funcionalidade: Gestão do Reserva Campo
  Como usuário do sistema
  Quero poder me cadastrar, registrar quadras e realizar reservas
  Para automatizar e organizar o gerenciamento do complexo esportivo

  # Funcionalidade: Cadastro de Usuário
  Cenário: Cadastro de usuário com sucesso
    Dado que o usuário está na página de cadastro de usuário
    Quando ele preenche nome "Maria Silva", email "maria@email.com" e telefone "11999999999"
    E clica no botão de salvar
    Então ele deve ver a mensagem "Usuário cadastrado com sucesso"
    E deve ser redirecionado para a lista de gerenciamento

  Cenário: Tentativa de cadastro de usuário com campos vazios
    Dado que o usuário está na página de cadastro de usuário
    Quando ele deixa campos obrigatórios em branco
    E clica no botão de salvar
    Então ele deve ver uma mensagem de validação de formulário
    E deve permanecer na página de cadastro de usuário

  # Funcionalidade: Cadastro de Quadra
  Cenário: Cadastro de quadra válida
    Dado que o administrador está na página de cadastro de quadra
    Quando ele preenche o nome da quadra "Quadra Central", tipo "Futebol" e preço "120"
    E clica no botão de salvar
    Então a quadra deve ser salva corretamente
    E ele deve ver a mensagem "Quadra cadastrada com sucesso!"

  Cenário: Cadastro de quadra com preço inválido
    Dado que o administrador está na página de cadastro de quadra
    Quando ele preenche o nome da quadra "Quadra de Tênis", tipo "Tênis" e preço "-50"
    E clica no botão de salvar
    Então ele deve ver uma mensagem indicando erro no valor

  # Funcionalidade: Cadastro de Reserva
  Cenário: Cadastro de reserva de quadra
    Dado que o usuário está na página de cadastro de reserva
    Quando ele seleciona o cliente "1", seleciona a quadra "1", data "2026-10-10" e horário "14:00"
    E clica no botão de salvar
    Então a reserva deve ser confirmada
    E ele deve ver a mensagem "Reserva salva com sucesso"

  Cenário: Conflito de horário de reservas
    Dado que o usuário está na página de cadastro de reserva
    E já existe uma reserva para a quadra "1" na data "2026-10-10" às "14:00"
    Quando ele tenta agendar a mesma quadra para "2026-10-10" às "14:00"
    E clica no botão de salvar
    Então o sistema deve impedir a reserva
    E exibir a mensagem "Conflito de horários"

