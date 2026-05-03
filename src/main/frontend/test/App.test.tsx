import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import App from '../src/App';

describe('Componente App', () => {
    it('renderiza sem apresentar erros', () => {
        render(<App />);
        expect(screen.getByRole('navigation')).toBeInTheDocument();
    });
});
