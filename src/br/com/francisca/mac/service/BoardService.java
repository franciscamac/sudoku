package br.com.francisca.mac.service;

import br.com.francisca.mac.exception.FixedCellModificationException;
import br.com.francisca.mac.exception.InvalidBoardConfigurationException;
import br.com.francisca.mac.exception.InvalidPositionException;
import br.com.francisca.mac.exception.InvalidValueException;
import br.com.francisca.mac.model.Board;
import br.com.francisca.mac.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BoardService {

    private static final int BOARD_LIMIT = 9;
    private final Board board;

    public BoardService(final Map<String, String> gameConfig) {
        this.board = new Board(initBoard(gameConfig));
    }

    public List<List<Space>> getSpaces() {
        return board.getSpaces();
    }

    public void reset() {
        board.reset();
    }

    public boolean hasErrors() {
        return board.hasErrors();
    }

    public br.com.francisca.mac.model.GameStatusEnum getStatus() {
        return board.getStatus();
    }

    public boolean gameIsFinished() {
        return board.gameIsFinished();
    }

//     Tratados em Board

    public void changeValue(int col, int row, int value) {
        board.changeValue(col, row, value);
    }

    public void clearValue(int col, int row) {
        board.clearValue(col, row);
    }


//     Versões "try" para a UI: retornam boolean, evitando try/catch na camada superior.

    public boolean tryChangeValue(int col, int row, int value) {
        try {
            changeValue(col, row, value);
            return true;
        } catch (InvalidPositionException | InvalidValueException | FixedCellModificationException e) {
            // opcional: log.debug(e.getMessage());
            return false;
        } catch (RuntimeException e) {
            // evita vazamento de exceções inesperadas para a UI
            return false;
        }
    }

    public boolean tryClearValue(int col, int row) {
        try {
            clearValue(col, row);
            return true;
        } catch (InvalidPositionException | FixedCellModificationException e) {
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }


//     Inicializa o tabuleiro a partir do map de configuração.
//     Valida presença de todas as posições e parsing de valores.
//     Lança InvalidBoardConfigurationException em caso de problema.

    private List<List<Space>> initBoard(final Map<String, String> gameConfig) {
        if (Objects.isNull(gameConfig)) {
            throw new InvalidBoardConfigurationException("gameConfig não pode ser nulo");
        }

        List<List<Space>> spaces = new ArrayList<>(BOARD_LIMIT);

        for (int i = 0; i < BOARD_LIMIT; i++) {
            List<Space> column = new ArrayList<>(BOARD_LIMIT);
            for (int j = 0; j < BOARD_LIMIT; j++) {
                String key = "%d,%d".formatted(i, j);
                String positionConfig = gameConfig.get(key);
                if (positionConfig == null) {
                    throw new InvalidBoardConfigurationException("Configuração ausente para posição: " + key);
                }
                String[] parts = positionConfig.split(",");
                if (parts.length < 2) {
                    throw new InvalidBoardConfigurationException("Formato inválido para posição: " + key);
                }
                try {
                    int expected = Integer.parseInt(parts[0].trim());
                    boolean fixed = Boolean.parseBoolean(parts[1].trim());
                    column.add(new Space(expected, fixed));
                } catch (NumberFormatException e) {
                    throw new InvalidBoardConfigurationException("Valor numérico inválido na posição: " + key, e);
                }
            }
            spaces.add(column);
        }

        return spaces;
    }
}