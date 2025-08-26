package br.com.francisca.mac;

import br.com.francisca.mac.exception.FixedCellModificationException;
import br.com.francisca.mac.exception.InvalidPositionException;
import br.com.francisca.mac.exception.InvalidValueException;
import br.com.francisca.mac.model.Board;
import br.com.francisca.mac.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static br.com.francisca.mac.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Board board;
    private static final int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        final Map<String, String> positions = Stream.of(args)
                .map(s -> s.split(";", 2))
                .filter(parts -> parts.length == 2)
                .collect(toMap(parts -> parts[0], parts -> parts[1]));

        while (true) {
            System.out.println("Selecione uma das opções a seguir: ");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            int option = readIntLine(1, 8);

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu: ");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("O jogo já foi iniciado!");
            return;
        }

        try {
            List<List<Space>> spaces = new ArrayList<>();
            for (int i = 0; i < BOARD_LIMIT; i++) {
                List<Space> column = new ArrayList<>();
                for (int j = 0; j < BOARD_LIMIT; j++) {
                    String key = "%d,%d".formatted(i, j);
                    String positionConfig = positions.get(key);
                    if (positionConfig == null) {
                        throw new IllegalArgumentException("Configuração ausente para posição: " + key);
                    }
                    String[] parts = positionConfig.split(",", 2);
                    if (parts.length < 2) {
                        throw new IllegalArgumentException("Formato inválido na posição: " + key);
                    }
                    int expected = Integer.parseInt(parts[0].trim());
                    boolean fixed = Boolean.parseBoolean(parts[1].trim());
                    column.add(new Space(expected, fixed));
                }
                spaces.add(column);
            }

            board = new Board(spaces);
            System.out.println("O jogo está pronto para começar!");
        } catch (NumberFormatException e) {
            System.out.println("Erro: número inválido na configuração do tabuleiro: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro na configuração do tabuleiro: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado ao iniciar o jogo: " + e.getMessage());
        }
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado!");
            return;
        }

        System.out.println("Informe a coluna em que o número será inserido: ");
        int col = readIntLine(0, 8);
        System.out.println("Informe a linha em que o número será inserido: ");
        int row = readIntLine(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [%d,%d]\n", col, row);
        int value = readIntLine(1, 9);

        try {
            board.changeValue(col, row, value);
            System.out.println("Valor inserido com sucesso.");
        } catch (InvalidValueException e) {
            System.out.println("Valor inválido: " + e.getMessage());
        } catch (FixedCellModificationException e) {
            System.out.printf("A posição [%d,%d] tem um valor fixo%n", col, row);
        } catch (InvalidPositionException e) {
            System.out.println("Posição inválida: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao inserir número: " + e.getMessage());
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado! ");
            return;
        }

        System.out.println("Informe a coluna em que o número será removido: ");
        int col = readIntLine(0, 8);
        System.out.println("Informe a linha em que o número será removido: ");
        int row = readIntLine(0, 8);

        try {
            board.clearValue(col, row);
            System.out.println("Valor removido com sucesso.");
        } catch (FixedCellModificationException e) {
            System.out.printf("A posição [%d,%d] tem um valor fixo%n", col, row);
        } catch (InvalidPositionException e) {
            System.out.println("Posição inválida: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao remover número: " + e.getMessage());
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado! ");
            return;
        }

        Object[] args = new Object[81];
        int argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col : board.getSpaces()) {
                Integer actual = col.get(i).getActual();
                args[argPos++] = " " + (actual == null ? " " : actual);
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma");
        System.out.printf(BOARD_TEMPLATE + "%n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado! ");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status %s%n", board.getStatus().getLabel());
        if (board.hasErrors()) {
            System.out.println("O jogo contém erros! ");
        } else {
            System.out.println("O jogo não contém erros! ");
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado!");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso? (sim/não)");
        String confirm = scanner.nextLine().trim();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não") && !confirm.equalsIgnoreCase("nao")) {
            System.out.println("Informe 'sim' ou 'não'");
            confirm = scanner.nextLine().trim();
        }

        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
            System.out.println("Jogo limpo com sucesso.");
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado!");
            return;
        }

        if (board.gameIsFinished()) {
            System.out.println("Parabéns você concluiu o jogo! \\0/\\0/");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contém erros, verifique seu board e ajuste-o");
        } else {
            System.out.println("Você ainda precisa preencher algum espaço");
        }
    }

    private static int readIntLine(final int min, final int max) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("Informe um número entre %d e %d%n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Informe um número válido.");
            }
        }
    }
}