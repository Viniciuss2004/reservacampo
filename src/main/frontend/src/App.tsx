import './App.css';
import Inicio from './view/Inicio';
import CadastroUsuario from './view/CadastroUsuario';
import CadastroReserva from './view/CadastroReserva';
import CadastroQuadra from './view/CadastroQuadra';
import Gerenciar from './view/Gerenciar';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';

function App() {
  return (
    <BrowserRouter>
      <div className="landing-container">
        <header>
          <div className="logo" style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
              <path d="M8 14l2 2 4-4"></path>
            </svg>
            ReservaCampo
          </div>
          <nav style={{ display: 'flex', gap: '20px' }}>
            <Link to="/">Início</Link>
            <Link to="/campos">Campos/Quadras</Link>
            <Link to="/reservas">Reservas</Link>
            <Link to="/usuario">Usuários</Link>
            <Link to="/gerenciar">Gerenciar</Link>
          </nav>
        </header>

        <Routes>
          <Route path="/" element={<Inicio />} />
          <Route path="/usuario" element={<CadastroUsuario />} />
          <Route path="/reservas" element={<CadastroReserva />} />
          <Route path="/campos" element={<CadastroQuadra />} />
          <Route path="/gerenciar" element={<Gerenciar />} />
        </Routes>

        <footer id="contact">
          <p>&copy; {new Date().getFullYear()} ReservaCampo. Todos os direitos reservados.</p>
        </footer>
      </div>
    </BrowserRouter>
  )
}

export default App
