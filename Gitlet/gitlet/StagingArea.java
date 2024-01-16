package gitlet;
import java.io.Serializable;
import java.io.File;
import java.util.LinkedList;
import java.util.HashMap;

/** The staging area of the gitlet.
 *  @author Grace Lei
 */
class StagingArea implements Serializable {

    /** Last commit. */
    private Commit currCommit;

    /** A list of files already in the staging area waiting for next commit. */
    private LinkedList<String> filesInStaging = new LinkedList<>();

    /** A list of files newly staged waiting for next commit. */
    private LinkedList<String> filesNewInStaging = new LinkedList<>();

    /** A list of files marked to be removed for next commit. */
    private LinkedList<String> filesToBeRemoved = new LinkedList<>();

    /** Map staged files to their id hashing by the content while staging. */
    private HashMap<String, String> filesStagedToStagingContent
            = new HashMap<>();


    /** Constructor.
     * @param lastCom previous commit. */
    StagingArea(Commit lastCom) {
        this.currCommit = lastCom;
        filesInStaging.addAll(filesNewInStaging);
    }

    /** Returns currCommit. */
    Commit getCurrCommit() {
        return currCommit;
    }

    /** Returns filesInStaging. */
    LinkedList<String> getFilesInStaging() {
        return filesInStaging;
    }

    /** Returns filesNewInStaging. */
    LinkedList<String> getFilesNewInStaging() {
        return filesNewInStaging;
    }


    /** Returns filesToBeRemoved. */
    LinkedList<String> getFilesToBeRemoved() {
        return filesToBeRemoved;
    }


    /** Stages the file.
     * @param fileName name of the added file. */
    void stageFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Main.exitWithError("File does not exist.");
        }
        String currFileContent = Utils.readContentsAsString(file);
        String currFileId = Utils.sha1(currFileContent);
        if (currCommit.trackingFile(fileName)) {
            if (currFileId.equals(currCommit.getFileNameToID()
                    .get(fileName))) {
                if (filesNewInStaging.contains(fileName)) {
                    filesNewInStaging.remove(fileName);
                }
            } else {
                filesInStaging.add(fileName);
                filesNewInStaging.add(fileName);
            }
        } else {
            filesInStaging.add(fileName);
            filesNewInStaging.add(fileName);
        }
        if (filesToBeRemoved.contains(fileName)) {
            filesToBeRemoved.remove(fileName);
        }
        filesStagedToStagingContent.put(fileName, currFileContent);
    }

    /** Unstages the file if it is currently staged. If the file is tracked
     * in the current commit, mark it to indicate that it is not to be included
     * in the next commit and remove the file from the working directory
     * if the user has not already done so.
     * @param fileName name of the removing file. */
    void rm(String fileName) {
        if (!(filesInStaging.contains(fileName)
                && filesNewInStaging.contains(fileName))
                && !currCommit.trackingFile(fileName)) {
            Main.exitWithError("No reason to remove the file.");
        } else {
            filesNewInStaging.remove(fileName);
            filesInStaging.remove(fileName);
            if (currCommit.trackingFile(fileName)) {
                Utils.restrictedDelete(fileName);
                filesToBeRemoved.add(fileName);
            }
            filesStagedToStagingContent.remove(fileName);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Staged Files === \n");
        for (String fileName : filesNewInStaging) {
            sb.append(fileName + "\n");
        }
        sb.append("\n=== Removed Files === \n");
        for (String fileName : filesToBeRemoved) {
            sb.append(fileName + "\n");
        }
        return sb.toString();
    }
}
