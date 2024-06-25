package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.example.BookOuterClass.*;
import org.example.BookServiceGrpc.*;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class BookClient {
    private final ManagedChannel channel;
    private final BookServiceBlockingStub blockingStub;

    public BookClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
    }

    public BookClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = BookServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void addBook(Book book) {
        AddBookRequest request = AddBookRequest.newBuilder()
                .setBook(book)
                .build();
        AddBookResponse response = blockingStub.addBook(request);
        System.out.println("Add Book Response: " + response.getMessage());
    }

    public void updateBook(String isbn, Book book) {
        UpdateBookRequest request = UpdateBookRequest.newBuilder()
                .setIsbn(isbn)
                .setBook(book)
                .build();
        UpdateBookResponse response = blockingStub.updateBook(request);
        System.out.println("Update Book Response: " + response.getMessage());
    }

    public void deleteBook(String isbn) {
        DeleteBookRequest request = DeleteBookRequest.newBuilder()
                .setIsbn(isbn)
                .build();
        DeleteBookResponse response = blockingStub.deleteBook(request);
        System.out.println("Delete Book Response: " + response.getMessage());
    }

    public void getAllBooks() {
        GetBooksRequest request = GetBooksRequest.newBuilder().build();
        GetBooksResponse response = blockingStub.getBooks(request);
        System.out.println("List of Books:");
        response.getBooksList().forEach(book -> System.out.println(book.toString()));
    }

    public static void main(String[] args) {
        BookClient client = new BookClient("localhost", 50051);

        // Add a new book
        Book newBook = Book.newBuilder()
                .setIsbn("1234567890")
                .setTitle("Sample Book")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(200)
                .build();
        client.addBook(newBook);

        // Update the book
        Book updatedBook = Book.newBuilder()
                .setIsbn("1234567890")
                .setTitle("Updated Book Title")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(250)
                .build();
        client.updateBook("1234567890", updatedBook);

        // Delete the book
        client.deleteBook("1234567890");

        // Add the book
        client.addBook(newBook);
        // Get all books
        client.getAllBooks();

        try {
            client.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
