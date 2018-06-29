package com.clothescloset.signin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    private static LinkedList<Volunteer> volunteers = new LinkedList<>();
    private static LinkedList<Patron> patrons = new LinkedList<>();

    private static DateFormat dateFormat;
    private static Date date;

    private static File volunteerInput;
    private static File patronInput;

    private static File outputFile;
    private static PrintWriter outputWriter;

    private static Scanner textInput;

    private static int totalTime;
    private static int totalPatrons;

    public static void main(String[] args) {
        //set up global scanners
        setup();

        //write date to new output file
        outputWriter.println("Volunteer information for " + dateFormat.format(date));
        outputWriter.flush();

        //read volunteer information out of input file
        getVolunteers();
        getPatrons();

        while(true) {

            System.out.println("Choose one of the following options");
            System.out.println("-Scan your barcode");
            System.out.println("-Type 1 to export files");
            System.out.println("-Type 2 to add a volunteer");
            System.out.println("-Type 3 to add a patron");
            System.out.println("-Type 4 to exit");

            //add code to check for barcode going off
            int choice = parse(textInput.nextLine());

            //Checks if the choice is a valid id
            for(int i = 0; i < volunteers.size(); i++) {
                Volunteer volunteer = volunteers.get(i);
                if(volunteer.id == choice) {
                    if(!volunteer.signedIn) {
                        volunteer.signIn();
                    } else {
                        //sign out
                        volunteer.signOut();
                        totalTime += volunteer.sessionSeconds();

                        //update input for individual total time
                        replaceLine((i + 1) * 3, volunteer.totalSeconds + "");

                        //write data to the output file
                        outputWriter.println(volunteer.name + " worked " + volunteer.sessionTime.format());
                        outputWriter.println("Total time worked is " + new VolunteerTime(totalTime).format());
                        outputWriter.println();
                        outputWriter.flush();
                    }
                }
            }

            for(int i = 0; i < patrons.size(); i++) {
                Patron patron = patrons.get(i);
                if(patron.id == choice) {
                    totalPatrons++;

                    outputWriter.println(patron.name + " visited and has a family of " + patron.familySize);
                    outputWriter.println("The total number of patrons today is " + totalPatrons);
                    outputWriter.println();
                    outputWriter.flush();

                    System.out.println(patron.name + " has signed in.");
                    System.out.println();
                }
            }

            if(choice == 1) {
                //export files
                export();
            } else if(choice == 2) {
                //add a volunteer
                addVolunteer();
            } else if(choice == 3) {
                //add a patron
                addPatron();
            } else if(choice == 4) {
                //exit program
                exit();
            }
        }
    }

    //Sets up variables and various utilities
    private static void setup() {
        dateFormat = new SimpleDateFormat("MM_dd_yyyy");
        date = new Date();

        String fileName = "/home/chris/IdeaProjects/ClothesCloset/src/com/clothescloset/signin/Output_" + dateFormat.format(date) + ".txt";
        int counter = 1;

        while(true) {
            if(new File(fileName).exists()) {
                fileName = "/home/chris/IdeaProjects/ClothesCloset/src/com/clothescloset/signin/Output_" + dateFormat.format(date) + "_" + counter + ".txt";
                counter++;
            } else break;
        }

        volunteerInput = new File("/home/chris/IdeaProjects/ClothesCloset/src/com/clothescloset/signin/VolunteerInput.txt");
        patronInput = new File("/home/chris/IdeaProjects/ClothesCloset/src/com/clothescloset/signin/PatronInput.txt");
        outputFile = new File(fileName);

        try {
            outputFile.createNewFile();
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            outputWriter = new PrintWriter(outputFile);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        textInput = new Scanner(System.in);

        totalTime = 0;
        totalPatrons = 0;
    }

    //Read data from files and add it to lists
    private static void getVolunteers() {
        Scanner fileInput;

        try {
            fileInput = new Scanner(volunteerInput);

            while(fileInput.hasNextLine()) {
                int id = parse(fileInput.nextLine());
                String name = fileInput.nextLine();
                int totalSeconds = parse(fileInput.nextLine());

                volunteers.add(new Volunteer(id, name, totalSeconds));
            }

            fileInput.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void getPatrons() {
        Scanner fileInput;

        try {
            fileInput = new Scanner(patronInput);

            while(fileInput.hasNextLine()) {
                int id = parse(fileInput.nextLine());
                String name = fileInput.nextLine();
                int familySize = parse(fileInput.nextLine());

                patrons.add(new Patron(id, name, familySize));
            }

            fileInput.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //Add a person
    private static void addVolunteer() {
        Scanner fileInput;
        //the size will be the current volunteer number times three plus an additional three since we are adding a volunteer
        String[] lines = new String[volunteers.size() * 3 + 3];

        //read
        try {
            fileInput = new Scanner(volunteerInput);

            System.out.println("Scan the volunteers new barcode.");
            int id = parse(textInput.nextLine());

            System.out.println("Enter the volunteer's name.");
            String name = textInput.nextLine();

            volunteers.addFirst(new Volunteer(id, name, 0));

            //read current file data
            lines[0] = id + "";
            lines[1] = name;
            lines[2] = "0";

            for(int i = 3; i < lines.length; i++) {
                if(fileInput.hasNextLine()) lines[i] = fileInput.nextLine();
                else lines[i] = "Error";
            }

            fileInput.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        //write
        PrintWriter inputWriter;

        try {
            inputWriter = new PrintWriter(volunteerInput);

            for(int i = 0; i < lines.length; i++) {
                inputWriter.println(lines[i]);
            }

            inputWriter.flush();
            inputWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println("Volunteer added");
    }

    private static void addPatron() {
        Scanner fileInput;
        //the size will be the current patron number times three plus an additional three since we are adding a patron
        String[] lines = new String[volunteers.size() * 3];

        //read
        try {
            fileInput = new Scanner(patronInput);

            System.out.println("Scan the patron's new barcode.");
            int id = parse(textInput.nextLine());

            System.out.println("Enter the patron's name.");
            String name = textInput.nextLine();

            System.out.println("Enter the patron's family size.");
            int familySize = parse(textInput.nextLine());

            patrons.addFirst(new Patron(id, name, familySize));

            lines[0] = id + "";
            lines[1] = name;
            lines[2] = familySize + "";

            for(int i = 3; i < lines.length; i++) {
                if(fileInput.hasNextLine()) lines[i] = fileInput.nextLine();
                else lines[i] = "Error";
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        //write
        PrintWriter inputWriter;

        try {
            inputWriter = new PrintWriter(patronInput);

            for(int i = 0; i < lines.length; i++) {
                inputWriter.println(lines[i]);
            }

            //flush and close print writer
            inputWriter.flush();
            inputWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println("Patron added");
    }

    //Utilities
    private static int parse(String data) {
        int i = 0;

        try {
            i = Integer.parseInt(data);
        } catch(NumberFormatException e) {
            throw e;
        }

        return i;
    }

    private static void replaceLine(int lineNumber, String line) {
        Scanner fileInput;
        String[] lines = new String[volunteers.size() * 3];

        try {
            fileInput = new Scanner(volunteerInput);

            for(int i = 1; i < lines.length + 1; i++) {
                if(i == lineNumber) {
                    lines[i - 1] = line;
                    //advance past skipped line
                    fileInput.nextLine();
                } else lines[i - 1] = fileInput.nextLine();
            }

            fileInput.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        PrintWriter inputWriter;
        try {
            inputWriter = new PrintWriter(volunteerInput);

            for(int i = 0; i < lines.length; i++) {
                inputWriter.println(lines[i]);
            }

            inputWriter.flush();
            inputWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void signOutAll() {
        for(Volunteer volunteer : volunteers) {
            if(volunteer.signedIn) {
                //sign out
                volunteer.signOut();

                //update input for individual total time
                replaceLine((i + 1) * 3, volunteer.totalSeconds + "");

                //write data to the output file
                outputWriter.println(volunteer.name + " worked " + volunteer.sessionTime.format());
                outputWriter.println("Total time worked is " + new VolunteerTime(totalTime).format());
                outputWriter.println();
                outputWriter.flush();
            }
        }

        outputWriter.println(totalPatrons + " patrons visited.");

        outputWriter.close();
    }

    private static void createReport() {
        try {
            String fileName = "/home/chris/IdeaProjects/ClothesCloset/src/com/clothescloset/signin/Report.txt";
            int counter = 1;

            while(true) {
                if(newFile(fileName).exists()) {
                    fileName = "/home/chris/IdeaProjects/ClothesCloset/src/com/clothescloset/signin/Report_" + counter + ".txt";
                    counter++;
                } else break;
            }

            File reportFile = new File(fileName);
            OutputWriter reportWriter =  new OutputWriter(reportFile);

            for(Volunteer volunteer : volunteers) {
                volunteer.totalTime = new VolunteerTime(volunteer.totalSeconds);
                reportWriter.println(volunteer.name + " has worked a total of " + volunteer.totalTime.format());
                reportWriter.println();
            }

            reportWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //This function exports all output files to an inserted drive and then exits
    private static void export() {
        System.out.println("Exporting");

        File newDir = new File("/home/chris/OutputTest");
        System.out.println(newDir.getAbsolutePath());

        signOutAll();
        createReport();

        if(newDir.exists()) {
            System.out.println("IF");
            File currentDir = new File("/home/chris/IdeaProjects/ClothesCloset/src/com/clothescloset/signin/");

            File[] outputFiles = currentDir.listFiles((dir, name) -> {
                if(name.startsWith("Output")) return true;
                return false;
            });

            for(File file : outputFiles) {
                try {
                    System.out.println("Exporting " + file.getName());

                    Path source = Paths.get(file.getAbsolutePath());
                    Path target = Paths.get(newDir.getAbsolutePath() + "/" + file.getName());

                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            File[] reports = currentDir.listFiles((dir, name) -> {
                if(name.startsWith("Report")) return true;
                else return false;
            });

            for(File file : reports) {
                try {
                    System.out.println("Exporting " + file.getName());

                    Path source = Paths.get(file.getAbsolutePath());
                    Path target = Paths.get(newDir.getAbsolutePath() + "/" + file.getName());

                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No drive found");
        }

        System.exit(0);
    }

    //Cleans up open processes and exits
    private static void exit() {
        signOutAll();

        System.out.println("Exiting");
        System.exit(0);
    }
}
