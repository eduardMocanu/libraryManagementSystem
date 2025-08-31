package com.demo.app;

import com.demo.controller.BookController;
import com.demo.controller.ClientController;
import com.demo.controller.LoanController;
import com.demo.model.Book;
import com.demo.model.Client;
import com.demo.model.Loan;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import java.time.LocalDate;
import java.util.*;

public class App {
//    public static final LogsServiceCsv logsServiceCsv = new LogsServiceCsv("data/logs.csv");
//    public static final BookServiceCsv bookServiceCsv = new BookServiceCsv("data/books.csv");
//    public static final LoanServiceCsv loanServiceCsv = new LoanServiceCsv("data/loans.csv");
//    public static final ClientServiceCsv clientServiceCsv = new ClientServiceCsv("data/clients.csv");
    private static final Scanner scanner = new Scanner(System.in);

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
        System.out.println("9.CHECK ACTIVE LOANS OF A CLIENT");
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
//        books = bookServiceCsv.readCSVFile();
//        loans = loanServiceCsv.readCSVFile();
//        clients = clientServiceCsv.readCSVFile();
        MenuOptions menuOption;

        while(run){
            menuPrinter();
            menuOption = handleUserMenuInput();

            switch (menuOption){
                case CHECK_ALL_LOANS -> {
                    checkAllLoansIfEnding();
                }
                case CHECK_A_LOAN ->{
                    checkALoanData();
                }
                case ADD_LOAN -> {
                    addLoan();
                }
                case DEACTIVATE_LOAN -> {
                    deactivateLoanTechnicalProblem();
                }
                case ADD_CLIENT -> {
                    addClient();
                }
                case REMOVE_CLIENT -> {
                    removeClient();
                }
                case ADD_BOOK -> {
                    addBook();
                }
                case REMOVE_BOOK -> {
                    removeBook();
                }
                case CHECK_ACTIVE_LOANS_CLIENT -> {
                    checkActiveLoansClient();
                }
                case GIVE_BACK_LOANED_BOOK -> {
                    giveBackLoanedBook();
                }
                case CHECK_HISTORY_OF_CLIENT -> {
                    checkHistoryOfClient();
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
    }

    static MenuOptions handleUserMenuInput(){
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

    static int readInt(String prompt){
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

    static void checkALoanData(){
        System.out.println("Give me your client Id:");
        Integer clientId = scanner.nextInt();
        scanner.nextLine();
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
        }else{
            System.out.println("There is not a loan found with the data provided");
        }
    }

    static void addLoan(){
        LocalDate dateNow = LocalDate.now();
        LocalDate dateEnd;
        String bookISBN;
        Integer clientId;
        int length;
        HashMap<String, ArrayList<String>> booksAvailable = BookController.getAvailableBooksByAuthor();
        if(!booksAvailable.isEmpty()){
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
            length = readInt("Give me the length of the loan");
            scanner.nextLine();//to empty the buffer from the int
            System.out.println("Give me your id");
            clientId = scanner.nextInt();
            bookISBN = BookController.getBookISBNByNameAndAuthor(bookName, bookAuthor);
            if(bookISBN!=null){
                dateEnd = dateNow.plusDays(length);
                boolean ok = LoanController.addLoan(new Loan(dateNow, dateEnd, clientId, bookISBN, true, false));
                if(ok){
                    System.out.println("Loan added successfully");
                }
                else{
                    System.out.println("Could not add the loan");
                }
            }
            else{
                System.out.println("Did not find book");
            }
        }else{
            System.out.println("There are no available books");
        }

    }

    static void deactivateLoanTechnicalProblem(){
        Integer loanId;
        System.out.println("Give me the id of the loan you want to deactivate");
        loanId = scanner.nextInt();
        int status = LoanController.deactivateLoan(loanId);
        if(status == 2){
            System.out.println("Loan deactivated successfully");
        } else if (status == 1) {
            System.out.println("Could not find the active loan");
        }else{
            System.out.println("Could not deactivate the loan");
        }
    }

    static void addClient(){
        String name, email;
        System.out.println("Give me the name:");
        name = scanner.nextLine().trim().toUpperCase();
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

    static void removeClient(){
        Integer id;
        System.out.println("Give me the id of the client:");
        id = scanner.nextInt();
        int status = ClientController.removeClient(id);
        if(status == 2){
            System.out.println("The deletion was a success");
        }else if(status == 1){
            System.out.println("The client is not in the database");
        }else if(status == 0){
            System.out.println("Can't delete the client because he/she has active loans");
        }else{
            System.out.println("Can't remove the client, technical problem");
        }

    }
    static void addBook(){
        String ISBN, bookName, author;
        int pages;
        System.out.println("Give me the ISBN of the book:");
        ISBN = scanner.nextLine().trim();
        System.out.println("Give me the name of the book");
        bookName = scanner.nextLine().toUpperCase().trim();
        System.out.println("Give me the author");
        author = scanner.nextLine().trim().toUpperCase();
        pages = readInt("Give me the number of pages that the book has:");
        boolean ok = BookController.addBook(new Book(ISBN, bookName, author, pages, false));
        if(ok){
            System.out.println("Book added successfully");
        }
    }

    static void removeBook(){
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

    static void checkActiveLoansClient(){
        Integer clientId;
        System.out.println("Provide me the user ID:");
        clientId = scanner.nextInt();
        Client client = ClientController.getClientIfExists(clientId);
        if(client != null){
            HashSet<Loan> activeLoans = LoanController.getActiveLoansOfClient(clientId);
            if(!activeLoans.isEmpty()){
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
            }else{
                System.out.println("No active loans");
            }

        }
        else{
            System.out.println("Client does not exist");
        }
    }

    static void giveBackLoanedBook(){
        //change the status of the loan and of the book that was loaned
        String bookName, bookAuthor;
        Integer clientId;
        System.out.println("Give me the name of the book you want to give back:");
        bookName = scanner.nextLine().trim().toUpperCase();
        System.out.println("Give me the name of the author of the book");
        bookAuthor = scanner.nextLine().trim().toUpperCase();
        System.out.println("Give me your client ID");
        clientId = scanner.nextInt();
        int status = LoanController.giveBookBack(bookName, bookAuthor, clientId);
        if(status == 2){
            System.out.println("The give back was ok");
        }else if(status == 1){
            System.out.println("Problem at loan inactivation, client id is not correct");
        }else if(status == 0){
            System.out.println("The book is not found in the database");
        }else{
            System.out.println("Can't give back the book, most probably a technical problem");
        }
    }

    static void checkHistoryOfClient(){
        Integer clientId;
        System.out.println("Give me the user ID:");
        clientId = scanner.nextInt();
        Client client = ClientController.getClientIfExists(clientId);
        if(client!=null){
            HashMap<Integer, Loan> history = LoanController.getHistoryOfClient(clientId);
            if(!history.isEmpty()){
                for(Loan i : history.values()){
                    Book book = BookController.getBookObjByISBN(i.getBookISBN());
                    if(book != null){
                        String status = i.getActive() ? "Active" : "Inactive";
                        System.out.println("BOOK: " + book.getName() + " started: " + i.getLoanStart() + " current status: " + status);
                    }
                }
            }
            else{
                System.out.println("You have no history in this library");
            }
        }else{
            System.out.println("The given client id does not exist");
        }

    }

    static void exit(){
        System.out.println("Exiting...");
    }
    //TO DO sql transition:
    //add role based login
    //update loan end when giving the book back so that I can display correct info when I get the history of client to the endDate

}
