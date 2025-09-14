package com.example.demo.service;

import com.example.demo.dto.CategoriaResponseDTO;
import com.example.demo.dto.TarefaRequestDTO;
import com.example.demo.dto.TarefaResponseDTO;
import com.example.demo.exception.AcessoNegadoException;
import com.example.demo.exception.CategoriaNaoEncontradaException;
import com.example.demo.exception.TarefaNaoEncontradaException;
import com.example.demo.model.Categoria;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Tarefa;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class TarefaServiceImpl implements TarefaService {

    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;

    public TarefaServiceImpl(TarefaRepository tarefaRepository, CategoriaRepository categoriaRepository) {
        this.tarefaRepository = tarefaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public TarefaResponseDTO criarTarefa(TarefaRequestDTO tarefaDTO, Usuario usuario) {

        Categoria categoriaDaTarefa = null;

        if (tarefaDTO.categoriaId() != null) {
            categoriaDaTarefa = this.categoriaRepository.findById(tarefaDTO.categoriaId())
                    .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria informada nao existe"));

            if (!categoriaDaTarefa.getUsuario().getId().equals(usuario.getId())) {
                throw new AcessoNegadoException("Acesso negado: a categoria selecionada não pertence ao usuário.");
            }
        }

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo(tarefaDTO.titulo());
        novaTarefa.setDescricao(tarefaDTO.descricao());
        novaTarefa.setStatus(tarefaDTO.status());
        novaTarefa.setPrioridade(tarefaDTO.prioridade());
        novaTarefa.setUsuario(usuario);
        novaTarefa.setDataDeVencimento(tarefaDTO.dataDeVencimento());
        novaTarefa.setCategoria(categoriaDaTarefa);

        Tarefa tarefaSalva = this.tarefaRepository.save(novaTarefa);

        CategoriaResponseDTO categoriaResponse = null;

        if (tarefaSalva.getCategoria() != null) {
            categoriaResponse = new CategoriaResponseDTO(
                    tarefaSalva.getCategoria().getId(),
                    tarefaSalva.getCategoria().getNome(),
                    tarefaSalva.getCategoria().getDescricao(),
                    tarefaSalva.getCategoria().getUsuario().getId()
            );
        }

        return new TarefaResponseDTO(
                tarefaSalva.getId(),
                tarefaSalva.getTitulo(),
                categoriaResponse,
                tarefaSalva.getPrioridade(),
                tarefaSalva.getDescricao(),
                tarefaSalva.getStatus(),
                tarefaSalva.getDataDeCriacao(),
                tarefaSalva.getDataDeVencimento()
        );
    }

    @Override
    public TarefaResponseDTO atualizarTarefa(UUID id, TarefaRequestDTO tarefaDTO, Usuario usuario) {
        Tarefa tarefaExistente = this.tarefaRepository.findById(id).orElseThrow(() ->
                new TarefaNaoEncontradaException("Tarefa não encontrada com o ID: " + id)
        );

        if (!tarefaExistente.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Acesso negado: esta tarefa não pertence ao usuário.");
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

        CategoriaResponseDTO categoriaResponse = null;
        if (tarefaSalva.getCategoria() != null) {
            categoriaResponse = new CategoriaResponseDTO(
                    tarefaSalva.getCategoria().getId(),
                    tarefaSalva.getCategoria().getNome(),
                    tarefaSalva.getCategoria().getDescricao(),
                    tarefaSalva.getCategoria().getUsuario().getId()
            );
        }

        return new TarefaResponseDTO(
                tarefaSalva.getId(),
                tarefaSalva.getTitulo(),
                categoriaResponse,
                tarefaSalva.getPrioridade(),
                tarefaSalva.getDescricao(),
                tarefaSalva.getStatus(),
                tarefaSalva.getDataDeCriacao(),
                tarefaSalva.getDataDeVencimento()
        );
    }

    @Override
    public List<TarefaResponseDTO> listarTarefas(Usuario usuario, Prioridade prioridade, UUID categoriaId) {

        List<Tarefa> tarefas;

        if (categoriaId != null) {
            Categoria categoria = this.categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria informada não existe."));

            tarefas = this.tarefaRepository.findByUsuarioAndCategoria(usuario, categoria);

        } else if (prioridade != null) {
            tarefas = this.tarefaRepository.findByUsuarioAndPrioridade(usuario, prioridade);

        } else {
            tarefas = this.tarefaRepository.findByUsuario(usuario);
            tarefas.sort(Comparator.comparing(t -> t.getPrioridade().getValor()));
        }

        return tarefas.stream().map(tarefa -> {

            CategoriaResponseDTO categoriaResponse = null;
            if (tarefa.getCategoria() != null) {
                categoriaResponse = new CategoriaResponseDTO(
                        tarefa.getCategoria().getId(),
                        tarefa.getCategoria().getNome(),
                        tarefa.getCategoria().getDescricao(),
                        tarefa.getCategoria().getUsuario().getId()
                );
            }

            return new TarefaResponseDTO(
                    tarefa.getId(),
                    tarefa.getTitulo(),
                    categoriaResponse,
                    tarefa.getPrioridade(),
                    tarefa.getDescricao(),
                    tarefa.getStatus(),
                    tarefa.getDataDeCriacao(),
                    tarefa.getDataDeVencimento()
            );
        }).toList();
    }

    @Override
    public void deletarTarefa(UUID id,Usuario usuario) {
        Tarefa tarefaEscolhida = this.tarefaRepository.findById(id).orElseThrow(() ->
                new TarefaNaoEncontradaException("Tarefa nao encontrada com o ID:" + id)
        );

        if (!tarefaEscolhida.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Acesso negado: esta tarefa não pertence ao usuário.");
        }

        this.tarefaRepository.deleteById(id);
    }
}
