import React, { useState, ChangeEvent, useEffect } from 'react';
import '../App.css';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import { validateForm, validators, ValidationErrors } from '../utils/validators';

interface Quadra {
  id: number;
  nome: string;
  tipo: string;
  precoHora: number;
}

interface Usuario {
  id: number;
  nome: string;
  telefone: string;
}

const CadastroReserva = () => {
  const [formData, setFormData] = useState({
    quadraId: '',
    usuarioId: '',
    dataHoraInicio: '',
    dataHoraFim: ''
  });

  const [quadras, setQuadras] = useState<Quadra[]>([]);
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [errors, setErrors] = useState<ValidationErrors>({});
  const { status, showToast, closeToast } = useToast();
  const [isLoading, setIsLoading] = useState(false);


  useEffect(() => {
    const carregarDados = async () => {
      try {
        const [resQuadras, resUsuarios] = await Promise.all([
          fetch('/api/quadras'),
          fetch('/api/usuarios')
        ]);
        
        if (resQuadras.ok) setQuadras(await resQuadras.json());
        if (resUsuarios.ok) setUsuarios(await resUsuarios.json());
      } catch (error) {
        console.error('Erro ao carregar dados:', error);
      }
    };
    
    carregarDados();
  }, []);

  const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    closeToast();
    

    const validationRules = {
      quadraId: [validators.required('Selecione uma quadra/campo')],
      usuarioId: [validators.required('Selecione um cliente')],
      dataHoraInicio: [validators.required('A data/hora de início é obrigatória')],
      dataHoraFim: [
        validators.required('A data/hora de término é obrigatória'),
        validators.dateAfter('dataHoraInicio', 'A data de fim deve ser posterior à data de início')
      ]
    };

    const formErrors = validateForm(formData, validationRules);
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors);
      showToast('erro', 'Verifique os erros nos campos antes de continuar.');
      return;
    }

    setIsLoading(true);

    try {
      const response = await fetch('/api/reservas', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          quadraId: Number(formData.quadraId),
          usuarioId: Number(formData.usuarioId),
          dataHoraInicio: formData.dataHoraInicio,
          dataHoraFim: formData.dataHoraFim
        }),
      });

      if (response.ok) {
        showToast('sucesso', 'Reserva agendada com sucesso!');
        setFormData({ quadraId: '', usuarioId: '', dataHoraInicio: '', dataHoraFim: '' });
      } else {
        const errorData = await response.json().catch(() => ({}));
        showToast('erro', errorData.message || 'Erro ao agendar. Verifique os dados e horários.');
      }
    } catch (error) {
      showToast('erro', 'Erro de conexão com o servidor.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="landing-container">
      <main className="form-main">
        <section className="form-section">
          <div className="form-header">
            <h2>Agendar Reserva</h2>
            <p>Selecione um cliente e os horários para bloquear a agenda.</p>
          </div>

          <Toast status={status} onClose={closeToast} />

          <form onSubmit={handleSubmit} className="modern-form" noValidate>
            <div className="form-group">
              <label htmlFor="quadraId">Campo / Quadra</label>
              <select 
                id="quadraId" 
                name="quadraId" 
                value={formData.quadraId} 
                onChange={handleChange} 
                className={errors.quadraId ? 'input-error' : ''}
                disabled={isLoading}
              >
                <option value="">Selecione uma quadra...</option>
                {quadras.map(quadra => (
                  <option key={quadra.id} value={quadra.id}>
                    {quadra.nome} - ({quadra.tipo}) - R$ {quadra.precoHora}/h
                  </option>
                ))}
              </select>
              {errors.quadraId && <span className="error-message">{errors.quadraId}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="usuarioId">Cliente (Usuário)</label>
              <select 
                id="usuarioId" 
                name="usuarioId" 
                value={formData.usuarioId} 
                onChange={handleChange} 
                className={errors.usuarioId ? 'input-error' : ''}
                disabled={isLoading}
              >
                <option value="">Selecione um cliente...</option>
                {usuarios.map(user => (
                  <option key={user.id} value={user.id}>
                    {user.nome} - {user.telefone}
                  </option>
                ))}
              </select>
              {errors.usuarioId && <span className="error-message">{errors.usuarioId}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="dataHoraInicio">Horário de Início</label>
              <input 
                type="datetime-local" 
                id="dataHoraInicio" 
                name="dataHoraInicio" 
                value={formData.dataHoraInicio} 
                onChange={handleChange} 
                className={errors.dataHoraInicio ? 'input-error' : ''}
                disabled={isLoading}
              />
              {errors.dataHoraInicio && <span className="error-message">{errors.dataHoraInicio}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="dataHoraFim">Horário de Fim</label>
              <input 
                type="datetime-local" 
                id="dataHoraFim" 
                name="dataHoraFim" 
                value={formData.dataHoraFim} 
                onChange={handleChange} 
                className={errors.dataHoraFim ? 'input-error' : ''}
                disabled={isLoading}
              />
              {errors.dataHoraFim && <span className="error-message">{errors.dataHoraFim}</span>}
            </div>

            <button type="submit" className="cta-button submit-btn" disabled={isLoading}>
              {isLoading ? 'Agendando aguarde...' : 'Confirmar Reserva'}
            </button>
          </form>
        </section>
      </main>
    </div>
  );
};

export default CadastroReserva;
