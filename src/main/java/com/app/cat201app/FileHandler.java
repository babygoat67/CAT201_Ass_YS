package com.app.cat201app;

import java.io.*;
import java.util.*;

public class FileHandler {
    private static final String FILE_NAME = "corrected_books.csv";

    // Save books to the file in CSV format
    public static void saveBooks(List<Book> books) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Book book : books) {
                // Ensure that borrowerName is not null when saving
                String borrowerName = (book.getBorrowerName() != null && !book.getBorrowerName().isEmpty())
                        ? book.getBorrowerName() : "-";

                // Format each book's details as CSV
                writer.printf("%s,%s,%s,%b,%s%n", book.getTitle(), book.getAuthor(), book.getIsbn(),
                        book.isAvailable(), borrowerName);
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    // Load books from the CSV file
    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();

        try (InputStream inputStream = FileHandler.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (inputStream == null) {
                System.out.println("File not found in resources, returning empty list.");
                return books;
            }

            // Read the file using BufferedReader
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        Book book = parseBook(parts);
                        books.add(book);
                    } else {
                        System.err.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading books: " + e.getMessage());
        }

        return books;
    }


    // Parse book details from a CSV line
    private static Book parseBook(String[] parts) {
        String title = parts[0].trim();
        String author = parts[1].trim();
        String isbn = parts[2].trim();
        boolean isAvailable = Boolean.parseBoolean(parts[3].trim());

        // Handle the borrower name, defaulting to "-" if not present
        String borrowerName = parts[4].trim();
        if (borrowerName.isEmpty()) {
            borrowerName = "-";  // Treat empty string as default placeholder
        }

        // Create a new Book object
        Book book = new Book(title, author, isbn, borrowerName);

        // If the book is not available, check if borrower name is set
        if (!isAvailable && !borrowerName.equals("-")) {
            book.borrow(borrowerName);  // Mark the book as borrowed if applicable
        }

        return book;
    }
}
