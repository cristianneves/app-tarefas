package com.example.demo.service;

import com.example.demo.dto.ConvidarMembroRequestDTO;
import com.example.demo.dto.ConviteResponseDTO;
import com.example.demo.exception.*;
import com.example.demo.model.*;
import com.example.demo.repository.ConviteRepository;
import com.example.demo.repository.TarefaMembroRepository;
import com.example.demo.repository.TarefaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConviteServiceImpl implements ConviteService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConviteRepository conviteRepository;
    private final TarefaMembroRepository tarefaMembroRepository;

    public ConviteServiceImpl(TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository, ConviteRepository conviteRepository,TarefaMembroRepository tarefaMembroRepository) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
        this.conviteRepository = conviteRepository;
        this.tarefaMembroRepository = tarefaMembroRepository;
    }

    @Override
    @Transactional
    public void convidarMembro(UUID tarefaId, ConvidarMembroRequestDTO conviteDTO, Usuario donoDaTarefa) {
        Tarefa tarefa = this.tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa não encontrada."));

        if (!tarefa.getUsuario().getId().equals(donoDaTarefa.getId())) {
            throw new AcessoNegadoException("Apenas o dono da tarefa pode convidar membros.");
        }

        Usuario destinatario = this.usuarioRepository.findByEmail(conviteDTO.email())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com o e-mail fornecido não foi encontrado."));

        if (donoDaTarefa.getId().equals(destinatario.getId())) {
            throw new RegraDeNegocioException("Você não pode convidar a si mesmo para uma de suas tarefas.");
        }

        // Verificando tanto os convites pendentes quanto os membros ativos
        boolean jaTemConvitePendente = conviteRepository.existsByTarefaAndDestinatarioAndStatus(tarefa, destinatario, StatusConvite.PENDENTE);
        if (jaTemConvitePendente) {
            throw new RegraDeNegocioException("Já existe um convite pendente para este usuário nesta tarefa.");
        }

        boolean jaEhMembro = tarefa.getMembros().stream()
                .anyMatch(membro -> membro.getMembro().getId().equals(destinatario.getId()));
        if (jaEhMembro) {
            throw new RegraDeNegocioException("Este usuário já é um membro desta tarefa.");
        }

        Convite novoConvite = new Convite();
        novoConvite.setTarefa(tarefa);
        novoConvite.setRemetente(donoDaTarefa);
        novoConvite.setDestinatario(destinatario);
        novoConvite.setPermissao(conviteDTO.permissao());

        this.conviteRepository.save(novoConvite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConviteResponseDTO> listarConvitesPendentes(Usuario usuarioLogado) {
        List<Convite> convites = this.conviteRepository.findByDestinatarioAndStatus(
                usuarioLogado,
                StatusConvite.PENDENTE
        );

        return convites.stream().map(convite -> {
            var tarefaInfo = new ConviteResponseDTO.TarefaInfoDTO(
                    convite.getTarefa().getId(),
                    convite.getTarefa().getTitulo()
            );

            var remetenteInfo = new ConviteResponseDTO.UsuarioInfoDTO(
                    convite.getRemetente().getId(),
                    convite.getRemetente().getLogin()
            );

            return new ConviteResponseDTO(
                    convite.getId(),
                    tarefaInfo,
                    remetenteInfo,
                    convite.getPermissao(),
                    convite.getStatus(),
                    convite.getDataEnvio()
            );
        }).toList();
    }

    @Override
    @Transactional
    public void aceitarConvite(UUID conviteId, Usuario usuarioLogado) {
        Convite convite = this.conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ConviteNaoEncontradoException("Convite não encontrado."));

        if (!convite.getDestinatario().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não tem permissão para aceitar este convite.");
        }

        if (convite.getStatus() != StatusConvite.PENDENTE) {
            throw new RegraDeNegocioException("Este convite não está mais pendente e não pode ser aceito.");
        }

        convite.setStatus(StatusConvite.ACEITO);

        TarefaMembro novoMembro = new TarefaMembro();
        novoMembro.setTarefa(convite.getTarefa());
        novoMembro.setMembro(usuarioLogado);
        novoMembro.setPermissao(convite.getPermissao());

        this.conviteRepository.save(convite);
        this.tarefaMembroRepository.save(novoMembro);
    }

    @Override
    @Transactional
    public void recusarConvite(UUID conviteId, Usuario usuarioLogado) {
        Convite convite = this.conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ConviteNaoEncontradoException("Convite não encontrado."));

        if (!convite.getDestinatario().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não tem permissão para recusar este convite.");
        }

        if (convite.getStatus() != StatusConvite.PENDENTE) {
            throw new RegraDeNegocioException("Este convite não está mais pendente.");
        }

        convite.setStatus(StatusConvite.RECUSADO);

        this.conviteRepository.save(convite);
    }
}
