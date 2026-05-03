import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Toast from '../../src/components/Toast';

describe('Componente Toast', () => {
    it('não renderiza quando o status for nulo', () => {
        const { container } = render(<Toast status={{ tipo: null, mensagem: '' }} onClose={() => {}} />);
        expect(container.firstChild).toBeNull();
    });

    it('renderiza a mensagem de sucesso corretamente', () => {
        render(<Toast status={{ tipo: 'sucesso', mensagem: 'Operação realizada com sucesso' }} onClose={() => {}} />);
        expect(screen.getByText('Operação realizada com sucesso')).toBeInTheDocument();
        expect(screen.getByText('Operação realizada com sucesso').parentElement).toHaveClass('toast-sucesso');
    });

    it('chama a função onClose quando o botão de fechar é clicado', () => {
        const onCloseMock = vi.fn();
        render(<Toast status={{ tipo: 'erro', mensagem: 'Falha na operação' }} onClose={onCloseMock} />);
        
        const closeButton = screen.getByRole('button', { name: /fechar/i });
        fireEvent.click(closeButton);
        
        expect(onCloseMock).toHaveBeenCalledTimes(1);
    });
});
