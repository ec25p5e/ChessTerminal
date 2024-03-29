package presentation;

import chess.ChessGame;
import chess.ChessPiece;
import storage.FileManager;
import utils.ChessUtils;
import utils.Constants;

import java.util.*;

/**
 * This class totally handles terminal events.
 */
@SuppressWarnings("all")
public class TerminalManager {
    private static FileManager fileManager = new FileManager();
    private static final Scanner scn = new Scanner(System.in);

    /**
     * This method in addition to being the first one called at program execution deals with user interactions.
     */
    public static void init() {
        System.out.print(ChessUtils.ANSI_BLUE);
        System.out.printf("Informazioni:\n" +
                "Assieme al codice è presente un file json chiamato '%s' Questo file è il file di lettura di esempio per poter verificare il funzionamento.\n" +
                "Di default il programma cerca questo file con questo nome nel disco '%s'.\n" +
                "Invece se richiesto di eseguire la scrittura di default il programma crea un file chiamato '%s' nel disco '%s'.\n",
                Constants.GENERIC_FILE_READ, Constants.GENERIC_DIRECTORY, Constants.GENERIC_FILE_WRITE, Constants.GENERIC_DIRECTORY);
        System.out.print("\nVuoi iniziare? (si/no)? ");
        String choose = scn.nextLine();

        if(checkChoose(choose)) {
            System.out.print(ChessUtils.ANSI_YELLOW);

            System.out.print("Vuoi dover impostare manualmente il gioco? ");
            choose = scn.nextLine();

            if(checkChoose(choose)) {
                System.out.println("In caso un paramtero si voglia lasciare a default utilizzare il carattere '-'");
                String directory = validateInput("Directory: ", Constants.GENERIC_DIRECTORY);
                String read = validateInput("Nome file di lettura: ", Constants.GENERIC_FILE_READ);
                String write = validateInput("Nome file di scrittura: ", Constants.GENERIC_FILE_WRITE);

                fileManager = new FileManager(directory, read, write);
            }

            do {
                System.out.printf("Hai inserito il file '%s' nel percorso '%s' ? ", fileManager.getFileReadName(), fileManager.getDirectory());
                choose = scn.next();
            } while(!(checkChoose(choose) && fileManager.existFile(fileManager.getReadSource()) && !fileManager.isEmptyFile(fileManager.getReadSource())));

            ChessGame chessGame = new ChessGame(fileManager);

            System.out.print(ChessUtils.ANSI_CYAN);
            System.out.print("Vuoi rappresentare la scacchiera in formato tabellare o ASCII (1/2): ");
            String chooseInt = scn.next();

            System.out.print(ChessUtils.ANSI_RESET);
            clearTerminal();

            if(chooseInt.equals("1")) {
                printTable(chessGame.getPieces());
            } else {
                printBoard(chessGame.getPieces());

                System.out.print("\nVuoi eseguire il test di scrittura del file? ");
                if(checkChoose(scn.next())) {
                    chessGame.testWritingFile();
                }
            }
        } else {
            System.out.close();
            clearTerminal();
        }
    }

    /**
     * This method takes care of printing the list of all the checkers on the board. It shows the Unicode character, the extended name and the coordinates.
     * @param pieces two-dimensional array which contains all the tokens from which you can extract the necessary information.
     */
    private static void printTable(ChessPiece[][] pieces) {
        for (ChessPiece[] piece : pieces) {
            for (int k = 0; k < pieces.length; k++) {
                if (piece[k] != null) {
                    System.out.printf("%s - %s ==> %s%s %n", piece[k], piece[k].getPieceName(), piece[k].getChessCoordinate().getColumn(), piece[k].getChessCoordinate().getRow());
                }
            }
        }
    }

    /**
     * This method is used whether you want to show the chessboard in ASCII or Unicode format.
     * @param pieces two-dimensional array containing a matrix on the basis of which the chessboard will be created in "graphical" format.
     * @param mode this parameter is used to figure out what kind of layout/presentation to set.
     */
    private static void printBoard(ChessPiece[][] pieces) {
        for(int i = 0; i < pieces.length; i++) {
            System.out.print((8 - i) + "\s");

            for(int k = 0; k < pieces.length; k++) {
                printPieceAscii(pieces[i][k]);
            }

            System.out.println();
        }

        System.out.println("  A B C D E F G H");
    }

    /**
     * This method is used to represent and color the provided token cell. It differs from the "printPieceUnicode" method simply because instead of invoking the "toString()" of the chunk, the method is called to return the ascii character based on its number.
     * @param piece object which can represent all the existing token types in the game.
     */
    private static void printPieceAscii(ChessPiece piece) {
        if(piece != null) {
            if(piece.getColor() == ChessUtils.YELLOW) {
                System.out.print(ChessUtils.ANSI_YELLOW + piece + ChessUtils.ANSI_RESET );
            } else {
                System.out.print(ChessUtils.ANSI_WHITE + piece + ChessUtils.ANSI_RESET);
            }
        } else {
            System.out.print(ChessUtils.ANSI_PURPLE + ChessUtils.convertToAscii(88) + ChessUtils.ANSI_RESET);
        }

        System.out.print(" ");
    }

    /**
     * This method is used to request a piece of information from the user and not stop until the user has provided a correct value. If the "-" character is supplied, the parameter will be set to the default value supplied.
     * @param message message to print
     * @param defaultValue this parameter is used in case you are told to set the default value.
     * @return the supplied value or the default value is returned.
     */
    private static String validateInput(String message, String defaultValue) {
        String variable;

        do {
            System.out.print(message);
            variable = scn.nextLine();
        } while(variable.equals(""));

        return (variable.equals("-") ? defaultValue : variable);
    }

    /**
     * This method is used to empty the stream of bytes in memory, so as to lighten the console (System.out.flush()).
     */
    private static void clearTerminal(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * This method is used to test questions to which the answer can be yes/no. In addition, multiple affirmative and negative responses are accepted.
     * @param choose response provided by the user
     * @return returns true in case of a "yes" and false in case of a "no".
     */
    private static boolean checkChoose(String choose) {
        return choose.equals("si") || choose.equals("s") || choose.equals("yes") || choose.equals("y");
    }
}
