import '../App.css';

const Inicio = () => {
  return (
    <div className="landing-container">
      <main>
        <section className="hero-section" id="home">
          <h1>Painel de Gerenciamento</h1>
          <p>
            Administre suas quadras, campos e controle todos os agendamentos em um só lugar de forma rápida e prática.
          </p>
        </section>

        <section className="features" id="features">
          <div className="feature-card">
            <h3>⚽ Campos e Quadras</h3>
            <p>
              Cadastre e gerencie a disponibilidade e os valores de seus campos society, quadras de futsal, vôlei ou basquete.
            </p>
          </div>
          <div className="feature-card">
            <h3>📅 Controle de Reservas</h3>
            <p>
              Acompanhe todos os horários agendados, confirmações e cancelamentos de maneira centralizada.
            </p>
          </div>
          <div className="feature-card">
            <h3>👥 Gestão de Usuários</h3>
            <p>
              Futuramente, unifique aqui o cadastro de todos os clientes que utilizam as suas instalações.
            </p>
          </div>
        </section>
      </main>
    </div>
  );
};

export default Inicio;
