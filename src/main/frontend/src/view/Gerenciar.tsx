import { useState, useEffect } from 'react';
import { Calendar, Ban, Trash2, MapPin, Save, X, Edit2, Users } from 'lucide-react';
import '../App.css';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';

interface Usuario {
  id: number;
  nome: string;
  email: string;
  telefone: string;
}

interface Quadra {
  id: number;
  nome: string;
  tipo: string;
  localizacao: string;
  precoHora: number;
}

interface Reserva {
  id: number;
  quadraNome: string;
  usuarioNome: string;
  dataHoraInicio: string;
  dataHoraFim: string;
  valorTotal: number;
  status: string;
}

const ConfirmModal = ({ isOpen, message, onConfirm, onCancel }: { isOpen: boolean, message: string, onConfirm: () => void, onCancel: () => void }) => {
  if (!isOpen) return null;
  return (
    <div style={{
      position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, 
      backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', 
      alignItems: 'center', justifyContent: 'center', zIndex: 1000
    }}>
      <div style={{
        backgroundColor: '#fff', padding: '24px', borderRadius: '8px', 
        boxShadow: '0 4px 6px rgba(0,0,0,0.1)', maxWidth: '400px', width: '90%',
        textAlign: 'center'
      }}>
        <h3 style={{ marginTop: 0, color: '#1e293b' }}>Confirmação</h3>
        <p style={{ color: '#475569', margin: '16px 0 24px 0' }}>{message}</p>
        <div style={{ display: 'flex', justifyContent: 'center', gap: '12px' }}>
          <button onClick={onCancel} style={{
            padding: '8px 16px', borderRadius: '6px', border: '1px solid #cbd5e1', 
            backgroundColor: '#f8fafc', color: '#475569', cursor: 'pointer', fontWeight: 600, transition: 'all 0.2s'
          }}>Cancelar</button>
          <button onClick={onConfirm} style={{
            padding: '8px 16px', borderRadius: '6px', border: 'none', 
            backgroundColor: '#dc2626', color: '#fff', cursor: 'pointer', fontWeight: 600, transition: 'all 0.2s'
          }}>Confirmar</button>
        </div>
      </div>
    </div>
  );
};

const Gerenciar = () => {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [quadras, setQuadras] = useState<Quadra[]>([]);
  const [reservas, setReservas] = useState<Reserva[]>([]);


  const [pageUsuarios, setPageUsuarios] = useState(1);
  const [pageQuadras, setPageQuadras] = useState(1);
  const [pageReservas, setPageReservas] = useState(1);
  
  const ITEMS_PER_PAGE = 5;

  const [editUser, setEditUser] = useState<Usuario | null>(null);
  const [editQuadra, setEditQuadra] = useState<Quadra | null>(null);
  const { status, showToast, closeToast } = useToast();

  const [confirmModal, setConfirmModal] = useState<{
    isOpen: boolean;
    message: string;
    onConfirm: () => void;
  }>({ isOpen: false, message: '', onConfirm: () => {} });

  const fecharModal = () => setConfirmModal(p => ({ ...p, isOpen: false }));


  const fetchData = async () => {
    try {
      const [resUsers, resQuadras, resReservas] = await Promise.all([
        fetch('/api/usuarios'),
        fetch('/api/quadras'),
        fetch('/api/reservas')
      ]);
      
      if (resUsers.ok) {
        const response = await resUsers.json();
        const data: Usuario[] = response.content || response;
        setUsuarios(data.sort((a, b) => a.nome.localeCompare(b.nome)));
      }
      if (resQuadras.ok) {
        const response = await resQuadras.json();
        const data: Quadra[] = response.content || response;
        setQuadras(data.sort((a, b) => a.nome.localeCompare(b.nome)));
      }
      if (resReservas.ok) {
        const response = await resReservas.json();
        const data: Reserva[] = response.content || response;
        setReservas(data.sort((a, b) => a.quadraNome.localeCompare(b.quadraNome)));
      }
    } catch (error) {
      console.error("Erro ao carregar dados do painel administrador", error);
    }
  };
  useEffect(() => { fetchData(); }, []);


  const formataData = (isoData: string) => {
    if (!isoData) return '-';

    return new Date(isoData).toLocaleString('pt-BR', {
      day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  };


  const paginar = <T,>(itens: T[], paginaAtual: number) => {
    const start = (paginaAtual - 1) * ITEMS_PER_PAGE;
    return itens.slice(start, start + ITEMS_PER_PAGE);
  };

  const calcTotalPages = (totalItems: number) => Math.ceil(totalItems / ITEMS_PER_PAGE) || 1;


  const removerUsuario = (id: number) => {
    setConfirmModal({
      isOpen: true,
      message: "Deseja realmente remover este usuário?",
      onConfirm: async () => {
        const res = await fetch(`/api/usuarios/${id}`, { method: 'DELETE' });
        if(res.ok) {
          setUsuarios(p => p.filter(u => u.id !== id));
          fetchData();
        }
        fecharModal();
      }
    });
  }

  const removerQuadra = (id: number) => {
    setConfirmModal({
      isOpen: true,
      message: "Deseja realmente remover esta quadra?",
      onConfirm: async () => {
        const res = await fetch(`/api/quadras/${id}`, { method: 'DELETE' });
        if(res.ok) {
          setQuadras(p => p.filter(q => q.id !== id));
          fetchData();
        }
        fecharModal();
      }
    });
  }

  const removerReserva = (id: number) => {
    setConfirmModal({
      isOpen: true,
      message: "Deseja realmente remover a reserva do sistema?",
      onConfirm: async () => {
        const res = await fetch(`/api/reservas/${id}`, { method: 'DELETE' });
        if(res.ok) {
          setReservas(p => p.filter(r => r.id !== id));
          fetchData();
        }
        fecharModal();
      }
    });
  }


  const atualizarUsuario = async () => {
    if(!editUser) return;
    const res = await fetch(`/api/usuarios/${editUser.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(editUser)
    });
    if(res.ok) {
      setUsuarios(p => p.map(u => u.id === editUser.id ? editUser : u));
      setEditUser(null);
      showToast('sucesso', 'Usuário atualizado com sucesso!');
      fetchData();
    } else showToast('erro', 'Falha ao atualizar dados. Verifique os campos.');
  }

  const atualizarQuadra = async () => {
    if(!editQuadra) return;
    const res = await fetch(`/api/quadras/${editQuadra.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(editQuadra)
    });
    if(res.ok) {
      setQuadras(p => p.map(q => q.id === editQuadra.id ? editQuadra : q));
      setEditQuadra(null);
      showToast('sucesso', 'Quadra atualizada com sucesso!');
      fetchData();
    } else showToast('erro', 'Falha ao atualizar a quadra.');
  }

  const cancelarReservaBackend = (id: number) => {
    setConfirmModal({
      isOpen: true,
      message: "Deseja cancelar (inativar) essa reserva?",
      onConfirm: async () => {
        const res = await fetch(`/api/reservas/${id}/cancelar`, { method: 'PATCH' });
        if(res.ok) {
          const dtoNovo = await res.json();
          setReservas(p => p.map(r => r.id === id ? { ...r, status: dtoNovo.status } : r));
          fetchData();
        }
        fecharModal();
      }
    });
  }

  return (
    <div className="landing-container">
      <Toast status={status} onClose={closeToast} />
      <ConfirmModal 
        isOpen={confirmModal.isOpen} 
        message={confirmModal.message} 
        onConfirm={confirmModal.onConfirm} 
        onCancel={fecharModal} 
      />
      
      <main className="admin-panel">
        
        <div style={{ textAlign: 'center', marginBottom: '30px' }}>
          <h2>Painel de Gerenciamento Geral</h2>
          <p style={{ color: '#64748b' }}>Visão completa de todos os registros do sistema</p>
        </div>

        {/* ================= TABELA DE RESERVAS ================= */}
        <section className="admin-card">
          <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Calendar size={24} /> Reservas Agendadas
          </h3>
          <div className="table-container">
            <table className="modern-table">
              <thead>
                <tr>
                  <th>Campo/Quadra</th>
                  <th>Cliente</th>
                  <th>Início</th>
                  <th>Fim</th>
                  <th>Valor Total</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {reservas.length === 0 ? (
                  <tr><td colSpan={7} style={{ textAlign: 'center' }}>Nenhuma reserva encontrada.</td></tr>
                ) : (
                  paginar(reservas, pageReservas).map(res => (
                    <tr key={res.id}>
                      <td>{res.quadraNome}</td>
                      <td>{res.usuarioNome}</td>
                      <td>{formataData(res.dataHoraInicio)}</td>
                      <td>{formataData(res.dataHoraFim)}</td>
                      <td>R$ {res.valorTotal?.toFixed(2)}</td>
                      <td><span className={`status-badge status-${res.status}`}>{res.status}</span></td>
                      <td>
                        <button className="action-btn" title="Cancelar Reserva (Mudar para Vermelho)" onClick={() => cancelarReservaBackend(res.id)}>
                          <Ban size={18} color="#dc2626" />
                        </button>
                        <button className="action-btn" title="Excluir Para Sempre" onClick={() => removerReserva(res.id)}>
                          <Trash2 size={18} color="#475569" />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
          <div className="pagination">
            <button 
              onClick={() => setPageReservas(p => Math.max(1, p - 1))} 
              disabled={pageReservas === 1}
            >
              &laquo; Anterior
            </button>
            <span>Página {pageReservas} de {calcTotalPages(reservas.length)}</span>
            <button 
              onClick={() => setPageReservas(p => Math.min(calcTotalPages(reservas.length), p + 1))} 
              disabled={pageReservas === calcTotalPages(reservas.length)}
            >
              Próxima &raquo;
            </button>
          </div>
        </section>

        {/* ================= TABELA DE QUADRAS ================= */}
        <section className="admin-card">
          <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <MapPin size={24} /> Campos e Quadras
          </h3>
          <div className="table-container">
            <table className="modern-table">
              <thead>
                <tr>
                  <th>Nome do Local</th>
                  <th>Modalidade</th>
                  <th>Referências</th>
                  <th>Preço P/ Hora</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {quadras.length === 0 ? (
                  <tr><td colSpan={5} style={{ textAlign: 'center' }}>Nenhum campo encontrado.</td></tr>
                ) : (
                  paginar(quadras, pageQuadras).map(quad => {
                    const isEdit = editQuadra?.id === quad.id;
                    return (
                    <tr key={quad.id} style={{ backgroundColor: isEdit ? '#f3f4f6' : 'transparent' }}>
                      <td>{isEdit ? <input className="inline-input" value={editQuadra.nome} onChange={e => setEditQuadra({...editQuadra, nome: e.target.value})} /> : quad.nome}</td>
                      <td>{isEdit ? <select className="inline-input" value={editQuadra.tipo} onChange={e => setEditQuadra({...editQuadra, tipo: e.target.value})}>
                          <option value="FUTEBOL">FUTEBOL</option>
                          <option value="FUTSAL">FUTSAL</option>
                          <option value="VOLEI">VOLEI</option>
                          <option value="BASQUETE">BASQUETE</option>
                          <option value="TENIS">TENIS</option>
                        </select> : quad.tipo}</td>
                      <td>{isEdit ? <input className="inline-input" value={editQuadra.localizacao} onChange={e => setEditQuadra({...editQuadra, localizacao: e.target.value})} /> : quad.localizacao}</td>
                      <td>{isEdit ? <input type="number" className="inline-input" value={editQuadra.precoHora} onChange={e => setEditQuadra({...editQuadra, precoHora: Number(e.target.value)})} /> : <>R$ {quad.precoHora?.toFixed(2)}</>}</td>
                      <td>
                         {isEdit ? (
                           <div style={{ gap: '4px' }}>
                             <button className="action-btn" title="Salvar" onClick={atualizarQuadra}>
                               <Save size={18} color="#16a34a" />
                             </button>
                             <button className="action-btn" title="Cancelar Edição" onClick={() => setEditQuadra(null)}>
                               <X size={18} color="#dc2626" />
                             </button>
                           </div>
                         ) : (
                           <div style={{ gap: '4px' }}>
                             <button className="action-btn" title="Editar Quadra" onClick={() => setEditQuadra(quad)}>
                               <Edit2 size={18} color="#2563eb" />
                             </button>
                             <button className="action-btn" title="Excluir Quadra" onClick={() => removerQuadra(quad.id)}>
                               <Trash2 size={18} color="#475569" />
                             </button>
                           </div>
                         )}
                      </td>
                    </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
          <div className="pagination">
            <button 
              onClick={() => setPageQuadras(p => Math.max(1, p - 1))} 
              disabled={pageQuadras === 1}
            >
              &laquo; Anterior
            </button>
            <span>Página {pageQuadras} de {calcTotalPages(quadras.length)}</span>
            <button 
              onClick={() => setPageQuadras(p => Math.min(calcTotalPages(quadras.length), p + 1))} 
              disabled={pageQuadras === calcTotalPages(quadras.length)}
            >
              Próxima &raquo;
            </button>
          </div>
        </section>

        {/* ================= TABELA DE USUÁRIOS ================= */}
        <section className="admin-card">
          <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Users size={24} /> Clientes / Usuários
          </h3>
          <div className="table-container">
            <table className="modern-table">
              <thead>
                <tr>
                  <th>Nome Completo</th>
                  <th>Email</th>
                  <th>Telefone / Contato</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {usuarios.length === 0 ? (
                  <tr><td colSpan={4} style={{ textAlign: 'center' }}>Nenhum cliente cadastrado.</td></tr>
                ) : (
                  paginar(usuarios, pageUsuarios).map(user => {
                    const isEdit = editUser?.id === user.id;
                    return (
                    <tr key={user.id} style={{ backgroundColor: isEdit ? '#f3f4f6' : 'transparent' }}>
                      <td>{isEdit ? <input className="inline-input" value={editUser.nome} onChange={e => setEditUser({...editUser, nome: e.target.value})} /> : user.nome}</td>
                      <td>{isEdit ? <input className="inline-input" type="email" value={editUser.email} onChange={e => setEditUser({...editUser, email: e.target.value})} /> : user.email}</td>
                      <td>{isEdit ? <input className="inline-input" value={editUser.telefone} onChange={e => setEditUser({...editUser, telefone: e.target.value})} /> : user.telefone}</td>
                      <td>
                         {isEdit ? (
                           <div style={{ gap: '4px' }}>
                             <button className="action-btn" title="Salvar" onClick={atualizarUsuario}>
                               <Save size={18} color="#16a34a" />
                             </button>
                             <button className="action-btn" title="Cancelar Edição" onClick={() => setEditUser(null)}>
                               <X size={18} color="#dc2626" />
                             </button>
                           </div>
                         ) : (
                           <div style={{ gap: '4px' }}>
                             <button className="action-btn" title="Editar Usuário" onClick={() => setEditUser(user)}>
                               <Edit2 size={18} color="#2563eb" />
                             </button>
                             <button className="action-btn" title="Excluir Usuário" onClick={() => removerUsuario(user.id)}>
                               <Trash2 size={18} color="#475569" />
                             </button>
                           </div>
                         )}
                      </td>
                    </tr>
                   );
                  })
                )}
              </tbody>
            </table>
          </div>
          <div className="pagination">
            <button 
              onClick={() => setPageUsuarios(p => Math.max(1, p - 1))} 
              disabled={pageUsuarios === 1}
            >
              &laquo; Anterior
            </button>
            <span>Página {pageUsuarios} de {calcTotalPages(usuarios.length)}</span>
            <button 
              onClick={() => setPageUsuarios(p => Math.min(calcTotalPages(usuarios.length), p + 1))} 
              disabled={pageUsuarios === calcTotalPages(usuarios.length)}
            >
              Próxima &raquo;
            </button>
          </div>
        </section>

      </main>
    </div>
  );
};

export default Gerenciar;
