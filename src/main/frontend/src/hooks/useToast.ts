import { useState } from 'react';

export interface FormStatus {
  tipo: 'sucesso' | 'erro' | null;
  mensagem: string;
}

export function useToast() {
  const [status, setStatus] = useState<FormStatus>({ tipo: null, mensagem: '' });
  
  const showToast = (tipo: 'sucesso' | 'erro', mensagem: string) => {
    setStatus({ tipo, mensagem });
  };

  const closeToast = () => {
    setStatus({ tipo: null, mensagem: '' });
  };

  return { status, showToast, closeToast };
}

