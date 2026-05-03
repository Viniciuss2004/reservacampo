import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import CadastroQuadra from '../../src/view/CadastroQuadra';
import { BrowserRouter } from 'react-router-dom';

const renderWithRouter = (ui: React.ReactElement) => {
  return render(<BrowserRouter>{ui}</BrowserRouter>);
};

describe('Componente CadastroQuadra', () => {
    beforeEach(() => {
        globalThis.fetch = vi.fn();
    });

    it('renderiza o formulário de cadastro de quadra', () => {
        renderWithRouter(<CadastroQuadra />);
        expect(screen.getByRole('heading', { name: /Cadastro de Quadra/i })).toBeInTheDocument();
        expect(screen.getByLabelText(/Nome do Campo\/Quadra/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Modalidade \/ Tipo/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Valor da Hora Alugada/i)).toBeInTheDocument();
    });

    it('exibe erros de validação ao tentar salvar formulário vazio', async () => {
        renderWithRouter(<CadastroQuadra />);
        
        const btnSubmit = screen.getByRole('button', { name: /Salvar Campo\/Quadra/i });
        fireEvent.click(btnSubmit);
        
        await waitFor(() => {
            expect(screen.getByText('Nome do campo é obrigatório')).toBeInTheDocument();
            expect(screen.getByText('Selecione uma modalidade')).toBeInTheDocument();
            expect(screen.getByText('Localização referencial é obrigatória')).toBeInTheDocument();
            expect(screen.getByText('Informe o valor da hora')).toBeInTheDocument();
        });
    });

    it('chama a api para salvar e exibe toast de sucesso se form for válido', async () => {
        (globalThis.fetch as any).mockResolvedValueOnce({
            ok: true,
            json: async () => ({})
        });

        renderWithRouter(<CadastroQuadra />);
        
        fireEvent.change(screen.getByLabelText(/Nome do Campo\/Quadra/i), { target: { value: 'Quadra 1' } });
        fireEvent.change(screen.getByLabelText(/Modalidade \/ Tipo/i), { target: { value: 'FUTSAL' } });
        fireEvent.change(screen.getByLabelText(/Localização/i), { target: { value: 'Setor A' } });
        fireEvent.change(screen.getByLabelText(/Valor da Hora Alugada/i), { target: { value: '100' } });

        const btnSubmit = screen.getByRole('button', { name: /Salvar Campo\/Quadra/i });
        fireEvent.click(btnSubmit);

        await waitFor(() => {
            expect(globalThis.fetch).toHaveBeenCalledWith('/api/quadras', expect.objectContaining({
                method: 'POST',
            }));
            expect(screen.getByText('Campo/Quadra cadastrado com sucesso!')).toBeInTheDocument();
        });
    });
});




