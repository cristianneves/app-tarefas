package com.example.demo.service;

import com.example.demo.dto.AnexoDownloadDTO;
import com.example.demo.dto.AnexoResponseDTO;
import com.example.demo.exception.AcessoNegadoException;
import com.example.demo.exception.AnexoNaoEncontradoException;
import com.example.demo.exception.TarefaNaoEncontradaException;
import com.example.demo.model.Anexo;
import com.example.demo.model.Tarefa;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AnexoRepository;
import com.example.demo.repository.TarefaRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class AnexoServiceImpl implements AnexoService {

    private final AnexoRepository anexoRepository;
    private final TarefaRepository tarefaRepository;
    private final FileStorageService fileStorageService;

    public AnexoServiceImpl(AnexoRepository anexoRepository, TarefaRepository tarefaRepository, FileStorageService fileStorageService) {
        this.anexoRepository = anexoRepository;
        this.tarefaRepository = tarefaRepository;
        this.fileStorageService = fileStorageService;
    }

    public AnexoResponseDTO anexarArquivo(MultipartFile arquivo, UUID tarefaId, Usuario usuario){

        Tarefa tarefa = this.tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa com o ID fornecido não foi encontrada."));

        if (!tarefa.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Acesso negado: a tarefa não pertence ao usuário.");
        }

        String nomeArquivoUnico = this.fileStorageService.salvarArquivo(arquivo);

        Anexo novoAnexo = new Anexo();
        novoAnexo.setNomeArquivo(arquivo.getOriginalFilename());
        novoAnexo.setTipoArquivo(arquivo.getContentType());
        novoAnexo.setCaminhoArquivo(nomeArquivoUnico);
        novoAnexo.setTarefa(tarefa);
        novoAnexo.setUsuario(usuario);

        Anexo anexoSalvo = this.anexoRepository.save(novoAnexo);

        return new AnexoResponseDTO(
                anexoSalvo.getId(),
                anexoSalvo.getNomeArquivo()
        );
    }

    @Override
    public AnexoDownloadDTO baixarArquivo(UUID idDoAnexo, Usuario usuario) {
        Anexo anexo = this.anexoRepository.findById(idDoAnexo)
                .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado."));

        if (!anexo.getTarefa().getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Acesso negado: este anexo não pertence a uma tarefa sua.");
        }

        Resource recurso = this.fileStorageService.carregarArquivoComoRecurso(anexo.getCaminhoArquivo());

        return new AnexoDownloadDTO(anexo.getNomeArquivo(), recurso);
    }
}
