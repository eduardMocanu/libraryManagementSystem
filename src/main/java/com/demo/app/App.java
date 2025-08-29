package com.demo.app;

import com.demo.controller.BookController;
import com.demo.controller.ClientController;
import com.demo.controller.LoanController;
import com.demo.model.Book;
import com.demo.model.Client;
import com.demo.model.Loan;
import com.demo.service.BookServiceCsv;
import com.demo.service.ClientServiceCsv;
import com.demo.service.LoanServiceCsv;
import com.demo.service.LogsServiceCsv;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.*;

public class App {
//    public static final LogsServiceCsv logsServiceCsv = new LogsServiceCsv("data/logs.csv");
//    public static final BookServiceCsv bookServiceCsv = new BookServiceCsv("data/books.csv");
//    public static final LoanServiceCsv loanServiceCsv = new LoanServiceCsv("data/loans.csv");
//    public static final ClientServiceCsv clientServiceCsv = new ClientServiceCsv("data/clients.csv");

    public static void main(String[] args){
        menuInputHandler();
    }


    //helper functions for the main

    static void menuPrinter(){
        System.out.println("What do you want to do? Provide the number of the option to execute it!");
        System.out.println("1.CHECK ALL CURRENT LOANS AND SEND EMAILS IF EXPIRED LOANS");
        System.out.println("2.CHECK A SPECIFIC LOAN");
        System.out.println("3.ADD LOAN");
        System.out.println("4.DEACTIVATE LOAN BECAUSE OF TECHNICAL DIFFICULTIES");
        System.out.println("5.ADD CLIENT");
        System.out.println("6.REMOVE CLIENT");
        System.out.println("7.ADD BOOK");
        System.out.println("8.REMOVE BOOK");
        System.out.println("9.CHECK ALL LOANS FOR A CLIENT");
        System.out.println("10.GIVE BACK LOANED BOOK");
        System.out.println("11.CHECK THE HISTORY OF CLIENT");
        System.out.println("12.EXIT");
        System.out.print("YOUR INPUT: ");
    }

    static void menuInputHandler(){
//        Map<String, Loan> loans;
//        Map<String, Book> books;
//        Map<String, Client> clients;

        boolean run = true;
        Scanner scanner = new Scanner(System.in);
//        books = bookServiceCsv.readCSVFile();
//        loans = loanServiceCsv.readCSVFile();
//        clients = clientServiceCsv.readCSVFile();
        MenuOptions menuOption;

        while(run){
            menuPrinter();
            menuOption = handleUserMenuInput(scanner);

            switch (menuOption){
                case CHECK_ALL_LOANS -> {
                    checkAllLoansIfEnding();
                }
                case CHECK_A_LOAN ->{
                    checkALoanData(scanner,);
                }
                case ADD_LOAN -> {
                    addLoan(scanner);
                }
                case DEACTIVATE_LOAN -> {
                    deactivateLoanTechnicalProblem(scanner);
                }
                case ADD_CLIENT -> {
                    addClient(scanner);
                }
                case REMOVE_CLIENT -> {
                    removeClient(scanner);
                }
                case ADD_BOOK -> {
                    addBook(scanner);
                }
                case REMOVE_BOOK -> {
                    removeBook(scanner);
                }
                case CHECK_ACTIVE_LOANS_CLIENT -> {
                    checkActiveLoansClient(scanner);
                }
                case GIVE_BACK_LOANED_BOOK -> {
                    giveBackLoanedBook(scanner);
                }
                case CHECK_HISTORY_OF_CLIENT -> {
                    checkHistoryOfClient(scanner);
                }
                case EXIT ->{
                    run = false;
                    exit();
                }
                case null -> {
                    System.out.println("Invalid option");
                }
            }
        }
        scanner.close();
    }

    static MenuOptions handleUserMenuInput(Scanner scanner){
        int valueRead;
        while(true){
            try{
                valueRead = scanner.nextInt();
                scanner.nextLine();
                return MenuOptions.fromInt(valueRead);
            } catch (InputMismatchException e) {
                System.out.println("Wrong type of data, needs to be a number");
                scanner.nextLine();
            }
        }

    }

    static int readInt(Scanner scanner, String prompt){
        int value;
        while(true){
            try{
                System.out.println(prompt);
                value = scanner.nextInt();
                if(value <= 0){
                    throw new InputMismatchException("Negative or null value");
                }
                return value;
            }catch(InputMismatchException e){
                System.out.println("Invalid data type or negative value");
                scanner.nextLine();
            }
        }
    }


    //menu behaviour
    static void checkAllLoansIfEnding(){
        LoanController.checkAllLoansSendEmail();
    }

    static void checkALoanData(Scanner scanner){
        System.out.println("Give me your client Id:");
        Integer clientId = scanner.nextInt();
        System.out.println("Give me the name of the book you have loaned:");
        String bookName = scanner.nextLine().trim().toUpperCase();
        System.out.println("Give me the author of the book you have loaned:");
        String bookAuthor = scanner.nextLine().trim().toUpperCase();
        Loan loanData = LoanController.getALoanData(bookName, bookAuthor, clientId);
        if(loanData != null){
            if(loanData.getActive()){
                if(loanData.getLoanEnd().isAfter(LocalDate.now())){
                    System.out.println("The loan has not expired yet, is available until: " + loanData.getLoanEnd());
                }else{
                    System.out.println("Loan has expired on "+ loanData.getLoanEnd());
                    if(loanData.getEmailed()){
                        System.out.println("You have been emailed about this");
                    }
                }
            }
            else{
                System.out.println("Loan is inactive");
            }
        }
    }

    static void addLoan(Scanner scanner){
        LocalDate dateNow = LocalDate.now();
        LocalDate dateEnd;
        String bookISBN;
        Integer clientId;
        int length;
        HashMap<String, ArrayList<String>> booksAvailable = BookController.getAvailableBooksByAuthor();
        for(String i:booksAvailable.keySet()){
            ArrayList<String> titles = booksAvailable.get(i);
            System.out.println(i + ":");
            for(String title:titles){
                System.out.println("  -" + title);
            }
        }
        System.out.println("Give me the book name");
        String bookName = scanner.nextLine().toUpperCase().trim();
        System.out.println("Give me the book author");
        String bookAuthor = scanner.nextLine().trim().toUpperCase();
        length = readInt(scanner, "Give me the length of the loan");
        scanner.nextLine();//to empty the buffer from the int
        System.out.println("Give me your id");
        clientId = Integer.getInteger(scanner.nextLine().trim());
        bookISBN = BookController.getBookISBNByNameAndAuthor(bookName, bookAuthor);
        dateEnd = dateNow.plusDays(length);
        boolean ok = LoanController.addLoan(new Loan(dateNow, dateEnd, clientId, bookISBN, true, false));
        if(ok){
            System.out.println("Loan added successfully");
        }
        else{
            System.out.println("Could not add the loan");
        }
    }

    static void deactivateLoanTechnicalProblem(Scanner scanner){
        Integer loanId;
        System.out.println("Give me the id of the loan you want to deactivate");
        loanId = scanner.nextInt();
        int status = LoanController.deactivateLoan(loanId);
        if(status == 2){
            System.out.println("Loan deactivated successfully");
        } else if (status == 1) {
            System.out.println("Could not give the book back");
        }else{
            System.out.println("Could not deactivate the loan");
        }
    }

    static void addClient(Scanner scanner){
        String name, email;
        System.out.println("Give me the name:");
        name = scanner.nextLine().trim().toUpperCase();
        System.out.println("Give me the surname:");
        while(true){
            try{
                System.out.println("Give me your email:");
                email = scanner.nextLine().trim();
                InternetAddress temp = new InternetAddress(email);
                temp.validate();
                if(!email.contains(".")){
                    throw new AddressException("Invalid");
                }
                break;
            }catch(AddressException e){
                System.out.println("Invalid address");
            }
        }
        Integer id = ClientController.addClient(new Client(name, email));
        if(id != null){
            System.out.println("Added the client with id " + id);
        }
    }

    static void removeClient(Scanner scanner){
        Integer id;
        System.out.println("Give me the id of the client:");
        id = scanner.nextInt();
        boolean status = ClientController.removeClient(id);
        if(status){
            System.out.println("The deletion was a success");
        }else{
            System.out.println("Can't delete the client because he/she has active loans");
        }

    }
    static void addBook(Scanner scanner){
        String ISBN, bookName, author;
        int pages;
        System.out.println("Give me the ISBN of the book:");
        ISBN = scanner.nextLine().trim();
        System.out.println("Give me the name of the book");
        bookName = scanner.nextLine().toUpperCase().trim();
        System.out.println("Give me the author");
        author = scanner.nextLine().trim().toUpperCase();
        pages = readInt(scanner, "Give me the number of pages that the book has:");
        boolean ok = BookController.addBook(new Book(ISBN, bookName, author, pages, false));
        if(ok){
            System.out.println("Book added successfully");
        }
    }

    static void removeBook(Scanner scanner){
        String bookName, ISBN, authorName;
        System.out.println("Give me the name of the book you want to remove:");
        bookName = scanner.nextLine().toUpperCase().trim();
        System.out.println("Give me the author of the book you want to remove:");
        authorName = scanner.nextLine().trim().toUpperCase();
        ISBN = BookController.getBookISBNByNameAndAuthor(bookName, authorName);
        int statusRemove = BookController.removeBook(ISBN);
        if(statusRemove == 2){
            System.out.println("Successfully removed");
        }else if(statusRemove == 1){
            System.out.println("Could not remove because the book is not in db");
        }else{
            System.out.println("Could not remove because the book is loaned");
        }
    }

    static void checkActiveLoansClient(Scanner scanner){
        Integer clientId;
        System.out.println("Provide me the user ID:");
        clientId = scanner.nextInt();
        Client client = ClientController.getClientIfExists(clientId);
        if(client != null){
            HashSet<Loan> activeLoans = LoanController.getActiveLoansOfClient(clientId);
            for(Loan i : activeLoans){
                Book book = BookController.getBookObjByISBN(i.getBookISBN());
                if(book != null){
                    System.out.println("Loan for book " + book.getName() + " by " + book.getAuthor());
                    if(i.getLoanEnd().isBefore(LocalDate.now())){
                        System.out.println("Expired on " + i.getLoanEnd());
                    }else{
                        System.out.println("Available until " + i.getLoanEnd());
                    }
                }
                System.out.println();
            }
        }
        else{
            System.out.println("Client does not exist");
        }
    }
//here
    static void giveBackLoanedBook(Scanner scanner){
        //change the status of the loan and of the book that was loaned
        String bookName, bookISBN, clientID;
        System.out.println("Give me the name of the book you want to give back:");
        bookName = scanner.nextLine().trim().toUpperCase();
        System.out.println("Give me your client ID");
        clientID = scanner.nextLine().trim();
        bookISBN = BookController.getBookISBNByName(scanner, books, bookName);
        LoanController.giveBookBack(loans, clients, books, bookISBN, clientID);

    }

    static void checkHistoryOfClient(Scanner scanner){
        String clientId;
        System.out.println("Give me the user ID:");
        clientId = scanner.nextLine().trim();
        if(clients.containsKey(clientId)){
            ClientController.getHistoryOfClient(loans, books, clientId);
        }else{
            System.out.println("The client is not registered");
        }
    }

    static void exit(){
        System.out.println("Exiting...");

    }
    //TO DO sql transition:
    //Scanner - member of App class
    //add role based login
}
