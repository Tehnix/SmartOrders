package kaist.restaurantownerapp.communication;


/*
 * Return two values, with Left usually indicating an error value and
 * Right usually indicating a successful result.
 *
 * Inspired by Haskell's Either typeclass, which means that either may contain null since that is
 * not disallowed.
 */
public class Either<Left, Right> {

    private boolean isLeft = false;

    private boolean isRight = false;

    private Left left = null;

    private Right right = null;

    /*
     * Internal constructor for the class.
     */
    private Either(Left l, Right r, boolean isL, boolean isR) {
        isLeft = isL;
        isRight = isR;
        left = l;
        right = r;
    }

    /*
     * If the either holds a value in Left, then Right is null and isRight is false. That is, there
     * is only allowed one or the other.
     */
    public static <Left, Right> Either<Left, Right> left(Left l) {
        return new Either<Left, Right>(l, null, true, false);
    }

    /*
     * If the either holds a value in Right, then Left is null and isLeft is false. That is, there
     * is only allowed one or the other.
     */
    public static <Left, Right> Either<Left, Right> right(Right r) {
        return new Either<Left, Right>(null, r, false, true);
    }

    /*
     * Return the value in left.
     */
    public Left left() {
        return left;
    }

    /*
     * Alternative method to return the value in left.
     */
    public Left error() {
        return left;
    }

    /*
     * Return the value in right.
     */
    public Right right() {
        return right;
    }

    /*
     * Alternative method to return the value in right.
     */
    public Right success() {
        return right;
    }

    /*
     * Check if the value is a left.
     */
    public boolean isLeft() {
        return isLeft;
    }

    /*
     * Alternative method to check if the value is a left.
     */
    public boolean isError() {
        return isLeft();
    }

    /*
     * Check if the value is a right.
     */
    public boolean isRight() {
        return isRight;
    }

    /*
     * Alternative method to check if the value is a right.
     */
    public boolean isSuccessful() {
        return isRight();
    }

}
