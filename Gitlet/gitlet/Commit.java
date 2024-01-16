package gitlet;
import java.io.Serializable;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;

/** The saved contents of entire directories of files.
 *  @author Grace Lei
 */
public class Commit implements Serializable {

    /** User input message of the commit. */
    private String commitMsg;

    /** When commit was made. */
    private String commitTime;

    /** Where the commit is stored in .gitlet. */
    private File commitDir;

    /** The commit made previous to current commit. */
    private Commit parentCommit;

    /** SHA-1 ID of the commit. */
    private String commitId;

    /** Map from the name of each file to its own SHA-1 ID.
     * Inherits from parent by default.
     * Updates iff file is different from parent commit's version
     * (parent's fileNameTOID). */
    private HashMap<String, String> fileNameToID = new HashMap<>();

    /** Map from the name of every committed file to its commit
     *  ID (including dir).  */
    private HashMap<String, String> committedFilesToId = new HashMap<>();


    /** Indicates whether the commit is caused after a merging conflict. */
    private boolean isMergingCommit;

    /** second parent commit for merging commit. */
    private Commit mergingParent;

    /** Return commitId. */
    String getCommitId() {
        return commitId;
    }

    /** Return the keys of committedFilesToId. */
    Set<String> getCommittedFiles() {
        return committedFilesToId.keySet();
    }

    /** Return commitMsg. */
    String getCommitMsg() {
        return commitMsg;
    }

    /** Return commitTime. */
    String getCommitTime() {
        return commitTime;
    }

    /** Return fileNameToID. */
    HashMap<String, String> getFileNameToID() {
        return fileNameToID;
    }

    /** Return parent commit. */
    Commit getParentCommit() {
        return parentCommit;
    }

    /** Return merging parent. */
    Commit getMergingParent() {
        return mergingParent;
    }

    /** Set parent commit, for testing purpose.
     * @param parentC parent commit
     * */
    void setParentCommit(Commit parentC) {
        parentCommit = parentC;
    }

    /** Set merging parent commit, for testing purpose.
     * @param mergingP merging parent. */
    void setMergingParent(Commit mergingP) {
        mergingParent = mergingP;
    }

    /** Normal commit constructor.
     * @param commitM user input commit msg
     * @param snapShot current staging area. */
    Commit(String commitM, StagingArea snapShot) {
        setMsgAndTime(commitM);
        if (snapShot == null) {
            commitId = Utils.sha1(getCommitTime(), getCommitMsg());
        } else {
            parentCommit = snapShot.getCurrCommit();
            fileNameToID.putAll(parentCommit.fileNameToID);
            committedFilesToId.putAll(parentCommit.committedFilesToId);
            LinkedList<String> modifiedOrNewFiles = changed(snapShot);

            commitId = Utils.sha1(getCommitTime(), getCommitMsg(),
                    fileNameToID.values().toString(),
                    getParentCommit().getCommitId());
            for (String fileName : modifiedOrNewFiles) {
                committedFilesToId.put(fileName, commitId);
            }

        }
        if (!changedCommit()) {
            Main.exitWithError("No changes added to the commit.");
        }
        commitDir = Utils.join(Main.GITLET_FOLDER, getCommitId());
        writeFiles();
    }

    /** Set the commit message and time.
     * @param commitM user input commit msg. */
    private void setMsgAndTime(String commitM) {
        this.commitMsg = commitM;
        Date dateTime = new Date();
        SimpleDateFormat t = new SimpleDateFormat(
                "EEE MMM d HH:mm:ss yyyy Z");
        this.commitTime = t.format(dateTime);
    }

    /** Whether the staging area has changed.
     * @param snapShot current staging area.
     * @return Whether the staging area has changed. */
    private LinkedList<String> changed(StagingArea snapShot) {
        for (String fileName : snapShot.getFilesToBeRemoved()) {
            if (committedFilesToId.containsKey(fileName)) {
                committedFilesToId.remove(fileName);
                fileNameToID.remove(fileName);
            }
        }
        LinkedList<String> modifiedOrNewFiles = new LinkedList<>();
        for (String fileName : snapShot.getFilesInStaging()) {
            String currBlobId = Utils.sha1(Utils.readContentsAsString(
                    new File(fileName)));
            if (fileNameToID.containsKey(fileName)) {
                if (!fileNameToID.get(fileName).equals(currBlobId)) {
                    fileNameToID.put(fileName, currBlobId);
                    modifiedOrNewFiles.add(fileName);
                }
            } else {
                fileNameToID.put(fileName, currBlobId);
                modifiedOrNewFiles.add(fileName);
            }
        }
        return modifiedOrNewFiles;
    }

    /** Whether the commit has changed compared to last commit.
     * @return whether the commit has changed. */
    private boolean changedCommit() {
        if (parentCommit == null) {
            return true;
        }
        if (getCommittedFiles().size() != parentCommit.
                getCommittedFiles().size()) {
            return true;
        }
        for (String fileName : getCommittedFiles()) {
            if (!parentCommit.getCommittedFiles().contains(fileName)) {
                return true;
            } else if (!parentCommit.getFileNameToID().get(fileName).
                    equals(fileNameToID.get(fileName))) {
                return true;
            }
        }
        return false;
    }

    /** Constructor for merge commit.
     * @param commitM user input commit msg.
     * @param snapShot current staging area.
     * @param mergingP merged in branch head. */
    Commit(String commitM, StagingArea snapShot, Commit mergingP) {
        setMsgAndTime(commitM);
        parentCommit = snapShot.getCurrCommit();
        this.mergingParent = mergingP;
        isMergingCommit = true;

        fileNameToID.putAll(parentCommit.fileNameToID);
        fileNameToID.putAll(mergingP.fileNameToID);

        committedFilesToId.putAll(parentCommit.committedFilesToId);
        committedFilesToId.putAll(mergingP.committedFilesToId);

        LinkedList<String> modifiedOrNewFiles = changed(snapShot);

        commitId = Utils.sha1(getCommitTime(), getCommitMsg(),
                fileNameToID.values().toString(),
                getParentCommit().getCommitId(), mergingP.getCommitId());
        for (String fileName : modifiedOrNewFiles) {
            committedFilesToId.put(fileName, commitId);
        }
        commitDir = Utils.join(Main.GITLET_FOLDER, getCommitId());
        writeFiles();

    }

    /** Stores the file in .gitlet. */
    private void writeFiles() {
        commitDir.mkdirs();
        for (String fileName : getCommittedFiles()) {
            File file = new File(fileName);
            if (file.exists()) {
                String blobContent = Utils.readContentsAsString(file);
                File fileDir = Utils.join(commitDir, fileNameToID.
                        get(fileName));
                Utils.writeContents(fileDir, blobContent);
            }
        }

    }


    /** Takes the version of the file as it exists in the commit
     * with the given id, and puts it in the working directory.
     *  @param fileName the name of the file being checkouted out. */
    void checkoutFile(String fileName) {
        File file = new File(fileName);
        File dir = Utils.join(commitDir, fileNameToID.get(fileName));
        Utils.writeContents(file, Utils.readContentsAsString(dir));
    }

    /** Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory. */
    void checkOutAllFiles() {
        for (String fileName : getCommittedFiles()) {
            checkoutFile(fileName);
        }
    }

    /** Whether the commit contains the file.
     * @param fileName the name of the file.
     * @return whether the commit is tracking the file. */
    boolean trackingFile(String fileName) {
        return !committedFilesToId.isEmpty() && getCommittedFiles()
                .contains(fileName);
    }

    /** Whether file content changes from OTHER commit.
     * @param fileName the name of the file
     * @param other the commit to check with.
     * @return whether the file has the same content compared to other. */
    boolean fileSameContent(String fileName, Commit other) {
        String fildIdAtOther = other.getFileNameToID().get(fileName);
        String fileIdAtHead = getFileNameToID().get(fileName);
        return fileIdAtHead.equals(fildIdAtOther);
    }

    /** Replace the fileName with the following content if the
     * commit is in conflict.
     * @param fileName the name of the file
     * @param givenHead the head of the given branch.
     * @return merge conflicting file replaced content. */
    String mergeConflict(String fileName, Commit givenHead) {
        StringBuilder sb = new StringBuilder();
        String contentsInCurr = "";
        String contentsInGiven = "";
        if (getCommittedFiles().contains(fileName)) {
            contentsInCurr = Utils.readContentsAsString(getFileDir(fileName));
        }
        if (givenHead.getCommittedFiles().contains(fileName)) {
            contentsInGiven = Utils.readContentsAsString(givenHead.
                    getFileDir(fileName));
        }
        sb.append("<<<<<<< HEAD\n");
        sb.append(contentsInCurr);

        sb.append("=======\n");
        sb.append(contentsInGiven);

        sb.append(">>>>>>>\n");
        return sb.toString();
    }

    /** Returns the File address of where the file is stored.
     * @param fileName given file name. */
    private File getFileDir(String fileName) {
        return Utils.join(commitDir,
                fileNameToID.get(fileName));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== \n" + "commit " + commitId);
        if (isMergingCommit) {
            sb.append("\nMerge: " + parentCommit.getCommitId().
                    substring(0, 7) + " "
                    + mergingParent.getCommitId().substring(0, 7));
        }
        sb.append("\nDate: " + commitTime + " \n" + commitMsg);
        return sb.toString();
    }
}
