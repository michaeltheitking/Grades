package cs315.prog;

/**
 * This class reads in a file and allows reading line by line. Also checks for stream errors.
 * @author michael king and michael fradkin
 * @version 1.0
 */
public class ReadFile
{
    private java.io.FileReader reader;			//FileReader reference for reading files
    private java.io.BufferedReader br;			//Wrapper for FileReader object, to read lines

    /**
     * Constructor for ReadFile. <BR>
     * pre: file is a valid pathname. <BR>
     * post: a file is opened and ready for reading. <BR>
     * complexity: O(1)<BR>
     * memory usage: O(1)<BR>
     * @param file is a pathname to a file.
     */
    public ReadFile (String file) throws java.io.FileNotFoundException
    {
        reader = new java.io.FileReader(file);		//Contruct the file reader
        br = new java.io.BufferedReader(reader);	//Wrap it with a BufferedReader
        assert this.isValid();            			//Make sure all is well and we can read the file
    }

    /**
     * Method to read a file one line at a time. <BR>
     * pre: file is opened
     * post: a line of a file is read and returned. <BR>
     * complexity: O(1)<BR>
     * memory usage: O(1)<BR>
     * @return representation of the next line in a file.
     */
    public String readLine() {

        String temp = null;

        try
        {
            assert this.isValid();			//Test that we can read the file
            temp = br.readLine();			//Read the next line and store it as temp
        }

		//Catch possible exceptions
        catch(java.io.IOException e) {
            System.out.println("Error reading file: " + e );
        }

        return temp;
    }

    /**
     * Method to make sure file is ok to be read. <BR>
     * post: file is checked for validity. <BR>
     * complexity: O(1)<BR>
     * memory usage: O(1)<BR>
     * @return true if file is ready to be read; otherwise throws exception
     * @exception BadInputException
     */
    private boolean isValid() throws BadInputException {
        try {
            if( ! br.ready() ) 						//Is the reader ready?
                throw new BadInputException();		//Must be a problem with the file, throw exception
        }
        catch(java.io.IOException e) {
            System.out.println("Error validating readability of file: " + e.toString() );
        }

        return true;
    }

    /**
     * Method to close the file. <BR>
     * post: file is closed <BR>
     * complexity: O(1) <BR>
     * memory usage: O(1) <BR>
     * @exception java.io.IOException
     */
    public void close() throws java.io.IOException {
		br.close();   			//Closes the file
	}

} // end class