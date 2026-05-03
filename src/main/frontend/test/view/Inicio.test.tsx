import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Inicio from '../../src/view/Inicio';

describe('Componente Inicio', () => {
    it('renderiza o título principal do painel', () => {
        render(<Inicio />);
        expect(screen.getByRole('heading', { level: 1, name: /painel de gerenciamento/i })).toBeInTheDocument();
    });

    it('renderiza os cartões de funcionalidades (features)', () => {
        render(<Inicio />);
        expect(screen.getByRole('heading', { level: 3, name: /campos e quadras/i })).toBeInTheDocument();
        expect(screen.getByRole('heading', { level: 3, name: /controle de reservas/i })).toBeInTheDocument();
        expect(screen.getByRole('heading', { level: 3, name: /gestão de usuários/i })).toBeInTheDocument();
    });
});

