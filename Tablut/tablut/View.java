package tablut;

/** A view of a Tablut board.
 *  @author P. N. Hilfinger */
interface View {

    /** Update the current view according to the game on CONTROLLER. */
    void update(Controller controller);

}
