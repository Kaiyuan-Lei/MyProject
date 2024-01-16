package gitlet;

import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Grace Lei
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void commitTest() {
        Commit com = new Commit("first", null);
        assertEquals("first", com.getCommitMsg());
        assertNull(null, com.getParentCommit());
        assertEquals(0, com.getFileNameToID().size());
        assertEquals(0, com.getCommittedFiles().size());
    }

    @Test
    public void branchTest() {
        Commit com = new Commit("second", null);
        Branch b = new Branch("b", com);
        assertEquals("b", b.getBranchName());
        assertEquals(com, b.getCurrHeadNode());
    }

    @Test
    public void stagingAreaTest() {
        Commit com = new Commit("third", null);
        StagingArea stage = new StagingArea(com);
        assertEquals(com, stage.getCurrCommit());
        assertEquals(0, stage.getFilesInStaging().size());
        assertEquals(0, stage.getFilesNewInStaging().size());
    }

    @Test
    public void repositoryTest() {
        Repository initialized = Repository.init();
        Branch master = initialized.getHead();
        Commit initialCommit = master.getCurrHeadNode();
        assertEquals("master", master.getBranchName());
        assertEquals("initial commit", master.getCurrHeadNode().getCommitMsg());
        assertEquals(initialCommit, initialized.getIdToCommit().
                get(initialCommit.getCommitId()));
        assertEquals(initialCommit.getCommitId(),
                initialized.getMsgToIds().get("initial commit").get(0));
        assertEquals(master, initialized.getBranchNameToBranch().get("master"));
        assertEquals(0, initialized.getFilesBeenRemoved().size());
    }

//    @Test
//    public void ancestorRegularTest() {
//        Commit c1 = new Commit("c1", null);
//        Commit c2 = new Commit("c2", null);
//        c2.setParentCommit(c1);
//        Commit c3 = new Commit("c3", null);
//        Commit c4 = new Commit("c4", null);
//        c3.setParentCommit(c4);
//        Commit c0 = new Commit("c0", null);
//        c1.setParentCommit(c0);
//        c4.setParentCommit(c0);
//        assertEquals(c0, Branch.splitPoint(c2, c4));
//
//    }

//    @Test
//    public void mergingAncestorTest() {
//        Commit c1 = new Commit("c1", null);
//        Commit c2 = new Commit("c2", null);
//        Commit c3 = new Commit("c3", null);
//        Commit c4 = new Commit("c4", null);
//        Commit c5 = new Commit("c5", null);
//        Commit c6 = new Commit("c6", null);
//        c2.setParentCommit(c1);
//        c3.setParentCommit(c2);
//        c5.setParentCommit(c4);
//        c4.setParentCommit(c1);
//        c5.setMergingParent(c2);
//        c6.setParentCommit(c5);
//        assertEquals(c2, Branch.splitPoint(c6, c3));
//    }

    @Test
    public void testCommitId() {
        Repository initialized = Repository.init();
        Branch master = initialized.getHead();
        Commit initialCommit = master.getCurrHeadNode();
        assertEquals(initialCommit.getCommitId(),
                initialized.toFullId(initialCommit.
                        getCommitId().substring(0, 10)));
    }
}


