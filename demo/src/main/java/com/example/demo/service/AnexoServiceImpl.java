package com.example.demo.service;

import com.example.demo.dto.AnexoDownloadDTO;
import com.example.demo.dto.AnexoResponseDTO;
import com.example.demo.exception.AcessoNegadoException;
import com.example.demo.exception.AnexoNaoEncontradoException;
import com.example.demo.exception.TarefaNaoEncontradaException;
import com.example.demo.model.Anexo;
import com.example.demo.model.Permissao;
import com.example.demo.model.Tarefa;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AnexoRepository;
import com.example.demo.repository.TarefaRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    @Transactional
    public AnexoResponseDTO anexarArquivo(MultipartFile arquivo, UUID tarefaId, Usuario usuarioLogado) {
        Tarefa tarefa = this.tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa com o ID fornecido não foi encontrada."));

        boolean ehDono = tarefa.getUsuario().getId().equals(usuarioLogado.getId());
        boolean podeEditar = tarefa.getMembros().stream()
                .anyMatch(membro ->
                        membro.getMembro().getId().equals(usuarioLogado.getId()) &&
                                membro.getPermissao() == Permissao.EDITAR
                );

        // Se não for o dono E também não for um colaborador com permissão de edição, nega o acesso.
        if (!ehDono && !podeEditar) {
            throw new AcessoNegadoException("Acesso negado: você não tem permissão para anexar arquivos a esta tarefa.");
        }

        String nomeArquivoUnico = this.fileStorageService.salvarArquivo(arquivo);

        Anexo novoAnexo = new Anexo();
        novoAnexo.setNomeArquivo(arquivo.getOriginalFilename());
        novoAnexo.setTipoArquivo(arquivo.getContentType());
        novoAnexo.setCaminhoArquivo(nomeArquivoUnico);
        novoAnexo.setTarefa(tarefa);
        novoAnexo.setUsuario(usuarioLogado);

        Anexo anexoSalvo = this.anexoRepository.save(novoAnexo);

        return new AnexoResponseDTO(anexoSalvo.getId(), anexoSalvo.getNomeArquivo());
    }

    @Override
    @Transactional(readOnly = true)
    public AnexoDownloadDTO baixarArquivo(UUID idDoAnexo, Usuario usuarioLogado) {
        Anexo anexo = this.anexoRepository.findById(idDoAnexo)
                .orElseThrow(() -> new AnexoNaoEncontradoException("Anexo não encontrado."));

        Tarefa tarefaDoAnexo = anexo.getTarefa();

        boolean ehDono = tarefaDoAnexo.getUsuario().getId().equals(usuarioLogado.getId());
        boolean ehMembro = tarefaDoAnexo.getMembros().stream()
                .anyMatch(membro -> membro.getMembro().getId().equals(usuarioLogado.getId()));

        // Se não for o dono E também não for um membro, nega o acesso.
        if (!ehDono && !ehMembro) {
            throw new AcessoNegadoException("Acesso negado: você não tem permissão para ver este anexo.");
        }

        Resource recurso = this.fileStorageService.carregarArquivoComoRecurso(anexo.getCaminhoArquivo());

        return new AnexoDownloadDTO(anexo.getNomeArquivo(), recurso);
    }
}
