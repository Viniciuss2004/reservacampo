import React, { useState, ChangeEvent } from 'react';
import '../App.css';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import { validateForm, validators, ValidationErrors } from '../utils/validators';

const CadastroUsuario = () => {
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    telefone: ''
  });

  const [errors, setErrors] = useState<ValidationErrors>({});
  const { status, showToast, closeToast } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    
    let finalValue = value;
    
    if (name === 'telefone') {
      finalValue = value.replace(/\D/g, '');
      
      if (finalValue.length > 11) finalValue = finalValue.substring(0, 11);
      
      if (finalValue.length > 10) {
        finalValue = finalValue.replace(/^(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
      } else if (finalValue.length > 6) {
        finalValue = finalValue.replace(/^(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
      } else if (finalValue.length > 2) {
        finalValue = finalValue.replace(/^(\d{2})(\d{0,5})/, '($1) $2');
      } else if (finalValue.length > 0) {
        finalValue = finalValue.replace(/^(\d*)/, '($1');
      }
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
      nome: [validators.required('Nome é obrigatório'), validators.minLength(3, 'Nome muito curto')],
      email: [validators.required('E-mail é obrigatório'), validators.email('E-mail inválido')],
      telefone: [validators.required('Telefone é obrigatório'), validators.phone('Telefone inválido (mínimo 10 dígitos)')]
    };

    const formErrors = validateForm(formData, validationRules);
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors);
      showToast('erro', 'Verifique os erros nos campos antes de continuar.');
      return;
    }

    setIsLoading(true);

    try {
      const response = await fetch('/api/usuarios', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        showToast('sucesso', 'Usuário cadastrado com sucesso!');
        setFormData({ nome: '', email: '', telefone: '' });
      } else {
        const errorData = await response.json().catch(() => ({}));
        showToast('erro', errorData.message || 'Erro ao cadastrar usuário. Verifique os dados.');
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
            <h2>Cadastro de Usuário</h2>
            <p>Preencha os detalhes abaixo para registrar um novo cliente.</p>
          </div>

          <Toast status={status} onClose={closeToast} />

          <form onSubmit={handleSubmit} className="modern-form" noValidate>
            <div className="form-group">
              <label htmlFor="nome">Nome Completo</label>
              <input
                type="text"
                id="nome"
                name="nome"
                value={formData.nome}
                onChange={handleChange}
                className={errors.nome ? 'input-error' : ''}
                placeholder="Ex: João da Silva"
                disabled={isLoading}
              />
              {errors.nome && <span className="error-message">{errors.nome}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="email">E-mail</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className={errors.email ? 'input-error' : ''}
                placeholder="Ex: joao@email.com"
                disabled={isLoading}
              />
              {errors.email && <span className="error-message">{errors.email}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="telefone">Telefone</label>
              <input
                type="tel"
                id="telefone"
                name="telefone"
                value={formData.telefone}
                onChange={handleChange}
                className={errors.telefone ? 'input-error' : ''}
                placeholder="Ex: (11) 99999-9999"
                maxLength={15}
                disabled={isLoading}
              />
              {errors.telefone && <span className="error-message">{errors.telefone}</span>}
            </div>

            <button type="submit" className="cta-button submit-btn" disabled={isLoading}>
              {isLoading ? 'Salvando aguarde...' : 'Salvar Usuário'}
            </button>
          </form>
        </section>
      </main>
    </div>
  );
};

export default CadastroUsuario;
