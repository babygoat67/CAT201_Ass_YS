package com.app.cat201app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.util.Optional;

public class LibraryController {
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> indexColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, Boolean> availabilityColumn;
    @FXML private TableColumn<Book, String> borrowerColumn;

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;

    private final Library library = new Library();
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up cell value factories for each column
        indexColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(bookTable.getItems().indexOf(cellData.getValue()) + 1))
        );
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));

        // Load and sort books
        bookList.addAll(FileHandler.loadBooks());
        library.getAllBooks().addAll(bookList);
        FXCollections.sort(bookList, (book1, book2) -> book1.getTitle().compareToIgnoreCase(book2.getTitle()));

        bookTable.setItems(bookList);
    }

    @FXML
    protected void onDisplayBorrowedBooks() {
        ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();
        for (Book book : library.getAllBooks()) {
            if (!book.isAvailable()) {
                borrowedBooks.add(book);
            }
        }
        bookTable.setItems(borrowedBooks);
    }

    @FXML
    protected void onDisplayAllBooks() {
        FXCollections.sort(bookList, (book1, book2) -> book1.getTitle().compareToIgnoreCase(book2.getTitle()));
        bookTable.setItems(bookList);
    }

    @FXML
    protected void onAddBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            showAlert("Input Error", "Please fill all fields.");
            return;
        }

        Book newBook = new Book(title, author, isbn, "-");
        library.addBook(newBook);
        bookList.add(newBook);
        FXCollections.sort(bookList, (book1, book2) -> book1.getTitle().compareToIgnoreCase(book2.getTitle()));

        titleField.clear();
        authorField.clear();
        isbnField.clear();
    }

    @FXML
    protected void onSearchBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();

        ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
        for (Book book : library.getAllBooks()) {
            boolean matches = title.isEmpty() || book.getTitle().toLowerCase().contains(title.toLowerCase());
            if (!author.isEmpty() && !book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                matches = false;
            }
            if (!isbn.isEmpty() && !book.getIsbn().contains(isbn)) {
                matches = false;
            }
            if (matches) {
                filteredBooks.add(book);
            }
        }
        bookTable.setItems(filteredBooks);
        FXCollections.sort(filteredBooks, (book1, book2) -> book1.getTitle().compareToIgnoreCase(book2.getTitle()));
    }

    @FXML
    protected void onBorrowBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null && selectedBook.isAvailable()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Borrow Book");
            dialog.setHeaderText("Enter your name to borrow the book:");
            dialog.setContentText("Borrower's Name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                selectedBook.borrow(result.get());
                bookTable.refresh();
            } else {
                showAlert("Input Error", "Borrower's name cannot be empty.");
            }
        } else {
            showAlert("Cannot Borrow", "Book is not available or not selected.");
        }
    }

    @FXML
    protected void onReturnBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null && !selectedBook.isAvailable()) {
            selectedBook.returnBook();
            bookTable.refresh();
        } else {
            showAlert("Cannot Return", "Book is already available or not selected.");
        }
    }

    @FXML
    protected void onDeleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            library.deleteBook(selectedBook.getIsbn());
            bookList.remove(selectedBook);
        } else {
            showAlert("No book selected", "Please select a book to delete.");
        }
    }

    @FXML
    protected void onSaveBooks() {
        FileHandler.saveBooks(library.getAllBooks());
        showAlert("Save Successful", "Library data saved successfully.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
