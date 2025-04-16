package com.taskboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;
import java.util.List;

@SpringBootApplication
public class TaskboardApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TaskboardApplication.class, args);
        BoardRepository boardRepository = context.getBean(BoardRepository.class);
        TaskColumnRepository columnRepository = context.getBean(TaskColumnRepository.class);
        CardRepository cardRepository = context.getBean(CardRepository.class);

        Scanner scanner = new Scanner(System.in);
        Board selectedBoard = null;

        while (true) {
            try {
                System.out.println("\nMenu Principal:");
                System.out.println("1 - Criar novo board");
                System.out.println("2 - Selecionar board");
                System.out.println("3 - Excluir board");
                System.out.println("4 - Sair");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                if (opcao == 1) {
                    System.out.print("Nome do board: ");
                    String name = scanner.nextLine();
                    if (name == null || name.trim().isEmpty()) {
                        System.out.println("Erro: O nome do board não pode ser vazio!");
                        continue;
                    }

                    System.out.print("Responsável: ");
                    String responsible = scanner.nextLine();
                    if (responsible == null || responsible.trim().isEmpty()) {
                        responsible = "Desconhecido";
                    }

                    Board board = new Board(name, responsible);
                    boardRepository.save(board);
                    System.out.println("Board criado com sucesso!");
                } else if (opcao == 2) {
                    System.out.println("Boards disponíveis:");
                    boardRepository.findAll().forEach(board -> 
                        System.out.println("ID: " + board.getId() + ", Nome: " + board.getName()));
                    System.out.print("Digite o ID do board para selecionar: ");
                    Long boardId = scanner.nextLong();
                    scanner.nextLine(); // Limpar o buffer
                    selectedBoard = boardRepository.findById(boardId).orElse(null);
                    if (selectedBoard != null) {
                        System.out.println("Board selecionado: " + selectedBoard.getName());
                        manageBoard(selectedBoard, columnRepository, cardRepository, scanner);
                    } else {
                        System.out.println("Erro: Board com ID " + boardId + " não encontrado!");
                    }
                } else if (opcao == 3) {
                    System.out.print("ID do board para excluir: ");
                    Long id = scanner.nextLong();
                    if (!boardRepository.existsById(id)) {
                        System.out.println("Erro: Board com ID " + id + " não encontrado!");
                        continue;
                    }
                    boardRepository.deleteById(id);
                    System.out.println("Board excluído!");
                } else if (opcao == 4) {
                    break;
                } else {
                    System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Ocorreu um erro: " + e.getMessage());
                System.out.println("Por favor, tente novamente.");
            }
        }
        scanner.close();
    }

    private static void manageBoard(Board board, TaskColumnRepository columnRepository, CardRepository cardRepository, Scanner scanner) {
        while (true) {
            try {
                System.out.println("\nMenu do Board " + board.getName() + ":");
                System.out.println("1 - Criar coluna");
                System.out.println("2 - Criar card");
                System.out.println("3 - Mover card");
                System.out.println("4 - Bloquear/Desbloquear card");
                System.out.println("5 - Listar colunas e cards");
                System.out.println("6 - Voltar ao menu principal");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                if (opcao == 1) {
                    System.out.print("Nome da coluna: ");
                    String columnName = scanner.nextLine();
                    if (columnName == null || columnName.trim().isEmpty()) {
                        System.out.println("Erro: O nome da coluna não pode ser vazio!");
                        continue;
                    }
                    TaskColumn column = new TaskColumn(columnName, board);
                    columnRepository.save(column);
                    System.out.println("Coluna criada com sucesso!");
                } else if (opcao == 2) {
                    System.out.println("Colunas disponíveis:");
                    List<TaskColumn> columns = columnRepository.findAll().stream()
                        .filter(col -> col != null && col.getBoard() != null && col.getBoard().getId().equals(board.getId()))
                        .toList();
                    if (columns.isEmpty()) {
                        System.out.println("Nenhuma coluna encontrada para este board. Crie uma coluna primeiro!");
                        continue;
                    }
                    columns.forEach(col -> System.out.println("ID: " + col.getId() + ", Nome: " + col.getName()));
                    System.out.print("Digite o ID da coluna para adicionar o card: ");
                    Long columnId = scanner.nextLong();
                    scanner.nextLine();
                    TaskColumn column = columnRepository.findById(columnId).orElse(null);
                    if (column != null && column.getBoard() != null && column.getBoard().getId().equals(board.getId())) {
                        System.out.print("Título do card: ");
                        String title = scanner.nextLine();
                        if (title == null || title.trim().isEmpty()) {
                            System.out.println("Erro: O título do card não pode ser vazio!");
                            continue;
                        }
                        System.out.print("Descrição do card: ");
                        String description = scanner.nextLine();
                        Card card = new Card(title, description, column);
                        cardRepository.save(card);
                        System.out.println("Card criado com sucesso!");
                    } else {
                        System.out.println("Erro: Coluna não encontrada ou não pertence a este board!");
                    }
                } else if (opcao == 3) {
                    System.out.println("Cards disponíveis:");
                    List<Card> cards = cardRepository.findAll().stream()
                        .filter(card -> card != null && card.getColumn() != null && card.getColumn().getBoard().getId().equals(board.getId()))
                        .toList();
                    if (cards.isEmpty()) {
                        System.out.println("Nenhum card encontrado para este board.");
                        continue;
                    }
                    cards.forEach(card -> System.out.println("ID: " + card.getId() + ", Título: " + card.getTitle() + ", Coluna: " + card.getColumn().getName()));
                    System.out.print("Digite o ID do card para mover: ");
                    Long cardId = scanner.nextLong();
                    scanner.nextLine();
                    Card card = cardRepository.findById(cardId).orElse(null);
                    if (card != null && card.getColumn() != null && card.getColumn().getBoard().getId().equals(board.getId())) {
                        System.out.println("Colunas disponíveis:");
                        List<TaskColumn> columnsForMove = columnRepository.findAll().stream()
                            .filter(col -> col != null && col.getBoard() != null && col.getBoard().getId().equals(board.getId()))
                            .toList();
                        if (columnsForMove.isEmpty()) {
                            System.out.println("Nenhuma coluna disponível para mover o card.");
                            continue;
                        }
                        columnsForMove.forEach(col -> System.out.println("ID: " + col.getId() + ", Nome: " + col.getName()));
                        System.out.print("Digite o ID da nova coluna: ");
                        Long newColumnId = scanner.nextLong();
                        scanner.nextLine();
                        TaskColumn newColumn = columnRepository.findById(newColumnId).orElse(null);
                        if (newColumn != null && newColumn.getBoard() != null && newColumn.getBoard().getId().equals(board.getId())) {
                            card.setColumn(newColumn);
                            cardRepository.save(card);
                            System.out.println("Card movido com sucesso!");
                        } else {
                            System.out.println("Erro: Coluna não encontrada ou não pertence a este board!");
                        }
                    } else {
                        System.out.println("Erro: Card não encontrado ou não pertence a este board!");
                    }
                } else if (opcao == 4) {
                    System.out.println("Cards disponíveis:");
                    List<Card> cards = cardRepository.findAll().stream()
                        .filter(card -> card != null && card.getColumn() != null && card.getColumn().getBoard().getId().equals(board.getId()))
                        .toList();
                    if (cards.isEmpty()) {
                        System.out.println("Nenhum card encontrado para este board.");
                        continue;
                    }
                    cards.forEach(card -> System.out.println("ID: " + card.getId() + ", Título: " + card.getTitle() + ", Bloqueado: " + card.isBlocked()));
                    System.out.print("Digite o ID do card para bloquear/desbloquear: ");
                    Long cardId = scanner.nextLong();
                    scanner.nextLine();
                    Card card = cardRepository.findById(cardId).orElse(null);
                    if (card != null && card.getColumn() != null && card.getColumn().getBoard().getId().equals(board.getId())) {
                        if (card.isBlocked()) {
                            System.out.print("Motivo para desbloquear: ");
                            String reason = scanner.nextLine();
                            if (reason == null || reason.trim().isEmpty()) {
                                System.out.println("Erro: O motivo não pode ser vazio!");
                                continue;
                            }
                            card.setBlocked(false);
                            card.setBlockReason(reason);
                            System.out.println("Card desbloqueado!");
                        } else {
                            System.out.print("Motivo para bloquear: ");
                            String reason = scanner.nextLine();
                            if (reason == null || reason.trim().isEmpty()) {
                                System.out.println("Erro: O motivo não pode ser vazio!");
                                continue;
                            }
                            card.setBlocked(true);
                            card.setBlockReason(reason);
                            System.out.println("Card bloqueado!");
                        }
                        cardRepository.save(card);
                    } else {
                        System.out.println("Erro: Card não encontrado ou não pertence a este board!");
                    }
                } else if (opcao == 5) {
                    if (board == null) {
                        System.out.println("Erro: Nenhum board selecionado!");
                        continue;
                    }
                    System.out.println("Colunas e Cards do Board " + board.getName() + ":");
                    List<TaskColumn> columns = columnRepository.findAll().stream()
                        .filter(col -> col != null && col.getBoard() != null && col.getBoard().getId().equals(board.getId()))
                        .toList();
                    if (columns.isEmpty()) {
                        System.out.println("Nenhuma coluna encontrada para este board.");
                    } else {
                        columns.forEach(col -> {
                            System.out.println("Coluna: " + col.getName());
                            List<Card> cards = cardRepository.findAll().stream()
                                .filter(card -> card != null && card.getColumn() != null && card.getColumn().getId().equals(col.getId()))
                                .toList();
                            if (cards.isEmpty()) {
                                System.out.println("  - Nenhuma tarefa nesta coluna.");
                            } else {
                                cards.forEach(card -> System.out.println("  - Card: " + card.getTitle() + " (Bloqueado: " + card.isBlocked() + ")"));
                            }
                        });
                    }
                } else if (opcao == 6) {
                    break;
                } else {
                    System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Ocorreu um erro: " + e.getMessage());
                System.out.println("Por favor, tente novamente.");
            }
        }
    }
}