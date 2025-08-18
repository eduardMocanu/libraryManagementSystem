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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static final LogsServiceCsv logsServiceCsv = new LogsServiceCsv("logs.csv");
    public static final BookServiceCsv bookServiceCsv = new BookServiceCsv("books.csv");
    public static final LoanServiceCsv loanServiceCsv = new LoanServiceCsv("loans.csv");
    public static final ClientServiceCsv clientServiceCsv = new ClientServiceCsv("clients.csv");

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
        Map<String, Loan> loans;
        Map<String, Book> books;
        Map<String, Client> clients;

        boolean run = true;
        Scanner scanner = new Scanner(System.in);
        books = bookServiceCsv.readCSVFile();
        loans = loanServiceCsv.readCSVFile();
        clients = clientServiceCsv.readCSVFile();
        LoanController.removeInvalidDatesLoans(loans, books);
        MenuOptions menuOption;

        while(run){
            menuPrinter();
            menuOption = handleUserMenuInput(scanner);

            switch (menuOption){
                case CHECK_ALL_LOANS -> {
                    checkAllLoansIfEnding(loans, clients, books);
                }
                case CHECK_A_LOAN ->{
                    checkALoanData(scanner, loans, clients, books);
                }
                case ADD_LOAN -> {
                    addLoan(scanner, loans, books);
                }
                case DEACTIVATE_LOAN -> {
                    deactivateLoanTechnicalProblem(scanner, books, loans);
                }
                case ADD_CLIENT -> {
                    addClient(scanner, clients);
                }
                case REMOVE_CLIENT -> {
                    removeClient(scanner, clients, loans);
                }
                case ADD_BOOK -> {
                    addBook(scanner, books);
                }
                case REMOVE_BOOK -> {
                    removeBook(scanner, books);
                }
                case CHECK_ACTIVE_LOANS_CLIENT -> {
                    checkActiveLoansClient(scanner, loans, books, clients);
                }
                case GIVE_BACK_LOANED_BOOK -> {
                    giveBackLoanedBook(scanner, loans, books, clients);
                }
                case CHECK_HISTORY_OF_CLIENT -> {
                    checkHistoryOfClient(scanner, loans, books, clients);
                }
                case EXIT ->{
                    run = false;
                    exit(books, clients, loans);
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
    static void checkAllLoansIfEnding(Map<String, Loan> loans, Map<String, Client>clients, Map<String, Book> books){
        LoanController.checkAllLoansSendEmail(loans, clients, books);
        loanServiceCsv.writeCSVFile(loans);
        logsServiceCsv.writeToLogsCsv("All loans have been checked");
    }

    static void checkALoanData(Scanner scanner, Map<String, Loan> loans, Map<String, Client> clients, Map<String, Book> books){
        System.out.println("Give me the id of the loan you want to check");
        String loanId = scanner.nextLine().trim();
        if(loans.containsKey(loanId) && loans.get(loanId).getActive()){
            Loan loan = loans.get(loanId);
            System.out.println("Loaned the book with the title: " + books.get(loan.getBookISBN()).getName());
            System.out.println("The loan started at " + loan.getLoanStart() + " and ends at " + loan.getLoanEnd());
            System.out.println("The person that loaned it is: " + clients.get(loan.getClientId().trim()).getFullName());
        }else if(loans.containsKey(loanId) && !loans.get(loanId).getActive()){
            System.out.println("Loan is not active anymore");
        }else{
            System.out.println("Loan doesn't exist");
        }
        logsServiceCsv.writeToLogsCsv("Checked the loan data of loan with id " +loanId);
    }

    static void addLoan(Scanner scanner, Map<String, Loan> loans, Map<String, Book> books){
        LocalDate dateNow = LocalDate.now();
        LocalDate dateEnd;
        String bookISBN, clientId;
        String idLoan = LoanController.getNewLoanId(loans), bookName;
        int length;
        System.out.println("Give me the book name");
        bookName = scanner.nextLine().toUpperCase().trim();
        length = readInt(scanner, "Give me the length of the loan");
        while (length <= 0) {
            System.out.println("Give me a valid timespan");
            length = scanner.nextInt();
        }
        scanner.nextLine();//to empty the buffer from the int
        System.out.println("Give me your id");
        clientId = scanner.nextLine().trim();
        bookISBN = BookController.getBookISBNByName(scanner, books, bookName);
        dateEnd = dateNow.plusDays(length);
        LoanController.addLoan(books, loans, new Loan(idLoan, dateNow, dateEnd, clientId, bookISBN, true, false));
        //books+loans
        bookServiceCsv.writeCSVFile(books);
        loanServiceCsv.writeCSVFile(loans);
        System.out.println("Added the loan with id: " + idLoan);
        logsServiceCsv.writeToLogsCsv("Opted to add loan with loan ID: " + idLoan);
    }

    static void deactivateLoanTechnicalProblem(Scanner scanner, Map<String, Book> books, Map<String, Loan> loans){
        String loanId;
        System.out.println("Give me the id of the loan you want to deactivate");
        loanId = scanner.nextLine().trim();
        LoanController.deactivateLoan(loans, books, loanId);
        //books+loans
        bookServiceCsv.writeCSVFile(books);
        loanServiceCsv.writeCSVFile(loans);
        System.out.println("Deactivated the loan with id: " + loanId);
        logsServiceCsv.writeToLogsCsv("Opted for option to deactivate loan with loan ID: " + loanId);

    }

    static void addClient(Scanner scanner, Map<String, Client> clients){
        String name, surname, email, id = ClientController.getNewClientId(clients);
        System.out.println("Give me the name:");
        name = scanner.nextLine().trim().toUpperCase();
        System.out.println("Give me the surname:");
        surname = scanner.nextLine().trim().toUpperCase();
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
        ClientController.addClient(clients, new Client(id, name, surname, email));
        //clients
        clientServiceCsv.writeCSVFile(clients);
        logsServiceCsv.writeToLogsCsv("Opted to add client with ID: " + id);
    }

    static void removeClient(Scanner scanner, Map<String, Client> clients, Map<String, Loan> loans){
        String id;
        System.out.println("Give me the id of the client:");
        id = scanner.nextLine().trim();
        ClientController.removeClient(clients, loans, id);
        //clients
        clientServiceCsv.writeCSVFile(clients);
        logsServiceCsv.writeToLogsCsv("Opted to remove client with ID: " + id);
    }

    static void addBook(Scanner scanner, Map<String, Book> books){
        String ISBN, bookName, author;
        int pages;
        System.out.println("Give me the ISBN of the book:");
        ISBN = scanner.nextLine().trim();
        System.out.println("Give me the name of the book");
        bookName = scanner.nextLine().toUpperCase().trim();
        System.out.println("Give me the author");
        author = scanner.nextLine().trim().toUpperCase();
        pages = readInt(scanner, "Give me the number of pages that the book has:");
        BookController.addBook(books, new Book(ISBN, bookName, author, pages, false));
        //books
        bookServiceCsv.writeCSVFile(books);
        logsServiceCsv.writeToLogsCsv("Opted to add a book with ISBN: " + ISBN);
    }

    static void removeBook(Scanner scanner, Map<String, Book> books){
        String bookName, ISBN;
        System.out.println("Give me the name of the book you want to remove:");
        bookName = scanner.nextLine().toUpperCase().trim();
        ISBN = BookController.getBookISBNByName(scanner, books, bookName);
        BookController.removeBook(books, ISBN);
        //books
        bookServiceCsv.writeCSVFile(books);
        logsServiceCsv.writeToLogsCsv("Opted to remove book with ISBN: " + ISBN);
    }

    static void checkActiveLoansClient(Scanner scanner, Map<String, Loan> loans, Map<String, Book> books, Map<String, Client> clients){
        String clientId;
        System.out.println("Provide me the user ID:");
        clientId = scanner.nextLine().trim();
        if(clients.containsKey(clientId)){
            HashSet<Loan> loansOfClient = ClientController.getActiveLoansOfClient(loans, clientId);
            if(!loansOfClient.isEmpty()){
                for(Loan i:loansOfClient){
                    System.out.println("Loan ID: " + i.getId() + " for book: " + books.get(i.getBookISBN()).getName());
                }
            }
            else{
                System.out.println("No active loans");
            }
        }else{
            System.out.println("The client ID is not valid");
        }
        logsServiceCsv.writeToLogsCsv("Opted to check the loans of client with ID: " + clientId);
    }

    static void giveBackLoanedBook(Scanner scanner, Map<String, Loan> loans, Map<String, Book> books, Map<String, Client> clients){
        String bookName, bookISBN, clientID;
        System.out.println("Give me the name of the book you want to give back:");
        bookName = scanner.nextLine().trim().toUpperCase();
        System.out.println("Give me your client ID");
        clientID = scanner.nextLine().trim();
        bookISBN = BookController.getBookISBNByName(scanner, books, bookName);
        LoanController.giveBookBack(loans, clients, books, bookISBN, clientID);
        //write
        bookServiceCsv.writeCSVFile(books);
        loanServiceCsv.writeCSVFile(loans);
        logsServiceCsv.writeToLogsCsv("Opted to give back the book with the name: " + bookName);
    }

    static void checkHistoryOfClient(Scanner scanner, Map<String, Loan> loans, Map<String, Book> books, Map<String, Client> clients){
        String clientId;
        System.out.println("Give me the user ID:");
        clientId = scanner.nextLine().trim();
        if(clients.containsKey(clientId)){
            ClientController.getHistoryOfClient(loans, books, clientId);
        }else{
            System.out.println("The client is not registered");
        }
        logsServiceCsv.writeToLogsCsv("Checked the history of client with ID:" + clientId);
    }

    static void exit(Map<String, Book> books, Map<String, Client> clients, Map<String, Loan> loans){
        System.out.println("Exiting...");
        //all
        bookServiceCsv.writeCSVFile(books);
        loanServiceCsv.writeCSVFile(loans);
        clientServiceCsv.writeCSVFile(clients);
        logsServiceCsv.writeToLogsCsv("Exited");
    }


    //TO DO:
    //when adding a new loan, show on the screen the book options by name that are available
}
