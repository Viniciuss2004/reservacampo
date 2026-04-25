import React, { useState, ChangeEvent } from 'react';
import '../App.css';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import { validateForm, validators, ValidationErrors } from '../utils/validators';

const CadastroQuadra = () => {
  const [formData, setFormData] = useState({
    nome: '',
    tipo: '',
    localizacao: '',
    precoHora: ''
  });

  const [errors, setErrors] = useState<ValidationErrors>({});
  const { status, showToast, closeToast } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    let finalValue = value;

    if (name === 'precoHora') {
      finalValue = value.replace(/[^0-9.]/g, '');
    }

    setFormData(prev => ({
      ...prev,
      [name]: finalValue
    }));

    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    closeToast();
    
    const validationRules = {
      nome: [validators.required('Nome do campo é obrigatório')],
      tipo: [validators.required('Selecione uma modalidade')],
      localizacao: [validators.required('Localização referencial é obrigatória')],
      precoHora: [
        validators.required('Informe o valor da hora'), 
        validators.minValue(1, 'O valor da hora não pode ser zero ou negativo')
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
      const response = await fetch('/api/quadras', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...formData,
          precoHora: Number(formData.precoHora)
        }),
      });

      if (response.ok) {
        showToast('sucesso', 'Campo/Quadra cadastrado com sucesso!');
        setFormData({ nome: '', tipo: '', localizacao: '', precoHora: '' });
      } else {
        const errorData = await response.json().catch(() => ({}));
        showToast('erro', errorData.message || 'Erro ao cadastrar a quadra. Verifique os dados.');
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
            <h2>Cadastro de Quadra</h2>
            <p>Registre um novo campo ou quadra esportiva no sistema.</p>
          </div>

          <Toast status={status} onClose={closeToast} />

          <form onSubmit={handleSubmit} className="modern-form" noValidate>
            <div className="form-group">
              <label htmlFor="nome">Nome do Campo/Quadra</label>
              <input
                type="text"
                id="nome"
                name="nome"
                value={formData.nome}
                onChange={handleChange}
                className={errors.nome ? 'input-error' : ''}
                placeholder="Ex: Arena Society Principal"
                disabled={isLoading}
              />
              {errors.nome && <span className="error-message">{errors.nome}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="tipo">Modalidade / Tipo</label>
              <select 
                id="tipo" 
                name="tipo" 
                value={formData.tipo} 
                onChange={handleChange} 
                className={errors.tipo ? 'input-error' : ''}
                disabled={isLoading}
              >
                <option value="">Selecione um tipo...</option>
                <option value="FUTEBOL">Futebol</option>
                <option value="FUTSAL">Futsal</option>
                <option value="BASQUETE">Basquete</option>
                <option value="VOLEI">Vôlei</option>
                <option value="TENIS">Tênis</option>
              </select>
              {errors.tipo && <span className="error-message">{errors.tipo}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="localizacao">Localização (Referência do Setor)</label>
              <input
                type="text"
                id="localizacao"
                name="localizacao"
                value={formData.localizacao}
                onChange={handleChange}
                className={errors.localizacao ? 'input-error' : ''}
                placeholder="Ex: Quadra Coberta 01"
                disabled={isLoading}
              />
              {errors.localizacao && <span className="error-message">{errors.localizacao}</span>}
            </div>
            
            <div className="form-group">
              <label htmlFor="precoHora">Valor da Hora Alugada (R$)</label>
              <input
                type="number"
                step="0.01"
                min="0"
                id="precoHora"
                name="precoHora"
                value={formData.precoHora}
                onChange={handleChange}
                className={errors.precoHora ? 'input-error' : ''}
                placeholder="Ex: 80.00"
                disabled={isLoading}
              />
              {errors.precoHora && <span className="error-message">{errors.precoHora}</span>}
            </div>

            <button type="submit" className="cta-button submit-btn" disabled={isLoading}>
              {isLoading ? 'Cadastrando aguarde...' : 'Salvar Campo/Quadra'}
            </button>
          </form>
        </section>
      </main>
    </div>
  );
};

export default CadastroQuadra;
