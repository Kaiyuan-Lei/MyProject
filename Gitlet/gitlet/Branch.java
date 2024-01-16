package gitlet;
import java.io.Serializable;

import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.io.File;

/** Branch in the commit tree.
 *  @author Grace Lei
 */

class Branch implements Serializable {

    /** The name of the branch. */
    private String branchName;

    /** The head node of the branch. */
    private Commit currHeadNode;

    /** The staging area. */
    private StagingArea stagingArea;

    /** Constructor.
     * @param branchN branch name.
     * @param currHeadN head of the curr branch. */
    Branch(String branchN, Commit currHeadN) {
        stagingArea = new StagingArea(currHeadN);
        this.branchName = branchN;
        this.currHeadNode = currHeadN;
    }

    /** Returns currHeadNode. */
    Commit getCurrHeadNode() {
        return currHeadNode;
    }

    /** Returns branchName. */
    String getBranchName() {
        return branchName;
    }

    /** Returns stagingArea. */
    StagingArea getStagingArea() {
        return stagingArea;
    }

    /** Sets staging area of the branch.
     * @param stagingA current staging area. */
    void setStagingArea(StagingArea stagingA) {
        this.stagingArea = stagingA;
    }

    /** Sets currHeadNode of the branch.
     * @param currHeadN head node of the current branch. */
    void setCurrHeadNode(Commit currHeadN) {
        this.currHeadNode = currHeadN;
    }

    /** Adds file to staging area.
     * @param fileName name of the added file. */
    void stageFile(String fileName) {
        stagingArea.stageFile(fileName);
    }


    /** Makes a commit for files in current staging area.
     * @param commitMsg input message for commit. */
    void commit(String commitMsg) {
        currHeadNode = new Commit(commitMsg, stagingArea);
        stagingArea = new StagingArea(currHeadNode);
    }

    /** Makes a commit for files in current staging area.
     * @param commitMsg input message for commit.
     * @param mergingParent merged in branch. */
    private void commit(String commitMsg, Commit mergingParent) {
        currHeadNode = new Commit(commitMsg, stagingArea, mergingParent);
        stagingArea = new StagingArea(currHeadNode);
    }

    /** Removes the file in staging area.
     * @param fileName name of the file */
    void rm(String fileName) {
        stagingArea.rm(fileName);
    }


    /** Merges files from the given branch into the current branch.
     * @param givenBranch merging branch. */
    void merge(Branch givenBranch) {
        Commit currHead = getCurrHeadNode();
        Commit givenHead = givenBranch.getCurrHeadNode();
        Commit splitPoint = splitPoint(currHead, givenHead);
        if (splitPoint == givenHead) {
            Main.exitWithError("Given branch is an ancestor "
                    + "of the current branch.");
        }
        if (splitPoint == currHead) {
            setCurrHeadNode(givenHead);
            Main.exitWithError("Current branch fast-forwarded.");
        }
        Set<String> currBranchCommittedFiles = currHead.getCommittedFiles();
        Set<String> givenBranchCommittedFiles = givenHead.getCommittedFiles();
        Set<String> splitPointCommittedFiles = splitPoint.getCommittedFiles();
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(currBranchCommittedFiles);
        allFiles.addAll(givenBranchCommittedFiles);
        allFiles.addAll(splitPointCommittedFiles);
        boolean mergeConflict = false;
        mergeConflict = mergeCases(allFiles, currBranchCommittedFiles,
                givenBranchCommittedFiles,
                splitPointCommittedFiles, currHead, givenHead,
                splitPoint, mergeConflict);

        mergeCommit(mergeConflict, givenBranch, givenHead);
    }

    /** Driver method for different merge cases.
     * @param allFiles all committed files.
     * @param currBranchCommittedFiles committed files under curr branch.
     * @param givenBranchCommittedFiles committed files under given branch.
     * @param splitPointCommittedFiles committed files under split point.
     * @param currHead head of curr branch
     * @param givenHead head of given branch
     * @param splitPoint split point of given and curr branch
     * @param mergeConflict wether there is a merge conflict.
     * @return mergeConflict*/
    private boolean mergeCases(Set<String> allFiles,
                               Set<String> currBranchCommittedFiles,
                               Set<String> givenBranchCommittedFiles,
                               Set<String> splitPointCommittedFiles,
                               Commit currHead, Commit givenHead,
                               Commit splitPoint, boolean mergeConflict) {
        for (String fileName : allFiles) {
            if (!splitPointCommittedFiles.contains(fileName)) {
                if (currBranchCommittedFiles.contains(fileName)
                        && !givenBranchCommittedFiles.contains(fileName)) {
                    continue;
                } else if (!currBranchCommittedFiles.contains(fileName)
                        && givenBranchCommittedFiles.contains(fileName)) {
                    givenHead.checkoutFile(fileName);
                    stageFile(fileName);
                } else if (!currHead.fileSameContent(fileName, givenHead)) {
                    String res = currHead.mergeConflict(fileName, givenHead);
                    writeConflictFile(new File(fileName), res, fileName);
                    mergeConflict = true;
                }
            } else {
                if (!currBranchCommittedFiles.contains(fileName)
                        && !givenBranchCommittedFiles.contains(fileName)) {
                    continue;
                } else if (!currBranchCommittedFiles.contains(fileName)) {
                    if (givenHead.fileSameContent(fileName, splitPoint)) {
                        continue;
                    } else {
                        String res = currHead.mergeConflict(fileName,
                                givenHead);
                        writeConflictFile(new File(fileName), res, fileName);
                        mergeConflict = true;
                    }
                } else if (!givenBranchCommittedFiles.contains(fileName)) {
                    if (currHead.fileSameContent(fileName, splitPoint)) {
                        rm(fileName);
                    } else {
                        String res = currHead.mergeConflict(fileName,
                                givenHead);
                        writeConflictFile(new File(fileName), res, fileName);
                        mergeConflict = true;
                    }
                } else if (!givenHead.fileSameContent(fileName, splitPoint)
                        && currHead.fileSameContent(fileName, splitPoint)) {
                    givenHead.checkoutFile(fileName);
                    stageFile(fileName);
                } else if (!currHead.fileSameContent(fileName, splitPoint)
                        && givenHead.fileSameContent(fileName, splitPoint)) {
                    continue;
                } else if (currHead.fileSameContent(fileName, givenHead)) {
                    continue;
                } else {
                    String res = currHead.mergeConflict(fileName, givenHead);
                    writeConflictFile(new File(fileName), res, fileName);
                    mergeConflict = true;
                }
            }
        }
        return mergeConflict;
    }

    /** Replace the files in conflict with specified content.
     * @param writeTo where to write
     * @param res replacing content
     * @param fileName fileName */
    private void writeConflictFile(File writeTo, String res, String fileName) {
        Utils.writeContents(writeTo, res);
        stageFile(fileName);
    }

    /** Commit for merge.
     * @param mergeConflict whether the merge is in conflict
     * @param givenBranch given branch
     * @param givenHead the head of the given branch. */
    private void mergeCommit(boolean mergeConflict, Branch givenBranch,
                             Commit givenHead) {
        commit("Merged " + givenBranch.getBranchName()
                + " into " + getBranchName() + ".", givenHead);
        if (mergeConflict) {
            System.out.println("Encountered a merge conflict.");
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

}
