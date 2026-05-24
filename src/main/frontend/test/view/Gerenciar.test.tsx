import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import Gerenciar from '../../src/view/Gerenciar';
import { BrowserRouter } from 'react-router-dom';

const renderWithRouter = (ui: React.ReactElement) => {
  return render(<BrowserRouter>{ui}</BrowserRouter>);
};

describe('Componente Gerenciar', () => {
    beforeEach(() => {
        globalThis.fetch = vi.fn().mockImplementation((url) => {
             if (url.includes('/api/reservas')) {
                return Promise.resolve({
                    ok: true,
                    json: async () => ({
                        content: [
                            { 
                                id: 1, 
                                quadraNome: 'Arena Leste',
                                usuarioNome: 'João', 
                                dataHoraInicio: '2026-05-10T10:00:00',
                                dataHoraFim: '2026-05-10T12:00:00',
                                valorTotal: 200,
                                status: 'AGENDADA'
                            }
                        ],
                        totalPages: 1
                    })
                });
             }
             if (url.includes('/api/quadras')) {
                 return Promise.resolve({ 
                     ok: true, 
                     json: async () => ({ 
                         content: [],
                         totalPages: 1 
                     }) 
                 });
             }
             if (url.includes('/api/usuarios')) {
                 return Promise.resolve({ 
                     ok: true, 
                     json: async () => ({ 
                         content: [],
                         totalPages: 1 
                     }) 
                 });
             }
             return Promise.resolve({ ok: true, json: async () => ({ content: [], totalPages: 1 }) });
        });
    });

    afterEach(() => {
        vi.clearAllMocks();
    });

    it('renderiza o título da tela de gerenciamento', async () => {
        renderWithRouter(<Gerenciar />);
        
        await waitFor(() => {
            expect(screen.getByRole('heading', { level: 2, name: /Painel de Gerenciamento Geral/i })).toBeInTheDocument();
        });
    });

    it('carrega e renderiza a lista de reservas do backend na tabela', async () => {
        renderWithRouter(<Gerenciar />);
        
        await waitFor(() => {
            expect(screen.getByText(/AGENDADA/i)).toBeInTheDocument();
        }, { timeout: 3000 });
    });
});

