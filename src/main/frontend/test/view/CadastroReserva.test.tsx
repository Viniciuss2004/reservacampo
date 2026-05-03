import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import CadastroReserva from '../../src/view/CadastroReserva';
import { BrowserRouter } from 'react-router-dom';

const renderWithRouter = (ui: React.ReactElement) => {
  return render(<BrowserRouter>{ui}</BrowserRouter>);
};

describe('Componente CadastroReserva', () => {
    beforeEach(() => {
        globalThis.fetch = vi.fn().mockImplementation((url) => {
            if (url.includes('/api/quadras')) {
                return Promise.resolve({
                    ok: true,
                    json: async () => [
                        { id: 1, nome: 'Campo 1' },
                        { id: 2, nome: 'Campo 2' }
                    ]
                });
            }
            if (url.includes('/api/usuarios')) {
                return Promise.resolve({
                    ok: true,
                    json: async () => [
                        { id: 1, nome: 'João da Silva' }
                    ]
                });
            }
            return Promise.resolve({
                ok: true,
                json: async () => ({})
            });
        });
    });

    it('renderiza os campos de agendamento de reserva', async () => {
        renderWithRouter(<CadastroReserva />);
        
        expect(screen.getByRole('heading', { level: 2, name: /Agendar Reserva/i })).toBeInTheDocument();
        expect(screen.getByLabelText(/Cliente/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Campo \/ Quadra/i)).toBeInTheDocument();
        
        await waitFor(() => {
            expect(screen.getByText(/Campo 1/i)).toBeInTheDocument();
            expect(screen.getByText(/João da Silva/i)).toBeInTheDocument();
        });
    });

    it('mostra erro de preenchimento quando tentar enviar vazio', async () => {
        renderWithRouter(<CadastroReserva />);

        const btn = screen.getByRole('button', { name: /Confirmar Reserva/i });
        fireEvent.click(btn);

        await waitFor(() => {
            expect(screen.getByText(/Selecione uma quadra\/campo/i)).toBeInTheDocument();
        });
    });
});


