package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.*;
import com.example.demo.model.*;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.TarefaMembroRepository;
import com.example.demo.repository.TarefaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class TarefaServiceImpl implements TarefaService {

    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;
    private final TarefaMembroRepository  tarefaMembroRepository;
    private final UsuarioRepository usuarioRepository;

    public TarefaServiceImpl(TarefaRepository tarefaRepository, CategoriaRepository categoriaRepository, TarefaMembroRepository tarefaMembroRepository, UsuarioRepository usuarioRepository) {
        this.tarefaRepository = tarefaRepository;
        this.categoriaRepository = categoriaRepository;
        this.tarefaMembroRepository = tarefaMembroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public TarefaResponseDTO criarTarefa(TarefaRequestDTO tarefaDTO, Usuario usuario) {
        Categoria categoriaDaTarefa = null;
        if (tarefaDTO.categoriaId() != null) {
            categoriaDaTarefa = this.categoriaRepository.findById(tarefaDTO.categoriaId())
                    .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria informada não existe."));
            if (!categoriaDaTarefa.getUsuario().getId().equals(usuario.getId())) {
                throw new AcessoNegadoException("Acesso negado: a categoria selecionada não pertence ao usuário.");
            }
        }

        Tarefa tarefaPai = null;
        if (tarefaDTO.tarefaPaiId() != null) {
            tarefaPai = this.tarefaRepository.findById(tarefaDTO.tarefaPaiId())
                    .orElseThrow(() -> new TarefaNaoEncontradaException("A tarefa pai informada não existe."));
            if (!tarefaPai.getUsuario().getId().equals(usuario.getId())) {
                throw new AcessoNegadoException("Acesso negado: a tarefa pai selecionada não pertence ao usuário.");
            }
        }

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo(tarefaDTO.titulo());
        novaTarefa.setDescricao(tarefaDTO.descricao());
        novaTarefa.setStatus(tarefaDTO.status());
        novaTarefa.setPrioridade(tarefaDTO.prioridade());
        novaTarefa.setDataDeVencimento(tarefaDTO.dataDeVencimento());
        novaTarefa.setUsuario(usuario);
        novaTarefa.setCategoria(categoriaDaTarefa);
        novaTarefa.setTarefaPai(tarefaPai);

        if (tarefaPai != null) {
            tarefaPai.getSubTarefas().add(novaTarefa);
        }

        Tarefa tarefaSalva = this.tarefaRepository.save(novaTarefa);
        return toTarefaResponseDTO(tarefaSalva);
    }

    @Override
    @Transactional
    public TarefaResponseDTO atualizarTarefa(UUID id, TarefaRequestDTO tarefaDTO, Usuario usuario) {
        Tarefa tarefaExistente = this.tarefaRepository.findById(id).orElseThrow(() ->
                new TarefaNaoEncontradaException("Tarefa não encontrada com o ID: " + id)
        );

        // 1. Verifica se o usuário logado é o dono da tarefa.
        boolean ehDono = tarefaExistente.getUsuario().getId().equals(usuario.getId());

        // 2. Se não for o dono, verifica se é um membro com permissão de editar.
        boolean podeEditar = tarefaExistente.getMembros().stream()
                .anyMatch(membro ->
                        membro.getMembro().getId().equals(usuario.getId()) &&
                                membro.getPermissao() == Permissao.EDITAR
                );

        // 3. Se não for nem o dono, nem um colaborador com permissão de edição, lança o erro.
        if (!ehDono && !podeEditar) {
            throw new AcessoNegadoException("Acesso negado: você não tem permissão para editar esta tarefa.");
        }

        Categoria categoriaDaTarefa = null;
        if (tarefaDTO.categoriaId() != null) {
            categoriaDaTarefa = this.categoriaRepository.findById(tarefaDTO.categoriaId())
                    .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria informada não existe."));

            if (!categoriaDaTarefa.getUsuario().getId().equals(usuario.getId())) {
                throw new AcessoNegadoException("Acesso negado: a categoria selecionada não pertence ao usuário.");
            }
        }

        tarefaExistente.setTitulo(tarefaDTO.titulo());
        tarefaExistente.setDescricao(tarefaDTO.descricao());
        tarefaExistente.setStatus(tarefaDTO.status());
        tarefaExistente.setPrioridade(tarefaDTO.prioridade());
        tarefaExistente.setDataDeVencimento(tarefaDTO.dataDeVencimento());
        tarefaExistente.setCategoria(categoriaDaTarefa);

        Tarefa tarefaSalva = this.tarefaRepository.save(tarefaExistente);

        return toTarefaResponseDTO(tarefaSalva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> listarTarefas(Usuario usuario, Prioridade prioridade, UUID categoriaId) {
        List<Tarefa> todasAsTarefas = this.tarefaRepository.findTarefasByUsuarioOrMembro(usuario);

        Stream<Tarefa> tarefasStream = todasAsTarefas.stream();

        if (categoriaId != null) {
            tarefasStream = tarefasStream.filter(tarefa -> tarefa.getCategoria() != null && tarefa.getCategoria().getId().equals(categoriaId));
        }

        if (prioridade != null) {
            tarefasStream = tarefasStream.filter(tarefa -> tarefa.getPrioridade() == prioridade);
        }

        List<Tarefa> tarefasFiltradas = tarefasStream.toList();


        List<Tarefa> tarefasPrincipais = tarefasFiltradas.stream()
                .filter(tarefa -> tarefa.getTarefaPai() == null)
                .collect(Collectors.toCollection(ArrayList::new));

        if (categoriaId == null && prioridade == null) {
            tarefasPrincipais.sort(Comparator.comparing(t -> t.getPrioridade().getValor()));
        }

        return tarefasPrincipais.stream()
                .map(this::toTarefaResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deletarTarefa(UUID id,Usuario usuario) {
        Tarefa tarefaEscolhida = this.tarefaRepository.findById(id).orElseThrow(() ->
                new TarefaNaoEncontradaException("Tarefa nao encontrada com o ID:" + id)
        );

        if (!tarefaEscolhida.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Acesso negado: esta tarefa não pertence ao usuário.");
        }

        this.tarefaRepository.deleteById(id);
    }

    private TarefaResponseDTO toTarefaResponseDTO(Tarefa tarefa) {
        if (tarefa == null) {
            return null;
        }

        CategoriaResponseDTO categoriaResponse = null;
        if (tarefa.getCategoria() != null) {
            categoriaResponse = new CategoriaResponseDTO(
                    tarefa.getCategoria().getId(),
                    tarefa.getCategoria().getNome(),
                    tarefa.getCategoria().getDescricao(),
                    tarefa.getCategoria().getUsuario().getId()
            );
        }

        Set<TarefaResponseDTO> subTarefasDTO = new HashSet<>();
        // A verificação explícita e a cópia para uma nova lista previnem o erro
        if (tarefa.getSubTarefas() != null && !tarefa.getSubTarefas().isEmpty()) {
            // Criamos uma cópia da coleção ANTES de iterar sobre ela com o stream
            Set<Tarefa> copiaSubTarefas = new HashSet<>(tarefa.getSubTarefas());
            subTarefasDTO = copiaSubTarefas.stream()
                    .map(this::toTarefaResponseDTO)
                    .collect(Collectors.toSet());
        }

        return new TarefaResponseDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                categoriaResponse,
                tarefa.getPrioridade(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getDataDeCriacao(),
                tarefa.getDataDeVencimento(),
                subTarefasDTO
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDTO gerarDashboard(Usuario usuario) {
        LocalDateTime agora = LocalDateTime.now();

        long totalTarefas = this.tarefaRepository.countTotalTarefasByUsuarioOrMembro(usuario);
        long tarefasPendentes = this.tarefaRepository.countTarefasByStatusForUsuarioOrMembro(usuario, "PENDENTE");
        long tarefasConcluidas = this.tarefaRepository.countTarefasByStatusForUsuarioOrMembro(usuario, "CONCLUIDA");
        long tarefasAtrasadas = this.tarefaRepository.countTarefasAtrasadasForUsuarioOrMembro(usuario, agora);

        Optional<Tarefa> proximaTarefaOpt = this.tarefaRepository.findProximaTarefaAVencerForUsuarioOrMembro(usuario, agora);

        DashboardDTO.TarefaResumidaDTO proximaTarefaDTO = proximaTarefaOpt
                .map(tarefa -> new DashboardDTO.TarefaResumidaDTO(
                        tarefa.getId(),
                        tarefa.getTitulo(),
                        tarefa.getDataDeVencimento()
                ))
                .orElse(null);

        return new DashboardDTO(
                totalTarefas,
                tarefasPendentes,
                tarefasConcluidas,
                tarefasAtrasadas,
                proximaTarefaDTO
        );
    }
}
