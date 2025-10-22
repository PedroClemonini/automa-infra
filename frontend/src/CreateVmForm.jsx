import React, { useState } from 'react';
import axios from 'axios';

// URL base da sua API
const API_URL = 'http://localhost:8080/api/infra';

/**
 * Componente de formulário para criar uma nova VM.
 * Ele coleta os dados e envia para a API usando uma requisição POST.
 */
function CreateVmForm() {
  // 1. Estado para armazenar os dados do formulário
  const [vmInfo, setVmInfo] = useState({
    nome: '', // Exemplo de campo, assumindo que ReceivedVmInfoDto tenha 'nome'
    sistemaOperacional: '', // Exemplo de campo
    memoriaEmGB: 4, // Exemplo de campo numérico
    // Adicione outros campos conforme a estrutura real do seu ReceivedVmInfoDto
  });

  // 2. Estado para feedback do usuário (sucesso, erro, carregando)
  const [status, setStatus] = useState({
    message: '',
    type: 'idle', // 'idle', 'loading', 'success', 'error'
  });

  // 3. Função para lidar com a mudança nos inputs do formulário
  const handleChange = (e) => {
    const { name, value, type } = e.target;
    // Converte para número se for um input de tipo 'number', senão mantém como string
    setVmInfo({
      ...vmInfo,
      [name]: type === 'number' ? Number(value) : value,
    });
  };

  // 4. Função para lidar com o envio do formulário
  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus({ message: 'Criando VM...', type: 'loading' });

    try {
      // 5. Requisição POST usando axios
      const response = await axios.post(API_URL, vmInfo);

      // 6. Atualiza o status com a resposta de sucesso
      setStatus({
        message: `VM "${response.data.nome}" criada com sucesso! ID: ${response.data.id}`,
        type: 'success',
      });
      // Opcional: Limpar o formulário após o sucesso
      setVmInfo({ nome: '', sistemaOperacional: '', memoriaEmGB: 4 });
    } catch (error) {
      // 7. Atualiza o status em caso de erro
      console.error('Erro ao criar VM:', error);
      setStatus({
        message: 'Falha ao criar VM. Verifique o console para mais detalhes.',
        type: 'error',
      });
    }
  };

  // 8. Definição das classes de estilo do Tailwind para o status
  const statusClasses = {
    loading: 'bg-blue-100 text-blue-800',
    success: 'bg-green-100 text-green-800',
    error: 'bg-red-100 text-red-800',
    idle: 'hidden',
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white shadow-xl rounded-lg p-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-6 text-center">
          Provisionar Nova VM ⚙️
        </h1>

        {/* Bloco de feedback/status */}
        {status.type !== 'idle' && (
          <div
            className={`p-3 mb-4 rounded-md font-medium ${
              statusClasses[status.type]
            }`}
          >
            {status.message}
          </div>
        )}
        
        {/* Formulário */}
        <form onSubmit={handleSubmit}>
          {/* Campo Nome */}
          <div className="mb-4">
            <label
              htmlFor="nome"
              className="block text-sm font-medium text-gray-700"
            >
              Nome da VM
            </label>
            <input
              type="text"
              id="nome"
              name="nome"
              value={vmInfo.nome}
              onChange={handleChange}
              required
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              placeholder="Ex: web-server-01"
              disabled={status.type === 'loading'}
            />
          </div>

          {/* Campo Sistema Operacional */}
          <div className="mb-4">
            <label
              htmlFor="sistemaOperacional"
              className="block text-sm font-medium text-gray-700"
            >
              Sistema Operacional
            </label>
            <select
              id="sistemaOperacional"
              name="sistemaOperacional"
              value={vmInfo.sistemaOperacional}
              onChange={handleChange}
              required
              className="mt-1 block w-full px-3 py-2 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              disabled={status.type === 'loading'}
            >
              <option value="">Selecione...</option>
              <option value="Ubuntu">Ubuntu 20.04</option>
              <option value="Windows">Windows Server 2022</option>
              <option value="CentOS">CentOS 7</option>
            </select>
          </div>

          {/* Campo Memória */}
          <div className="mb-6">
            <label
              htmlFor="memoriaEmGB"
              className="block text-sm font-medium text-gray-700"
            >
              Memória (em GB)
            </label>
            <input
              type="number"
              id="memoriaEmGB"
              name="memoriaEmGB"
              value={vmInfo.memoriaEmGB}
              onChange={handleChange}
              min="1"
              max="64"
              required
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              disabled={status.type === 'loading'}
            />
          </div>

          {/* Botão de Envio */}
          <button
            type="submit"
            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            disabled={status.type === 'loading'}
          >
            {status.type === 'loading' ? (
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            ) : (
              'Criar VM'
            )}
          </button>
        </form>
      </div>
    </div>
  );
}

export default CreateVmForm;
