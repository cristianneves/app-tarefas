package com.example.demo.service;

import com.example.demo.dto.CategoriaRequestDTO;
import com.example.demo.dto.CategoriaResponseDTO;
import com.example.demo.exception.AcessoNegadoException;
import com.example.demo.exception.CategoriaNaoEncontradaException;
import com.example.demo.model.Categoria;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public CategoriaResponseDTO criarCategoria(CategoriaRequestDTO categoriaDTO, Usuario usuario){

        Categoria novaCategoria = new Categoria();

        novaCategoria.setNome(categoriaDTO.nome());
        novaCategoria.setDescricao(categoriaDTO.descricao());
        novaCategoria.setUsuario(usuario);

        Categoria categoriaSalva = categoriaRepository.save(novaCategoria);

        return new CategoriaResponseDTO(
                categoriaSalva.getId(),
                categoriaSalva.getNome(),
                categoriaSalva.getDescricao(),
                categoriaSalva.getUsuario().getId()
        );
    }

    @Override
    @Transactional
    public CategoriaResponseDTO atualizarCategoria(UUID ID, CategoriaRequestDTO categoriaDTO, Usuario usuario) {
        Categoria categoriaEscolhida = this.categoriaRepository.findById(ID).orElseThrow(() ->
                new CategoriaNaoEncontradaException("Categoria nao encontrada.")
        );

        if (!categoriaEscolhida.getUsuario().getId().equals(usuario.getId())){
           throw new AcessoNegadoException("Acesso Negado: Essa categoria nao pertence ao usuario");
        }

        categoriaEscolhida.setNome(categoriaDTO.nome());
        categoriaEscolhida.setDescricao(categoriaDTO.descricao());

        Categoria categoriaAtualizada = categoriaRepository.save(categoriaEscolhida);

        return new CategoriaResponseDTO(
                categoriaAtualizada.getId(),
                categoriaAtualizada.getNome(),
                categoriaAtualizada.getDescricao(),
                categoriaAtualizada.getUsuario().getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias(Usuario usuario) {
        List<Categoria> categorias = this.categoriaRepository.findByUsuario(usuario);

        return categorias.stream().map(categoria ->
                new CategoriaResponseDTO(
                        categoria.getId(),
                        categoria.getNome(),
                        categoria.getDescricao(),
                        categoria.getUsuario().getId()
                )
        ).toList();
    }

    @Override
    @Transactional
    public void deletarCategoria(UUID id, Usuario usuario) {
        Categoria categoriaEscolhida = this.categoriaRepository.findById(id).orElseThrow(() ->
                new CategoriaNaoEncontradaException("Categoria nao encontrada.")
        );

        if (!categoriaEscolhida.getUsuario().getId().equals(usuario.getId())){
            throw new AcessoNegadoException("Acesso Negado: Essa categoria nao pertence ao usuario");
        }
        categoriaRepository.deleteById(categoriaEscolhida.getId());
    }
}
