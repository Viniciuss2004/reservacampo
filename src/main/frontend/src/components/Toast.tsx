import React, { useEffect } from 'react';
import '../App.css';

interface ToastProps {
  status: { tipo: 'sucesso' | 'erro' | null; mensagem: string };
  onClose: () => void;
}

const Toast: React.FC<ToastProps> = ({ status, onClose }) => {
  useEffect(() => {
    if (status.tipo) {
      const timer = setTimeout(() => {
        onClose();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [status, onClose]);

  if (!status.tipo) return null;

  return (
    <div className={`toast-container toast-${status.tipo}`}>
      <span>{status.mensagem}</span>
      <button className="toast-close" onClick={onClose} aria-label="Fechar">&times;</button>
    </div>
  );
};

export default Toast;

