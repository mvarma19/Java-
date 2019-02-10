/**
 * Classical: Hello, World
 *
 *
 * @version   $Id: Hello.java,v 1.3 2017/06/06 23:05:46 hpb Exp hpb $
 *
 * @author    hpb
 *
 * Revisions:
 *
 *	Revision 1.41  2017/06/06 16:19:12  hpb
 *	Initial revision
 *
 */
class Hello {
    public static void main (String args []) {
        double aDouble = 123456.780;
        String format = "%,15.2f\n";
        System.out.println("Hello World");
        System.out.printf("%-15.5s\n", "Hello World");
        System.out.printf("%17.5s\n", "Hello");
        System.out.printf("%,15.2f\n", aDouble);
        System.out.printf(format, aDouble);
    }
}
