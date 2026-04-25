export interface ValidationErrors {
  [key: string]: string;
}

export type ValidatorFunction = (value: any, formData?: any) => string | null;

export const validators = {
  required: (message = 'Campo obrigatório'): ValidatorFunction => 
    (value) => (!value || value.toString().trim() === '') ? message : null,
    
  email: (message = 'E-mail inválido'): ValidatorFunction => 
    (value) => {
      if (!value) return null;
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return !emailRegex.test(value) ? message : null;
    },
    
  minLength: (min: number, message = `Mínimo de ${min} caracteres`): ValidatorFunction => 
    (value) => {
      if (!value) return null;
      return value.toString().length < min ? message : null;
    },

  phone: (message = 'Telefone inválido'): ValidatorFunction => 
    (value) => {
      if (!value) return null;
      const digitsOnly = value.toString().replace(/\D/g, '');
      return digitsOnly.length < 10 ? message : null;
    },
    
  minValue: (min: number, message = `Valor mínimo é ${min}`): ValidatorFunction =>
    (value) => {
      if (!value && value !== 0) return null;
      return Number(value) < min ? message : null;
    },

  dateAfter: (fieldNameToCompare: string, message = 'Data inválida'): ValidatorFunction =>
    (value, formData) => {
      if (!value || !formData || !formData[fieldNameToCompare]) return null;
      const date1 = new Date(value);
      const date2 = new Date(formData[fieldNameToCompare]);
      return date1 <= date2 ? message : null;
    }
};

export const validateField = (
  value: any, 
  fieldValidators: ValidatorFunction[],
  formData?: any
): string => {
  for (const validator of fieldValidators) {
    const error = validator(value, formData);
    if (error) return error;
  }
  return '';
};

export const validateForm = (
  formData: any, 
  schema: { [key: string]: ValidatorFunction[] }
): ValidationErrors => {
  const errors: ValidationErrors = {};
  let isValid = true;

  Object.keys(schema).forEach(key => {
    const error = validateField(formData[key], schema[key], formData);
    if (error) {
      errors[key] = error;
      isValid = false;
    }
  });

  return isValid ? {} : errors;
};

