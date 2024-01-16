package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Grace Lei
 */
public class Main {

    /** Current Working Directory. */
    private static final File CWD = new File(".");

    /** .gitlet folder. */
    static final File GITLET_FOLDER = Utils.join(CWD, ".gitlet");

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
        Repository repo;
        if (args[0].equals("init")) {
            validateNumArgs(args, 1);
            repo = initialize();
        } else {
            repo = deserialize();
            commandMenu(repo, args);
        }
        serialize(repo);
    }

    /** Driver method for commands.
     * @param repo repository
     * @param args user input arguments */
    static void commandMenu(Repository repo, String... args) {
        try {
            switch (args[0]) {
            case "add":
                validateNumArgs(args, 2);
                repo.add(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                repo.commit(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                repo.rm(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                repo.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                repo.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                repo.find(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                repo.status();
                break;
            case "checkout":
                checkout(repo, args);
                break;
            case "branch":
                validateNumArgs(args, 2);
                repo.branch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                repo.rmBranch(args[1]);
                break;
            case "reset":
                validateNumArgs(args, 2);
                repo.reset(args[1]);
                break;
            case "merge":
                validateNumArgs(args, 2);
                repo.merge(args[1]);
                break;
            default:
                exitWithError("No command with that name exists.");
            }
        } catch (ArrayIndexOutOfBoundsException
                | IllegalArgumentException e1) {
            exitWithError("Incorrect operands.");
        } catch (NullPointerException e2) {
            exitWithError("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Start with initial commit.
     * @return return the initialized repo
     */
    private static Repository initialize() {
        if (!GITLET_FOLDER.exists()) {
            GITLET_FOLDER.mkdirs();
        } else {
            exitWithError("A Gitlet version-control system "
                    + "already exists in the current directory.");
        }
        return Repository.init();
    }

    /** Deserialize the commit tree.
     * @return deserizlied repository.*/
    private static Repository deserialize() {
        Repository repo;
        try {
            File inFile = Utils.join(GITLET_FOLDER, "serialize");
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(inFile));
            repo = (Repository) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            repo = null;
        }
        return repo;
    }

    /** Seriealize the commit tree.
     * @param repo the repository being serialized. */
    private static void serialize(Repository repo) {
        if (repo == null) {
            return;
        }
        File outFile = Utils.join(GITLET_FOLDER, "serialize");
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(repo);
            out.close();
        } catch (IOException excp) {
            exitWithError("");
        }
    }

    /** Three kinds of check out commands.
     * @param repo current commit tree
     * @param args input command. */
    private static void checkout(Repository repo, String...args) {
        if (args.length == 2) {
            repo.checkoutBranch(args[1]);
            return;
        } else if (args.length == 3) {
            if (args[1].equals("--")) {
                repo.checkoutFile(args[2]);
                return;
            }
        } else if (args.length == 4) {
            if (args[2].equals("--")) {
                repo.checkoutCommit(args[1], args[3]);
                return;
            }
        }
        exitWithError("Incorrect operands.");
    }

    /**
     * Prints out MESSAGE and exits with error code 0.
     * @param message message to print
     */
    static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            exitWithError("Incorrect operands.");
        }
    }

}
