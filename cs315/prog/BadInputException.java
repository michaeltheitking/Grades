package cs315.prog;

/**
 * This class makes a custom bad input exception for reading files.
 * @author michael king and michael fradkin
 * @version 1.0
 */

public class BadInputException extends java.lang.RuntimeException
{
    /**
     * Constructor for BadInputException <BR>
     * post: the exception is printed out. <BR>
     * complexity: Big O(1) constant <BR>
     * memory usage: Big O(1) constant <BR>
     */
	public BadInputException() {

		System.out.println("The input file you used contains bad data and cannot be processed.");
	}

    /**
     * Constructor for BadInputException <BR>
     * post: the exception is printed out. <BR>
     * complexity: Big O(1) constant <BR>
     * memory usage: Big O(1) constant <BR>
     * @param file is the filename of the bad file.
     */
	public BadInputException(String file) {

		System.out.println("The input file you used (" + file + ") contains bad data and cannot be processed.");
	}

}