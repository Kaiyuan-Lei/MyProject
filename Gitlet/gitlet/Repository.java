package gitlet;
import java.io.Serializable;

import java.util.*;
import java.io.File;


/** The commit graph structure.
 *  @author Grace Lei
 */

public class Repository implements Serializable {

    /** A pointer to the current branch.*/
    private Branch head;

    /** Map from each SHA-1 ID to its commit.*/
    private HashMap<String, Commit> idToCommit = new HashMap<>();

    /** Map from complete ID to abbreviated 6 digit ID. */
    private HashMap<String, String> abbrevIdToId = new HashMap<>();

    /** Map from commit msg to commit ids with that message. */
    private HashMap<String, LinkedList<String>> msgToIds = new HashMap<>();

    /** Map from branch name to branch. */
    private HashMap<String, Branch> branchNameToBranch = new HashMap<>();

    /** A list of files have been removed. */
    private LinkedList<String> filesBeenRemoved = new LinkedList<>();

    /** Return head. */
    Branch getHead() {
        return head;
    }

    /** Return idToCommit. */
    HashMap<String, Commit> getIdToCommit() {
        return idToCommit;
    }

    /** Return msgToIds. */
    HashMap<String, LinkedList<String>> getMsgToIds() {
        return msgToIds;
    }

    /** Return branchNameToBranch. */
    HashMap<String, Branch> getBranchNameToBranch() {
        return branchNameToBranch;
    }

    /** Return filesBeenRemoved. */
    LinkedList<String> getFilesBeenRemoved() {
        return filesBeenRemoved;
    }

    /** Creates a new Gitlet version-control system in the
     * current directory.
     * @return initialized repository. */
    static Repository init() {
        Repository repo = new Repository();
        String commitMsg = "initial commit";
        String branchName = "master";
        Commit initCommit = new Commit(commitMsg, null);
        String commitId = initCommit.getCommitId();
        Branch initBranch = new Branch(branchName, initCommit);
        repo.head = initBranch;
        repo.idToCommit.put(commitId, initCommit);
        repo.abbrevIdToId.put(commitId.substring(0, 6), commitId);
        LinkedList<String> msgList = new LinkedList<>();
        msgList.add(commitId);
        repo.msgToIds.put(commitMsg, msgList);
        repo.branchNameToBranch.put(branchName, initBranch);
        return repo;
    }


    /** Adds a copy of the file as it currently exists to the
     * staging area.
     * @param fileName the name of added file. */
    void add(String fileName) {
        head.stageFile(fileName);
        if (filesBeenRemoved.contains(fileName)) {
            filesBeenRemoved.remove(fileName);
        }
    }

    /** Saves a snapshot of certain files in the current commit and
     * staging area so they can be restored at a later time,
     * creating a new commit.
     * @param commitMsg user input commit message. */
    void commit(String commitMsg) {
        if (commitMsg.length() == 0) {
            Main.exitWithError("Please enter a commit message.");
        }
        head.commit(commitMsg);
        commitSetting(commitMsg);
    }

    /** Set up information for commit.
     * @param commitMsg user input commit msg. */
    private void commitSetting(String commitMsg) {
        Commit currHeadNode = head.getCurrHeadNode();
        idToCommit.put(currHeadNode.getCommitId(), currHeadNode);
        abbrevIdToId.put(currHeadNode.getCommitId().substring(0, 6),
                currHeadNode.getCommitId());
        if (msgToIds.get(commitMsg) != null) {
            msgToIds.get(commitMsg).add(currHeadNode.getCommitId());
        } else {
            LinkedList<String> msgList = new LinkedList<>();
            msgList.add(currHeadNode.getCommitId());
            msgToIds.put(commitMsg, msgList);
        }
    }

    /** Unstage the file if it is currently staged. If the file is
     * tracked in the current commit, mark it to indicate that it
     * is not to be included in the next commit.
     * @param fileName name of the removing file. */
    void rm(String fileName) {
        head.rm(fileName);
        filesBeenRemoved.add(fileName);
    }

    /** Starting at the current head commit, display information
     * about each commit backwards along the commit tree until
     * the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits.*/
    void log() {
        for (Commit curr = head.getCurrHeadNode(); curr != null;
             curr = curr.getParentCommit()) {
            System.out.println(curr + "\n");
        }
    }

    /** Like log, except displays information about all commits ever made. */
    void globalLog() {
        for (Commit c : idToCommit.values()) {
            System.out.println(c + "\n");
        }
    }

    /** Prints out the ids of all commits that have the given commit message,
     * one per line.
     * @param commitMsg user input commit msg. */
    void find(String commitMsg) {
        if (!msgToIds.containsKey(commitMsg)) {
            Main.exitWithError("Found no commit with that message.");
        } else {
            for (String commitId : msgToIds.get(commitMsg)) {
                System.out.println(commitId);
            }
        }
    }

    /** Displays what branches currently exist, and marks the current branch
     * with a *. Also displays what files have been staged or marked for
     * untracking. */
    void status() {
        System.out.println(this);
    }

    /** Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory.
     * @param branchName the name of the checked out branch. */
    void checkoutBranch(String branchName) {
        if (!branchNameToBranch.containsKey(branchName)) {
            Main.exitWithError("No such branch exists.");
        }
        if (head.getBranchName().equals(branchName)) {
            Main.exitWithError("No need to checkout the current branch.");
        }
        Branch checkedOutBranch = branchNameToBranch.get(branchName);
        for (String fileName : checkedOutBranch.getCurrHeadNode()
                .getCommittedFiles()) {
            if (!head.getCurrHeadNode().trackingFile(fileName)
                    && new File(fileName).exists()) {
                Main.exitWithError("There is an untracked file in the way"
                        + "; delete it or add it first.");
            }
        }
        for (String fileName : head.getCurrHeadNode().getCommittedFiles()) {
            if (!checkedOutBranch.getCurrHeadNode().trackingFile(fileName)) {
                Utils.restrictedDelete(new File(fileName));
            }
        }
        head = checkedOutBranch;
        head.getCurrHeadNode().checkOutAllFiles();
    }


    /** Convert input possible abbreviated id to full Id.
     * @param commitId user input id
     * @return full commit id*/
    String toFullId(String commitId) {
        if (commitId.length() == 6) {
            if (abbrevIdToId.containsKey(commitId)) {
                commitId = abbrevIdToId.get(commitId);
            } else {
                Main.exitWithError("No commit with that id exists.");
            }
        } else if (commitId.length() == (4 * 10)) {
            if (!idToCommit.containsKey(commitId)) {
                Main.exitWithError("No commit with that id exists.");
            }
        } else {
            String abbrevId = commitId.substring(0, 6);
            if (abbrevIdToId.containsKey(abbrevId)) {
                if (!abbrevIdToId.get(abbrevId).substring(0, commitId.length())
                        .equals(commitId)) {
                    Main.exitWithError("No commit with that id exists.");
                }
                commitId = abbrevIdToId.get(abbrevId);
            } else {
                Main.exitWithError("No commit with that id exists.");
            }
        }
        return commitId;
    }

    /** Takes the version of the file as it exists in the commit with
     *  the given id, and puts it in the working directory.
     *  @param commitId user input commit id
     *  @param fileName the name of the file being checkout out. */
    void checkoutCommit(String commitId, String fileName) {
        commitId = toFullId(commitId);
        Commit com = idToCommit.get(commitId);
        if (com.trackingFile(fileName)) {
            com.checkoutFile(fileName);
        } else {
            Main.exitWithError("File does not exist in that commit.");
        }
        head.setStagingArea(new StagingArea(com));
    }

    /** Takes the version of the file as it exists in the head commit,
     * the front of the current branch, and puts it in the working
     * directory.
     * @param fileName the name of the file being checkout out. */
    void checkoutFile(String fileName) {
        if (!head.getCurrHeadNode().trackingFile(fileName)) {
            Main.exitWithError("File does not exist in that commit.");
        }
        head.getCurrHeadNode().checkoutFile(fileName);
    }

    /** Creates a new branch with the given name, and points it at
     * the current head node.
     * @param branchName the name of the branch created. */
    void branch(String branchName) {
        if (branchNameToBranch.containsKey(branchName)) {
            Main.exitWithError("A branch with that name already exists.");
        }
        branchNameToBranch.put(branchName,
                new Branch(branchName, head.getCurrHeadNode()));
    }

    /** Deletes the branch with the given name.
     * @param branchName the name of the branch removed. */
    void rmBranch(String branchName) {
        if (!branchNameToBranch.containsKey(branchName)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        if (head.getBranchName().equals(branchName)) {
            Main.exitWithError("Cannot remove the current branch.");
        }
        branchNameToBranch.remove(branchName);
    }

    /** Checks out all the files tracked by the given commit.
     *  Removes tracked files that are not present in that commit.
     *  Also moves the current branch's head to that commit node.
     *  @param commitId the id of the commit being reset to. */
    void reset(String commitId) {
        commitId = toFullId(commitId);
        Commit checkedOutCommit = idToCommit.get(commitId);
        for (String fileName : checkedOutCommit.getCommittedFiles()) {
            if (!head.getCurrHeadNode().trackingFile(fileName)
                    && new File(fileName).exists()) {
                Main.exitWithError("There is an untracked file "
                        + "in the way; delete it or add it first.");
            }
        }
        checkedOutCommit.checkOutAllFiles();
        for (String fileName : head.getCurrHeadNode().getCommittedFiles()) {
            if (!checkedOutCommit.trackingFile(fileName)) {
                rm(fileName);
            }
        }
        head.setCurrHeadNode(checkedOutCommit);
        head.setStagingArea(new StagingArea(checkedOutCommit));
    }

    /** Merges files from the given branch into the current branch.
     * @param branchName the name of the branch being merged into. */
    void merge(String branchName) {
        if (!branchNameToBranch.containsKey(branchName)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        if (head.getBranchName().equals(branchName)) {
            Main.exitWithError("Cannot merge a branch with itself.");
        }
        if (!head.getStagingArea().getFilesNewInStaging().isEmpty()) {
            Main.exitWithError("You have uncommitted changes.");
        }
        Branch mergedInBranch = branchNameToBranch.get(branchName);
        for (String fileName : mergedInBranch.getCurrHeadNode()
                .getCommittedFiles()) {
            File file = new File(fileName);
            if (file.exists() && !head.getCurrHeadNode().trackingFile(fileName)
                    && !mergedInBranch.getCurrHeadNode().getFileNameToID().
                    get(fileName).equals(Utils.sha1(Utils.
                    readContentsAsString(file)))) {
                Main.exitWithError("There is an untracked file "
                            + "in the way; delete it or add it first.");
            }
        }

        Commit currHead = head.getCurrHeadNode();
        Commit givenHead = mergedInBranch.getCurrHeadNode();
        Commit splitPoint = splitPoint(currHead, givenHead);
        if (splitPoint == givenHead) {
            System.out.println("Given branch is an ancestor "
                    + "of the current branch.");
        } else if (splitPoint == currHead) {
            head = mergedInBranch;
            System.out.println("Current branch fast-forwarded.");
        } else {
            head.merge(currHead, givenHead, splitPoint, mergedInBranch);
            Commit mergeCommit = head.getCurrHeadNode();
            commitSetting(mergeCommit.getCommitMsg());
        }
    }

    /** Finds the latest common ancestor of current and given branch heads.
     * @param currHeadN head node of curr branch
     * @param givenHeadNode head node of given branch
     * @return the split point of currHeadNode and givenHeadNode. */
    static Commit splitPoint(Commit currHeadN, Commit givenHeadNode) {
        HashSet<Commit> givenHeadAllAncestors = ancestors(givenHeadNode);
        Queue<Commit> fringe = new LinkedList<>();
        fringe.offer(currHeadN);
        while (!fringe.isEmpty()) {
            Commit com = fringe.poll();
            if (givenHeadAllAncestors.contains(com)) {
                return com;
            }
            if (com.getParentCommit() != null) {
                fringe.offer(com.getParentCommit());
            }
            if (com.getMergingParent() != null) {
                fringe.offer(com.getMergingParent());
            }
        }
        return null;
    }

    /** Finds all ancestors of ME.
     * @return all ancestors of ME.
     * */
    static HashSet<Commit> ancestors(Commit me) {
        HashSet<Commit> ancestors = new HashSet<>();
        Stack<Commit> fringe = new Stack<>();
        fringe.push(me);
        while (!fringe.isEmpty()) {
            Commit com = fringe.pop();
            ancestors.add(com);
            if (com.getParentCommit() != null) {
                fringe.push(com.getParentCommit());
            }
            if (com.getMergingParent() != null) {
                fringe.push(com.getMergingParent());
            }
        }
        return ancestors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Branches === \n" + "*" + head.getBranchName() + "\n");
        for (String name : branchNameToBranch.keySet()) {
            if (!name.equals(head.getBranchName())) {
                sb.append(name + "\n");
            }
        }
        sb.append("\n");
        if (head.getStagingArea() != null) {
            sb.append(head.getStagingArea() + "\n");
        } else {
            sb.append("=== Staged Files === \n \n");
            sb.append("=== Removed Files === \n \n");
        }
        sb.append("=== Modifications Not Staged For Commit === \n");
        sb.append("\n=== Untracked Files === \n");
        return sb.toString();
    }
}
