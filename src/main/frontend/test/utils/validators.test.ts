import { describe, it, expect } from 'vitest';
import { validators, validateField, validateForm } from '../../src/utils/validators';

describe('validadores (validators)', () => {
    describe('obrigatório (required)', () => {
        it('retorna erro se estiver vazio', () => {
            const validator = validators.required();
            expect(validator('')).toBe('Campo obrigatório');
            expect(validator('   ')).toBe('Campo obrigatório');
            expect(validator(null)).toBe('Campo obrigatório');
        });
        it('retorna null se não estiver vazio', () => {
            const validator = validators.required();
            expect(validator('texto')).toBeNull();
            expect(validator(123)).toBeNull();
        });
    });

    describe('e-mail', () => {
        it('retorna erro se o e-mail for inválido', () => {
            const validator = validators.email();
            expect(validator('email.com')).toBe('E-mail inválido');
            expect(validator('email@')).toBe('E-mail inválido');
        });
        it('retorna null se o e-mail for válido', () => {
            const validator = validators.email();
            expect(validator('teste@teste.com')).toBeNull();
        });
    });

    describe('validateField', () => {
        it('retorna o primeiro erro encontrado', () => {
            const error = validateField('', [validators.required(), validators.email()]);
            expect(error).toBe('Campo obrigatório');
        });
        it('retorna string vazia se for válido', () => {
            const error = validateField('teste@teste.com', [validators.required(), validators.email()]);
            expect(error).toBe('');
        });
    });

    describe('validateForm', () => {
        it('retorna erros para campos inválidos', () => {
            const schema = {
                name: [validators.required()],
                email: [validators.required(), validators.email()]
            };
            const data = { name: '', email: 'invalid' };
            const errors = validateForm(data, schema);
            
            expect(errors.name).toBe('Campo obrigatório');
            expect(errors.email).toBe('E-mail inválido');
        });
        it('retorna objeto vazio se o formulário for válido', () => {
             const schema = {
                name: [validators.required()],
            };
            const data = { name: 'João' };
            const errors = validateForm(data, schema);
            expect(Object.keys(errors).length).toBe(0);
        });
    });
});
