package br.com.francisca.mac.model;

import br.com.francisca.mac.exception.FixedCellModificationException;
import br.com.francisca.mac.exception.InvalidPositionException;
import br.com.francisca.mac.exception.InvalidValueException;

import java.util.Collection;
import java.util.List;

import static br.com.francisca.mac.model.GameStatusEnum.COMPLETE;
import static br.com.francisca.mac.model.GameStatusEnum.INCOMPLETE;
import static br.com.francisca.mac.model.GameStatusEnum.NON_STARTED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        if (spaces == null || spaces.isEmpty()) {
            throw new IllegalArgumentException("spaces não pode ser nulo ou vazio");
        }
        // valida retangularidade básica
        final int rows = spaces.get(0).size();
        for (List<Space> col : spaces) {
            if (col == null || col.size() != rows) {
                throw new IllegalArgumentException("A estrutura spaces deve ser retangular e não conter nulls");
            }
        }
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus(){
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))){
            return NON_STARTED;
        }

        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    public boolean hasErrors(){
        if(getStatus() == NON_STARTED){
            return false;
        }

        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    private Space getSpace(final int col, final int row) {
        if (col < 0 || col >= spaces.size()) {
            //      InvalidPositionException (índice inválido)
            throw new InvalidPositionException("Coluna inválida: " + col);
        }
        List<Space> column = spaces.get(col);
        if (row < 0 || row >= column.size()) {
            //      InvalidPositionException (índice inválido)
            throw new InvalidPositionException("Linha inválida: " + row);
        }
        return column.get(row);
    }


//      altera o valor da célula. Lança exceções em caso de erro:
    public void changeValue(final int col, final int row, final int value){
        if (value < 1 || value > 9) {
            //      InvalidValueException (valor fora do intervalo esperado)
            throw new InvalidValueException("Valor inválido: " + value + ". Deve ser entre 1 e 9.");
        }
        var space = getSpace(col, row);
        if (space.isFixed()){
            //      FixedCellModificationException (célula fixa)
            throw new FixedCellModificationException("Não é possível alterar uma célula fixa na posição: [" + col + "," + row + "]");
        }

        space.setActual(value);
    }


//      Limpa o valor da célula. Lança exceções em caso de posição inválida ou célula fixa.

    public void clearValue(final int col, final int row){
        var space = getSpace(col, row);
        if (space.isFixed()){
            throw new FixedCellModificationException("Não é possível limpar uma célula fixa na posição: [" + col + "," + row + "]");
        }

        space.clearSpace();
    }

    public void reset(){
        spaces.forEach(c -> c.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished(){
        return !hasErrors() && getStatus().equals(COMPLETE);
    }

}